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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

//import com.aptana.ide.editor.html.BrowserExtensionLoader;
//import com.aptana.ide.editors.UnifiedEditorsPlugin;
import org.symbian.tools.wrttools.previewer.Activator;

public class WidgetPreviewPreferencePage extends FieldEditorPreferencePage implements 
 IWorkbenchPreferencePage {

	public static final String WIDGET_BROWSER_PREVIEW_PREFERENCE = "org.symbian.tools.wrttools.previewer.preview.WIDGET_BROWSER_PREVIEW_PREFERENCE";

	public WidgetPreviewPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Preferences for the Widget preview");
	}

	/**
	 * Gets the list of  embeddable browsers
	 * @return - list of browsers
	 */
//TODO - remove Aptana Browser specific code
/*
	public static List<IConfigurationElement> getBrowsers() {
		List<IConfigurationElement> browserList = new ArrayList<IConfigurationElement>();
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint ep = reg.getExtensionPoint(UnifiedEditorsPlugin.BROWSER_EXTENSION_POINT);
		IExtension[] extensions = ep.getExtensions();
		for (int i = 0; i < extensions.length; i++) {
			IConfigurationElement[] ce = extensions[i].getConfigurationElements();
			for (int j = 0; j < ce.length; j++) {
				String browserClass = ce[j].getAttribute(UnifiedEditorsPlugin.CLASS_ATTR);
				String name = BrowserExtensionLoader.getBrowserLabel(ce[j]);
				if (browserClass != null && name != null) {
					browserList.add(ce[j]);
				}
			}
		}
		Collections.sort(browserList, new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				String name1 = BrowserExtensionLoader.getBrowserLabel((IConfigurationElement) o1);
				String name2 = BrowserExtensionLoader.getBrowserLabel((IConfigurationElement) o2);
				if (name1 != null && name2 != null) {
					return name1.compareTo(name2);
				}
				return 0;
			}
		});
		return browserList;
	}
	*/

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */

	protected void createFieldEditors() {
//TODO - remove Aptana browser specific implementation
/*
		List<IConfigurationElement> browserList = getBrowsers();
		List<String> browserNames = new ArrayList<String>();
		for (int i = 0; i < browserList.size(); i++) {
			IConfigurationElement element = (IConfigurationElement) browserList.get(i);
			if (!element.getContributor().getName().equals(Activator.PLUGIN_ID)) {
				browserNames.add(BrowserExtensionLoader.getBrowserLabel((IConfigurationElement)
						browserList.get(i)));
			}
		}

		String[][] options = new String[browserNames.size()][2];
		for (int i = 0; i < options.length; i++) {
			options[i][0] = (String) browserNames.get(i);
			options[i][1] = (String) browserNames.get(i);
		}

		addField(new RadioGroupFieldEditor(WIDGET_BROWSER_PREVIEW_PREFERENCE,
				"Browser Options", 1, options, getFieldEditorParent(), true));
*/
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
}
