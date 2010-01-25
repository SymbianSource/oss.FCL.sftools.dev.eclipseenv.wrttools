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
package org.symbian.tools.wrttools.debug.internal;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Platform;

public final class ChromeDebugUtils {
	public static String getExecutablePath(String folder) {
		File file = new File(folder);
		if (file.isDirectory()) {
			File chromeExecutable = new File(file, getExecutable());
			if (chromeExecutable.isFile()) {
				return chromeExecutable.getAbsolutePath();
			}
		}
		return null;
	}

	private static String getExecutable() {
		// Add more ifs as we add support for new platforms
		if (isMac()) {
			return "Google Chrome.app/Contents/MacOS/Google Chrome";
		}	if (isLinux()) {
			return "chrome";
		} else {
			return "chrome.exe";
		}
	}
	
	private ChromeDebugUtils() {
		// No instantiating
	}

	public static String getChromeExecutible() {
		return getExecutablePath(Activator.getDefault().getPreferenceStore().getString(IConstants.PREF_NAME_CHROME_LOCATION));
	}

	public static boolean isWidgetProject(IProject project) {
		return project.findMember(IConstants.WRT_PREVIEW_HTML) != null;
	}
	
	public static boolean isWindows() {
		return "windows".equals(Platform.getOS());
	}

	public static boolean isMac() {
		return "macosx".equals(Platform.getOS());
	}

	public static boolean isLinux() {
		return "linux".equals(Platform.getOS());
	}
}
