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
package org.symbian.tools.wrttools.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.PlatformUI;
import org.symbian.tools.wrttools.WRTStatusListener;
import org.symbian.tools.wrttools.core.packager.WrtPackageActionDelegate;
import org.symbian.tools.wrttools.util.ProjectUtils;

public class PackageApplicationHandler extends AbstractHandler implements IHandler {
    private final class JobExtension extends Job {
        private final IProject project;

        private JobExtension(IProject project) {
            super(String.format("Package %s", project.getName()));
            setRule(ResourcesPlugin.getWorkspace().getRoot());
            this.project = project;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            WRTStatusListener statusListener = new WRTStatusListener();
            new WrtPackageActionDelegate().packageProject(project, statusListener);
            return Status.OK_STATUS;
        }
    }

    public Object execute(ExecutionEvent event) throws ExecutionException {
        final IProject project = ProjectUtils.getProjectFromCommandContext(event);
        if (project != null) {
            PlatformUI.getWorkbench().saveAllEditors(true);
            if (project != null) {
                new JobExtension(project).schedule();
            }
        }
        return null;
    }

}
