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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntime;

public interface IMTWProject {
    /**
     * @return primary target runtime of this project. Can never return null.
     */
    IMobileWebRuntime getTargetRuntime();

    /**
     * @return workspace project that back this MTW project.
     */
    IProject getProject();

    /**
     * @return symbolic MTW project name.
     */
    String getName();

    /**
     * Validate project configuration and contents.
     * 
     * @return <code>true</code> if the project has no errors. Warnings do not count.
     */
    boolean validate(IProgressMonitor monitor);
}
