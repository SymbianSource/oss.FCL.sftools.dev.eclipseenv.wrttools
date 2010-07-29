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

import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.resource.ImageDescriptor;
import org.symbian.tools.mtw.core.projects.IMTWProject;
import org.symbian.tools.mtw.ui.MTWCoreUI;
import org.symbian.tools.mtw.ui.deployment.IDeploymentTarget;
import org.symbian.tools.mtw.ui.deployment.IDeploymentTargetType;

public final class DeploymentTargetTypeDescriptor implements IDeploymentTargetType {
    public class NullProvider implements IDeploymentTargetType {
        public IDeploymentTarget[] getTargets(IMTWProject project) {
            return null;
        }

        public void discoverTargets(IProgressMonitor monitor) throws CoreException {
            // Do nothing
        }

        public IDeploymentTarget findTarget(IMTWProject project, String id) {
            return null;
        }

        public boolean targetsDiscovered() {
            return true;
        }

        public ISchedulingRule getSchedulingRule(IDeploymentTarget target) {
            return null; // No scheduling
        }
    }

    private static final DeploymentTargetWrapper[] NO_TARGETS = new DeploymentTargetWrapper[0];
    private final IConfigurationElement element;
    private IDeploymentTargetType type;
    private final Map<IDeploymentTarget, DeploymentTargetWrapper> wrappers = new WeakHashMap<IDeploymentTarget, DeploymentTargetWrapper>();
    private ImageDescriptor imageDescriptor = null;

    public DeploymentTargetTypeDescriptor(IConfigurationElement element) {
        this.element = element;
    }

    public boolean supports(IMTWProject project) {
        // We will support more declarative filtering later
        return true;
    }

    public DeploymentTargetWrapper[] getTargets(IMTWProject project) {
        final DeploymentTargetWrapper[] targets = wrap(getProvider().getTargets(project));
        return targets != null ? targets : NO_TARGETS;
    }

    private DeploymentTargetWrapper[] wrap(IDeploymentTarget[] targets) {
        if (targets == null) {
            return new DeploymentTargetWrapper[0];
        } else {
            final DeploymentTargetWrapper[] w = new DeploymentTargetWrapper[targets.length];
            for (int i = 0; i < targets.length; i++) {
                w[i] = wrap(targets[i]);
            }
            return w;
        }
    }

    private DeploymentTargetWrapper wrap(final IDeploymentTarget target) {
        DeploymentTargetWrapper wrapper = wrappers.get(target);
        if (wrapper == null) {
            wrapper = new DeploymentTargetWrapper(target, this);
            wrappers.put(target, wrapper);
        }
        return wrapper;
    }

    public void discoverTargets(IProgressMonitor monitor) throws CoreException {
        getProvider().discoverTargets(monitor);
    }

    public IDeploymentTarget findTarget(IMTWProject project, String id) {
        return wrap(getProvider().findTarget(project, id));
    }

    private synchronized IDeploymentTargetType getProvider() {
        if (type == null) {
            try {
                type = (IDeploymentTargetType) element.createExecutableExtension("class");
            } catch (CoreException e) {
                MTWCoreUI.log("Cannot instantiate provider " + getId(), e);
                type = new NullProvider();
            }
        }
        return type;
    }

    public String getId() {
        return element.getAttribute("id");
    }

    public boolean targetsDiscovered() {
        return getProvider().targetsDiscovered();
    }

    public int getPriority() {
        final String attribute = element.getAttribute("priority");
        if (attribute != null && attribute.trim().length() > 0) {
            try {
                return Integer.parseInt(attribute);
            } catch (NumberFormatException e) {
                MTWCoreUI.log("%s is not a valid priority value for %s provider", attribute, getId());
            }
        }
        return 0;
    }

    public ImageDescriptor getImageDescriptor() {
        if (imageDescriptor == null) {
            final String image = element.getAttribute("icon");
            if (image == null) {
                imageDescriptor = ImageDescriptor.getMissingImageDescriptor();
            } else {
                imageDescriptor = MTWCoreUI.imageDescriptorFromPlugin(element.getNamespaceIdentifier(), image);
            }
        }
        return imageDescriptor;
    }

    public ISchedulingRule getSchedulingRule(IDeploymentTarget target) {
        return type.getSchedulingRule(target);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        return result;
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
        DeploymentTargetTypeDescriptor other = (DeploymentTargetTypeDescriptor) obj;
        if (getId() == null) {
            if (other.getId() != null) {
                return false;
            }
        } else if (!getId().equals(other.getId())) {
            return false;
        }
        return true;
    }

}
