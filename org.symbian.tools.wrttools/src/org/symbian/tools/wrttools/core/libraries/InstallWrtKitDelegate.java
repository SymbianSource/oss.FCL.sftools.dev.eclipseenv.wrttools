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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.symbian.tools.tmw.core.projects.IProjectSetupConfig;
import org.symbian.tools.tmw.core.runtimes.LibraryInstallDelegate;
import org.symbian.tools.wrttools.Activator;

public class InstallWrtKitDelegate extends LibraryInstallDelegate {

    @Override
    protected boolean isIncludeFile(IPath entry) {
        return entry.equals(new Path("WRTKit.js"));
    }

    @Override
    protected InputStream openInputStream() throws CoreException {
        try {
            return FileLocator.openStream(Activator.getDefault().getBundle(),
                    new Path("libraries").append("wrtkit.zip"), true);
        } catch (IOException e) {
            throw new CoreException(
                    new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Can't access WRTKit library zip", e));
        }
    }

    @Override
    protected IPath getBasePath(IProject project, IProjectSetupConfig setupConfig) {
        return new Path("wrtkit");
    }
}
