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

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
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
import org.symbian.tools.wrttools.debug.internal.ChromeDebugUtils;
import org.symbian.tools.wrttools.debug.internal.IConstants;
import org.symbian.tools.wrttools.previewer.PreviewerPlugin;

public class WidgetLaunchDelegate implements
		ILaunchConfigurationDelegate {
	public static final String ID = "org.symbian.tools.wrttools.debug.widget";
	private static final String[] CHROME_ARGS = {
		"executable-placeholder", // Chrome executable. Configurable in preferences
		"port-placeholder", // Here we will set port
		"profile-placeholder", // Here we will set profile folder so user settings have no effect
		"--disable-web-security", // Widgets can use network now
		"--disable-extenions", // Use standard UI, should also re
		"--disable-plugins", // Run faster!
		"--no-default-browser-check", // Our users don't need this nagging
		"--no-first-run", // We don't care
		"widget-placeholder" // Here we will have widget URI as --app argument
	};
	private static final int EXECUTABLE_ARG_NUM = 0;
	private static final int PORT_ARG_NUM = 1;
	private static final int PROFILE_ARG_NUM = 2;
	private static final int APP_ARG_NUM = CHROME_ARGS.length - 1;
	
	public void launch(ILaunchConfiguration configuration, String mode,
			final ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		monitor.beginTask("Preparing WRT Debugger", IProgressMonitor.UNKNOWN);
		// 1. Load all parameters
		IProject project = getProject(configuration);
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		
		if (isProjectDebugged(project, launchManager, launch)) {
			showProjectIsDebugged();
			launchManager.removeLaunch(launch);
			throw createCoreException(MessageFormat.format("Project {0} is already running.", project.getName()), null);
		}

		int port = findFreePort();
		boolean debug = mode.equals(ILaunchManager.DEBUG_MODE);
		final URI uri = prepareDebugger(project, debug, launch, port);
		final String browserExecutable = ChromeDebugUtils.getChromeExecutible();
		if (browserExecutable == null) {
			launchManager.removeLaunch(launch);
			throw createCoreException("No Chrome browser available", null);
		}

		// 2. Start Chrome
		synchronized (CHROME_ARGS) { // No chances for collision. Still, better safe then spend several days looking for hard-to-reproduce problem
			CHROME_ARGS[EXECUTABLE_ARG_NUM] = browserExecutable;
			CHROME_ARGS[PROFILE_ARG_NUM] = "--user-data-dir=\"" + Activator.getDefault().getStateLocation().append("chromeprofile").toOSString() + "\"";
			CHROME_ARGS[PORT_ARG_NUM] = "--remote-shell-port=" + port;
			CHROME_ARGS[APP_ARG_NUM] = MessageFormat.format("--app={0}", uri.toASCIIString());
			try {
				Runtime.getRuntime().exec(CHROME_ARGS, null,
						new File(browserExecutable).getParentFile());
			} catch (IOException e) {
				launchManager.removeLaunch(launch);
				StringBuffer commandLine = new StringBuffer(CHROME_ARGS[0]);
				for (int i = 1; i < CHROME_ARGS.length; i++) {
					commandLine.append(" ").append(CHROME_ARGS[i]);
				}
				throw createCoreException("Cannot execute: {0}", commandLine
						.toString(), e);
			}
		}
		
		if (!debug) {
			launchManager.removeLaunch(launch);
		}
		monitor.done();
	}


	private int findFreePort() {
		try {
			final ServerSocket socket = new ServerSocket(0);
			int port = socket.getLocalPort();
			socket.close();
			return port;
		} catch (IOException e) {
			Activator.log(e);
			return 7222;
		}
	}


	private void showProjectIsDebugged() {
	}


	private boolean isProjectDebugged(IProject project, ILaunchManager launchManager, ILaunch l) throws CoreException {
		ILaunch[] launches = launchManager.getLaunches();
		for (ILaunch launch : launches) {
			ILaunchConfiguration launchConfiguration = launch.getLaunchConfiguration();
			if (!l.equals(launch) && ID.equals(launchConfiguration.getType().getIdentifier())) {
				IProject p2 = getProject(launchConfiguration);
				return project.equals(p2);
			}
		}
		return false;
	}


	private URI prepareDebugger(IProject project, boolean debug,
			final ILaunch launch, final int port) {
		if (debug) {
			final DebugConnectionJob job = new DebugConnectionJob(project, port, launch);
			return PreviewerPlugin.getDefault().getHttpPreviewer().previewProject(project, job);
		} else {
			return PreviewerPlugin.getDefault().getHttpPreviewer().previewProject(project);
		}
	}


	private IProject getProject(ILaunchConfiguration configuration)
			throws CoreException {
		String projectName = configuration.getAttribute(
				IConstants.PROP_PROJECT_NAME, (String) null);
		if (projectName == null) {
			throw createCoreException("Project is not selected", null);
		}

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				projectName);
		if (!project.isAccessible()) {
			throw createCoreException(MessageFormat.format(
					"Project {0} is not opened", projectName), null);
		}
		return project;
	}

	private CoreException createCoreException(String message, String arg,
			Throwable exeption) {
		return createCoreException(MessageFormat.format(message, arg), exeption);
	}

	private CoreException createCoreException(String message, Throwable exeption) {
		return new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
				message, exeption));
	}
}
