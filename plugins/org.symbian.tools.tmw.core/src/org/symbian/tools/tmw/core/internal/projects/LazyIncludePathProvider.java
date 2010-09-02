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
package org.symbian.tools.tmw.core.internal.projects;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.jsdt.core.IIncludePathEntry;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.projects.IFacetIncludePathProvider;
import org.symbian.tools.tmw.core.projects.ITMWProject;

public class LazyIncludePathProvider implements IFacetIncludePathProvider {
    private final IConfigurationElement element;
    private LazyIncludePathProvider provider;

    public LazyIncludePathProvider(IConfigurationElement configurationElement) {
        this.element = configurationElement;
    }

    public IIncludePathEntry[] getEntries(ITMWProject project) {
        if (provider == null) {
            try {
                provider = (LazyIncludePathProvider) element.createExecutableExtension("class");
            } catch (CoreException e) {
                TMWCore.log(null, e);
                return new IIncludePathEntry[0];
            }
        }
        return provider.getEntries(project);
    }

}
