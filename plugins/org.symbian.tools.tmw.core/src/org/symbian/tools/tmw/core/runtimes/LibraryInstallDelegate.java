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
package org.symbian.tools.tmw.core.runtimes;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.projects.IProjectSetupConfig;
import org.symbian.tools.tmw.core.utilities.NonClosingStream;

public abstract class LibraryInstallDelegate implements IDelegate {

    public void execute(IProject project, IProjectFacetVersion fv, Object config, IProgressMonitor monitor)
            throws CoreException {
        if (config instanceof IProjectSetupConfig) {
            final IProjectSetupConfig setupConfig = (IProjectSetupConfig) config;
            final IPath basePath = getBasePath(project, setupConfig);
            final InputStream file = openInputStream();
            final ZipInputStream stream = new ZipInputStream(file);
            try {
                ZipEntry en = stream.getNextEntry();
                while (en != null) {
                    if (!en.isDirectory()) {
                        final IPath entry = new Path(en.getName());
                        final IFile f = setupConfig.addFile(project, basePath.append(entry), new NonClosingStream(
                                stream), new NullProgressMonitor());
                        if (isIncludeFile(entry)) {
                            setupConfig.addIncludedJsFile(project, f);
                        }

                    }
                    en = stream.getNextEntry();
                }
            } catch (IOException e) {
                TMWCore.log(null, e);
            } finally {
                try {
                    file.close();
                } catch (IOException e) {
                    TMWCore.log(null, e);
                }
            }
        }
    }

    protected abstract IPath getBasePath(IProject project, IProjectSetupConfig setupConfig);

    protected abstract boolean isIncludeFile(IPath entry);

    protected abstract InputStream openInputStream() throws CoreException;

}
