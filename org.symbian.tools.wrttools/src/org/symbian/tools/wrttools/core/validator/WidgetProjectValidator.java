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

package org.symbian.tools.wrttools.core.validator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;

import org.symbian.tools.wrttools.sdt.utils.DefaultMessageListener;
import org.symbian.tools.wrttools.sdt.utils.FileUtils;
import org.symbian.tools.wrttools.sdt.utils.MessageLocation;
import org.symbian.tools.wrttools.sdt.utils.VariableSubstitutionEngine;
import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.core.exception.ValidationException;
import org.symbian.tools.wrttools.core.packager.WRTPackagerConstants;
import org.symbian.tools.wrttools.core.widgetmodel.WidgetModel;
import org.symbian.tools.wrttools.previewer.preview.PreviewSupport;

public class WidgetProjectValidator {
	
    static final String INFO_PLIST = "info.plist";
    boolean previewMainCreated = false; 
    private IProject project;
    private WidgetModel model = null;
   
	public static final String BUILDER_ID = "org.symbian.tools.wrttools.core.validator.widgetProjectValidator";

	public WidgetProjectValidator() {
		super();
		ValidateAction validator = new ValidateAction();
		validator.isValidProject(project);
		//return null;
	}
	void checkResource(IResource resource, IProject project  ) {
		if (resource instanceof IFile) {
			try {
				if(!previewMainCreated )	{
					//createEmptyPreviewMainHTML();
					createPreviewFrameHTML(project);
					
				}
				//if info.plist modified
				if (isInfoPList(resource)) {
					// did a change to info.plist result in a different main html file?
					
					IResource prevMainHTML = null;
					String mainHtmlName = project.getPersistentProperty(PreviewSupport.MAIN_HTML_PROPERTY);
					if (mainHtmlName != null) {
						prevMainHTML = project.findMember(mainHtmlName, false);
					}
				     model = null;
					IFile currMainHTML = getMainHTMLFromModel(project);

					 if ( currMainHTML != null){
						if(!currMainHTML.equals(prevMainHTML)) {
								updateHTMLPreview(currMainHTML);
								
						}
					 }
					else{
						createEmptyPreviewMainHTML(project);
						project.setPersistentProperty(PreviewSupport.MAIN_HTML_PROPERTY, null);
					}
					
					
				}
				else{		
					IFile currMainHTML = getMainHTMLFromModel(project);
				   String mainHtmlPath = project.getPersistentProperty(PreviewSupport.MAIN_HTML_PROPERTY);
				  if(mainHtmlPath == null){					  
					  if (currMainHTML != null && !currMainHTML.equals(resource)) {
					    	updateHTMLPreview(currMainHTML);
					 }
				 }
				else if (mainHtmlPath.equals(resource.getProjectRelativePath().toString())) {					
					updateHTMLPreview((IFile)resource);
					
				  }
				}
			} catch (CoreException e) {
				Activator.log(IStatus.ERROR, "Error building resource:"+resource.getFullPath().toString(), e);
			} catch (IOException e) {
				Activator.log(IStatus.ERROR, "Error building resource:"+resource.getFullPath().toString(), e);
			} catch (URISyntaxException e) {
				Activator.log(IStatus.ERROR, "Error building resource:"+resource.getFullPath().toString(), e);
			}
		}
	}
	
	private void updateHTMLPreview(IFile htmlFile) throws CoreException, IOException, URISyntaxException {
		createPreviewMainHTML(getMainHTML(project));	
		//createPreviewFrameHTML();

	}

