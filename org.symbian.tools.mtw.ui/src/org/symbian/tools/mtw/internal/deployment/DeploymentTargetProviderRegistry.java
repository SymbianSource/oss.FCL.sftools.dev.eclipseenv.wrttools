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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.symbian.tools.mtw.ui.deployment.IDeploymentTargetProvider;

public class DeploymentTargetProviderRegistry {
    private static DeploymentTargetProviderRegistry INSTANCE;

    private DeploymentTargetProviderDescriptor[] descriptors;

    private DeploymentTargetProviderRegistry() {
        readExtensions();
    }

    private void readExtensions() {
        final IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
                "org.symbian.tools.mtw.ui.deploymentTargetsProvider");
        descriptors = new DeploymentTargetProviderDescriptor[elements.length];
        for (int i = 0; i < elements.length; i++) {
            descriptors[i] = new DeploymentTargetProviderDescriptor(elements[i]);
        }
    }

    public static synchronized DeploymentTargetProviderRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DeploymentTargetProviderRegistry();
        }
        return INSTANCE;
    }

    public DeploymentTargetProviderDescriptor[] getProviders() {
        return descriptors;
    }

    public IDeploymentTargetProvider getProvider(String id) {
        DeploymentTargetProviderDescriptor[] providers = getProviders();
        for (DeploymentTargetProviderDescriptor descriptor : providers) {
            if (descriptor.getId().equals(id)) {
                return descriptor;
            }
        }
        return null;
    }
}
