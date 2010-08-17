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
package org.symbian.tools.tmw.ui.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public interface ITemplateInstaller {
    /**
     * Prepare the installer to work on specified project using template context.
     */
    void prepare(IProject project, IProjectTemplateContext context);

    /**
     * Cleanup after processing the project.
     */
    void cleanup();

    /**
     * Get list of files that will be created in the project.
     */
    IPath[] getFiles() throws CoreException;

    /**
     * Installer should only copy files specified. Keep in mind that some 
     * files may be overridden by other installers.
     */
    void copyFiles(IPath[] files, IProgressMonitor monitor) throws CoreException;

    /**
     * These actions will be ran after the new project setup completes. They will be
     * ran in SWT thread.
     * @return runnable that will be ran after the project create finishes. Can be null.
     */
    IRunnableWithProgress getPostCreateAction();
}
