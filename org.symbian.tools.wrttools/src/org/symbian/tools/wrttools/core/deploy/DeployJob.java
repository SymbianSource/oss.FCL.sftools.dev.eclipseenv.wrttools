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
package org.symbian.tools.wrttools.core.deploy;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressConstants2;
import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.WRTProject;
import org.symbian.tools.wrttools.core.deployer.DeployException;
import org.symbian.tools.wrttools.core.deployer.IWidgetDeployer;
import org.symbian.tools.wrttools.core.packager.WrtPackageActionDelegate;
import org.symbian.tools.wrttools.core.status.IWRTStatusListener;
import org.symbian.tools.wrttools.sdt.utils.Logging;
import org.symbian.tools.wrttools.wizards.deploy.DeploymentTarget;

public class DeployJob extends Job {
    public class AlwaysErrorMultiStatus extends MultiStatus {
        public AlwaysErrorMultiStatus(String message) {
            super(Activator.PLUGIN_ID, 0, message, null);
        }

        @Override
        public int getSeverity() {
            return IStatus.ERROR;
        }
    }

    private final DeploymentTarget target;
    private final WrtPackageActionDelegate packagerAction = new WrtPackageActionDelegate();
    private final WRTProject project;

    public DeployJob(WRTProject project, DeploymentTarget deploymentTarget) {
        super(String.format("Deploying %s to %s", project.getProject().getName(), deploymentTarget.getName()));
        setUser(true);
        setProperty(IProgressConstants2.SHOW_IN_TASKBAR_ICON_PROPERTY, Boolean.TRUE);
        this.project = project;
        this.target = deploymentTarget;
    }

    private void signalDeploymentComplete(final DeploymentTarget target) {
        if (target.getDeployMessage() != null) {
            final Shell shell = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell();
            shell.getDisplay().asyncExec(new Runnable() {
                public void run() {
                    MessageDialog.openWarning(shell, "WRT Application Deployment", target.getDeployMessage());
                }
            });
        }
    }

    public IStatus run(IProgressMonitor monitor) {
        monitor.beginTask("Deploying application", IProgressMonitor.UNKNOWN);
        final MultiStatus status = new AlwaysErrorMultiStatus(String.format("Cannot deploy appliction to %s",
                target.getName()));
        final IWRTStatusListener statusListener = new ProgressMonitorAndLogger(monitor, status);
        IWidgetDeployer wd = target.createDeployer(statusListener);

        IStatus result = new Status(IStatus.OK, Activator.PLUGIN_ID, 0, "", null);
        IProject p = project.getProject();
        if (p != null) {
            /* package the files before deployment */
            boolean packageSuccess = packagerAction.packageProject(p, statusListener);
            if (!packageSuccess) {
                return status;
            }
            String packagedPath;
            try {
                IPath wgzPath = new Path(p.getName() + ".wgz");
                IFile wgz = p.getFile(wgzPath);
                packagedPath = wgz.getLocation().toFile().getCanonicalFile().toString();
                try {
                    result = wd.deploy(packagedPath, target.getName(), monitor);
                    if (result.isOK()) {
                        project.setDeploymentTarget(target);
                        signalDeploymentComplete(target);
                    } else {
                        result = status;
                    }
                } catch (DeployException e) {
                    result = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, e.getMessage(), e);
                    Logging.log(Activator.getDefault(), result);
                }
            } catch (IOException e) {
                Activator.log(IStatus.ERROR, "Error deploying widget", e);
            }
        }
        return result;
    }

}
