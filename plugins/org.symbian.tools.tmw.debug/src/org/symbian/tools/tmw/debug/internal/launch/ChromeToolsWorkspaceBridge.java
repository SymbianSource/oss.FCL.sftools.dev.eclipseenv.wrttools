/*******************************************************************************
 * Copyright (c) 2010 Symbian Foundation and/or its subsidiary(-ies).
 * All rights reserved.
 * This component and the accompanying materials are made available
 * under the terms of the License "Eclipse Public License v1.0"
 * which accompanies this distribution, and is available
 * at the URL "http://www.eclipse.org/legal/epl-v10.html".
 *
 * Initial Contributors:
 * Symbian Foundation - initial contribution.
 * Contributors:
 * Description:
 * Overview:
 * Details:
 * Platforms/Drives/Compatibility:
 * Assumptions/Requirement/Pre-requisites:
 * Failures and causes:
 *******************************************************************************/
package org.symbian.tools.tmw.debug.internal.launch;

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
import org.eclipse.debug.core.sourcelookup.containers.DirectorySourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.ProjectSourceContainer;
import org.symbian.tools.tmw.debug.internal.Activator;
import org.symbian.tools.tmw.debug.internal.model.ResourceManager;
import org.symbian.tools.tmw.debug.internal.model.WorkspaceBreakpointHandler;

public class ChromeToolsWorkspaceBridge implements WorkspaceBridge {
    public static final class Factory implements WorkspaceBridge.Factory {
        private final IProject project;

        public Factory(IProject workspaceProject) {
            this.project = workspaceProject;
        }

        public WorkspaceBridge attachedToVm(DebugTargetImpl debugTargetImpl, JavascriptVm javascriptVm) {
            return new ChromeToolsWorkspaceBridge(debugTargetImpl, javascriptVm, project);
        }

        public String getDebugModelIdentifier() {
            return DEBUG_MODEL_ID;
        }

        public JsLabelProvider getLabelProvider() {
            return new TmwLabelProvider();
        }
    }

    public static final String DEBUG_MODEL_ID = "org.symbian.debug";

    private final BreakpointHandler breakpointHandler;
    private final JavascriptVm javascriptVm;
    private final IProject project;
    private final ResourceManager resourceManager;

    public ChromeToolsWorkspaceBridge(DebugTargetImpl debugTargetImpl, JavascriptVm vm, IProject workspaceProject) {
        this.javascriptVm = vm;
        this.project = workspaceProject;
        this.resourceManager = new ResourceManager();
        this.sourceLocator = new WebApplicationSourceLocator(resourceManager);
        breakpointHandler = new WorkspaceBreakpointHandler(debugTargetImpl);
        ILaunch launch = debugTargetImpl.getLaunch();
        try {
            sourceLocator.initializeDefaults(launch.getLaunchConfiguration());
            sourceLocator.setSourceContainers(new ISourceContainer[] {
                    new ProjectSourceContainer(workspaceProject, false),
                    new DirectorySourceContainer(Activator.getDefault().getStateLocation(), true) });
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
                        // If it is not ChromiumLineBreakpoint - something's gone horribly wrong. Better get ClassCastException
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

        public VmResourceImpl(VmResourceId resourceId, Script browserScript, String resourceName) {
            super();
            this.id = resourceId;
            this.script = browserScript;
            this.name = resourceName;
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
