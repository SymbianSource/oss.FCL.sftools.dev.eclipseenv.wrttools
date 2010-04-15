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
package org.symbian.tools.wrttools.wizards.deploy;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.WRTProject;
import org.symbian.tools.wrttools.core.deployer.DeployException;
import org.symbian.tools.wrttools.core.deployer.IWidgetDeployer;
import org.symbian.tools.wrttools.core.packager.WrtPackageActionDelegate;
import org.symbian.tools.wrttools.core.status.IWRTStatusListener;
import org.symbian.tools.wrttools.core.status.WRTStatus;
import org.symbian.tools.wrttools.sdt.utils.Logging;
import org.symbian.tools.wrttools.util.ProjectUtils;

public class DeployWizard extends Wizard {
    public class PagePrinter implements IWRTStatusListener {
        private final DeploymentSummaryWizardPage page;

        public PagePrinter(DeploymentSummaryWizardPage summaryPage) {
            page = summaryPage;
        }

        public void emitStatus(WRTStatus status) {
            page.log(status.getStatusDescription().toString());
        }

        public boolean isStatusHandled(WRTStatus status) {
            return true;
        }

        public void close() {
            // Do nothing
        }

        public boolean canPackageWithErrors(IProject project) {
            return ProjectUtils.canPackageWithErrors(project);
        }

    }

    private final DeployWizardContext context;
    private final DeploymentSummaryWizardPage summaryPage = new DeploymentSummaryWizardPage();
    private final WRTProject project;

    public DeployWizard(WRTProject project) {
        this.project = project;
        setNeedsProgressMonitor(true);
        setWindowTitle("Deploy WRT Application");
        context = new DeployWizardContext(project);
    }

    @Override
    public void addPages() {
        addPage(new DeploymentTargetWizardPage(context, project.getDeploymentTarget()));
        addPage(summaryPage);
    }

    @Override
    public boolean performFinish() {
        summaryPage.clear();
        getContainer().showPage(summaryPage);
        return deploy();
    }

    @Override
    public boolean needsPreviousAndNextButtons() {
        return false;
    }

    /**
     * deploys the actual widget.
     */
    private boolean deploy() {
        DeploymentTarget target = context.getTarget();
        DeployJob job = new DeployJob(context.getProject(), target);
        try {
            getContainer().run(true, true, job);
        } catch (InvocationTargetException e) {
            Activator.log(e);
        } catch (InterruptedException e) {
            Activator.log(e);
        }
        return job.isSuccessful();
    }

    private final class DeployJob implements IRunnableWithProgress {
        private final DeploymentTarget target;
        private final WrtPackageActionDelegate packagerAction = new WrtPackageActionDelegate();
        private final IProject project;
        private boolean successful = false;

        private DeployJob(IProject project, DeploymentTarget deploymentTarget) {
            this.project = project;
            this.target = deploymentTarget;
        }

        public boolean isSuccessful() {
            return successful;
        }

        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
            IWRTStatusListener statusListener = new PagePrinter(summaryPage);
            IWidgetDeployer wd = target.createDeployer(statusListener);

            IStatus result = new Status(IStatus.OK, Activator.PLUGIN_ID, 0, "", null);

            if (project != null) {
                /* package the files before deployment */
                boolean packageSuccess = packagerAction.packageProject(project, statusListener);
                if (!packageSuccess) {
                    return;
                }
                String packagedPath;
                try {
                    IPath wgzPath = new Path(project.getName() + ".wgz");
                    IFile wgz = project.getFile(wgzPath);
                    packagedPath = wgz.getLocation().toFile().getCanonicalFile().toString();
                    try {
                        result = wd.deploy(packagedPath, target.getName(), monitor);
                        if (result.isOK()) {
                            DeployWizard.this.project.setDeploymentTarget(target);
                            successful = true;
                            signalDeploymentComplete(target);
                        }
                    } catch (DeployException e) {
                        result = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, e.getMessage(), e);
                        Logging.log(Activator.getDefault(), result);
                    }
                } catch (IOException e) {
                    Activator.log(IStatus.ERROR, "Error deploying widget", e);
                }
            }
        }

    }

    private void signalDeploymentComplete(final DeploymentTarget target) {
        if (target.getDeployMessage() != null) {
            getShell().getDisplay().asyncExec(new Runnable() {
                public void run() {
                    MessageDialog.openWarning(getShell(), "WRT Application Deployment", target.getDeployMessage());
                }
            });
        }
    }
}
