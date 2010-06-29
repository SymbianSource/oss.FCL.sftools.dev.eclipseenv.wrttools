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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.symbian.tools.wrttools.debug.internal.Activator;
import org.symbian.tools.wrttools.previewer.PreviewerPlugin;

public class WidgetLaunchDelegate implements ILaunchConfigurationDelegate {
	public static final String ID = "org.symbian.tools.wrttools.debug.widget";

	public void launch(ILaunchConfiguration configuration, String mode, final ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		monitor.beginTask("Preparing Mobile Web Debugger", IProgressMonitor.UNKNOWN);
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
        final IWorkbench workbench = PlatformUI.getWorkbench();
        final IWorkbenchWindow window = workbench.getWorkbenchWindows()[0];
        final boolean[] retvalue = new boolean[1];
        window.getShell().getDisplay().syncExec(new Runnable() {
            public void run() {
                retvalue[0] = workbench.saveAllEditors(true);
            }
        });
        if (!retvalue[0]) {
            launchManager.removeLaunch(launch);
            return;
        }
		boolean debug = mode.equals(ILaunchManager.DEBUG_MODE);
		try {
			// 1. Load all parameters
			IProject project = DebugUtil.getProject(configuration);
            if (DebugUtil.isProjectDebugged(project, launchManager, launch)) {
                throw DebugUtil.createCoreException(MessageFormat.format("Project {0} is already running.", project.getName()),
                        null);
            }

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

    private URI prepareDebugger(IProject project, boolean debug, final ILaunch launch, final int port) {
        DebugConnectionJob job = null;
		if (debug) {
            job = new DebugConnectionJob(project, port, launch);
		}
        return PreviewerPlugin.getDefault().getHttpPreviewer().previewProject(project, job);
	}
}
