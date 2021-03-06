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
package org.symbian.tools.tmw.internal.ui.deployment;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.symbian.tools.tmw.core.projects.ITMWProject;
import org.symbian.tools.tmw.ui.TMWCoreUI;

public class DeployWizardContext {
    private DeploymentTargetWrapper target;
    private final ITMWProject project;
    private boolean logging;

    public DeployWizardContext(ITMWProject project) {
        this.project = project;
    }

    public void setTarget(DeploymentTargetWrapper target) {
        this.target = target;
    }

    public DeploymentTargetWrapper getTarget() {
        return target;
    }

    public ITMWProject getProject() {
        return project;
    }

    public DeploymentTargetWrapper[] getDeploymentTargets() {
        final DeploymentTargetTypeDescriptor[] providers = TMWCoreUI.getDefault().getDeploymentTypesRegistry()
                .getProviders();
        Collection<DeploymentTargetWrapper> targets = new HashSet<DeploymentTargetWrapper>();

        for (DeploymentTargetTypeDescriptor provider : providers) {
            if (provider.supports(project)) {
                targets.addAll(Arrays.asList(provider.getTargets(project)));
            }
        }
        return targets.toArray(new DeploymentTargetWrapper[targets.size()]);
    }

    public void doSearch(IProgressMonitor monitor) throws CoreException {
        final DeploymentTargetTypeDescriptor[] providers = TMWCoreUI.getDefault().getDeploymentTypesRegistry()
                .getProviders();
        monitor.beginTask("Discovering deployment targets", providers.length * 10);
        for (DeploymentTargetTypeDescriptor descriptor : providers) {
            descriptor.discoverTargets(new SubProgressMonitor(monitor, 10));
        }
        monitor.done();
    }

    public boolean areTargetsReady() {
        final DeploymentTargetTypeDescriptor[] providers = TMWCoreUI.getDefault().getDeploymentTypesRegistry()
                .getProviders();
        for (DeploymentTargetTypeDescriptor descriptor : providers) {
            if (!descriptor.targetsDiscovered()) {
                return false;
            }
        }
        return true;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    public boolean isLogging() {
        return logging;
    }
}
