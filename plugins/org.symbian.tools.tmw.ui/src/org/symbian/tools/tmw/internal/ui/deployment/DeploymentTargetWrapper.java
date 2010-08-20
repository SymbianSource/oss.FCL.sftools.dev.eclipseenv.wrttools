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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter2;
import org.symbian.tools.tmw.core.projects.ITMWProject;
import org.symbian.tools.tmw.core.runtimes.IPackager;
import org.symbian.tools.tmw.ui.deployment.IDeploymentTarget;

public class DeploymentTargetWrapper implements IDeploymentTarget {
    public class WorkbenchAdapter2Wrapper implements IWorkbenchAdapter2 {
        private final IWorkbenchAdapter2 adapter;

        public WorkbenchAdapter2Wrapper(IWorkbenchAdapter2 adapter) {
            this.adapter = adapter;
        }

        public RGB getForeground(Object element) {
            return adapter.getForeground(((DeploymentTargetWrapper) element).getActualTarget());
        }

        public RGB getBackground(Object element) {
            return adapter.getBackground(((DeploymentTargetWrapper) element).getActualTarget());
        }

        public FontData getFont(Object element) {
            return adapter.getFont(((DeploymentTargetWrapper) element).getActualTarget());
        }
    }

    public class WorkbenchAdapterWrapper implements IWorkbenchAdapter {
        private final IWorkbenchAdapter adapter;

        public WorkbenchAdapterWrapper(IWorkbenchAdapter adapter) {
            this.adapter = adapter;
        }

        public Object[] getChildren(Object o) {
            return adapter.getChildren(((DeploymentTargetWrapper) o).getActualTarget());
        }

        public ImageDescriptor getImageDescriptor(Object object) {
            return adapter.getImageDescriptor(((DeploymentTargetWrapper) object).getActualTarget());
        }

        public String getLabel(Object o) {
            return adapter.getLabel(((DeploymentTargetWrapper) o).getActualTarget());
        }

        public Object getParent(Object o) {
            return adapter.getParent(((DeploymentTargetWrapper) o).getActualTarget());
        }
    }

    private final DeploymentTargetTypeDescriptor type;
    private final IDeploymentTarget target;

    public void save(IMemento memento) {
        target.save(memento);
    }

    public void init(ITMWProject project, IPackager packager, IMemento memento) {
        target.init(project, packager, memento);
    }

    public DeploymentTargetWrapper(IDeploymentTarget target, DeploymentTargetTypeDescriptor type) {
        this.target = target;
        this.type = type;
    }

    public IStatus deploy(ITMWProject project, IPackager packager, IProgressMonitor monitor)
            throws CoreException {
        return target.deploy(project, packager, monitor);
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
        Object a = target.getAdapter(adapter);
        if (IWorkbenchAdapter.class.isAssignableFrom(adapter)) {
            return a == null ? new TargetWorkbenchAdapter(this) : new WorkbenchAdapterWrapper((IWorkbenchAdapter) a);
        } else if (IWorkbenchAdapter2.class.isAssignableFrom(adapter)) {
            return a == null ? new TargetWorkbenchAdapter(this) : new WorkbenchAdapter2Wrapper((IWorkbenchAdapter2) a);
        } else {
            return a;
        }
    }

    public int getCategory() {
        return type.getPriority() * 0xFFFF + (type.getId().hashCode() & 0xFFFF);
    }

    public String getId() {
        return target.getId();
    }

    public String getName() {
        return target.getName();
    }

    public String getProviderId() {
        return type.getId();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((target == null) ? 0 : target.hashCode());
        return result;
    }

    public DeploymentTargetTypeDescriptor getType() {
        return type;
    }

    public IDeploymentTarget getActualTarget() {
        return target;
    }

    public String getDescription() {
        return target.getDescription();
    }
}
