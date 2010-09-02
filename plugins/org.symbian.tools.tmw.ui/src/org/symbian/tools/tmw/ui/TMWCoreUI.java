/*******************************************************************************
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
 *******************************************************************************/
package org.symbian.tools.tmw.ui;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.symbian.tools.tmw.core.projects.ITMWProject;
import org.symbian.tools.tmw.internal.ui.deployment.DeploymentTargetPresentationsManager;
import org.symbian.tools.tmw.internal.ui.deployment.DeploymentTargetTypesRegistry;
import org.symbian.tools.tmw.internal.ui.project.ProjectTemplateManagerImpl;
import org.symbian.tools.tmw.ui.project.IApplicationImporter;
import org.symbian.tools.tmw.ui.project.IProjectTemplateManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class TMWCoreUI extends AbstractUIPlugin {
    // The shared instance
    private static TMWCoreUI plugin;
    // The plug-in ID
    public static final String PLUGIN_ID = "org.symbian.tools.tmw.ui"; //$NON-NLS-1$

    public static IApplicationImporter[] getApplicationImporters() {
        if (getDefault().importers == null) {
            final Collection<IApplicationImporter> collection = new LinkedList<IApplicationImporter>();
            final IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
                    PLUGIN_ID, "applicationImporter");
            for (IConfigurationElement element : elements) {
                try {
                    collection.add((IApplicationImporter) element.createExecutableExtension("class"));
                } catch (CoreException e) {
                    TMWCoreUI.log(String.format(
                            "Application importer %s cannot be instantiated. It is declared in plug-in %s",
                            element.getAttribute("class"), element.getContributor().getName()), e);
                }
            }
            getDefault().importers = collection.toArray(new IApplicationImporter[collection.size()]);
        }
        return getDefault().importers;
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static TMWCoreUI getDefault() {
        return plugin;
    }
    public static Images getImages() {
        if (getDefault().images == null) {
            getDefault().images = new Images(getDefault().getImageRegistry());
        }
        return getDefault().images;
    }
    public static ProjectMemo getMemo(ITMWProject project) {
        return getDefault().getMemoForProject(project);
    }
    public static IProjectTemplateManager getProjectTemplateManager() {
        return getDefault().projectTemplateManager;
    }

    public static void log(Exception e) {
        log(null, e);
    }

    public static void log(String message, Exception e) {
        getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, message, e));
    }

    public static void log(String message, Object... args) {
        log(String.format(message, args), (Exception) null);
    }

    private Images images;
    private IApplicationImporter[] importers;
    private final Map<IProject, ProjectMemo> memos = new WeakHashMap<IProject, ProjectMemo>();
    private final DeploymentTargetPresentationsManager presentations = new DeploymentTargetPresentationsManager();
    private IProjectTemplateManager projectTemplateManager;
    private final DeploymentTargetTypesRegistry typesRegistry = new DeploymentTargetTypesRegistry();

    public DeploymentTargetTypesRegistry getDeploymentTypesRegistry() {
        return typesRegistry;
    }

    private synchronized ProjectMemo getMemoForProject(ITMWProject project) {
        ProjectMemo memo = memos.get(project.getProject());
        if (memo == null) {
            memo = new ProjectMemo(project);
            memos.put(project.getProject(), memo);
        }
        return memo;
    }

    public DeploymentTargetPresentationsManager getPresentations() {
        return presentations;
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
    }

    public void start(BundleContext context) throws Exception {
        super.start(context);
        projectTemplateManager = new ProjectTemplateManagerImpl();
        plugin = this;
    }

    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }
}
