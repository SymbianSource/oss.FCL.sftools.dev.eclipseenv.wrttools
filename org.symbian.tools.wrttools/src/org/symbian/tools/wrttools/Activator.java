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

import java.io.PrintStream;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import com.intel.bluetooth.BlueCoveImpl;

import org.symbian.tools.wrttools.core.deploy.PreferenceConstants;
import org.symbian.tools.wrttools.sdt.utils.Logging;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.symbian.tools.wrttools";
	
	public static final String NAVIGATOR_ID = PLUGIN_ID + ".wrtnavigator";

	// The shared instance
	private static Activator plugin;
	
	private static PrintStream savedSysOut;
	
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
		
		// set parameters for BlueCove
		String param = Integer.toString(65*1024);
		System.setProperty("bluecove.obex.mtu", param);
		BlueCoveImpl.setConfigProperty("bluecove.obex.mtu", param);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
/*	
	public void startBluetoothOperation() {
		IPreferenceStore prefStore = getPreferenceStore();
		if (prefStore.getBoolean(PreferenceConstants.DEBUG_ENABLED))
			enableBlueCoveDiagnostics(true);
	}
	
	public void stopBluetoothOperation() {
		enableBlueCoveDiagnostics(false);
	}
*/
	
	/** Toggle BlueCove logging
	 */
	public static void enableBlueCoveDiagnostics(boolean enable) {
		System.setProperty("bluecove.debug", Boolean.valueOf(enable).toString());
		BlueCoveImpl.instance().enableNativeDebug(enable);
		if (enable) {
			System.setOut(new PrintStream(ConsoleFactory.createStream()));
		} else {
			System.setOut(savedSysOut);
		}
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
	
	public static void log(int severity, String message, Throwable x) {
		IStatus status = new Status(severity, PLUGIN_ID, 0, message, x);
		Logging.log(getDefault(), status);
	}

}
