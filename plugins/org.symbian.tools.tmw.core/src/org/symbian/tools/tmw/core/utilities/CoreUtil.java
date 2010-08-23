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
package org.symbian.tools.tmw.core.utilities;

import org.eclipse.core.runtime.Platform;

/**
 * Utilities used all over the TMW code base. Users can rely on this methods 
 * even though they are not specific to mobile development.
 * 
 * @author Eugene Ostroukhov (eugeneo@symbian.org)
 */
public final class CoreUtil {

    private CoreUtil() {
        // Class with only static methods
    }

    public static boolean isMac() {
        return "macosx".equals(Platform.getOS());
    }

    public static boolean isLinux() {
        return "linux".equals(Platform.getOS());
    }
}
