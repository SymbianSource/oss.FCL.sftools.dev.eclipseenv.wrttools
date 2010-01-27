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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.core.exception.ReportException;
import org.symbian.tools.wrttools.core.exception.ValidationException;
import org.symbian.tools.wrttools.core.marker.MarkerUtil;
import org.symbian.tools.wrttools.core.packager.WRTPackagerConstants;
import org.symbian.tools.wrttools.core.report.IMessageListener;
import org.symbian.tools.wrttools.core.report.Message;
import org.symbian.tools.wrttools.core.status.IWRTConstants;
import org.symbian.tools.wrttools.core.status.IWRTStatusListener;
import org.symbian.tools.wrttools.core.status.WRTStatus;
import org.symbian.tools.wrttools.core.status.WRTStatus;
import org.symbian.tools.wrttools.core.validator.Messages;
public class ValidateAction implements IObjectActionDelegate {
	
//	WRTStatusListener statusListener = new WRTStatusListener();
	private StatusListener statusListener = new StatusListener();
	public MessageListener messageListener = new MessageListener();
	private final List<IProject> projectList = new ArrayList<IProject>();
//	private String widgetProjectPath;
	IProject projectvalidating  =null;
	private Shell shell;
	
	private boolean validateSucess=false;
	private boolean initialValidate;	
	
	
	public ValidateAction() {
		super();
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void run(IAction action) {
		try{		  
			PlatformUI.getWorkbench().saveAllEditors(true);
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.ui.views.ProblemView");
		}
		catch(PartInitException e)
		{
			Activator.log(IStatus.ERROR, "Problem View  exception", e);
		}
		IProject emptyfilesProject = null;
		initialValidate = false;
		if (projectList != null && projectList.size() > 0) {
			for (IProject project : projectList) {
				if (project != null) {
					validateSucess=true;
					validateProject( project);
					emptyfilesProject = project;					
				}
			}
		}
		/* if all the mandatory files are missing then display error on the screen*/
		if (emptyfilesProject != null && ! initialValidate) {
			WidgetValidator widgetValidator = new WidgetValidator(messageListener, statusListener);
			try {			
				widgetValidator.validateWidgetProject(emptyfilesProject.getLocation().toString());
			} catch (ValidationException e) {
				Activator.log(IStatus.ERROR, "Validation exception ", e);
			} catch (ReportException e) {
				Activator.log(IStatus.ERROR, "Report exception", e);
			}			
		}
	}

	public boolean isValidProject(IProject project) {

		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			
			public void run() {
				
				IWorkbenchWindow dwindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				IWorkbenchPage page = dwindow.getActivePage();
				
				if (page != null) {
					try {
						page.showView("org.eclipse.ui.views.ProblemView");
					}catch (PartInitException pie) {
					}
				}
			}
		});
		
		validateSucess = true;
		validateProject(project);
		
		return validateSucess;
	}
	
	public void validateProject(IProject project) {
		projectvalidating=project;
//        statusListener.setStatusSource(IWRTConstants.StatusSourceType.VALIDATOR.name());
        messageListener.setMessageSource(IWRTConstants.VALIDATOR);
        WidgetValidator widgetValidator = new WidgetValidator(messageListener, statusListener);
    	final List<String> filesToPackage = new ArrayList<String>();
        try {
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
		} 
        catch (CoreException x) {
			Activator.log(IStatus.ERROR,	"Core Exception ", x);
		}
        if (filesToPackage != null && filesToPackage.size() > 0) {
			widgetValidator.setFilesSelected(filesToPackage);
	
			try {
				MarkerUtil.deleteMarker(project);
				widgetValidator.validateWidgetProject(project.getLocation().toString());
				initialValidate = true;
			} catch (ValidationException e) {
				Activator.log(IStatus.ERROR, "Validation exception ", e);
			} catch (ReportException e) {
				Activator.log(IStatus.ERROR, "Report exception", e);
			}

			if (validateSucess) {
//				WRTStatus status = new WRTStatus();
//				status.setStatusSource(IWRTConstants.StatusSourceType.VALIDATOR.name());
//				status.setStatusDescription("Validation completed, no errors were found.");
//				status.setStatusDescription(Messages.getString("widget.validation.complete"));
//				statusListener.emitStatus(status);
			}
		}
        else {
	        	if(shell != null)
	        	MessageDialog.openInformation(shell,"Validate Widget Project","Please Select Files to validate");
		}
		
	}

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
	   
//	-------------------------------inner class-------------------------------------//

	public class MessageListener implements IMessageListener {
		private int messageSource;
		public int getMessageSource() {
			return 1;
		}
		public void setMessageSource(int messageSource) {
			this.messageSource = messageSource;
		}
		public boolean isMessageHandled(Message msg) {

			if (msg.getMessageSource() == (messageSource)) {
				return true;
			}
			return false;
		}
		public void receiveMessage(Message msg) {
			if (IWRTConstants.ERROR.equals(msg.getSeverity())) {
				validateSucess = false;
			}
			MarkerUtil.addMarker(msg, projectvalidating);
		}
	}
	
	public class StatusListener implements IWRTStatusListener {
		private String statusSource;

		public String getStatusSource() {
			return statusSource;
		}

		public void setStatusSource(String statusSource) {
			this.statusSource = statusSource;
		}

		public boolean isStatusHandled(WRTStatus status) {
//
//			if (status.getStatusSource().equals(statusSource)) {
//				return true;
//			}
			return false;
		}

		public void emitStatus(WRTStatus status) {
			// TODO Auto-generated method stub
			
		}

//		public void emitStatus(final WRTStatus status) {
//			parentShell.getDisplay().asyncExec(new Runnable(){
//				public void run() {					
//					setStatusText(status.getStatusDescription().toString());
//				}
//			});
//		}
	}
//	-------------------------------inner class-------------------------------------//
	
}

