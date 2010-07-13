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
 **/
package org.symbian.tools.wrttools.core;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.core.libraries.JSLibrary;
import org.symbian.tools.wrttools.wizards.IWizardPageFactory;
import org.symbian.tools.wrttools.wizards.WRTProjectFilesWizardPage;
import org.symbian.tools.wrttools.wizards.WizardContext;

public class ProjectTemplate {
    private static ProjectTemplate[] templates;

    private final IConfigurationElement element;

    private Image icon;

    public ProjectTemplate(IConfigurationElement element) {
        this.element = element;
    }

    public Image getIcon() {
        if (icon == null) {
            String path = element.getAttribute("icon");
            final ImageDescriptor imageDescriptor;
            if (path != null) {
                imageDescriptor = Activator.imageDescriptorFromPlugin(
                        element.getNamespaceIdentifier(), path);
            } else {
                imageDescriptor = ImageDescriptor.getMissingImageDescriptor();
            }
            icon = imageDescriptor.createImage(true);
        }
        return icon;
    }

    public String getName() {
        return element.getAttribute("name");
    }

    public String getDescription() {
        IConfigurationElement[] children = element.getChildren("description");
        if (children.length == 1) {
            return children[0].getValue();
        } else {
            return "";
        }
    }

    private String[] getLibraryIds() {
        IConfigurationElement[] elements = element
                .getChildren("requires-library");
        String[] ids = new String[elements.length];
        for (int i = 0; i < elements.length; i++) {
            IConfigurationElement element = elements[i];
            ids[i] = element.getValue();
        }
        return ids;
    }

    public static ProjectTemplate[] getAllTemplates() {
        if (templates == null) {
            IExtensionPoint point = Platform.getExtensionRegistry()
                    .getExtensionPoint(Activator.PLUGIN_ID, "projectTemplates");
            IConfigurationElement[] elements = point.getConfigurationElements();
            templates = new ProjectTemplate[elements.length];
            for (int i = 0; i < elements.length; i++) {
                IConfigurationElement element = elements[i];
                templates[i] = new ProjectTemplate(element);
            }
        }
        return templates;
    }

    public String getDefaultCssFile() {
        String file = element.getAttribute("default-css-name");
        return file != null ? file : "main";
    }

    public String getDefaultJsFile() {
        String file = element.getAttribute("default-js-name");
        return file != null ? file : "main";
    }

    public String getDefaultHtmlFile() {
        String file = element.getAttribute("default-html-name");
        return file != null ? file : "index";
    }

    public WRTProjectFilesWizardPage createWizardPage(WizardContext context,
            DataBindingContext bindingContext) {
        try {
            if (element.getAttribute("wizard-page-factory") != null) {
                IWizardPageFactory factory = (IWizardPageFactory) element
                        .createExecutableExtension("wizard-page-factory");
                return factory.createPage(context, bindingContext);
            }
        } catch (CoreException e) {
            Activator.log("Problem with template " + getName(), e);
        }
        return new WRTProjectFilesWizardPage(context, bindingContext);
    }

    @Override
    public String toString() {
        return getName();
    }

    public URL getProjectContents() {
        String uri = element.getAttribute("archive");
        if (uri != null) {
            Bundle bundle = Platform.getBundle(element.getContributor()
                    .getName());
            URL resource = bundle.getResource(uri);
            return resource;
        }
        return null;
    }

    public boolean requires(JSLibrary library) {
        for (String id : getLibraryIds()) {
            if (library.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public JSLibrary[] getRequiredLibraries() {
        Set<String> ids = new TreeSet<String>(Arrays.asList(getLibraryIds()));
        Set<JSLibrary> libraries = new HashSet<JSLibrary>();
        JSLibrary[] jsLibraries = Activator.getJSLibraries();
        for (JSLibrary jsLibrary : jsLibraries) {
            if (ids.contains(jsLibrary.getId())) {
                libraries.add(jsLibrary);
            }
        }
        return libraries.toArray(new JSLibrary[libraries.size()]);
    }

    public int getOrder() {
        String attribute = element.getAttribute("order");
        if (attribute != null) {
            return Integer.parseInt(attribute);
        }
        return Integer.MAX_VALUE;
    }
}
