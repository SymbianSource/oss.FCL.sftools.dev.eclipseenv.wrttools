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

package org.symbian.tools.wrttools.core.packager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;

import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.WRTStatusListener;
import org.symbian.tools.wrttools.core.status.IWRTStatusListener;

import org.symbian.tools.wrttools.core.status.IWRTConstants;
import org.symbian.tools.wrttools.core.status.WRTStatus;
import org.symbian.tools.wrttools.core.validator.ValidateAction;

public class WrtPackageActionDelegate extends ActionDelegate implements
		IObjectActionDelegate {


	/**
	 * @see ActionDelegate#run(IAction)
	 */

	private final List<IProject> projectList = new ArrayList<IProject>();
	

	public void run(IAction action) {		
		PlatformUI.getWorkbench().saveAllEditors(true);
		if (projectList != null && projectList.size() > 0) {
			for (IProject project : projectList) {
				if (project != null) {
					packageProject( project);
				}
			}
		}
	}

	public boolean packageProject(IProject project) {
		boolean packaedSucess=false;
		if (project != null) {

			ValidateAction validator = new ValidateAction();
			if(!validator.isValidProject(project)) {
				System.out.println("Invalid widget, can not be packaged!");
				reportStatus("For the project "+ project.getLocation());
				reportStatus(WRTPackagerConstants.STA_PKG_FAILED);
				reportStatus("See errors from the Problems View for more details...");
				return packaedSucess;
			}

			try {
				final List<String> fileList = new ArrayList<String>();
				//--->>
				project.accept(new IResourceVisitor() {
					public boolean visit(IResource resource)	throws CoreException {
						if (resource instanceof IFile) {
							IFile file = (IFile) resource;
							boolean add = true;
							// skip user-excluded and automatically-excluded files
							String value = file.getPersistentProperty(WRTPackagerConstants.EXCLUDE_PROPERTY);
							if (value != null) {
								add = false;
							}
							String name = file.getName();
							// skip Aptana temporarily generated files
							if (name.startsWith(".tmp_")) {
								add = false;
							}
							//Bug fix when import project with different name was unable to package it
							if(name.endsWith(".wgz")){
								add = false;
							}
							if (add) {
								if(file.getProject().getLocation().toString().endsWith(file.getProject().getName())){
									fileList.add(file.getLocation().toString().substring(file.getProject().getLocation().toString().length()-file.getProject().getName().length()));
									}
								else{
									String projectDir=file.getProject().getLocation().toString().substring(file.getProject().getLocation().toString().lastIndexOf("/")+1);
									String fullpath=file.getFullPath().toString();
									fullpath=fullpath.substring(fullpath.indexOf(file.getProject().getName())+file.getProject().getName().length());
									fullpath=projectDir+fullpath;								
									fileList.add(fullpath);
						
								}						
							}									
						}		
						return true;
					}
				});
				//<<--<<

				String projectPath = project.getLocation().toString();
				String prjName = project.getName();
				String dprojectPath = projectPath+"/"+prjName;
				WRTStatusListener statusListener = new WRTStatusListener();
				WidgetPackager widgetPackager = new WidgetPackager(statusListener);

				try {
					IProgressMonitor pm = new NullProgressMonitor();
					// deleting the previous build --->>
					IPath wgzPath = new Path(project.getName()+".wgz");
					IFile wgz = project.getFile(wgzPath);
					//do not delete the here, delete only if packaging is success
					/*if (wgz.exists()) {
						wgz.delete(true, false, pm);
					}*/
					widgetPackager.packageWidget(projectPath, dprojectPath, fileList);
					packaedSucess=true;
					project.refreshLocal(IResource.DEPTH_ONE, pm);
					wgz = project.getFile(wgzPath);
					if (wgz.exists()) {
						wgz.setPersistentProperty(WRTPackagerConstants.EXCLUDE_PROPERTY, Boolean.TRUE.toString());
					}
					
				} catch (PackageException e) {
					Activator.log(IStatus.ERROR, "Error packaging widget archive", e);
				} finally {
					statusListener.close();
				}
			} catch (CoreException x) {
				Activator.log(IStatus.ERROR,	"Error packaging widget archive", x);
			}
			
		}	
		return packaedSucess;
	}
	/**
	 * Reporting status
	 * @param statusMessage
	 */
	private void reportStatus(String statusMessage) {
		WRTStatus status = new WRTStatus();
		WRTStatusListener statusListener = new WRTStatusListener();
		status.setStatusSource(IWRTConstants.StatusSourceType.PACKAGER.name());
		status.setStatusDescription(statusMessage);
		statusListener.emitStatus(status);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			projectList.clear();
			IStructuredSelection ss = (IStructuredSelection) selection;
			for (Iterator iter = ss.iterator(); iter.hasNext();) {

				Object obj = iter.next();
				if (obj instanceof IProject) {
					projectList.add((IProject) obj);

				}
			}
		}
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {

	}
	
	
	
}
