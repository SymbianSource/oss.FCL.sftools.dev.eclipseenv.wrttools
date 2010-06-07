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

import org.eclipse.jface.wizard.Wizard;
import org.symbian.tools.wrttools.WRTProject;
import org.symbian.tools.wrttools.core.deploy.DeployJob;

public class DeployWizard extends Wizard {
    private final DeployWizardContext context;
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
    }

    @Override
    public boolean performFinish() {
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
        DeployJob job = new DeployJob(project, target);
        job.schedule();
        return true;
    }
}
