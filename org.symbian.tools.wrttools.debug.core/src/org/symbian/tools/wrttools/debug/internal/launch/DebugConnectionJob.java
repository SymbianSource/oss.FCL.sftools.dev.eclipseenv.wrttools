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

import org.chromium.debug.core.model.DebugTargetImpl;
import org.chromium.debug.core.model.Destructable;
import org.chromium.debug.core.model.DestructingGuard;
import org.chromium.debug.core.model.JavascriptVmEmbedder;
import org.chromium.debug.core.model.JavascriptVmEmbedder.ConnectionToRemote;
import org.chromium.debug.core.model.JavascriptVmEmbedderFactory;
import org.chromium.debug.core.model.NamedConnectionLoggerFactory;
import org.chromium.debug.core.model.WorkspaceBridge;
import org.chromium.sdk.ConnectionLogger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.symbian.tools.wrttools.debug.internal.Activator;
import org.symbian.tools.wrttools.previewer.http.IPreviewStartupListener;

public class DebugConnectionJob implements IPreviewStartupListener {
	static final NamedConnectionLoggerFactory NO_CONNECTION_LOGGER_FACTORY = new NamedConnectionLoggerFactory() {
		public ConnectionLogger createLogger(String title) {
			return null;
		}
	};
	private final int port;
	private final ILaunch launch;
	private final IProject project;

	public DebugConnectionJob(IProject project, final int port,
			final ILaunch launch) {
		if (Activator.DEBUG_CONNECTION) {
			System.out.println("Debugging " + project.getName() + " on port "
					+ port + ", launch: " + launch.getLaunchConfiguration());
		}
		this.project = project;
		this.port = port;
		this.launch = launch;
	}

	protected ConnectionToRemote createConnectionToRemote(int port,
			ILaunch launch, URI uri) throws CoreException {
		return JavascriptVmEmbedderFactory.connectToChromeDevTools(port,
				NO_CONNECTION_LOGGER_FACTORY, new WidgetTabSelector(uri));
	}

	private static void terminateTarget(DebugTargetImpl target) {
		target.setDisconnected(true);
		target.fireTerminateEvent();
	}

    public boolean browserRunning(URI uri, String sId) throws CoreException {
		if (Activator.DEBUG_CONNECTION) {
			System.out.println("Browser running, connecting @" + hashCode());
		}
		DestructingGuard destructingGuard = new DestructingGuard();
		try {
			JavascriptVmEmbedder.ConnectionToRemote remoteServer = createConnectionToRemote(
					port, launch, uri);
			Destructable lauchDestructor = new Destructable() {
				public void destruct() {
					if (!launch.hasChildren()) {
						DebugPlugin.getDefault().getLaunchManager()
								.removeLaunch(launch);
					}
				}
			};
			if (Activator.DEBUG_CONNECTION) {
				System.out.println("Setting up 1@" + hashCode());
			}
			destructingGuard.addValue(lauchDestructor);

			WorkspaceBridge.Factory bridgeFactory = new WRTProjectWorkspaceBridge.Factory(
					project);
			final DebugTargetImpl target = new DebugTargetImpl(launch,
					bridgeFactory);
			if (Activator.DEBUG_CONNECTION) {
				System.out.println("Setting up 2@" + hashCode());
			}

			Destructable targetDestructor = new Destructable() {
				public void destruct() {
					terminateTarget(target);
				}
			};
			destructingGuard.addValue(targetDestructor);

			if (Activator.DEBUG_CONNECTION) {
				System.out.println("Setting up 3@" + hashCode());
			}
			boolean attached = target.attach(remoteServer, destructingGuard,
					null, new NullProgressMonitor());
			if (Activator.DEBUG_CONNECTION) {
				System.out.printf("Is attached: %b, @%d\n", attached, hashCode());
			}
			if (!attached) {
				// Error
				return false;
			}

			launch.addDebugTarget(target);
            launch.setAttribute("http.service.id", sId);
			// All OK
			destructingGuard.discharge();
            addResourceListenerIfNotInstalled();
		} catch (CoreException e) {
			DebugPlugin.getDefault().getLaunchManager().removeLaunch(launch);
			throw e;
		} finally {
			destructingGuard.doFinally();
		}
		return true;
	}

    private static boolean listenerAdded = false;

    private void addResourceListenerIfNotInstalled() {
        synchronized (DebugConnectionJob.class) {
            if (!listenerAdded) {
                ResourcesPlugin.getWorkspace().addResourceChangeListener(new ResourcesChangeListener());
                listenerAdded = true;
            }
        }
    }
}
