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
package org.symbian.tools.mtw.internal.deployment;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchAdapter;
import org.symbian.tools.mtw.core.projects.IMTWProject;
import org.symbian.tools.mtw.core.runtimes.IMobileWebRuntime;
import org.symbian.tools.mtw.ui.deployment.IDeploymentTarget;
import org.symbian.tools.mtw.ui.deployment.IDeploymentTargetProvider;

public class DeploymentTargetWrapper implements IDeploymentTarget {
    public class TargetWorkbenchAdapter extends WorkbenchAdapter {
        @Override
        public String getLabel(Object object) {
            return getName();
        }

        @Override
        public ImageDescriptor getImageDescriptor(Object object) {
            return provider.getImageDescriptor();
        }
    }

    private final DeploymentTargetProviderDescriptor provider;
    private final IDeploymentTarget target;

    public DeploymentTargetWrapper(IDeploymentTarget target, DeploymentTargetProviderDescriptor provider) {
        this.target = target;
        this.provider = provider;
    }

    public IStatus deploy(IMTWProject project, IMobileWebRuntime runtime, IProgressMonitor monitor)
            throws CoreException {
        return target.deploy(project, runtime, monitor);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DeploymentTargetWrapper other = (DeploymentTargetWrapper) obj;
        if (target == null) {
            if (other.target != null) {
                return false;
            }
        } else if (!target.equals(other.target)) {
            return false;
        }
        return true;
    }

    public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
        Object ad = target.getAdapter(adapter);
        if (ad == null && adapter.equals(IWorkbenchAdapter.class)) {
            return new TargetWorkbenchAdapter();
        }
        return ad;
    }

    public int getCategory() {
        return provider.getPriority() * 0xFFFF + (provider.getId().hashCode() & 0xFFFF);
    }

    public String getId() {
        return target.getId();
    }

    public String getName() {
        return target.getName();
    }

    public String getProviderId() {
        return provider.getId();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((target == null) ? 0 : target.hashCode());
        return result;
    }

    public void save(IMemento child) {
        target.save(child);
    }

    public void load(IMemento child) {
        target.load(child);
    }

    public IDeploymentTargetProvider getProvider() {
        return provider;
    }

    public IDeploymentTarget getActualTarget() {
        return target;
    }
}
