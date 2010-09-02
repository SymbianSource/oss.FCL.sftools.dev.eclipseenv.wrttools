/*******************************************************************************
 * Copyright (c) 2009 Symbian Foundation and/or its subsidiary(-ies).
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
 *******************************************************************************/
package org.symbian.tools.tmw.debug.internal;

import java.io.File;

import org.symbian.tools.tmw.core.utilities.CoreUtil;

public final class ChromeDebugUtils {
    public static String getExecutablePath(String folder) {
        File file = new File(folder);
        if (file.isDirectory()) {
            File chromeExecutable = new File(file, getExecutable());
            if (chromeExecutable.isFile()) {
                return chromeExecutable.getAbsolutePath();
            } else if (CoreUtil.isMac() && file.getName().equals("Google Chrome.app")) {
                return getExecutablePath(file.getParent());
            }
        } else if (file.isFile()) {
            if (file.getName().equalsIgnoreCase(getExecutable())) {
                return file.getAbsolutePath();
            }
        }
        return null;
    }

    private static String getExecutable() {
        // Add more ifs as we add support for new platforms
        if (CoreUtil.isMac()) {
            return "Google Chrome.app/Contents/MacOS/Google Chrome";
        } else if (CoreUtil.isLinux()) {
            return "chrome";
        } else {
            return "chrome.exe";
        }
    }

    private ChromeDebugUtils() {
        // No instantiating
    }

    public static String getChromeExecutible() {
        return getExecutablePath(Activator.getDefault().getPreferenceStore()
                .getString(IConstants.PREF_NAME_CHROME_LOCATION));
    }

}
