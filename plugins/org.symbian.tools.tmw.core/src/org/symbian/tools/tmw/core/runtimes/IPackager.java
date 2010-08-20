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
package org.symbian.tools.tmw.core.runtimes;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.symbian.tools.tmw.core.projects.ITMWProject;

/**
 * Packager creates a runtime-specific application package that can be 
 * deployed to compatible targets.
 * 
 * @author Eugene Ostroukhov (eugeneo@symbian.org)
 */
public interface IPackager {
    /**
     * Synchronously packages application for the specified runtime.
     * 
     * @return {@link File} denoting location of the application package that can be deployed to targets
     */
    File packageApplication(ITMWProject project, IProgressMonitor monitor)
            throws CoreException;

    /**
     * @return application package root-relative path where the workspace resource will be packaged. Can be <code>null</code>.
     */
    IPath getPathInPackage(IResource resource);

    /**
     * @return file type of the application archive
     */
    String getFileType(ITMWProject project);

    /**
     * @return target runtime that this packager packages for
     */
    IMobileWebRuntime getTargetRuntime();

    /**
     * @return source runtime. It is the runtime that the project should target to be packaged with this packager.
     */
    IMobileWebRuntime getSourceRuntime();
}
