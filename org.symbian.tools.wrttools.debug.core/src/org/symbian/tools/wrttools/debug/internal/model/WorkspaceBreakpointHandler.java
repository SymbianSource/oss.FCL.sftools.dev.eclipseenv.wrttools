/**
 * 
 */
package org.symbian.tools.wrttools.debug.internal.model;

import java.util.Collection;

import org.chromium.debug.core.ChromiumDebugPlugin;
import org.chromium.debug.core.model.ChromiumLineBreakpoint;
import org.chromium.debug.core.model.DebugTargetImpl;
import org.chromium.debug.core.model.VProjectWorkspaceBridge;
import org.chromium.debug.core.model.WorkspaceBridge.BreakpointHandler;
import org.chromium.sdk.Breakpoint;
import org.chromium.sdk.JavascriptVm;
import org.chromium.sdk.JavascriptVm.BreakpointCallback;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.symbian.tools.wrttools.debug.internal.Activator;
import org.symbian.tools.wrttools.debug.internal.launch.WRTProjectWorkspaceBridge;

public final class WorkspaceBreakpointHandler implements BreakpointHandler {
	private final ResourceManager resourceManager;
	private final DebugTargetImpl debugTarget;
	private final JavascriptVm vm;

	public WorkspaceBreakpointHandler(DebugTargetImpl debugTarget, JavascriptVm vm, ResourceManager resourceManager) {
		this.debugTarget = debugTarget;
		this.vm = vm;
		this.resourceManager = resourceManager;
	}
	
	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		return (WRTProjectWorkspaceBridge.DEBUG_MODEL_ID.equals(breakpoint
				.getModelIdentifier()) || VProjectWorkspaceBridge.DEBUG_MODEL_ID
				.equals(breakpoint.getModelIdentifier()))
				&& !debugTarget.isDisconnected();
	}

	public void registerBreakpoint(final ChromiumLineBreakpoint breakpoint,
			final String script) throws CoreException {
		final int line = (breakpoint.getLineNumber() - 1);
		BreakpointCallback callback = new BreakpointCallback() {
			public void success(Breakpoint b) {
				breakpoint.setBreakpoint(b);
			}

			public void failure(String errorMessage) {
				Activator.log(errorMessage);
			}
		};
		// ILineBreakpoint lines are 1-based while V8 lines are 0-based
		JavascriptVm javascriptVm = vm;
		if (script != null) {
			javascriptVm.setBreakpoint(Breakpoint.Type.SCRIPT_NAME, script
					,line, Breakpoint.EMPTY_VALUE, breakpoint
					.isEnabled(), breakpoint.getCondition(), breakpoint
					.getIgnoreCount(), callback);
		} else {
			javascriptVm.setBreakpoint(Breakpoint.Type.SCRIPT_ID, String
					.valueOf(script), line, Breakpoint.EMPTY_VALUE,
					breakpoint.isEnabled(), breakpoint.getCondition(),
					breakpoint.getIgnoreCount(), callback);
		}
	}

	public void breakpointAdded(IBreakpoint breakpoint) {
		if (!supportsBreakpoint(breakpoint)) {
			return;
		}
        try {
            // Class cast is ensured by the supportsBreakpoint implementation
            final ChromiumLineBreakpoint lineBreakpoint = (ChromiumLineBreakpoint) breakpoint;
            IFile file = (IFile) breakpoint.getMarker().getResource();
            if (!resourceManager.isAddingFile(file)) {
                final String script = resourceManager.translateResourceToScript(file);
                if (script != null) {
                    registerBreakpoint(lineBreakpoint, script);
                }
            }
        } catch (CoreException e) {
            Activator.log(e);
        }
	}

	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
		if (!supportsBreakpoint(breakpoint)) {
			return;
		}
		// Class cast is ensured by the supportsBreakpoint implementation
		((ChromiumLineBreakpoint) breakpoint).changed();
	}

	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		if (!supportsBreakpoint(breakpoint)) {
			return;
		}
		try {
			if (breakpoint.isEnabled()) {
				// Class cast is ensured by the supportsBreakpoint
				// implementation
				ChromiumLineBreakpoint lineBreakpoint = (ChromiumLineBreakpoint) breakpoint;
				lineBreakpoint.clear();
			}
		} catch (CoreException e) {
			ChromiumDebugPlugin.log(e);
		}
	}

	public void breakpointsHit(
			Collection<? extends Breakpoint> breakpointsHit) {
		if (breakpointsHit.isEmpty()) {
			return;
		}
		IBreakpoint[] breakpoints = DebugPlugin.getDefault()
				.getBreakpointManager().getBreakpoints(WRTProjectWorkspaceBridge.DEBUG_MODEL_ID);
		for (IBreakpoint breakpoint : breakpoints) {
			ChromiumLineBreakpoint jsBreakpoint = (ChromiumLineBreakpoint) breakpoint;
			if (breakpointsHit
					.contains(jsBreakpoint.getBrowserBreakpoint())) {
				jsBreakpoint.setIgnoreCount(-1); // reset ignore count as
													// we've hit it
			}
		}
	}
}