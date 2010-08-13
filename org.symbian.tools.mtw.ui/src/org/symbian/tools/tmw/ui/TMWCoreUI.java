package org.symbian.tools.tmw.ui;

import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.symbian.tools.tmw.core.projects.IMTWProject;
import org.symbian.tools.tmw.internal.ui.deployment.DeploymentTargetPresentationsManager;
import org.symbian.tools.tmw.internal.ui.deployment.DeploymentTargetTypesRegistry;
import org.symbian.tools.tmw.internal.ui.project.ProjectTemplateManagerImpl;
import org.symbian.tools.tmw.ui.project.IProjectTemplateManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class TMWCoreUI extends AbstractUIPlugin {
    private final Map<IProject, ProjectMemo> MEMOS = new WeakHashMap<IProject, ProjectMemo>();
    private final DeploymentTargetTypesRegistry typesRegistry = new DeploymentTargetTypesRegistry();

    // The plug-in ID
    public static final String PLUGIN_ID = "org.symbian.tools.tmw.ui"; //$NON-NLS-1$

    // The shared instance
    private static TMWCoreUI plugin;
    private IProjectTemplateManager projectTemplateManager;
    private Images images;
    private final DeploymentTargetPresentationsManager presentations = new DeploymentTargetPresentationsManager();

    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
    }

    public void start(BundleContext context) throws Exception {
        super.start(context);
        projectTemplateManager = new ProjectTemplateManagerImpl();
        plugin = this;
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
    public static TMWCoreUI getDefault() {
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

    public static IProjectTemplateManager getProjectTemplateManager() {
        return getDefault().projectTemplateManager;
    }
}
