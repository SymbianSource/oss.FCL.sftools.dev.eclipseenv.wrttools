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

import java.io.InputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.symbian.tools.tmw.core.projects.IProjectSetupAction;
import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntime;

/**
 * Provides template values.
 * 
 * @author Eugene Ostroukhov (eugeneo@symbian.org)
 */
public interface IProjectTemplateContext extends IProjectSetupAction {
    IMobileWebRuntime getRuntime();
    Object getParameter(String parameter);
    String[] getParameterNames();
    void putParameter(String key, Object value);
    void addFile(IProject project, IPath name, InputStream contents, IProgressMonitor monitor)
            throws CoreException;
}
