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
package org.symbian.tools.wrttools.core.packager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.symbian.tools.tmw.core.projects.IMTWProject;
import org.symbian.tools.tmw.core.runtimes.IPackagerDelegate;
import org.symbian.tools.tmw.core.utilities.ZipApplicationVisitor;
import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.util.ProjectUtils;

public class WrtPackager implements IPackagerDelegate {
    public File packageApplication(IMTWProject project, IProgressMonitor monitor) throws CoreException {
        monitor.beginTask(String.format("Packaging %s", project.getName()), IProgressMonitor.UNKNOWN);

        IPath stateLocation = Activator.getDefault().getStateLocation();
        final File f = stateLocation.append(project.getName()).addFileExtension(getFileType(project)).toFile(); // It should be OK to overwrite - this is private location
        f.getParentFile().mkdirs();
        try {
            ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(f));
            project.getProject().accept(new ZipApplicationVisitor(zip, this));
            zip.close();
        } catch (IOException e) {
            throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, String.format(
                    "Packaging application %s failed", project.getName()), e));
        }
        monitor.done();
        return f;
    }

    public String getFileType(IMTWProject project) {
        return "wgz";
    }

    public IPath getPathInPackage(IResource resource) {
        return ProjectUtils.isExcluded(resource) ? null : resource.getFullPath().makeRelative();
    }

}
