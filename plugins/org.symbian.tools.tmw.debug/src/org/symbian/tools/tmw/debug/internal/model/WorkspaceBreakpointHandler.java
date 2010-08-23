/**
 * 
 */
package org.symbian.tools.tmw.debug.internal.model;

import java.util.Collection;

import org.chromium.debug.core.ChromiumDebugPlugin;
import org.chromium.debug.core.model.BreakpointMap;
import org.chromium.debug.core.model.BreakpointSynchronizer.BreakpointHelper.CreateCallback;
import org.chromium.debug.core.model.ChromiumLineBreakpoint;
import org.chromium.debug.core.model.DebugTargetImpl;
import org.chromium.debug.core.model.VProjectWorkspaceBridge;
import org.chromium.debug.core.model.VmResourceId;
import org.chromium.debug.core.model.WorkspaceBridge.BreakpointHandler;
import org.chromium.sdk.Breakpoint;
import org.chromium.sdk.JavascriptVm;
import org.chromium.sdk.SyncCallback;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.symbian.tools.tmw.debug.internal.launch.WRTProjectWorkspaceBridge;
import org.symbian.tools.tmw.previewer.PreviewerPlugin;

public final class WorkspaceBreakpointHandler implements BreakpointHandler {
	private final BreakpointMap.InTargetMap breakpointInTargetMap = new BreakpointMap.InTargetMap();
	private final DebugTargetImpl debugTarget;
	
    public WorkspaceBreakpointHandler(DebugTargetImpl debugTarget) {
		this.debugTarget = debugTarget;
	}

    public void breakpointAdded(IBreakpoint breakpoint) {
        ChromiumLineBreakpoint lineBreakpoint = tryCastBreakpoint(breakpoint);
        if (lineBreakpoint == null) {
            return;
        }
        if (ChromiumLineBreakpoint.getIgnoreList().contains(breakpoint)) {
            return;
        }
        if (!lineBreakpoint.isEnabled()) {
            return;
        }
        IFile file = (IFile) lineBreakpoint.getMarker().getResource();
        VmResourceId vmResourceId;
        try {
            vmResourceId = findVmResourceIdFromWorkspaceFile(file);
        } catch (CoreException e) {
            ChromiumDebugPlugin.log(new Exception("Failed to resolve script for the file " + file, e)); //$NON-NLS-1$
            return;
        }
        if (vmResourceId == null) {
            // Might be a script from a different debug target
            return;
        }
        createBreakpointOnRemote(lineBreakpoint, vmResourceId, null, null);
    }

    public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
        ChromiumLineBreakpoint lineBreakpoint = tryCastBreakpoint(breakpoint);
        if (lineBreakpoint == null) {
            return;
        }
        if (ChromiumLineBreakpoint.getIgnoreList().contains(lineBreakpoint)) {
            return;
        }
        Breakpoint sdkBreakpoint = breakpointInTargetMap.getSdkBreakpoint(lineBreakpoint);
        if (sdkBreakpoint == null) {
            return;
        }

        try {
            ChromiumLineBreakpoint.Helper.updateOnRemote(sdkBreakpoint, lineBreakpoint);
        } catch (RuntimeException e) {
            ChromiumDebugPlugin.log(new Exception("Failed to change breakpoint in " + //$NON-NLS-1$
                    getTargetNameSafe(), e));
        }

    }

    public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
        ChromiumLineBreakpoint lineBreakpoint = tryCastBreakpoint(breakpoint);
        if (lineBreakpoint == null) {
            return;
        }
        if (ChromiumLineBreakpoint.getIgnoreList().contains(lineBreakpoint)) {
            return;
        }

        Breakpoint sdkBreakpoint = breakpointInTargetMap.getSdkBreakpoint(lineBreakpoint);
        if (sdkBreakpoint == null) {
            return;
        }

        try {
            if (!breakpoint.isEnabled()) {
            return;
            }
        } catch (CoreException e) {
            ChromiumDebugPlugin.log(e);
            return;
        }
        JavascriptVm.BreakpointCallback callback = new JavascriptVm.BreakpointCallback() {
            public void failure(String errorMessage) {
                ChromiumDebugPlugin.log(new Exception("Failed to remove breakpoint in " + //$NON-NLS-1$
                        getTargetNameSafe() + ": " + errorMessage)); //$NON-NLS-1$
            }

            public void success(Breakpoint breakpoint) {
            }
        };
        try {
            sdkBreakpoint.clear(callback, null);
        } catch (RuntimeException e) {
            ChromiumDebugPlugin.log(new Exception("Failed to remove breakpoint in " + //$NON-NLS-1$
                    getTargetNameSafe(), e));
        }
        breakpointInTargetMap.remove(lineBreakpoint);
    }

    public void breakpointsHit(Collection<? extends Breakpoint> breakpointsHit) {
        if (breakpointsHit.isEmpty()) {
            return;
        }

        for (Breakpoint sdkBreakpoint : breakpointsHit) {
            ChromiumLineBreakpoint uiBreakpoint = breakpointInTargetMap.getUiBreakpoint(sdkBreakpoint);
            if (uiBreakpoint != null) {
                uiBreakpoint.setIgnoreCount(-1); // reset ignore count as we've hit it
            }
        }
    }

    public void createBreakpointOnRemote(final ChromiumLineBreakpoint lineBreakpoint, final VmResourceId vmResourceId,
            final CreateCallback createCallback, SyncCallback syncCallback) {
        try {
            ChromiumLineBreakpoint.Helper.CreateOnRemoveCallback callback = new ChromiumLineBreakpoint.Helper.CreateOnRemoveCallback() {
                public void failure(String errorMessage) {
                    if (createCallback == null) {
                        ChromiumDebugPlugin.logError(errorMessage);
                    } else {
                        createCallback.failure(new Exception(errorMessage));
                    }
                }

                public void success(Breakpoint breakpoint) {
                    breakpointInTargetMap.add(breakpoint, lineBreakpoint);
                    if (createCallback != null) {
                        createCallback.success();
                    }
                }
            };

            ChromiumLineBreakpoint.Helper.createOnRemote(lineBreakpoint, vmResourceId, debugTarget, callback,
                    syncCallback);
        } catch (CoreException e) {
            ChromiumDebugPlugin.log(new Exception("Failed to create breakpoint in " + //$NON-NLS-1$
                    getTargetNameSafe(), e));
        }
    }

    private VmResourceId findVmResourceIdFromWorkspaceFile(IFile resource) throws CoreException {
        return VmResourceId.forName(PreviewerPlugin.getDefault().getHttpPreviewer().getHttpUrl(resource));
    }

    private String getTargetNameSafe() {
        try {
            return debugTarget.getLaunch().getLaunchConfiguration().getName();
        } catch (RuntimeException e) {
          return "<unknown>"; //$NON-NLS-1$
        }
    }

    public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		return (WRTProjectWorkspaceBridge.DEBUG_MODEL_ID.equals(breakpoint
				.getModelIdentifier()) || VProjectWorkspaceBridge.DEBUG_MODEL_ID
				.equals(breakpoint.getModelIdentifier()))
				&& !debugTarget.isDisconnected();
	}

    public ChromiumLineBreakpoint tryCastBreakpoint(IBreakpoint breakpoint) {
        if (!supportsBreakpoint(breakpoint)) {
            return null;
        }
        if (breakpoint instanceof ChromiumLineBreakpoint == false) {
            return null;
        }
        return (ChromiumLineBreakpoint) breakpoint;
    }

}