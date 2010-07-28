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
import org.symbian.tools.mtw.ui.deployment.IDeploymentTargetProvider;

public final class DeploymentTargetProviderDescriptor implements IDeploymentTargetProvider {
    public class NullProvider implements IDeploymentTargetProvider {
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
    private IDeploymentTargetProvider provider;
    private final Map<IDeploymentTarget, DeploymentTargetWrapper> wrappers = new WeakHashMap<IDeploymentTarget, DeploymentTargetWrapper>();
    private ImageDescriptor imageDescriptor = null;

    public DeploymentTargetProviderDescriptor(IConfigurationElement element) {
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
        final DeploymentTargetWrapper[] w = new DeploymentTargetWrapper[targets.length];
        for (int i = 0; i < targets.length; i++) {
            final IDeploymentTarget target = targets[i];
            DeploymentTargetWrapper wrapper = wrappers.get(target);
            if (wrapper == null) {
                wrapper = new DeploymentTargetWrapper(target, this);
                wrappers.put(target, wrapper);
            }
            w[i] = wrapper;
        }
        return w;
    }

    public void discoverTargets(IProgressMonitor monitor) throws CoreException {
        getProvider().discoverTargets(monitor);
    }

    public IDeploymentTarget findTarget(IMTWProject project, String id) {
        return getProvider().findTarget(project, id);
    }

    private synchronized IDeploymentTargetProvider getProvider() {
        if (provider == null) {
            try {
                provider = (IDeploymentTargetProvider) element.createExecutableExtension("class");
            } catch (CoreException e) {
                MTWCoreUI.log("Cannot instantiate provider " + getId(), e);
                provider = new NullProvider();
            }
        }
        return provider;
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
                MTWCoreUI.log(String.format("%s is not a valid priority value for %s provider", attribute, getId()),
                        null);
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
        return provider.getSchedulingRule(target);
    }

}
