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
package org.symbian.tools.tmw.internal.deployment;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.symbian.tools.tmw.ui.TMWCoreUI;
import org.symbian.tools.tmw.ui.deployment.IDeploymentTargetType;

public class DeploymentTargetTypesRegistry {
    private DeploymentTargetTypeDescriptor[] descriptors;

    public DeploymentTargetTypesRegistry() {
        readExtensions();
    }

    private void readExtensions() {
        final IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
                TMWCoreUI.PLUGIN_ID, "deploymentTargetType");
        descriptors = new DeploymentTargetTypeDescriptor[elements.length];
        for (int i = 0; i < elements.length; i++) {
            descriptors[i] = new DeploymentTargetTypeDescriptor(elements[i]);
        }
    }

    public DeploymentTargetTypeDescriptor[] getProviders() {
        return descriptors;
    }

    public IDeploymentTargetType getType(String id) {
        DeploymentTargetTypeDescriptor[] providers = getProviders();
        for (DeploymentTargetTypeDescriptor descriptor : providers) {
            if (descriptor.getId().equals(id)) {
                return descriptor;
            }
        }
        return null;
    }
}
