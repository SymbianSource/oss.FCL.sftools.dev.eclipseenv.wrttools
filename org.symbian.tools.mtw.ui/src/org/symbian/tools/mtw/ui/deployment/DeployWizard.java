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
package org.symbian.tools.mtw.ui.deployment;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.statushandlers.StatusManager;
import org.symbian.tools.mtw.core.MTWCore;
import org.symbian.tools.mtw.core.projects.IMTWProject;
import org.symbian.tools.mtw.core.runtimes.IPackager;
import org.symbian.tools.mtw.internal.deployment.DeployWizardContext;
import org.symbian.tools.mtw.internal.deployment.DeploymentTargetWizardPage;
import org.symbian.tools.mtw.internal.deployment.DeploymentTargetWrapper;
import org.symbian.tools.mtw.ui.MTWCoreUI;
import org.symbian.tools.mtw.ui.ProjectMemo;

public final class DeployWizard extends Wizard {
    private final DeployWizardContext context;
    private final IMTWProject project;

    public DeployWizard(IMTWProject project) {
        this.project = project;
        setNeedsProgressMonitor(true);
        setWindowTitle("Deploy WRT Application");
        context = new DeployWizardContext(project);
    }

    @Override
    public void addPages() {
        addPage(new DeploymentTargetWizardPage(context, MTWCoreUI.getMemo(project)));
    }

    @Override
    public boolean performFinish() {
        final DeploymentTargetWrapper target = context.getTarget();
        if (target.getType().isLongRunning()) {
            DeployJob job = new DeployJob(context.getProject(), target);
            job.schedule();
            return true;
        } else {
            final IStatus[] retval = new IStatus[1];
            try {
                getContainer().run(false, true, new IRunnableWithProgress() {
                    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                        retval[0] = doDeploy(target, context.getProject(), monitor);
                    }
                });
            } catch (InvocationTargetException e) {
                MTWCoreUI.log(e);
            } catch (InterruptedException e) {
                MTWCoreUI.log(e);
            }
            switch (retval[0].getSeverity()) {
            case IStatus.ERROR:
                StatusManager.getManager().handle(retval[0], StatusManager.SHOW | StatusManager.BLOCK);
                return false;
            case IStatus.WARNING:
                StatusManager.getManager().handle(retval[0], StatusManager.SHOW | StatusManager.BLOCK);
            default:
                return true;
            }
        }
    }

    @Override
    public boolean needsPreviousAndNextButtons() {
        return false;
    }

    private IStatus doDeploy(DeploymentTargetWrapper target, IMTWProject project, IProgressMonitor monitor) {
        IStatus status;
        try {
            IPackager packager = MTWCore.getDefault().getRuntimesManager().getPackager(project);
            status = target.deploy(project, packager, monitor);
        } catch (CoreException e) {
            status = e.getStatus();
        }
        ProjectMemo memo = MTWCoreUI.getMemo(project);
        memo.setDeploymentTarget(target.getProviderId(), target);
        return status;
    }

    private final class DeployJob extends Job {
        private final DeploymentTargetWrapper target;
        private final IMTWProject project;

        private DeployJob(IMTWProject project, DeploymentTargetWrapper deploymentTarget) {
            super(String.format("Deploying %s to %s", project.getName(), deploymentTarget.getName()));
            ISchedulingRule rule = deploymentTarget.getType().getSchedulingRule(deploymentTarget.getActualTarget());
            if (rule != null) {
                rule = MultiRule.combine(rule, project.getProject());
            } else {
                rule = project.getProject();
            }
            setRule(rule);
            setUser(true);
            this.project = project;
            this.target = deploymentTarget;
        }

        public IStatus run(IProgressMonitor monitor) {
            final IStatus status = doDeploy(target, project, monitor);
            if (status.getSeverity() != IStatus.ERROR && status.getSeverity() != IStatus.WARNING) {
                StatusManager.getManager().handle(status, StatusManager.SHOW);
            }
            return status;
        }

    }
}