	protected void updatePreviewFiles(IProject project)
			throws CoreException {
		final List<String> filesToPackage = new ArrayList<String>();
		try {
			updatePreviewSupportFiles(project);
			project.accept(new IResourceVisitor() {

				public boolean visit(IResource resource) throws CoreException {
					if (resource instanceof IFile) {
						IFile file = (IFile) resource;
						boolean add = true;
						// skip user-excluded and automatically-excluded files
						String value = file
								.getPersistentProperty(WRTPackagerConstants.EXCLUDE_PROPERTY);
						if (value != null) {
							add = false;
						}
						String name = file.getName();
						// skip Aptana temporarily generated files
						if (name.startsWith(".tmp_")) {
							add = false;
						}
						// if(name.endsWith(".wgz")){
						// add = false;
						// }
						if (name.equals("Thumbs.db")) {
							add = false;
						}
						if (add) {
							if (file.getProject().getLocation().toString()
									.endsWith(file.getProject().getName())) {

								filesToPackage.add(file.getLocation()
										.toString().substring(
												file.getProject().getLocation()
														.toString().length()
														- file.getProject()
																.getName()
																.length()));
							} else {
								String projectDir = file.getProject()
										.getLocation().toString().substring(
												file.getProject().getLocation()
														.toString()
														.lastIndexOf("/") + 1);
								String fullpath = file.getFullPath().toString();
								fullpath = fullpath.substring(fullpath
										.indexOf(file.getProject().getName())
										+ file.getProject().getName().length());
								fullpath = projectDir + fullpath;
								filesToPackage.add(fullpath);

							}
						}
					}
					return true;
				}
			});
		} catch (CoreException x) {
			Activator.log(IStatus.ERROR, "Error updating widget preview", x);
		}
	}
	
	private boolean isInfoPList(IResource resource) {
		return INFO_PLIST.equalsIgnoreCase(resource.getProjectRelativePath().toString());
	}
	
	private IFile getMainHTML(IProject project) {
		IFile result = null;
		try {
			String mainHtmlName = project.getPersistentProperty(PreviewSupport.MAIN_HTML_PROPERTY);
			if (mainHtmlName != null) {
				IResource resource = project.findMember(mainHtmlName, false);
				if (resource != null && resource instanceof IFile) {
					result = (IFile) resource;
				} else {
					project.setPersistentProperty(PreviewSupport.MAIN_HTML_PROPERTY, null);
				}
			}
			if (result == null) {
				result = getMainHTMLFromModel(project);
			}
		} catch (CoreException x) {
			Activator.log(IStatus.ERROR, "error getting main html property", x);
		}
		return result;
	}
	
	private IFile getMainHTMLFromModel(IProject project) {
		IFile result = null;
		if(model == null)
			model = getModel(project);
		String mainHtml = model.getMainHtml();
		if(mainHtml != null) {
			IResource resource = project.findMember(mainHtml, false);
			if (resource != null && resource instanceof IFile) {
				try {
					project.setPersistentProperty(PreviewSupport.MAIN_HTML_PROPERTY, mainHtml);
					result = (IFile) resource;
				}catch (CoreException x) {
					Activator.log(IStatus.ERROR, "error setting file persistent property", x);
				}
			}else{
				try {
					project.setPersistentProperty(PreviewSupport.MAIN_HTML_PROPERTY, null);
					result = (IFile) resource;
				}catch (CoreException x) {
					Activator.log(IStatus.ERROR, "error setting file persistent property", x);
				}
			}
		}
		return result;
	}
	
	private WidgetModel getModel(IProject project) {
		WidgetModel model = new WidgetModel();
		String path = project.getLocation().toString();
		model.setWidgetDirectory(path);
		try {
			model.getWidgetModel(new File(path));
		} catch (ValidationException x) {
			Activator.log(IStatus.ERROR, "invalid widget", x);
		}
		return model;
	}
	
	private String readFileIntoString(File file) throws CoreException, FileNotFoundException {
		InputStream is = new FileInputStream(file);
		return new String(FileUtils.readInputStreamContents(is, "UTF-8"));
	}

	private void setFileFromString(IFile file, String contents) throws UnsupportedEncodingException, CoreException {
		ByteArrayInputStream is = new ByteArrayInputStream(contents.getBytes("UTF-8"));
		if (!file.exists()) {
			file.create(is, true, new NullProgressMonitor());
		} else {
			file.setContents(is, true, false, new NullProgressMonitor());
		}
	}
	
