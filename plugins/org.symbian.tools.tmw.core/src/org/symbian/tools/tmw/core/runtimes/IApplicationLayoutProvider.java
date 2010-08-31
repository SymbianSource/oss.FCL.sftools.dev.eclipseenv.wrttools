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

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * This interface provides application structure as it will be used in web 
 * runtime. Note that project layout may not directly correspond to application
 * layout.
 * 
 * @author Eugene Ostroukhov (eugeneo@symbian.org)
 */
public interface IApplicationLayoutProvider {
    /**
     * @param file workspace resource
     * @return path relative to application package root
     */
    IPath getResourcePath(IFile file);

    /**
     * @param path path relative to application root
     * @return workspace resource
     * @throws CoreException if cannot access resource contents 
     */
    InputStream getResourceFromPath(IProject project, IPath path) throws CoreException;

    /**
     * @return workspace file that corresponds to applicationPath or 
     * <code>null</code> if none
     */
    IFile getWorkspaceFile(IProject project, IPath applicationPath) throws CoreException;

    /**
     * @return main HTML page (application entry point)
     */
    IFile getIndexPage(IProject project);
}
