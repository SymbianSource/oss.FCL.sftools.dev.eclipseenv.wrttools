package org.symbian.tools.wrttools.debug.internal.launch;

import java.util.ArrayList;
import java.util.Collection;

import org.chromium.debug.core.model.ChromiumLineBreakpoint;
import org.chromium.debug.core.model.DebugTargetImpl;
import org.chromium.debug.core.model.StackFrame;
import org.chromium.debug.core.model.VProjectWorkspaceBridge;
import org.chromium.debug.core.model.WorkspaceBridge;
import org.chromium.sdk.JavascriptVm;
import org.chromium.sdk.Script;
import org.chromium.sdk.JavascriptVm.ScriptsCallback;
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

public class WRTProjectWorkspaceBridge implements WorkspaceBridge {
	public static final class Factory implements WorkspaceBridge.Factory {
		private final IProject project;

		public Factory(IProject project) {
			this.project = project;
		}

		@Override
		public WorkspaceBridge attachedToVm(DebugTargetImpl debugTargetImpl,
				JavascriptVm javascriptVm) {
			return new WRTProjectWorkspaceBridge(debugTargetImpl, javascriptVm,
					project);
		}

		@Override
		public String getDebugModelIdentifier() {
			return DEBUG_MODEL_ID;
		}

		@Override
		public JsLabelProvider getLabelProvider() {
			return new WrtLabelProvider();
		}

	}

	public final static String DEBUG_MODEL_ID = VProjectWorkspaceBridge.DEBUG_MODEL_ID;

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

	@Override
	public void beforeDetach() {
		// Do nothing
	}

	@Override
	public BreakpointHandler getBreakpointHandler() {
		return breakpointHandler;
	}

	@Override
	public IFile getScriptResource(Script script) {
		return resourceManager.getResource(script);
	}

	@Override
	public void handleVmResetEvent() {
		resourceManager.clear();
	}

	@Override
	public void launchRemoved() {
		// Do nothing
	}

	@Override
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

	@Override
	public void scriptLoaded(Script newScript) {
		resourceManager.addScript(newScript);
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

			return resourceManager.getResource(script);
		}
	};
}
