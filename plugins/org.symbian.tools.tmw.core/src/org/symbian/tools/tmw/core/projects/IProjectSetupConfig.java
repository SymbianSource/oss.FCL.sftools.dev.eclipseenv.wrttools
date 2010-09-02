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
package org.symbian.tools.tmw.core.projects;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This object can be set as facet install config for the core TMW facet
 *
 * @author Eugene Ostroukhov (eugeneo@symbian.org)
 */
public interface IProjectSetupConfig {
    /**
     * Performs project setup. Project will already be configured with
     * validation support and JSDT support.
     */
    void initialize(IProject project, IProgressMonitor monitor);

    /**
     * Allows the framework to reduce dependence on exact project layout. I.e.
     * some IDEs may want to introduce separation of the web resources and
     * JavaScript source files.
     *
     * @param project project to add file to
     * @param name file path relative to application root
     * @param contents stream with file contents
     * @param monitor progress monitor
     * @throws CoreException
     */
    IFile addFile(IProject project, IPath name, InputStream contents, IProgressMonitor monitor) throws CoreException;

    /**
     * Adds specified file to the list of the JS files that will be included in
     * the application page
     * @param file workspace file. Framework is responsible to create proper
     * application root-relative path
     */
    void addIncludedJsFile(IProject project, IFile file);
}
