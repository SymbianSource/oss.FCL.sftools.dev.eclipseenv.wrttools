package org.symbian.tools.tmw.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;
import org.symbian.tools.tmw.core.internal.projects.ProjectsSupportManager;
import org.symbian.tools.tmw.core.internal.runtimes.RuntimeClasspathManager;
import org.symbian.tools.tmw.core.internal.runtimes.RuntimesManagerImpl;
import org.symbian.tools.tmw.core.projects.IMTWProject;
import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntimeManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class TMWCore extends Plugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.symbian.tools.tmw.core"; //$NON-NLS-1$

    // The shared instance
    private static TMWCore plugin;

    private IMobileWebRuntimeManager runtimesManager;
    private ProjectsSupportManager projectsSupport;
    private RuntimeClasspathManager classpathManager;

    /**
     * The constructor
     */
    public TMWCore() {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        runtimesManager = new RuntimesManagerImpl();
        projectsSupport = new ProjectsSupportManager();
        classpathManager = new RuntimeClasspathManager();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    public IMobileWebRuntimeManager getRuntimesManager() {
        return runtimesManager;
    }

    public RuntimeClasspathManager getClasspathManager() {
        return classpathManager;
    }

    public IMTWProject create(IProject project) {
        return projectsSupport.create(project);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static TMWCore getDefault() {
        return plugin;
    }

    public static void log(String message, Exception exception) {
        getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, message, exception));
    }

    public static void log(String message, Object... args) {
        log(String.format(message, args), (Exception) null);
    }
}
