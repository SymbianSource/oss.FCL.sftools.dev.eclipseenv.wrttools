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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.symbian.tools.mtw.ui.MTWCoreUI;
import org.symbian.tools.mtw.ui.deployment.IDeploymentTargetType;
import org.symbian.tools.mtw.ui.deployment.ITargetDetailsPane;

public class DeploymentTargetPresentationsManager {
    private Map<IDeploymentTargetType, IConfigurationElement> presentations;

    public ITargetDetailsPane createDetailsPane(IDeploymentTargetType targetType) {
        readRegistry();
        final IConfigurationElement element = presentations.get(targetType);
        if (element != null && element.getAttribute("detailsPane") != null) {
            try {
                return (ITargetDetailsPane) element.createExecutableExtension("detailsPane");
            } catch (CoreException e) {
                MTWCoreUI.log(e);
            }
        }
        return new DefaultDeploymentTypePresentation();
    }

    private synchronized void readRegistry() {
        if (presentations == null) {
            presentations = new HashMap<IDeploymentTargetType, IConfigurationElement>();
            final IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
                    MTWCoreUI.PLUGIN_ID, "targetPresentation");
            for (IConfigurationElement element : elements) {
                final String runtimeId = element.getAttribute("targetTypeId");
                final IDeploymentTargetType targetType = MTWCoreUI.getDefault().getDeploymentTypesRegistry()
                        .getType(runtimeId);
                if (targetType == null) {
                    MTWCoreUI.log("Runtime %s was not found. It is referenced from plugin %s", runtimeId, element
                            .getContributor().getName());
                    continue;
                } else {
                    presentations.put(targetType, element);
                }
            }
        }
    }
}
