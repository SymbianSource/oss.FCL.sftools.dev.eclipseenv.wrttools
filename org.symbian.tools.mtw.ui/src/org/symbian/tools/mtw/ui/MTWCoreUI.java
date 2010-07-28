package org.symbian.tools.mtw.ui;

import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.symbian.tools.mtw.core.projects.IMTWProject;
import org.symbian.tools.mtw.internal.deployment.DeploymentTargetPresentationsManager;
import org.symbian.tools.mtw.internal.deployment.DeploymentTargetTypesRegistry;

/**
 * The activator class controls the plug-in life cycle
 */
public class MTWCoreUI extends AbstractUIPlugin {
    private final Map<IProject, ProjectMemo> MEMOS = new WeakHashMap<IProject, ProjectMemo>();
    private final DeploymentTargetTypesRegistry typesRegistry = new DeploymentTargetTypesRegistry();

    // The plug-in ID
    public static final String PLUGIN_ID = "org.symbian.tools.mtw.ui"; //$NON-NLS-1$

    // The shared instance
    private static MTWCoreUI plugin;
    private Images images;
    private final DeploymentTargetPresentationsManager presentations = new DeploymentTargetPresentationsManager();

    /**
     * The constructor
     */
    public MTWCoreUI() {
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
    }

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

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static MTWCoreUI getDefault() {
        return plugin;
    }

    public static void log(String message, Exception e) {
        getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, message, e));
    }

    public static void log(String message, Object... args) {
        log(String.format(message, args), (Exception) null);
    }

    public static void log(Exception e) {
        log(null, e);
    }

    public static ProjectMemo getMemo(IMTWProject project) {
        return getDefault().getMemoForProject(project);
    }

    private synchronized ProjectMemo getMemoForProject(IMTWProject project) {
        ProjectMemo memo = MEMOS.get(project.getProject());
        if (memo == null) {
            memo = new ProjectMemo(project);
            MEMOS.put(project.getProject(), memo);
        }
        return memo;
    }

    public DeploymentTargetTypesRegistry getDeploymentTypesRegistry() {
        return typesRegistry;
    }

    public static Images getImages() {
        if (getDefault().images == null) {
            getDefault().images = new Images(getDefault().getImageRegistry());
        }
        return getDefault().images;
    }

    public DeploymentTargetPresentationsManager getPresentations() {
        return presentations;
    }
}
