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
package org.symbian.tools.wrttools.debug.internal.launch;

import java.net.URI;
import java.text.MessageFormat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.symbian.tools.wrttools.debug.internal.Activator;
import org.symbian.tools.wrttools.debug.internal.IConstants;
import org.symbian.tools.wrttools.previewer.PreviewerPlugin;

public class WidgetLaunchDelegate implements ILaunchConfigurationDelegate {
	public static final String ID = "org.symbian.tools.wrttools.debug.widget";

	public void launch(ILaunchConfiguration configuration, String mode, final ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		monitor.beginTask("Preparing WRT Debugger", IProgressMonitor.UNKNOWN);
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		boolean debug = mode.equals(ILaunchManager.DEBUG_MODE);
		try {
			// 1. Load all parameters
			IProject project = getProject(configuration);
			isProjectDebugged(project, launchManager, launch);

			int port = PortPolicy.getPortNumber();
			final URI uri = prepareDebugger(project, debug, launch, port);

			Activator.getDefault().getChromeInstancesManager().startChrome(launch, port, uri.toASCIIString());
		} catch (CoreException e) {
			launchManager.removeLaunch(launch);
			throw e;
		}
		if (!debug) {
			launchManager.removeLaunch(launch);
		}
		monitor.done();
	}

	private void isProjectDebugged(IProject project, ILaunchManager launchManager, ILaunch l) throws CoreException {
		ILaunch[] launches = launchManager.getLaunches();
		for (ILaunch launch : launches) {
			ILaunchConfiguration launchConfiguration = launch.getLaunchConfiguration();
			if (!l.equals(launch) && ID.equals(launchConfiguration.getType().getIdentifier())) {
				IProject p2 = getProject(launchConfiguration);
				if (project.equals(p2)) {
					throw createCoreException(MessageFormat
							.format("Project {0} is already running.", project.getName()), null);
				}
			}
		}
	}

	private URI prepareDebugger(IProject project, boolean debug, final ILaunch launch, final int port) {
		if (debug) {
			final DebugConnectionJob job = new DebugConnectionJob(project, port, launch);
			return PreviewerPlugin.getDefault().getHttpPreviewer().previewProject(project, job);
		} else {
			return PreviewerPlugin.getDefault().getHttpPreviewer().previewProject(project);
		}
	}

	private IProject getProject(ILaunchConfiguration configuration) throws CoreException {
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

	private CoreException createCoreException(String message, Throwable exeption) {
		return new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, exeption));
	}
}
