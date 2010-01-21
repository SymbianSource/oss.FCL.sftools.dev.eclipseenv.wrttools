/**
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
 */

package org.symbian.tools.wrttools.previewer.preview;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import org.symbian.tools.wrttools.previewer.Activator;


public class WidgetPreviewPreferenceInitializer extends
		AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		if (Platform.getOS().equals(Platform.OS_WIN32)) {
			store.setDefault(WidgetPreviewPreferencePage.WIDGET_BROWSER_PREVIEW_PREFERENCE, "Firefox"); //$NON-NLS-1$
		} else if (Platform.getOS().equals(Platform.OS_LINUX)) {
			store.setDefault(WidgetPreviewPreferencePage.WIDGET_BROWSER_PREVIEW_PREFERENCE, "Default"); //$NON-NLS-1$
		} else if (Platform.getOS().equals(Platform.OS_MACOSX)) {
			store.setDefault(WidgetPreviewPreferencePage.WIDGET_BROWSER_PREVIEW_PREFERENCE, "Safari"); //$NON-NLS-1$
		}
	}

}
