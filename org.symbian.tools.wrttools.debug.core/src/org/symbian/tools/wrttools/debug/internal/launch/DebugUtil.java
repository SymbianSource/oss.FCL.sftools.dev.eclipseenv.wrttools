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
package org.symbian.tools.wrttools.debug.internal.launch;

import java.text.MessageFormat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.symbian.tools.wrttools.debug.internal.Activator;
import org.symbian.tools.wrttools.debug.internal.IConstants;

public class DebugUtil {
    public static CoreException createCoreException(String message, Throwable exeption) {
        return new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, exeption));
    }

    public static IProject getProject(ILaunch configuration) {
        try {
            if (WidgetLaunchDelegate.ID.equals(configuration.getLaunchConfiguration().getType().getIdentifier())) {
                String projectName = configuration.getLaunchConfiguration().getAttribute(IConstants.PROP_PROJECT_NAME,
                        (String) null);
                if (projectName != null) {
                    IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
                    if (project.isAccessible()) {
                        return project;
                    }
                }
            }
        } catch (CoreException e) {
            Activator.log(e);
        }
        return null;
    }

    public static IProject getProject(ILaunchConfiguration configuration) throws CoreException {
        if (!WidgetLaunchDelegate.ID.equals(configuration.getType().getIdentifier())) {
            return null;
        }
        String projectName = configuration.getAttribute(IConstants.PROP_PROJECT_NAME, (String) null);
        if (projectName == null) {
            throw createCoreException("Project is not selected", null);
        }

        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        if (!project.isAccessible()) {
            throw createCoreException(MessageFormat.format("Project {0} is not opened", projectName), null);
        }
        return project;
    }

    public static boolean isProjectDebugged(IProject project, ILaunchManager launchManager, ILaunch l)
            throws CoreException {
        ILaunch[] launches = launchManager.getLaunches();
        for (ILaunch launch : launches) {
            ILaunchConfiguration launchConfiguration = launch.getLaunchConfiguration();
            if ((l == null || !l.equals(launch)) && !launch.isTerminated()
                    && WidgetLaunchDelegate.ID.equals(launchConfiguration.getType().getIdentifier())) {
                IProject p2 = getProject(launchConfiguration);
                if (project.equals(p2)) {
                    return true;
                }
            }
        }
        return false;
    }

}
