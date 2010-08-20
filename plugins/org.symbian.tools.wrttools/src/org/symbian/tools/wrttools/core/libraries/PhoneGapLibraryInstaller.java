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

import java.io.InputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.symbian.tools.tmw.core.projects.IProjectSetupConfig;
import org.symbian.tools.tmw.core.runtimes.LibraryInstallDelegate;

public class PhoneGapLibraryInstaller extends LibraryInstallDelegate {

    public void execute(IProject project, IProjectFacetVersion fv, Object config, IProgressMonitor monitor)
            throws CoreException {

    }

    @Override
    protected IPath getBasePath(IProject project, IProjectSetupConfig setupConfig) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isIncludeFile(IPath entry) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected InputStream openInputStream() throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

}
