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

/**
 * Bridges between project models.
 *
 * @author Eugene Ostroukhov (eugeneo@symbian.org)
 */
public interface ITMWProjectProvider {
    /**
     * Returns {@link ITMWProject} object that works with specified {@link IProject} object.
     * @return non-<code>null</code> object
     */
    ITMWProject create(IProject project);

    /**
     * @return <code>true</code> if the {@link ITMWProject} object can be created.
     */
    boolean isSupportedProject(IProject project);
}
