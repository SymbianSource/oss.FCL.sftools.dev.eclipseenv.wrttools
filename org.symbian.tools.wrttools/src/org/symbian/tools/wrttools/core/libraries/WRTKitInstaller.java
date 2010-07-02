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

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.util.CoreUtil;
import org.symbian.tools.wrttools.util.ProjectUtils;

public class WRTKitInstaller implements IJSLibraryInstaller {
    private static final String JS_PATH = "WRTKit/WRTKit.js";

    public void install(IProject project, Map<String, String> parameters, IProgressMonitor monitor)
            throws CoreException, IOException {
        monitor.beginTask("Installing WRTKit library", 15);

        IFolder folder = project.getFolder("WRTKit");

        if (folder != null && !folder.exists()) {
            folder.create(false, true, new SubProgressMonitor(monitor, 1));
        }
        InputStream zip = FileLocator.openStream(Activator.getDefault().getBundle(), new Path("/libraries/wrtkit.zip"),
                true);
        ProjectUtils.unzip(zip, folder, 0, "WRTKit", new SubProgressMonitor(monitor, 10));

        LibrariesUtils.addJSToHtml(project, "Adding WRTKit Library", new String[] { JS_PATH }, null);
        monitor.done();
    }

    public boolean isInstalled(IProject project) {
        final IJavaScriptProject jsProject = JavaScriptCore.create(project);
        return CoreUtil.hasType(jsProject, "NotificationPopup") && CoreUtil.hasType(jsProject, "UIManager");
    }
}
