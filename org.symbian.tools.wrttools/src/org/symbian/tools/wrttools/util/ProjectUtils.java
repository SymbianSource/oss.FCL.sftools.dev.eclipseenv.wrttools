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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.zip.ZipInputStream;

import javax.swing.filechooser.FileSystemView;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.wst.jsdt.internal.ui.wizards.buildpaths.BuildPathsBlock;
import org.eclipse.wst.validation.ValidationFramework;
import org.symbian.tools.wrttools.Activator;

public class ProjectUtils {
	private static final String DEFAULT_APTANA_WORKSPACE = "Aptana Studio Workspace";
	private static final String WRT_PREVIEW_MAIN_HTML = "wrt_preview_frame.html";
	public static final String PREVIEW_FOLDER = "preview";
	public static final String PREVIEW_FRAME_FILE = "wrt_preview_frame.html";

	@SuppressWarnings("restriction")
	public static IProject createWrtProject(String name, URI uri,
			IProgressMonitor monitor) throws CoreException {
		monitor.beginTask("Create project resources", 20);
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject project = workspace.getRoot().getProject(name);
		BuildPathsBlock.createProject(project, uri, new SubProgressMonitor(
				monitor, 10));
		ValidationFramework.getDefault().addValidationBuilder(project);

		BuildPathsBlock.addJavaNature(project, new SubProgressMonitor(monitor,
				10));

		// TODO: Build path, super type, etc.
		// BuildPathsBlock.flush(classPathEntries, javaScriptProject, superType,
		// monitor)


		monitor.done();
		return project;
	}

	public static void addPreviewer(IProject project, IPath mainHtml) {
		try {
			createPreviewerHomePage(project, mainHtml);
		} catch (IOException e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Unable to add previewer to project"));
		} catch (CoreException e) {
			StatusManager.getManager().handle(e, Activator.PLUGIN_ID);
		}
	}

	private static void createPreviewerHomePage(IProject project, IPath mainHtml)
			throws CoreException, IOException {
		IFile newFile = project.getFile(WRT_PREVIEW_MAIN_HTML);
		newFile.create(new ByteArrayInputStream("Should not be in release!"
				.getBytes()), false, new NullProgressMonitor());
	}

	public static String getDefaultAptanaLocation() {
		File myDocuments = FileSystemView.getFileSystemView()
				.getDefaultDirectory();
		File file = new File(myDocuments, DEFAULT_APTANA_WORKSPACE); // Windows
		if (file.exists()) {
			return file.getAbsolutePath();
		}
		file = new File(myDocuments, "Documents" + File.separator + DEFAULT_APTANA_WORKSPACE); // Mac OS X
		if (file.exists()) {
			return file.getAbsolutePath();
		}
		return "";
	}

	public static boolean isAptanaProject(File f) {
		return new File(f, PREVIEW_FOLDER).isDirectory()
				&& new File(f, PREVIEW_FRAME_FILE).isFile();
	}

	public static void copyFile(IProject project, String name, ZipInputStream stream,
			long size, IProgressMonitor monitor) throws CoreException,
			IOException {
		IFile file = project.getFile(name);
		file.create(new NonClosingStream(stream), true,
				new SubProgressMonitor(monitor, 1));
	}

	public static boolean isAptanaProject(URI locationURI) {
		return isAptanaProject(new File(locationURI));
	}
}
