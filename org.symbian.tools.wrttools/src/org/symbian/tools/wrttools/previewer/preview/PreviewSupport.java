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

package org.symbian.tools.wrttools.previewer.preview;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;

import org.symbian.tools.wrttools.sdt.utils.Check;
import org.symbian.tools.wrttools.sdt.utils.TrackedResource;

	/**
	 * This class encapsulates the creation and naming 
	 * of the preview folder and support files
	 * @author dpodwall
	 *
	 */
public class PreviewSupport {
	
	public static final String PREVIEW_FOLDER = "preview";
	public static final String PREVIEW_MAIN_HTML = "wrt_preview_main.html";
	public static final String PREVIEW_FRAME_HTML = "wrt_preview_frame.html";
	public static final String PREVIEW_HTML_TEMPLATE = "wrt_preview.html";
	public static final QualifiedName MAIN_HTML_PROPERTY = new QualifiedName("org.symbian.tools.wrttools", "mainHTML");
	//public static final String PREVIEW_BROWSER_NAME1 = "WRT Widget (S60)";
	public static final String PREVIEW_BROWSER_NAME =  "Web Runtime (WRT)";
	
	private TrackedResource projectTracker;
	
	public PreviewSupport(IProject project) {
		Check.checkArg(project);
		this.projectTracker = new TrackedResource(project);
	}
	
	public void dispose() {
		if (projectTracker != null) {
			projectTracker.dispose();
		}
	}
	
	public IProject getProject() {
		return projectTracker.getProject();
	}
	
	/**
	 * Returns the folder containing preview support files, creating it
	 * if needed.
	 * @throws CoreException
	 */
	public IFolder getPreviewFolder() throws CoreException {
		IFolder result = getProject().getFolder(PREVIEW_FOLDER);
		if (!result.exists()) {
			result.create(true, true, new NullProgressMonitor());
		}
		return result;
	}
	
	/**
	 * Returns the main file, which is a copy of the user's main html
	 * file. The copy is modified to include the widget preview environment
	 * JavaScript files.
	 * The returned IFile may not exist yet
	 * @throws CoreException
	 */
	public IFile getPreviewMainHtml() throws CoreException {
		IFile result = getProject().getFile(PREVIEW_MAIN_HTML);
		return result;
	}
	
	/**
	 * Returns the preview file, which is the outermost html
	 * file of the preview environment. It includes the main
	 * file in an iframe.
	 * The returned IFile may not exist yet
	 * @throws CoreException
	 */
	public IFile getPreviewFrameHtml() throws CoreException {
		IFile result = getProject().getFile(PREVIEW_FRAME_HTML);
		return result;
	}
	public IFile getMainHtml() throws CoreException {
		IFile result = null;
		String mainFilePath = getProject().getPersistentProperty(MAIN_HTML_PROPERTY);
		if (mainFilePath != null) {
			IResource resource = getProject().findMember(mainFilePath);
			if (resource instanceof IFile) {
				result = (IFile) resource;
			}
		}
		return result;
	}
	
	/**
	 * Returns the base path for preview support files
	 * in the plugin
	 * @return
	 */
	public IPath getPreviewPluginBase() {
		return new Path("preview");
	}
	
	public IPath getPreviewFrameTemplate() {
		return getPreviewPluginBase().append(PREVIEW_HTML_TEMPLATE);
	}

}
