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

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.symbian.tools.tmw.core.projects.IProjectSetupConfig;
import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntime;

/**
 * Provides template values.
 *
 * @author Eugene Ostroukhov (eugeneo@symbian.org)
 */
public interface IProjectTemplateContext extends IProjectSetupConfig {
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
     * Allows binding to parameter value from UI.
     *
     * @param name parameter value
     * @return observable that may be used to bind value
     */
    IObservableValue getParameterObservable(String name);
}
