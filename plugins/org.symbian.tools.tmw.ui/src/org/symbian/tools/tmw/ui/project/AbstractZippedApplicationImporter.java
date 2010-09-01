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
package org.symbian.tools.tmw.ui.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntime;
import org.symbian.tools.tmw.core.utilities.NonClosingStream;
import org.symbian.tools.tmw.ui.TMWCoreUI;

/**
 * This is base class for importers that work with zip-based application
 * packages. W3C specification mandates using Zip as archive format and
 * it looks like all mobile web runtime use Zip as their format.
 * 
 * @author Eugene Ostroukhov (eugeneo@symbian.org)
 */
public abstract class AbstractZippedApplicationImporter implements IApplicationImporter {
    public Map<String, String> getFileFilters() {
        return Collections.singletonMap("*.zip", "Application Archive (*.zip)");
    }

    public void extractFiles(File file, IMobileWebRuntime runtime, IProject project, IProgressMonitor monitor)
            throws CoreException {
        ZipInputStream zipInputStream = null;
        try {
            zipInputStream = new ZipInputStream(new FileInputStream(file));
            ZipEntry entry = null;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    final IPath path = getApplicationRelativePath(entry);
                    final IFile f = project.getFile(path);
                    if (!f.exists()) {
                        createContainers(f.getParent());
                        f.create(new NonClosingStream(zipInputStream), false, new NullProgressMonitor());
                    }
                }
            }
        } catch (IOException e) {
            throw new CoreException(new Status(IStatus.ERROR, TMWCoreUI.PLUGIN_ID, String.format(
                    "Cannot extract files from archive %s", file.getAbsoluteFile()), e));
        } finally {
            if (zipInputStream != null) {
                try {
                    zipInputStream.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
        }
    }

    private void createContainers(IContainer parent) throws CoreException {
        if (parent != null && !parent.exists() && parent.getType() == IResource.FOLDER) {
            createContainers(parent.getParent());
            ((IFolder) parent).create(false, true, new NullProgressMonitor());
        }
    }

    /**
     * This base calss assumes application files are in the application
     * package root. Symbian WRT has files in the subfolder so it will need
     * to override this method.
     */
    protected IPath getApplicationRelativePath(ZipEntry entry) {
        return new Path(entry.getName());
    }
}
