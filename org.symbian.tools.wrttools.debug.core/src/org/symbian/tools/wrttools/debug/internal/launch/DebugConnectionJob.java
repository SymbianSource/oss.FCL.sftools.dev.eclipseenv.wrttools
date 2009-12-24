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

import org.chromium.debug.core.model.Destructable;
import org.chromium.debug.core.model.DestructingGuard;
import org.chromium.debug.core.model.JavascriptVmEmbedder;
import org.chromium.debug.core.model.JavascriptVmEmbedderFactory;
import org.chromium.debug.core.model.NamedConnectionLoggerFactory;
import org.chromium.debug.core.model.JavascriptVmEmbedder.ConnectionToRemote;
import org.chromium.sdk.ConnectionLogger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.symbian.tools.wrttools.debug.internal.Activator;
import org.symbian.tools.wrttools.debug.internal.model.DebugTargetImpl;

public class DebugConnectionJob extends Job {
	static final NamedConnectionLoggerFactory NO_CONNECTION_LOGGER_FACTORY = new NamedConnectionLoggerFactory() {
		public ConnectionLogger createLogger(String title) {
			return null;
		}
	};
	private URI uri;
	private final int port;
	private final ILaunch launch;
	private final IProject project;
	private boolean connected = false; 

	public DebugConnectionJob(IProject project, final int port, final ILaunch launch) {
		super("Establish debug connection");
		this.project = project;
		this.port = port;
		this.launch = launch;
		setUser(false);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			connect(monitor);
		} catch (CoreException e) {
			return e.getStatus();
		}
		return new Status(IStatus.OK, Activator.PLUGIN_ID, "");
	}

	private void connect(IProgressMonitor monitor) throws CoreException {
		final DebugTargetImpl target = new DebugTargetImpl(launch, project);
		final JavascriptVmEmbedder.ConnectionToRemote remoteServer = createConnectionToRemote(
				port, launch, uri);
		try {

			DestructingGuard destructingGuard = new DestructingGuard();
			try {
				Destructable lauchDestructor = new Destructable() {
					public void destruct() {
						if (!launch.hasChildren()) {
							DebugPlugin.getDefault().getLaunchManager()
									.removeLaunch(launch);
						}
					}
				};

				destructingGuard.addValue(lauchDestructor);

				Destructable targetDestructor = new Destructable() {
					public void destruct() {
						terminateTarget(target);
					}
				};
				destructingGuard.addValue(targetDestructor);
				boolean attached = target.attach(project.getName(), remoteServer,
						destructingGuard, new Runnable() {
							public void run() {
								openProjectExplorerView(target);
							}
						}, monitor);
				if (!attached) {
					// Error
					return;
				}

				launch.setSourceLocator(target.getSourceLocator());

				launch.addDebugTarget(target);
				monitor.done();

				// All OK
				destructingGuard.discharge();
			} finally {
				destructingGuard.doFinally();
			}

		} finally {
			remoteServer.disposeConnection();
		}
	}

	protected ConnectionToRemote createConnectionToRemote(int port,
			ILaunch launch, URI uri) throws CoreException {
		return JavascriptVmEmbedderFactory.connectToChromeDevTools(port,
				NO_CONNECTION_LOGGER_FACTORY, new WidgetTabSelector(uri));
	}

	protected void openProjectExplorerView(final DebugTargetImpl target) {
		target.setupBreakpointsFromResources();
		connected = true;
	}
	
	public boolean isConnected() {
		return connected;
	}

	private static void terminateTarget(DebugTargetImpl target) {
		target.setDisconnected(true);
		target.fireTerminateEvent();
	}

	public void setTabUri(URI uri) {
		this.uri = uri;
	}
}
