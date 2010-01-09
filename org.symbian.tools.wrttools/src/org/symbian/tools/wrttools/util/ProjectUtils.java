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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.swing.filechooser.FileSystemView;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
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
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.wst.jsdt.internal.ui.wizards.buildpaths.BuildPathsBlock;
import org.eclipse.wst.validation.ValidationFramework;
import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.dialogs.AptanaProjectSelectionDialog;

public class ProjectUtils {
	public static final String PREVIEW_FOLDER = "preview";
	public static final String PREVIEW_FRAME_FILE = "wrt_preview_frame.html";

	public static IProject createWrtProject(String name, URI uri,
			IProgressMonitor monitor) throws CoreException {
		monitor.beginTask("Create project resources", 20);
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject project = workspace.getRoot().getProject(name);
		BuildPathsBlock.createProject(project, uri, new SubProgressMonitor(
				monitor, 10));

		BuildPathsBlock.addJavaNature(project, new SubProgressMonitor(monitor,
				10));

		// TODO: Build path, super type, etc.
		// BuildPathsBlock.flush(classPathEntries, javaScriptProject, superType,
		// monitor)

		ValidationFramework.getDefault().addValidationBuilder(project);

		monitor.done();
		return project;
	}

	public static void addPreviewer(IProject project, IPath mainHtml) {
		URI archive = getPreviewerArchive();
		try {
			if (archive != null) {
				ZipInputStream stream = new ZipInputStream(archive.toURL().openStream());
				ZipEntry entry;
				while ((entry = stream.getNextEntry()) != null) {
					if (!entry.isDirectory()) {
						copyFile(project, entry.getName(), stream, entry.getSize(), new NullProgressMonitor());
					} else {
						IFolder folder = project.getFolder(entry.getName());
						if (!folder.exists()) {
							folder.create(false, true, new NullProgressMonitor());
						}
					}
					stream.closeEntry();
				}
			}
			IFile file = project.getFile(mainHtml + ".html");
			if (file.exists()) {
				file.copy(project.getFullPath().append("wrt_preview_main.html"), false, new NullProgressMonitor());
			}
			
		} catch (IOException e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Unable to add previewer to project"));
		} catch (CoreException e) {
			StatusManager.getManager().handle(e, Activator.PLUGIN_ID);
		}
	}

	private static URI getPreviewerArchive() {
		File file = getPreviewerZip();
		if (file.isFile()) {
			return file.toURI();
		}
		Display display = Display.getDefault();
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				importPreviewer();
			}
		});
		if (file.isFile()) {
			return file.toURI();
		}
		return null;
	}

	private static File getPreviewerZip() {
		return Activator.getDefault().getStateLocation()
				.append("previewer.zip").toFile();
	}

	private static void importPreviewer() {
		AptanaProjectSelectionDialog dialog = new AptanaProjectSelectionDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		int open = dialog.open();
		if (open == Window.OK) {
			File project = dialog.getProject();
			try {
				zipPreviewer(project);
			} catch (IOException e) {
				StatusManager
						.getManager()
						.handle(
								new Status(
										IStatus.ERROR,
										Activator.PLUGIN_ID,
										"Failed to create Web Runtime previewer archive.",
										e), StatusManager.SHOW);
			}
		}
	}

	private static void zipPreviewer(File project) throws IOException {
		ZipOutputStream stream = new ZipOutputStream(new FileOutputStream(
				getPreviewerZip()));
		try {
			zip(new File(project, PREVIEW_FOLDER), stream, PREVIEW_FOLDER + "/");
			zipFile(new File(project, PREVIEW_FRAME_FILE),
					PREVIEW_FRAME_FILE, stream);
		} finally {
			stream.close();
		}
	}

	private static void zip(File folder, ZipOutputStream stream, String path)
			throws IOException {
		ZipEntry entry = new ZipEntry(path);
		stream.putNextEntry(entry);
		stream.closeEntry();
		File[] files = folder.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				zipFile(file, path + file.getName(), stream);
			} else {
				zip(file, stream, path + file.getName() + "/");
			}
		}
	}

	private static void zipFile(File file, String zipEntry,
			ZipOutputStream stream) throws IOException, FileNotFoundException {
		ZipEntry entry = new ZipEntry(zipEntry);
		stream.putNextEntry(entry);
		BufferedInputStream inputStream = new BufferedInputStream(
				new FileInputStream(file));
		try {
			copy(inputStream, stream);
		} finally {
			inputStream.close();
		}
		stream.closeEntry();
	}

	private static void copy(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[131072]; // 128k - should be enough for most
		// JS/CSS files
		int count;
		while ((count = in.read(buffer)) > 0) {
			out.write(buffer, 0, count);
		}
	}

	public static String getDefaultAptanaLocation() {
		File myDocuments = FileSystemView.getFileSystemView()
				.getDefaultDirectory();
		File file = new File(myDocuments, "Aptana Studio Workspace");
		return file.exists() ? file.getAbsolutePath() : "";
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

	public static void importPreviewer(URI locationURI) {
		if (!getPreviewerZip().exists()) {
			File file = new File(locationURI);
			try {
				zipPreviewer(file);
			} catch (IOException e) {
				Activator.log(e);
			}
		}
	}

	public static boolean isAptanaProject(URI locationURI) {
		return isAptanaProject(new File(locationURI));
	}
}
