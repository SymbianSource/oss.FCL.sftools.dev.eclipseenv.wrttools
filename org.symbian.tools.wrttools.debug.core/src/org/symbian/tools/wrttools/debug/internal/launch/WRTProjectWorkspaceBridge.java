package org.symbian.tools.wrttools.debug.internal.launch;

import java.util.ArrayList;
import java.util.Collection;

import org.chromium.debug.core.model.BreakpointSynchronizer.Callback;
import org.chromium.debug.core.model.BreakpointSynchronizer.Direction;
import org.chromium.debug.core.model.ChromiumLineBreakpoint;
import org.chromium.debug.core.model.DebugTargetImpl;
import org.chromium.debug.core.model.VmResource;
import org.chromium.debug.core.model.VmResourceId;
import org.chromium.debug.core.model.WorkspaceBridge;
import org.chromium.sdk.CallFrame;
import org.chromium.sdk.JavascriptVm;
import org.chromium.sdk.JavascriptVm.ScriptsCallback;
import org.chromium.sdk.Script;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.containers.ProjectSourceContainer;
import org.symbian.tools.wrttools.debug.internal.Activator;
import org.symbian.tools.wrttools.debug.internal.model.ResourceManager;
import org.symbian.tools.wrttools.debug.internal.model.WorkspaceBreakpointHandler;

public class WRTProjectWorkspaceBridge implements WorkspaceBridge {
    public static final class Factory implements WorkspaceBridge.Factory {
        private final IProject project;

        public Factory(IProject project) {
            this.project = project;
        }

        public WorkspaceBridge attachedToVm(DebugTargetImpl debugTargetImpl, JavascriptVm javascriptVm) {
            return new WRTProjectWorkspaceBridge(debugTargetImpl, javascriptVm, project);
        }

        public String getDebugModelIdentifier() {
            return DEBUG_MODEL_ID;
        }

        public JsLabelProvider getLabelProvider() {
            return new WrtLabelProvider();
        }
    }

    public final static String DEBUG_MODEL_ID = "org.symbian.debug";

    private final BreakpointHandler breakpointHandler;
    private final JavascriptVm javascriptVm;
    private final IProject project;
    private final ResourceManager resourceManager;

    public WRTProjectWorkspaceBridge(DebugTargetImpl debugTargetImpl, JavascriptVm javascriptVm, IProject project) {
        this.javascriptVm = javascriptVm;
        this.project = project;
        this.resourceManager = new ResourceManager();
        this.sourceLocator = new WebApplicationSourceLocator(resourceManager);
        breakpointHandler = new WorkspaceBreakpointHandler(debugTargetImpl, resourceManager);
        ILaunch launch = debugTargetImpl.getLaunch();
        try {
            sourceLocator.initializeDefaults(launch.getLaunchConfiguration());
            sourceLocator.setSourceContainers(new ISourceContainer[] { new ProjectSourceContainer(project, false) });
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
        launch.setSourceLocator(sourceLocator);
    }

    public void beforeDetach() {
        // Do nothing
    }

    public BreakpointHandler getBreakpointHandler() {
        return breakpointHandler;
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
                    markers = project.findMarkers(ChromiumLineBreakpoint.BREAKPOINT_MARKER, true,
                            IResource.DEPTH_INFINITE);
                    Collection<ChromiumLineBreakpoint> breakpoints = new ArrayList<ChromiumLineBreakpoint>(
                            markers.length);
                    for (IMarker marker : markers) {
                        // If it is not ChromiumLineBreakpoint -
                        // something's gone horribly wrong. Better get
                        // ClassCastException
                        ChromiumLineBreakpoint breakpoint = (ChromiumLineBreakpoint) DebugPlugin.getDefault()
                                .getBreakpointManager().getBreakpoint(marker);
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

    private final ISourceLookupDirector sourceLocator;

    public VmResource findVmResourceFromWorkspaceFile(IFile file) throws CoreException {
        return new VmResourceImpl(resourceManager.findVmResource(file), resourceManager.getScript(file), file.getName());
    }

    private static final class VmResourceImpl implements VmResource {
        private final String name;
        private final Script script;
        private final VmResourceId id;

        public VmResourceImpl(VmResourceId id, Script script, String name) {
            super();
            this.id = id;
            this.script = script;
            this.name = name;
        }

        public VmResourceId getId() {
            return id;
        }

        public Script getScript() {
            return script;
        }

        public String getFileName() {
            return name;
        }

    }

    public void reloadScript(Script script) {
        System.out.println(script);

    }

    public void synchronizeBreakpoints(Direction direction, Callback callback) {
        System.out.println(direction);

    }

}
