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
package org.symbian.tools.wrttools.previewer;

import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.symbian.tools.wrttools.previewer.http.HttpPreviewer;

/**
 * The activator class controls the plug-in life cycle
 */
public class PreviewerPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.symbian.tools.wrttools.previewer";

	public static final boolean DEBUG = Platform.inDebugMode()
			&& Boolean.parseBoolean(Platform.getDebugOption(PLUGIN_ID + "/debug"));
	public static final boolean TRACE_SERVLET = DEBUG
			&& Boolean.parseBoolean(Platform.getDebugOption(PLUGIN_ID
					+ "/servlet"));
	public static final boolean TRACE_MAPPING = DEBUG
			&& Boolean.parseBoolean(Platform.getDebugOption(PLUGIN_ID
					+ "/mapping"));
	
	// The shared instance
	private static PreviewerPlugin plugin;

	private HttpPreviewer previewer = new HttpPreviewer();
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
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
		addImage(reg, Images.GREEN_SYNC);
		addImage(reg, Images.RED_SYNC);
		addImage(reg, Images.YELLOW_SYNC);
	}
	
	private void addImage(ImageRegistry reg, String path) {
		ImageDescriptor imageDescriptor = imageDescriptorFromPlugin(PLUGIN_ID, path);
		reg.put(path, imageDescriptor);
	}
	
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static PreviewerPlugin getDefault() {
		return plugin;
	}
	
	public static void log(Exception e) {
		log(null, e);
	}

	public static void log(String message, Exception e) {
		IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, message, e);
		getDefault().getLog().log(status);
	}

	public static ImageDescriptor getImageDescriptor(String key) {
		return getDefault().getImageRegistry().getDescriptor(key);
	}

	public IProxyService getProxyService() {
        BundleContext bundleContext = getDefault().getBundle().getBundleContext();
		ServiceReference serviceReference = bundleContext.getServiceReference(IProxyService.class.getName());
		IProxyService service = (IProxyService) bundleContext.getService(serviceReference);
		return service;
	}
	
	public HttpPreviewer getHttpPreviewer() {
		return previewer;
	}

}
