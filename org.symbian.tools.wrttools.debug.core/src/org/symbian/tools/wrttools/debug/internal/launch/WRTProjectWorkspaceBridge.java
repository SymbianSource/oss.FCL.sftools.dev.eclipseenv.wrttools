package org.symbian.tools.wrttools.debug.internal.launch;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.chromium.debug.core.model.ChromiumLineBreakpoint;
import org.chromium.debug.core.model.DebugTargetImpl;
import org.chromium.debug.core.model.StackFrame;
import org.chromium.debug.core.model.WorkspaceBridge;
import org.chromium.sdk.CallFrame;
import org.chromium.sdk.JavascriptVm;
import org.chromium.sdk.Script;
import org.chromium.sdk.JavascriptVm.ScriptsCallback;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.model.IStackFrame;
import org.symbian.tools.wrttools.debug.internal.Activator;
import org.symbian.tools.wrttools.debug.internal.model.ResourceManager;
import org.symbian.tools.wrttools.debug.internal.model.WorkspaceBreakpointHandler;
import org.symbian.tools.wrttools.previewer.PreviewerPlugin;

public class WRTProjectWorkspaceBridge implements WorkspaceBridge {
	public static final class Factory implements WorkspaceBridge.Factory {
		private final IProject project;

		public Factory(IProject project) {
			this.project = project;
		}

		public WorkspaceBridge attachedToVm(DebugTargetImpl debugTargetImpl,
				JavascriptVm javascriptVm) {
			return new WRTProjectWorkspaceBridge(debugTargetImpl, javascriptVm,
					project);
		}

		public String getDebugModelIdentifier() {
			return DEBUG_MODEL_ID;
		}

		public JsLabelProvider getLabelProvider() {
			return new WrtLabelProvider();
		}

	}

//	public final static String DEBUG_MODEL_ID = VProjectWorkspaceBridge.DEBUG_MODEL_ID;
	public final static String DEBUG_MODEL_ID = "org.symbian.debug";

	private final BreakpointHandler breakpointHandler;
	private final JavascriptVm javascriptVm;
	private final IProject project;
	private final ResourceManager resourceManager;

	public WRTProjectWorkspaceBridge(DebugTargetImpl debugTargetImpl,
			JavascriptVm javascriptVm, IProject project) {
		this.javascriptVm = javascriptVm;
		this.project = project;
		this.resourceManager = new ResourceManager();
		breakpointHandler = new WorkspaceBreakpointHandler(debugTargetImpl,
				javascriptVm, resourceManager);
		ILaunch launch = debugTargetImpl.getLaunch();
		launch.setSourceLocator(sourceLocator);
	}

	public void beforeDetach() {
		// Do nothing
	}

	public BreakpointHandler getBreakpointHandler() {
		return breakpointHandler;
	}

	public IFile getScriptResource(Script script) {
		return resourceManager.getResource(script);
	}

	public void handleVmResetEvent() {
		resourceManager.clear();
	}

	public void launchRemoved() {
		// Do nothing
	}

	public void reloadScriptsAtStart() {
		javascriptVm.getScripts(new ScriptsCallback() {
			public void failure(String errorMessage) {
				Activator.log(errorMessage);
			}

			public void success(Collection<Script> scripts) {
				if (!javascriptVm.isAttached()) {
					return;
				}
				for (Script script : scripts) {
					resourceManager.addScript(script);
				}

				IMarker[] markers;
				try {
					markers = project.findMarkers(
							ChromiumLineBreakpoint.BREAKPOINT_MARKER, true,
							IResource.DEPTH_INFINITE);
					Collection<ChromiumLineBreakpoint> breakpoints = new ArrayList<ChromiumLineBreakpoint>(
							markers.length);
					for (IMarker marker : markers) {
						// If it is not ChromiumLineBreakpoint -
						// something's gone horribly wrong. Better get
						// ClassCastException
						ChromiumLineBreakpoint breakpoint = (ChromiumLineBreakpoint) DebugPlugin
								.getDefault().getBreakpointManager()
								.getBreakpoint(marker);
						breakpointHandler.breakpointAdded(breakpoint);
						breakpoints.add(breakpoint);
					}
				} catch (CoreException e) {
					Activator.log(e);
				}

			}
		});
	}

	public void scriptLoaded(Script newScript) {
		resourceManager.addScript(newScript);
	}

	public int getLineNumber(CallFrame stackFrame) {
		int offset = 0;
		Script script = stackFrame.getScript();
		if (script != null) {
			offset = script.getStartLine();
		}
		return offset + stackFrame.getLineNumber() + 1;
	}

	/**
	 * This very simple source locator works because we provide our own source
	 * files. We'll have to try harder, once we link with resource js files.
	 */
	private final ISourceLocator sourceLocator = new ISourceLocator() {
		public Object getSourceElement(IStackFrame stackFrame) {
			if (stackFrame instanceof StackFrame == false) {
				return null;
			}
			StackFrame jsStackFrame = (StackFrame) stackFrame;

			Script script = jsStackFrame.getCallFrame().getScript();
			if (script == null) {
				return null;
			}

			IFile resource = resourceManager.getResource(script);
			if (resource != null) {
				return resource;
			} else {
				File file = PreviewerPlugin.getDefault().getHttpPreviewer().getLocalFile(script.getName());
				if (file != null) {
					try {
						return EFS.getStore(file.toURI());
					} catch (CoreException e) {
						Activator.log(e);
					}
				}
			}
			return null;
		}
	};

}
