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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.IActionConfigFactory;

public class ProjectCreationConfigFactory implements IActionConfigFactory {
    // Basically this is an ugly hack. Our project wizard will replace this config
    // object with WizardContext
    public static final Object CONFIG_STANDIN = "standing_config";

    public Object create() throws CoreException {
        return CONFIG_STANDIN;
    }

}