	private void updatePreviewSupportFiles(IProject project) {
		try {
			PreviewSupport ps = new PreviewSupport(project);
			IFolder previewFolder = ps.getPreviewFolder();
			IProgressMonitor progressMonitor = new NullProgressMonitor();
			IProject projects[] = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			
			if (previewFolder.exists()) {
				// Use File-based utility since IFolder.delete doesn't always delete subfolders
				FileUtils.delTree(previewFolder.getLocation().toFile());
				previewFolder.refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
				for(int u=0; u < projects.length; u++){
					projects[u].refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
				}
			}
			if(!previewFolder.isSynchronized(IResource.DEPTH_INFINITE)){
				for(int u=0; u < projects.length; u++){
					projects[u].refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
				}
				previewFolder.getProject().refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
				previewFolder.getParent().refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
				previewFolder.refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
			}
			//No need to delete empty folder, we will reuse the same folder for copying new files and sub folders into this.
			//previewFolder.delete(IResource.FORCE|IFolder.INCLUDE_TEAM_PRIVATE_MEMBERS|IContainer.INCLUDE_PHANTOMS, progressMonitor);
			
			if(!previewFolder.isSynchronized(IResource.DEPTH_INFINITE)){
				for(int u=0; u < projects.length; u++){
					projects[u].refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
				}
				previewFolder.getProject().refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
				previewFolder.getParent().refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
				previewFolder.refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
			}
			// No need to create preview folder as we are using existing empty preview folder and copying into that 
			//previewFolder.create(true, false, progressMonitor);
			
			if(!previewFolder.isSynchronized(IResource.DEPTH_INFINITE)){
				for(int u=0; u < projects.length; u++){
					projects[u].refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
				}
				previewFolder.getProject().refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
				previewFolder.getParent().refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
				previewFolder.refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
			}
			previewFolder.setDerived(true);
			
			File destRootDir = previewFolder.getLocation().toFile();
			copyPreviewFiles("/preview", "/preview", destRootDir);
			//previewFolder.refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
			//previewFolder.setPersistentProperty(WRTPackagerConstants.EXCLUDE_PROPERTY, Boolean.TRUE.toString());
			if(!previewFolder.isSynchronized(IResource.DEPTH_INFINITE)){
				for(int u=0; u < projects.length; u++){
					projects[u].refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
				}
				previewFolder.getProject().refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
				previewFolder.getParent().refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
				previewFolder.refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
			}
			previewFolder.setPersistentProperty(WRTPackagerConstants.EXCLUDE_PROPERTY, Boolean.TRUE.toString());

			
			previewFolder.accept(new IResourceVisitor() {
				public boolean visit(IResource r) {
					try {
						// inhibit packaging for preview files
				 		r.setPersistentProperty(WRTPackagerConstants.EXCLUDE_PROPERTY, Boolean.TRUE.toString());
				 		// inhibit version control for preview files
				 		r.setDerived(true);
					} catch (CoreException x) {
						Activator.log(IStatus.ERROR, "error setting packager property on "+r.getFullPath().toString(), x);
					}
					return true;
				}
			});
		}
		catch (IOException x) {
			Activator.log(IStatus.ERROR, "Error updating preview support files", x);
		} catch (CoreException x) {
			Activator.log(IStatus.ERROR, "Error updating preview support files", x);
		}
	}
	
	private void copyPreviewFiles(String sourceRootDir, String topRootDir, File destRootDir) throws IOException, CoreException {
		Bundle bundle = Activator.getDefault().getBundle();
		Enumeration e = bundle.getEntryPaths(sourceRootDir);
		if (e != null) {
			 while (e.hasMoreElements()) {
			 	String path = (String) e.nextElement();
			 	// paths indicating subdirectories end with '/'. 
			 	if (path.endsWith("/")) {
			 		// need to trim first component off path, which is the base preview dir
			 		IPath destPath = new Path(path);
			 		destPath = destPath.removeFirstSegments(1);
			 		File newDir = new File(destRootDir, destPath.toString());
			 		newDir.mkdirs();
			 		copyPreviewFiles(path, topRootDir, destRootDir);
			 	} else {
			 		// skip PREVIEW_HTML_TEMPLATE, we don't copy to preview folder
			 		IPath destPath = new Path(path);
			 		destPath = destPath.removeFirstSegments(1);			 	
			 		File newFile = new File(destRootDir, destPath.toString());
			 		if (!newFile.getName().equals(PreviewSupport.PREVIEW_HTML_TEMPLATE)) {
			 			InputStream is = openBundleFile(new Path(path));
			 			FileUtils.copyFile(is, newFile);
			 		}
			 	}
			 }
		}
	}
	
