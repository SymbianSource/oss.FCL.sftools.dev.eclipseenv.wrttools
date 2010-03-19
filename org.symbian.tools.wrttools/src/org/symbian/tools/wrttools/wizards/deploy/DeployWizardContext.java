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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.symbian.tools.wrttools.WRTProject;

public class DeployWizardContext {
    private DeploymentTarget target;
    private final WRTProject project;
    private boolean logging;

    public DeployWizardContext(WRTProject project) {
        this.project = project;
    }

    public void setTarget(DeploymentTarget target) {
        this.target = target;
    }

    public DeploymentTarget getTarget() {
        return target;
    }

    public IProject getProject() {
        return project.getProject();
    }

    public DeploymentTarget[] getDeploymentTargets() {
        return DeploymentTargetRegistry.getRegistry().getDeploymentTargets();
    }

    public void doSearch(SubProgressMonitor monitor) throws CoreException {
        DeploymentTargetRegistry.getRegistry().doSearch(monitor);
    }

    public boolean didBluetoothLookup() {
        return DeploymentTargetRegistry.getRegistry().didBluetoothLookup();
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    public boolean isLogging() {
        return logging;
    }
}
