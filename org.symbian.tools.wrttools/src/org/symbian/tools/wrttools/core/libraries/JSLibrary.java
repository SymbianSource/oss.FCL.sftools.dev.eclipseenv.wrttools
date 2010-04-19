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
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;

public class JSLibrary {
    private final String id;
    private final Image image;
    private final String name;
    private final IJSLibraryInstaller installer;

    public JSLibrary(String id, String name, Image image, IJSLibraryInstaller installer) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.installer = installer;
    }

    public String getId() {
        return id;
    }

    public Image getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public void install(IProject project, Map<String, String> parameters, IProgressMonitor monitor)
            throws CoreException, IOException {
        installer.install(project, parameters, monitor);
    }

    @Override
    public String toString() {
        return String.format("JSLibrary \"%s\"", getId());
    }
}
