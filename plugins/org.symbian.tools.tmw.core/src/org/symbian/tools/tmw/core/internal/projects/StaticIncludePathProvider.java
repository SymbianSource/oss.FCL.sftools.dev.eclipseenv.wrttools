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

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.jsdt.core.IIncludePathEntry;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.osgi.framework.Bundle;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.projects.IFacetIncludePathProvider;
import org.symbian.tools.tmw.core.projects.ITMWProject;

public class StaticIncludePathProvider implements IFacetIncludePathProvider {
    private final IConfigurationElement element;
    private IIncludePathEntry[] entries;

    public StaticIncludePathProvider(IConfigurationElement configurationElement) {
        this.element = configurationElement;
    }

    public IIncludePathEntry[] getEntries(ITMWProject project) {
        if (entries == null) {
            final String name = element.getContributor().getName();
            final String path = element.getAttribute("file");
            final Bundle bundle = Platform.getBundle(name);
            final URL url = FileLocator.find(bundle, new Path(path), null);
            try {
                final URL fileURL = FileLocator.toFileURL(url);
                final File file = new File(fileURL.getPath());
                final IIncludePathEntry entry = JavaScriptCore.newLibraryEntry(new Path(file.getAbsolutePath()), null,
                        null);
                entries = new IIncludePathEntry[] { entry };
            } catch (IOException e) {
                TMWCore.log(String.format("Can't find file %s in plugin %s", path, name), e);
            }
        }
        return entries;
    }

}
