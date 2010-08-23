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
package org.symbian.tools.wrttools.core.libraries;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.projects.IProjectSetupConfig;
import org.symbian.tools.wrttools.Activator;

public final class InstallPhoneGapDelegate implements IDelegate {
    private static final String PHONEGAP_JS = "phonegap.js";

    private IPath getBasePath(IProject project, IProjectSetupConfig setupConfig) {
        return new Path("phonegap");
    }

    public void execute(IProject project, IProjectFacetVersion fv, Object config, IProgressMonitor monitor)
            throws CoreException {
        if (config instanceof IProjectSetupConfig) {
            final IProjectSetupConfig setupConfig = (IProjectSetupConfig) config;
            final IPath basePath = getBasePath(project, setupConfig);
            InputStream file = null;
            try {
                URL url = FileLocator.find(Activator.getDefault().getBundle(),
                        new Path("libraries").append(PHONEGAP_JS), null);
                if (url != null) {
                    file = url.openStream();
                    final IFile f = setupConfig.addFile(project, basePath.append(PHONEGAP_JS), file,
                            new NullProgressMonitor());
                    setupConfig.addIncludedJsFile(f);
                }
            } catch (IOException e) {
                Activator.log(e);
            } finally {
                try {
                    if (file != null) {
                        file.close();
                    }
                } catch (IOException e) {
                    TMWCore.log(null, e);
                }
            }
        }
    }
}
