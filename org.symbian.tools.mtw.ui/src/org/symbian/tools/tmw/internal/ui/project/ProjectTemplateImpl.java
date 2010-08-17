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
package org.symbian.tools.tmw.internal.ui.project;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntime;
import org.symbian.tools.tmw.ui.TMWCoreUI;
import org.symbian.tools.tmw.ui.project.IProjectTemplate;
import org.symbian.tools.tmw.ui.project.IProjectTemplateContext;
import org.symbian.tools.tmw.ui.project.ITemplateInstaller;

public class ProjectTemplateImpl implements IProjectTemplate {
    private final IConfigurationElement element;
    private Image image;
    private IMobileWebRuntime[] runtimes;
    private IProjectFacetVersion[] facetVersions;
    private ITemplateInstaller installer;
    private Map<String, String> parameters;

    public ProjectTemplateImpl(IConfigurationElement element) {
        this.element = element;
    }

    public int getWeight() {
        final String weight = element.getAttribute("weight");
        try {
            return Integer.valueOf(weight).intValue();
        } catch (NumberFormatException e) {
            TMWCoreUI.log("Invalid weight: %s in project template %s", weight, getName());
            return Integer.MAX_VALUE;
        }
    }

    public Image getIcon() {
        if (image == null) {
            final ImageDescriptor imageDescriptor = TMWCoreUI.imageDescriptorFromPlugin(element.getContributor()
                    .getName(), element.getAttribute("icon"));
            final String key = "Template#" + getName();
            TMWCoreUI.getDefault().getImageRegistry().put(key, imageDescriptor);
            image = TMWCoreUI.getDefault().getImageRegistry().get(key);
        }
        return image;
    }

    public String getName() {
        return element.getAttribute("name");
    }

    public String getDescription() {
        IConfigurationElement[] children = element.getChildren("description");
        if (children.length > 0) {
            return children[0].getValue();
        }
        return "";
    }

    public IMobileWebRuntime[] getSupportedRuntimes() {
        if (runtimes == null) {
            final IConfigurationElement[] children = element.getChildren("supported-runtime");
            runtimes = new IMobileWebRuntime[children.length];
            for (int i = 0; i < children.length; i++) {
                final IConfigurationElement element = children[i];
                final String id = element.getAttribute("id");
                final String version = element.getAttribute("version");
                runtimes[i] = TMWCore.getRuntimesManager().getRuntime(id, version);
                if (runtimes[i] == null) {
                    TMWCoreUI.log("Runtime %s@%s was not found. It is needed for project template %s.", id, version,
                            getName());
                }
            }
        }
        return runtimes;
    }

    public IProjectFacetVersion[] getRequiredFacets() {
        if (facetVersions == null) {
            final IConfigurationElement[] children = element.getChildren("required-facet");
            facetVersions = new IProjectFacetVersion[children.length];
            for (int i = 0; i < children.length; i++) {
                final IConfigurationElement element = children[i];
                final IProjectFacet projectFacet = ProjectFacetsManager.getProjectFacet(element.getAttribute("id"));
                if (projectFacet != null) {
                    facetVersions[i] = projectFacet.getVersion(element.getAttribute("version"));
                }
                if (facetVersions[i] == null) {
                    TMWCoreUI.log("Facet %s@%s was not found. It is required by project template %s.",
                            element.getAttribute("id"), element.getAttribute("version"));
                }
            }
        }
        return facetVersions;
    }

    public void init(IProject project, IProjectTemplateContext context, IProgressMonitor monitor) {
        if (installer == null) {
            installer = CompoundInstaller.combine(null, element.getChildren());
        }
        final ITemplateInstaller templateInstaller;
        final IMobileWebRuntime runtime = context.getRuntime();
        if (runtime == null) {
            templateInstaller = installer;
        } else {
            templateInstaller = CompoundInstaller.combine(installer, TMWCoreUI.getProjectTemplateManager()
                    .getEmptyProjectTemplate(runtime));
        }
        templateInstaller.prepare(project, context);
        try {
            final IPath[] files = templateInstaller.getFiles();
            monitor.beginTask("Copying project files", files.length * 10);
            templateInstaller.copyFiles(files, new SubProgressMonitor(monitor, files.length * 10));
            final IRunnableWithProgress action = templateInstaller.getPostCreateAction();
            if (action != null) {
                Display.getDefault().asyncExec(new Runnable() {
                    public void run() {
                        ProgressMonitorDialog dialog = new ProgressMonitorDialog(PlatformUI.getWorkbench()
                                .getActiveWorkbenchWindow().getShell());
                        try {
                            dialog.run(false, true, action);
                        } catch (InvocationTargetException e) {
                            TMWCoreUI.log(e);
                        } catch (InterruptedException e) {
                            TMWCoreUI.log(e);
                        }
                    }
                });
            }
        } catch (CoreException e) {
            TMWCoreUI.log(e);
        } finally {
            templateInstaller.cleanup();
        }
        monitor.done();
    }

    public Map<String, String> getDefaultParameterValues() {
        if (parameters == null) {
            parameters = new TreeMap<String, String>();
            for (IConfigurationElement el : element.getChildren()) {
                if ("default-parameter-value".equals(el.getName())) {
                    parameters.put(el.getAttribute("name"), el.getAttribute("value"));
                }
            }
        }
        return parameters;
    }

    public String getId() {
        return element.getAttribute("id");
    }
}
