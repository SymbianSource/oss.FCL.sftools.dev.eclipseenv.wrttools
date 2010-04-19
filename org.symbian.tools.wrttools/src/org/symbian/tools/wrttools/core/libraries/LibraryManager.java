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
package org.symbian.tools.wrttools.core.libraries;

import org.symbian.tools.wrttools.core.WRTImages;

public final class LibraryManager {
    private JSLibrary[] jsLibraries;

    public JSLibrary[] getLibraries() {
        if (jsLibraries == null) {
            jsLibraries = new JSLibrary[] { new JSLibrary("org.symbian.wrtkit", "WRTKit", WRTImages.getWrtKitIcon(),
                    new WRTKitInstaller()) };
        }
        return jsLibraries;
    }

}
