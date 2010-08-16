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
package org.symbian.tools.tmw.internal.ui.wizard;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.symbian.tools.tmw.ui.TMWCoreUI;
import org.symbian.tools.tmw.ui.project.INewApplicationWizardPage;

public class PageContributions {
    private Map<String, List<IConfigurationElement>> templatePages;

    public INewApplicationWizardPage[] createPages(final String id) {
        if (templatePages == null) {
            walkExtensions();
        }
        final List<IConfigurationElement> pages = templatePages.get(id);
        if (pages == null || pages.size() == 0) {
            return new INewApplicationWizardPage[0];
        }
        final INewApplicationWizardPage[] newPages = new INewApplicationWizardPage[pages.size()];
        for (int i = 0; i < pages.size(); i++) {
            IConfigurationElement element = pages.get(i);
            try {
                newPages[i] = (INewApplicationWizardPage) element.createExecutableExtension("class");
            } catch (CoreException e) {
                TMWCoreUI.log(e);
            }
        }
        return newPages;
    }

    private void walkExtensions() {
        final IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
                TMWCoreUI.PLUGIN_ID, "wizardPages");
        templatePages = new TreeMap<String, List<IConfigurationElement>>();
        for (IConfigurationElement element : elements) {
            final String id = element.getAttribute("template-id");
            List<IConfigurationElement> extensions = templatePages.get(id);
            if (extensions == null) {
                extensions = new LinkedList<IConfigurationElement>();
                templatePages.put(id, extensions);
            }
            extensions.add(element);
        }
    }
}
