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
package org.symbian.tools.wrttools.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.filechooser.FileSystemView;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.MultiPartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.internal.ui.wizards.buildpaths.BuildPathsBlock;
import org.eclipse.wst.validation.ValidationFramework;
import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.WidgetProjectNature;

@SuppressWarnings("restriction")
public class ProjectUtils {
	private static final String DEFAULT_APTANA_WORKSPACE = "Aptana Studio Workspace";
	public static final String PREVIEW_FOLDER = "preview";
	public static final String PREVIEW_FRAME_FILE = "wrt_preview_frame.html";
	public static final String PREVIEW_MAIN_FILE = "wrt_preview_main.html";

	private static boolean isDefaultProjectLocation(URI uri) {
		if (uri == null) {
			return true;
		}
		File file = new File(uri);
		IPath project = new Path(file.getAbsolutePath());
		IPath workspace = ResourcesPlugin.getWorkspace().getRoot()
				.getLocation();
		return workspace.isPrefixOf(project);
	}

	public static IProject createWrtProject(String name, URI uri,
			IProgressMonitor monitor) throws CoreException {
		uri = isDefaultProjectLocation(uri) ? null : uri;
		monitor.beginTask("Create project resources", 20);
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject project = workspace.getRoot().getProject(name);
		BuildPathsBlock.createProject(project, uri, new SubProgressMonitor(
				monitor, 10));
		BuildPathsBlock.addJavaNature(project, new SubProgressMonitor(monitor,
				10));

		ValidationFramework.getDefault().addValidationBuilder(project);
		ValidationFramework.getDefault().applyChanges(
				ValidationFramework.getDefault().getProjectSettings(project),
				true);

		// TODO: Build path, super type, etc.
		// BuildPathsBlock.flush(classPathEntries, javaScriptProject, superType,
		// monitor)

		addWrtNature(project);

		monitor.done();
		return project;
	}

	public static void addWrtNature(IProject project) {
		if (!hasWrtNature(project)) {
			try {
				IProjectDescription description = project.getDescription();
				String[] natureIds = description.getNatureIds();
				String[] newNatures = new String[natureIds.length + 1];
				System.arraycopy(natureIds, 0, newNatures, 1, natureIds.length);
				newNatures[0] = WidgetProjectNature.ID;
				description.setNatureIds(newNatures);

				ICommand[] buildSpec = description.getBuildSpec();
				for (int i = 0; i < buildSpec.length; i++) {
					ICommand command = buildSpec[i];
					if (JavaScriptCore.BUILDER_ID.equals(command
							.getBuilderName())) {
						buildSpec[i] = buildSpec[buildSpec.length - 1];
						buildSpec[buildSpec.length - 1] = command;
						description.setBuildSpec(buildSpec);
						break;
					}
				}

				project.setDescription(description, new NullProgressMonitor());
			} catch (CoreException e) {
				Activator.log(e);
			}
		}
	}

	public static boolean hasWrtNature(IProject project) {
		try {
			return project.hasNature(WidgetProjectNature.ID);
		} catch (CoreException e) {
			Activator.log(e);
			return false;
		}
	}

	public static String getDefaultAptanaLocation() {
		File myDocuments = FileSystemView.getFileSystemView()
				.getDefaultDirectory();
		File file = new File(myDocuments, DEFAULT_APTANA_WORKSPACE); // Windows
		if (file.exists()) {
			return file.getAbsolutePath();
		}
		file = new File(myDocuments, "Documents" + File.separator
				+ DEFAULT_APTANA_WORKSPACE); // Mac OS X
		if (file.exists()) {
			return file.getAbsolutePath();
		}
		return "";
	}

	public static boolean isAptanaProject(File f) {
		return new File(f, PREVIEW_FOLDER).isDirectory()
				&& new File(f, PREVIEW_FRAME_FILE).isFile();
	}

	public static void copyFile(IProject project, String name,
			ZipInputStream stream, long size, IProgressMonitor monitor)
			throws CoreException, IOException {
		IFile file = project.getFile(name);
		file.create(new NonClosingStream(stream), true, new SubProgressMonitor(
				monitor, 1));
	}

	public static boolean isAptanaProject(URI locationURI) {
		return isAptanaProject(new File(locationURI));
	}

