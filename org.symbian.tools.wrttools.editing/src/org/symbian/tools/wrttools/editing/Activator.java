package org.symbian.tools.wrttools.editing;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.symbian.tools.wrttools.editing";

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

	public void stop(BundleContext context) throws Exception {
		plugin = null;
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

	public static void log(Exception e) {
		log(e.getLocalizedMessage(), e);
	}

	private static void log(String message, Exception e) {
		final IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, e);
		getDefault().getLog().log(status);
	}

	public static ImageDescriptor getImageDescriptor(String id) {
		return getDefault().getImageRegistry().getDescriptor(id);
	}
}
