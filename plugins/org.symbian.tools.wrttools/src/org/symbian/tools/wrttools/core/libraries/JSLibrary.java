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
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.core.WRTImages;

public class JSLibrary {
    public class NullInstaller implements IJSLibraryInstaller {
        public void install(IProject project, Map<String, String> parameters, IProgressMonitor monitor)
                throws CoreException, IOException {
            // Do nothing
        }

        public boolean isInstalled(IProject project) {
            return false;
        }
    }

    private final String id;
    private Image image;
    private final String name;
    private IJSLibraryInstaller installer;
    private final IConfigurationElement element;

    public JSLibrary(String id, String name, IConfigurationElement element) {
        this.id = id;
        this.name = name;
        this.element = element;
    }

    public String getId() {
        return id;
    }

    public Image getImage() {
        if (image == null) {
            String icon = element.getAttribute("icon");
            if (icon != null) {
                ImageDescriptor descriptor = Activator.imageDescriptorFromPlugin(element.getContributor().getName(),
                        icon);
                if (descriptor != null) {
                    image = descriptor.createImage();
                }
            }
            if (image == null) {
                image = WRTImages.getWrtKitIcon();
            }
        }
        return image;
    }

    public String getName() {
        return name;
    }

    public void install(IProject project, Map<String, String> parameters, IProgressMonitor monitor)
            throws CoreException, IOException {
        getInstaller().install(project, parameters, monitor);
    }

    private IJSLibraryInstaller getInstaller() throws CoreException {
        if (installer == null) {
            if (element.getAttribute("installer") != null) {
                installer = (IJSLibraryInstaller) element.createExecutableExtension("installer");
            }
            if (installer == null) {
                installer = new NullInstaller();
            }
        }
        return installer;
    }

    @Override
    public String toString() {
        return String.format("JSLibrary \"%s\"", getId());
    }

    public boolean isInstalled(IProject project) {
        try {
            return getInstaller().isInstalled(project);
        } catch (CoreException e) {
            Activator.log(e);
            return false;
        }
    }
}
