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

/**
 * This object can be set as facet install config for the core TMW facet
 * 
 * @author Eugene Ostroukhov (eugeneo@symbian.org)
 */
public interface IProjectSetupAction {
    /**
     * Performs project setup. Project will already be configured with 
     * validation support and JSDT support.
     */
    void initialize(IProject project, IProgressMonitor monitor);
}
