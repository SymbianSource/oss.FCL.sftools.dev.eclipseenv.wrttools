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
package org.symbian.tools.tmw.core.internal.runtimes;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.projects.ITMWProject;
import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntime;
import org.symbian.tools.tmw.core.runtimes.IPackager;
import org.symbian.tools.tmw.core.runtimes.IPackagerDelegate;

public class LazyPackager implements IPackager {
    private final IConfigurationElement element;
    private IPackagerDelegate packager;

    public LazyPackager(IConfigurationElement configurationElement) {
        this.element = configurationElement;
    }

    public File packageApplication(ITMWProject project, IProgressMonitor monitor) throws CoreException {
        return getPackager().packageApplication(project, monitor);
    }

    private IPackagerDelegate getPackager() {
        if (packager == null) {
            try {
                packager = (IPackagerDelegate) element.createExecutableExtension("delegate");
            } catch (CoreException e) {
                TMWCore.log(String.format("Cannot instantiate %s from plugin %s", element.getAttribute("delegate"),
                        element.getDeclaringExtension().getNamespaceIdentifier()), e);
                throw new RuntimeException(e);
            }
        }
        return packager;
    }

    public IPath getPathInPackage(IResource resource) {
        return getPackager().getPathInPackage(resource);
    }

    public String getFileType(ITMWProject project) {
        return getPackager().getFileType(project);
    }

    public IMobileWebRuntime getTargetRuntime() {
        String id = element.getAttribute("target-runtime");
        if (id != null) {
            return TMWCore.getRuntimesManager().getRuntime(id, element.getAttribute("target-runtime-version"));
        } else {
            return getSourceRuntime();
        }
    }

    public IMobileWebRuntime getSourceRuntime() {
        IMobileWebRuntime runtime = TMWCore.getRuntimesManager().getRuntime(element.getAttribute("source-runtime"),
                element.getAttribute("source-runtime-version"));
        if (runtime == null) {
            return getTargetRuntime();
        } else {
            return runtime;
        }
    }

}
