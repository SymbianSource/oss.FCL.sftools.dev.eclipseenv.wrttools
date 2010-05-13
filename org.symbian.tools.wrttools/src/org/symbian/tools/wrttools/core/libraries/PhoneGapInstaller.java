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
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.IType;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.core.JavaScriptModelException;
import org.symbian.tools.wrttools.Activator;

public class PhoneGapInstaller implements IJSLibraryInstaller {
    private static final String PHONEGAP_JS = "phonegap.js";

    public void install(IProject project, Map<String, String> parameters, IProgressMonitor monitor)
            throws CoreException, IOException {
        String folderName = "script";
        monitor.beginTask("Installing PhoneGap library", 10);
        IFolder folder = project.getFolder(folderName);
        if (!folder.isAccessible()) {
            folder.create(false, true, new SubProgressMonitor(monitor, 2));
        }
        IFile file = folder.getFile(PHONEGAP_JS);
        if (!file.isAccessible()) {
            InputStream stream = FileLocator.openStream(Activator.getDefault().getBundle(), new Path("libraries")
                    .append(PHONEGAP_JS), true);
            file.create(stream, false, new SubProgressMonitor(monitor, 3));
        }
        IPath path = new Path(folderName).append(PHONEGAP_JS);
        LibrariesUtils.addJSToHtml(project, "Adding PhoneGap", new String[] { path.toString() }, null);
        monitor.done();
    }

    public boolean isInstalled(IProject project) {
        IJavaScriptProject jsProject = JavaScriptCore.create(project);
        try {
            final IType accel = jsProject.findType("Accelerometer");
            final IType camera = jsProject.findType("Camera");
            final IType geo = jsProject.findType("Geolocation");
            return accel != null && camera != null && geo != null;
        } catch (JavaScriptModelException e) {
            Activator.log(e);
        }
        return false;
    }

}
