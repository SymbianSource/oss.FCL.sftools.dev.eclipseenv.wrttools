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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.symbian.tools.wrttools.util.CoreUtil;

public class PreferenceInitializer extends AbstractPreferenceInitializer {
	private final static String DEFAULT_CHROME_LOCATION = "Local Settings/Application Data/Google/Chrome/Application";

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		File folder = getDefaultFolder();
		if (ChromeDebugUtils.getExecutablePath(folder.getAbsolutePath()) != null) {
			store.setDefault(IConstants.PREF_NAME_CHROME_LOCATION, folder
					.getAbsolutePath());
		}
	}

	private File getDefaultFolder() {
		if (CoreUtil.isMac()) {
			return new File("/Applications");
		} else if (CoreUtil.isLinux()) {
			return new File("/opt/google/chrome");
		}
		String property = System.getProperty("user.home");
		File folder = new File(property, DEFAULT_CHROME_LOCATION);
		return folder;
	}

}
