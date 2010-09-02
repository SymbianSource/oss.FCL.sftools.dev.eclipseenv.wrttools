package org.symbian.tools.tmw.internal.ui.importwizard;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFramework;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntime;
import org.symbian.tools.tmw.internal.util.OpenFilesRunnable;
import org.symbian.tools.tmw.ui.TMWCoreUI;
import org.symbian.tools.tmw.ui.project.IApplicationImporter;

public class ApplicationImportWizard extends Wizard implements IImportWizard, INewWizard, IExecutableExtension {
    public static final String RECENT_IMPORT_PATH = "application.import.path";

    private IFile file;
    private ApplicationImportWizardPage page;
    private IConfigurationElement config;

    public ApplicationImportWizard() {
        setWindowTitle("Import WRT Application Archive");
        //        setDefaultPageImageDescriptor(WRTImages.importWgzWizardBanner());
        setNeedsProgressMonitor(true);
    }

    @Override
    public void addPages() {
        page = new ApplicationImportWizardPage(file);
        addPage(page);
    }

    private IProject createProject(String archiveName, String projectName, URI uri, IApplicationImporter importer,
            IProgressMonitor monitor) throws CoreException {
        monitor.beginTask("Importing application archive", 50);
        final IFacetedProjectWorkingCopy project = FacetedProjectFramework.createNewProject();
        final File file = new File(archiveName);
        final IMobileWebRuntime applicationRuntime = importer.getApplicationRuntime(file);
        final IRuntime runtime = TMWCore.getFProjSupport().getRuntime(applicationRuntime);

        project.setTargetedRuntimes(Collections.singleton(runtime));
        project.setPrimaryRuntime(runtime);
        project.setProjectName(projectName);
        if (uri != null) {
            final File loc = new File(uri);
            Path path = new Path(loc.getAbsolutePath());
            if (!path.removeLastSegments(1).equals(ResourcesPlugin.getWorkspace().getRoot().getLocation())) {
                project.setProjectLocation(path);
            }
        }
        final Set<IProjectFacetVersion> facets = getProjectFacetVersion(file, importer, applicationRuntime);
        project.setProjectFacets(facets);
        final Set<IProjectFacet> fcoll = new HashSet<IProjectFacet>();

        for (IProjectFacetVersion facetVersion : facets) {
            fcoll.add(facetVersion.getProjectFacet());
        }
        project.setFixedProjectFacets(fcoll);
        project.commitChanges(new SubProgressMonitor(monitor, 10));

        boolean success = false;
        try {
            importer.extractFiles(file, applicationRuntime, project.getProject(), new SubProgressMonitor(monitor, 40));
            success = true;
        } finally {
            if (!success) {
                project.getProject().delete(true, true, new NullProgressMonitor());
            }
        }
        final IFile[] toOpen = importer.getFilesToOpen(project.getProject());
        project.detect(new NullProgressMonitor());
        project.commitChanges(new NullProgressMonitor());
        monitor.done();
        if (toOpen != null && toOpen.length > 0) {
            final OpenFilesRunnable runnable = new OpenFilesRunnable(new HashSet<IFile>(Arrays.asList(toOpen)));
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    try {
                        runnable.run(new NullProgressMonitor());
                    } catch (InvocationTargetException e) {
                        TMWCoreUI.log(e);
                    } catch (InterruptedException e) {
                        TMWCoreUI.log(e);
                    }
                }
            });
        }
        return project.getProject();
    }

    private Set<IProjectFacetVersion> getProjectFacetVersion(final File file, IApplicationImporter importer,
            final IMobileWebRuntime applicationRuntime) {
        final Set<IProjectFacetVersion> facets = new HashSet<IProjectFacetVersion>();
        facets.addAll(TMWCore.getFProjSupport().getFixedFacetsVersions(applicationRuntime));
        final IProjectFacetVersion[] f = importer.getConfiguredFacets(file);
        if (f != null && f.length > 0) {
            facets.addAll(Arrays.asList(f));
        }
        return facets;
    }

    @Override
    public boolean performFinish() {
        try {
            final String projectName = page.getProjectName();
            final URI uri = page.getLocationURI();
            final String archiveName = page.getArchiveFile();
            final IApplicationImporter importer = page.getImporter();

            final IProject[] holder = new IProject[1];
            getContainer().run(true, true, new IRunnableWithProgress() {

                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    try {
                        ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {

                            public void run(IProgressMonitor monitor) throws CoreException {
                                holder[0] = createProject(archiveName, projectName, uri, importer, monitor);
                            }

                        }, monitor);
                    } catch (final CoreException e) {
                        getShell().getDisplay().asyncExec(new Runnable() {
                            public void run() {
                                StatusManager.getManager().handle(e.getStatus(),
                                        StatusManager.SHOW | StatusManager.BLOCK | StatusManager.LOG);
                            }
                        });
                    }
                }
            });
            if (holder[0] != null) {
                BasicNewProjectResourceWizard.updatePerspective(config);
                TMWCoreUI.getDefault().getPreferenceStore()
                        .setValue(RECENT_IMPORT_PATH, new File(archiveName).getParentFile().getAbsolutePath());
                //                ProjectUtils.focusOn(holder[0]);
            }
        } catch (InvocationTargetException e) {
            TMWCoreUI.log(e);
        } catch (InterruptedException e) {
            TMWCoreUI.log(e);
        }
        return true;
    }

    public void init(IWorkbench workbench, IStructuredSelection selection) {
        file = null;
        if (!selection.isEmpty()) {
            Object element = (selection).getFirstElement();
            if (element instanceof IAdaptable) {
                IResource resource = (IResource) ((IAdaptable) element).getAdapter(IResource.class);
                if (resource != null && resource.getType() == IResource.FILE
                        && "wgz".equalsIgnoreCase(resource.getProjectRelativePath().getFileExtension())) {
                    file = (IFile) resource;
                }
            }
        }
    }

    public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
            throws CoreException {
        this.config = config;
    }

}