	private InputStream openBundleFile(IPath path) throws IOException {
		InputStream is = FileLocator.openStream(Activator.getDefault().getBundle(),
				path, false);
		return is;
	}
	
	private void createPreviewFrameHTML(IProject project) throws IOException, CoreException, URISyntaxException {
		
		PreviewSupport ps = new PreviewSupport(project);
		InputStream templateStream = openBundleFile(ps.getPreviewFrameTemplate());
		String templateText = new String(FileUtils.readInputStreamContents(templateStream, null));

		Map<String, String> vars = new HashMap<String,String>();
		String previewBasePath = PreviewSupport.PREVIEW_FOLDER + "/";
		vars.put("preview_base", previewBasePath);
		VariableSubstitutionEngine engine = new VariableSubstitutionEngine(
				new DefaultMessageListener(), 
				new MessageLocation(ps.getProject(), 0, 0));
		engine.setVariableToken('(');
		String resultText = engine.substitute(vars, templateText);
		
		IFile previewFrame = ps.getPreviewFrameHtml();
		boolean updateProperty = !previewFrame.exists();
		setFileFromString(previewFrame, resultText);
		if (updateProperty) {
		previewFrame.setPersistentProperty(WRTPackagerConstants.EXCLUDE_PROPERTY, Boolean.TRUE.toString());
		}
		previewFrame.setDerived(true);
	}
	
	private void createEmptyPreviewMainHTML(IProject project) throws CoreException, UnsupportedEncodingException, FileNotFoundException {
		if(previewMainCreated)
			return;
		
		previewMainCreated = true;
		PreviewSupport ps = new PreviewSupport(project);
		
		IFile previewMainHtml = ps.getPreviewMainHtml();
		
		setFileFromString(previewMainHtml, "");
		previewMainHtml.setPersistentProperty(WRTPackagerConstants.EXCLUDE_PROPERTY, Boolean.TRUE.toString());
		previewMainHtml.setDerived(true);
		
	}
	
	private void createPreviewMainHTML(IFile src) throws CoreException, UnsupportedEncodingException, FileNotFoundException {
		
		PreviewSupport ps = new PreviewSupport(project);
		File inputFile = src.getLocation().toFile();
		String contents = readFileIntoString(inputFile);
		Pattern headPattern = Pattern.compile("<head\\b[^>]*>", Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
		
		String previewBasePath = PreviewSupport.PREVIEW_FOLDER;
		StringBuffer buf = new StringBuffer();
		buf.append("\t<script language=\"JavaScript\" type=\"text/javascript\" src=\"" + previewBasePath + "/script/lib/loader.js\"></script>\n");
		String previewScripts = buf.toString();
		
		Matcher matcher = headPattern.matcher(contents);
		if (matcher.find()) {
			String replacement = "<head>\n" + previewScripts;
			contents = matcher.replaceFirst(replacement);
		} else {
			Pattern bodyPattern = Pattern.compile("<body\\b[^>]*>", Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
			matcher = bodyPattern.matcher(contents);
			if (matcher.find()) {
				String replacement = "\n" + previewScripts + "<body>";
				contents = matcher.replaceFirst(replacement);
			}
		}
		
		IFile previewMainHtml = ps.getPreviewMainHtml();
		setFileFromString(previewMainHtml, contents);
		previewMainHtml.setPersistentProperty(WRTPackagerConstants.EXCLUDE_PROPERTY, Boolean.TRUE.toString());
		previewMainHtml.setDerived(true);
	}
}
