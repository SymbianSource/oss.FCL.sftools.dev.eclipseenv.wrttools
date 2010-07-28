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
package org.symbian.tools.mtw.core.internal.projects;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.symbian.tools.mtw.core.MTWCore;
import org.symbian.tools.mtw.core.projects.IMTWProject;

public class ProjectsSupportManager {
    private Collection<ProjectProvider> providers = null;

    public IMTWProject create(IProject project) {
        if (providers == null) {
            readProviders();
        }
        for (ProjectProvider provider : providers) {
            if (provider.isSupportedProject(project)) {
                return provider.create(project);
            }
        }
        return null;
    }

    private void readProviders() {
        providers = new LinkedList<ProjectProvider>();
        IConfigurationElement[] configurationElements = Platform.getExtensionRegistry().getConfigurationElementsFor(
                MTWCore.PLUGIN_ID, "projectProvider");
        for (IConfigurationElement element : configurationElements) {
            if (element.getName().equals("projectProvider")) {
                try {
                    providers.add(new ProjectProvider(element));
                } catch (CoreException e) {
                    MTWCore.log(null, e);
                }
            }
        }
    }
}
