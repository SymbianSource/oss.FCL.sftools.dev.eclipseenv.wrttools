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
package org.symbian.tools.wrttools;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.jsdt.web.core.javascript.search.JsIndexManager;
import org.osgi.framework.BundleContext;
import org.symbian.tools.wrttools.core.WRTImages;
import org.symbian.tools.wrttools.util.ProjectUtils;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
    // The plug-in ID
    public static final String PLUGIN_ID = "org.symbian.tools.wrttools";

    // The shared instance
    private static Activator plugin;

    /**
     * The constructor
     */
    public Activator() {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        JsIndexManager.getInstance();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        WRTImages.init(reg);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

    public static void log(Exception e) {
        log(e.getLocalizedMessage(), e);
    }

    public static void log(String message, Exception e) {
        getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, message, e));
    }

    public static IProject[] getWrtProjects() {
        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        Collection<IProject> prjs = new TreeSet<IProject>(new Comparator<IProject>() {
            public int compare(IProject o1, IProject o2) {
                if (o1 == o2) {
                    return 0;
                } else if (o1 == null) {
                    return -1;
                } else if (o2 == null) {
                    return 1;
                } else {
                    return o1.getName().compareTo(o2.getName());
                }
            }
        });
        for (IProject project : projects) {
            if (ProjectUtils.hasWrtNature(project)) {
                prjs.add(project);
            }
        }
        return prjs.toArray(new IProject[prjs.size()]);
    }

}
