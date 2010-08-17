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

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.resources.IFile;
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
    /**
     * @return runtime that the project targets
     */
    IMobileWebRuntime getRuntime();

    /**
     * @param parameter name of the parameter
     * @return parameter value. In most cases parameters will be string values 
     * but template developers can use any types
     */
    Object getParameter(String parameter);

    /**
     * @return array of parameter names
     */
    String[] getParameterNames();

    void putParameter(String key, Object value);

    /**
     * Allows the framework to reduce dependance on exact project layout. I.e. 
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
     * Allows binding to parameter value from UI.
     * 
     * @param name parameter value
     * @return observable that may be used to bind value
     */
    IObservableValue getParameterObservable(String name);
}
