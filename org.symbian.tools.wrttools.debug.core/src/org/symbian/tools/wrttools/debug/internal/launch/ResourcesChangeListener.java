/**
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
 */
package org.symbian.tools.wrttools.debug.internal.launch;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.symbian.tools.wrttools.debug.internal.Activator;
import org.symbian.tools.wrttools.debug.internal.IConstants;

public class ResourcesChangeListener implements IResourceChangeListener {
    public class ProjectListGatherer implements IResourceDeltaVisitor {
        public final Collection<IProject> projects = new HashSet<IProject>();
        private final ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();

        public boolean visit(IResourceDelta delta) throws CoreException {
            int d = delta.getFlags()
                    & (IResourceDelta.CONTENT | IResourceDelta.OPEN | IResourceDelta.MOVED_TO
                            | IResourceDelta.MOVED_FROM | IResourceDelta.SYNC | IResourceDelta.REPLACED);
            if (delta.getKind() != IResourceDelta.CHANGED || d != 0) {
                IProject project = delta.getResource().getProject();
                if (DebugUtil.isProjectDebugged(project, launchManager, null)) {
                    projects.add(project);
                }
                return false;
            }
            return true;
        }

    }

    public void resourceChanged(IResourceChangeEvent event) {
        if (event.getDelta() != null
                && !MessageDialogWithToggle.ALWAYS.equals(Activator.getDefault().getPreferenceStore().getString(
                        IConstants.PREF_SHOW_RESOURCE_CHANGE_ERROR))) {
            ProjectListGatherer gatherer = new ProjectListGatherer();
            try {
                event.getDelta().accept(gatherer);
            } catch (CoreException e) {
                Activator.log(e);
            }
            int size = gatherer.projects.size();
            if (size > 0) {
                Display.getDefault().asyncExec(new Runnable() {
                    public void run() {
                        String message = "Debug browser is not updated when the files are updated. You may notice discrepancies between your workspace contents and debug session. You should either refresh the browser or restart debugging session to see the latest changes made to the workspace.";
                        MessageDialogWithToggle.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                                .getShell(), "WebRuntime Debugger", message,
                                "Do not show this warning on code changes", false, Activator.getDefault()
                                        .getPreferenceStore(), IConstants.PREF_SHOW_RESOURCE_CHANGE_ERROR);
                        ResourcesPlugin.getWorkspace().removeResourceChangeListener(ResourcesChangeListener.this);
                    }
                });
            }
        }
    }

}
