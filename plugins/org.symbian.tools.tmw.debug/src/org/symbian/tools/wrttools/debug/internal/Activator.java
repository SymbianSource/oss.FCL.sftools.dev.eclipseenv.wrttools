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
package org.symbian.tools.wrttools.debug.internal;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.symbian.tools.wrttools.debug.internal.launch.ChromeInstancesManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.symbian.tools.wrttools.debug.core";

	public static final boolean DEBUG = Platform.inDebugMode() && Boolean.valueOf(Platform.getDebugOption(PLUGIN_ID + "/debug")); 
	public static final boolean DEBUG_CONNECTION = DEBUG && Boolean.valueOf(Platform.getDebugOption(PLUGIN_ID + "/debugConnection")); 
	public static final boolean DEBUG_RESOURCES = DEBUG && Boolean.valueOf(Platform.getDebugOption(PLUGIN_ID + "/debugResources")); 
	public static final boolean DEBUG_BREAKPOINTS = DEBUG
			&& Boolean.valueOf(Platform.getDebugOption(PLUGIN_ID
					+ "/debugBreakpoints")); 
	
	// The shared instance
	private static Activator plugin;

	private final ChromeInstancesManager chromes = new ChromeInstancesManager();
	
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
        Logger.getLogger("org.chromium.sdk").setLevel(Level.WARNING);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		chromes.shutdown();
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public ChromeInstancesManager getChromeInstancesManager() {
		return chromes;
	}

	public static void log(Throwable e) {
		log(e.getLocalizedMessage(), e);
	}

	private static void log(String message, Throwable exception) {
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, message, exception));
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		Images.initImageRegistry(reg);
	}

	public static void log(String message) {
		log(message, null);
	}
}
