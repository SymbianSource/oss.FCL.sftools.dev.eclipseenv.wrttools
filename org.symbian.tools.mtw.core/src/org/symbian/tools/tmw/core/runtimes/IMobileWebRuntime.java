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

/**
 * Represents mobile web runtimes supported by the IDE
 * 
 * @author Eugene Ostroukhov (eugeneo@symbian.org)
 */
public interface IMobileWebRuntime {
    /**
     * @return unique runtime ID
     */
    String getId();

    /**
     * @return version of the primary runtime component
     */
    String getVersion();

    /**
     * @return user-readable runtime name
     */
    String getName();
}
