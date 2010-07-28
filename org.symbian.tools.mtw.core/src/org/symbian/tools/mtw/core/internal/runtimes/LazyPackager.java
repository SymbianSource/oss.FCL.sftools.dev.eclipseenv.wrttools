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
package org.symbian.tools.mtw.core.internal.runtimes;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.symbian.tools.mtw.core.MTWCore;
import org.symbian.tools.mtw.core.projects.IMTWProject;
import org.symbian.tools.mtw.core.runtimes.IMobileWebRuntime;
import org.symbian.tools.mtw.core.runtimes.IPackager;

public class LazyPackager implements IPackager {
    private final IConfigurationElement element;
    private IPackager packager;

    public LazyPackager(IConfigurationElement element) {
        this.element = element;
    }

    public File packageApplication(IMTWProject project, IMobileWebRuntime runtime, IProgressMonitor monitor)
            throws CoreException {
        return getPackager().packageApplication(project, runtime, monitor);
    }

    private IPackager getPackager() {
        if (packager == null) {
            try {
                packager = (IPackager) element.createExecutableExtension("class");
            } catch (CoreException e) {
                MTWCore.log(String.format("Cannot instantiate %s from plugin %s", element.getAttribute("class"),
                        element.getDeclaringExtension().getNamespaceIdentifier()), e);
                throw new RuntimeException(e);
            }
        }
        return packager;
    }

    public IPath getPathInPackage(IResource resource) {
        return getPackager().getPathInPackage(resource);
    }

    public String getFileType(IMTWProject project) {
        return getPackager().getFileType(project);
    }

}
