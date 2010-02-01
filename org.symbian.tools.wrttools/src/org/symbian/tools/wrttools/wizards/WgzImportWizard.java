package org.symbian.tools.wrttools.wizards;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.statushandlers.StatusManager;
import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.util.ProjectUtils;

public class WgzImportWizard extends Wizard implements IImportWizard {
	private IFile file;
	private WgzImportWizardPage page;

	public WgzImportWizard() {
		setWindowTitle("Import WRT Application Archive");
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		page = new WgzImportWizardPage(file);
		addPage(page);
	}

	private void createProject(String archiveName, String projectName, URI uri,
			IProgressMonitor monitor) throws CoreException {
		monitor.beginTask("Importing WRT application archive", 50);
		// 1. Create project
		IProject project = ProjectUtils.createWrtProject(projectName, uri,
				new SubProgressMonitor(monitor, 10));

		// 2. Unpack archive
		try {
			ProjectUtils.unzip(archiveName, project, 1, new SubProgressMonitor(
					monitor, 40));
		} catch (IOException e) {
			new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Archive unpacking failed", e));
		}
		monitor.done();
	}

	@Override
	public boolean performFinish() {
		try {
			final String projectName = page.getProjectName();
			final URI uri = page.getLocationURI();
			final String archiveName = page.getArchiveFile();
			getContainer().run(true, true, new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					try {
						ResourcesPlugin.getWorkspace().run(
								new IWorkspaceRunnable() {

									public void run(IProgressMonitor monitor)
											throws CoreException {
										createProject(archiveName, projectName,
												uri, monitor);
									}

								}, monitor);
					} catch (CoreException e) {
						StatusManager.getManager().handle(e.getStatus(),
								StatusManager.SHOW);
					}
				}
			});
		} catch (InvocationTargetException e) {
			Activator.log(e);
		} catch (InterruptedException e) {
			Activator.log(e);
		}
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		file = null;
		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			Object element = ((IStructuredSelection) selection)
					.getFirstElement();
			if (element instanceof IAdaptable) {
				IResource resource = (IResource) ((IAdaptable) element)
						.getAdapter(IResource.class);
				if (resource != null
						&& resource.getType() == IResource.FILE
						&& "wgz".equalsIgnoreCase(resource
								.getProjectRelativePath().getFileExtension())) {
					file = (IFile) resource;
				}
			}
		}
	}

}