	public static File isAptanaProject(File[] contents) {
		File dotProjectFile = null;
		boolean hasPreviewer = false;
		boolean hasFrame = false;

		for (int i = 0; i < contents.length; i++) {
			File file = contents[i];
			if (file.isFile()
					&& file.getName().equals(
							IProjectDescription.DESCRIPTION_FILE_NAME)) {
				dotProjectFile = file;
			}
			if (file.isFile()
					&& PREVIEW_FRAME_FILE.equalsIgnoreCase(file.getName())) {
				hasFrame = true;
			}
			if (file.isDirectory()
					&& PREVIEW_FOLDER.equalsIgnoreCase(file.getName())) {
				hasPreviewer = true;
			}
		}
		if (!(hasFrame && hasPreviewer)) {
			dotProjectFile = null;
		}
		return dotProjectFile;
	}

	public static void unzip(String archiveFile, IContainer location,
			int trimSegments, IProgressMonitor progressMonitor)
			throws IOException, CoreException {
		progressMonitor.beginTask(MessageFormat.format("Unpacking {0}",
				archiveFile), IProgressMonitor.UNKNOWN);
		ZipInputStream stream = new ZipInputStream(new FileInputStream(
				archiveFile));

		try {
			ZipEntry nextEntry;
			while ((nextEntry = stream.getNextEntry()) != null) {
				IPath p = new Path(nextEntry.getName())
						.removeFirstSegments(trimSegments);
				if (!nextEntry.isDirectory()) {
					IFile file = location.getFile(p);
					checkParent(file.getParent());
					file.create(new NonClosingStream(stream), false,
							new SubProgressMonitor(progressMonitor, 1));
				}
			}
		} finally {
			stream.close();
		}
		progressMonitor.done();
	}

	private static void checkParent(IContainer parent) throws CoreException {
		if (parent.getType() == IResource.FOLDER && !parent.exists()) {
			checkParent(parent.getParent());
			((IFolder) parent).create(false, true, new NullProgressMonitor());
		}
	}

	public static boolean isPlist(IResource resource) {
		return resource.getType() == IResource.FILE
				&& resource.getName().equalsIgnoreCase("info.plist");
	}

	public static void focusOn(IProject... projects) {
		new FocusOnProjectJob(projects, Display.getCurrent()).schedule(50);
	}

	private static final class TouchAllResources implements IWorkspaceRunnable {
		private final IProject[] projects;

		public TouchAllResources(IProject[] projects) {
			this.projects = projects;

		}

		public void run(IProgressMonitor monitor) throws CoreException {
			for (IProject project : projects) {
				project.accept(new IResourceVisitor() {
					public boolean visit(IResource resource)
							throws CoreException {
						if (resource.isAccessible()
								&& resource.getType() == IResource.FILE
								&& resource.getFileExtension().equals("js")) {
							resource.touch(new NullProgressMonitor());
						}
						return true;
					}
				});
			}
		}
	}

	private static final class FocusOnProjectJob extends Job {

		private final IProject[] projects;
		private final Display display;

		public FocusOnProjectJob(IProject[] projects, Display display) {
			super("Preparing projects");
			this.projects = projects;
			this.display = display;
			setRule(this.projects.length == 1 ? projects[0] : projects[0]
					.getWorkspace().getRoot());
			setUser(false);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try {
				final Collection<IFile> files = new HashSet<IFile>(
						projects.length);
				for (IProject project : projects) {
					String file = CoreUtil.getIndexFile(project);
					if (file != null) {
						IFile index = project.getFile(file);
						if (index.isAccessible()) {
							files.add(index);
						}
					}
				}
				final IFile[] filesArray = files
						.toArray(new IFile[files.size()]);
				display.asyncExec(new Runnable() {

					public void run() {
						IWorkbenchPage activePage = PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow().getActivePage();
						IViewReference reference = activePage
								.findViewReference(Activator.NAVIGATOR_ID);
						IWorkbenchPart part = reference.getPart(false);
						if (part instanceof ISetSelectionTarget) {
							StructuredSelection selection;
							if (filesArray.length == 1) {
								selection = new StructuredSelection(
										filesArray[0]);
							} else {
								selection = new StructuredSelection(filesArray);
							}
							((ISetSelectionTarget) part)
									.selectReveal(selection);
						}
						try {
							IDE.openEditors(activePage, filesArray);
							ResourcesPlugin.getWorkspace().run(
									new TouchAllResources(projects),
									new NullProgressMonitor());
						} catch (CoreException e) {
							Activator.log(e);
						}
					}
				});
			} catch (CoreException e) {
				Activator.log(e);
			}
			return Status.OK_STATUS;
		}
	}
}
