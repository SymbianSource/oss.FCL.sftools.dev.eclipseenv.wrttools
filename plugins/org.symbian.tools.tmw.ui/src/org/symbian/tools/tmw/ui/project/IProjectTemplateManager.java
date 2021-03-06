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

import java.util.Map;

import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntime;

public interface IProjectTemplateManager {
    /**
     * Returns project templates that support mobile web runtime.
     *
     * @return sorted array of the templates. Templates are sorted based on weight and name.
     */
    IProjectTemplate[] getProjectTemplates(IMobileWebRuntime runtime);

    IProjectTemplate getDefaultTemplate(IMobileWebRuntime runtime);

    ITemplateInstaller getEmptyProjectTemplate(IMobileWebRuntime runtime);

    /**
     * Runtimes can provide default values for template parameters
     */
    Map<String, String> getDefaultTemplateParameterValues(IMobileWebRuntime runtime);
}
