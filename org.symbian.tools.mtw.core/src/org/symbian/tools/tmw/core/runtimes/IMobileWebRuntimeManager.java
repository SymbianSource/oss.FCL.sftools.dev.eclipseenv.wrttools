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

import org.symbian.tools.tmw.core.projects.IMTWProject;

/**
 * Manages mobile web runtimes.
 * 
 * @author Eugene Ostroukhov (eugeneo@symbian.org)
 */
public interface IMobileWebRuntimeManager {
    /**
     * Shorthand method to obtain packager for project default runtime.
     */
    IPackager getPackager(IMTWProject project);

    /**
     * Returns packager that can create a packaged application for provided 
     * runtime from the given project.
     * 
     * @return <code>null</null> if the project cannot be packaged for 
     * specified runtime
     */
    IPackager getPackager(IMTWProject project, IMobileWebRuntime runtime);

    /**
     * Returns runtimes for the given ID. Runtime will not be null.
     * 
     * @throws IllegalArgumentException if there is no runtime with the 
     * provided ID. It is assumed that this can only be caused by a broken 
     * install or coding error on IDE provider side.
     */
    IMobileWebRuntime getRuntime(String id, String version);

    /**
     * @return list of all runtimes known to an IDE
     */
    IMobileWebRuntime[] getAllRuntimes();
}
