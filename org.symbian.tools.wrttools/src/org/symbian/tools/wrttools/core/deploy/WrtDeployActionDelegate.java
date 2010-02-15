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

package org.symbian.tools.wrttools.core.deploy;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;

import org.symbian.tools.wrttools.sdt.utils.Logging;
import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.WRTStatusListener;
import org.symbian.tools.wrttools.core.deployer.DeployException;
import org.symbian.tools.wrttools.core.deployer.DeployerMessages;
import org.symbian.tools.wrttools.core.deployer.IWidgetDeployer;
import org.symbian.tools.wrttools.core.deployer.WidgetDeployerFactory;
import org.symbian.tools.wrttools.core.packager.WrtPackageActionDelegate;

public class WrtDeployActionDelegate extends ActionDelegate implements IObjectActionDelegate {

	private Shell shell;
	private static Job job;
	
	public void run(IAction action) {
		PlatformUI.getWorkbench().saveAllEditors(true);
		deploy();
	}
	
	/**
	 * deploys the actual widget.
	 */
	@SuppressWarnings("restriction")
	private void deploy() {
		String destinationPath = null;
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String deployerType = store.getString(PreferenceConstants.WRT_DEPLOY_CHOICE);
		
		if (deployerType.equalsIgnoreCase(PreferenceConstants.WRT_DEPLOY_CHOICE_EMULATOR)) {
			destinationPath = store.getString(PreferenceConstants.SELECTED_EMULATOR_PATH);
		} else if (deployerType.equalsIgnoreCase(PreferenceConstants.WRT_DEPLOY_CHOICE_DEVICE)) {
			destinationPath = store.getString(PreferenceConstants.SELECTED_DEVICE_NAME);
		}

		if (destinationPath != null
				&& destinationPath.length() > 0
				&& !destinationPath.equalsIgnoreCase(DeployMessages.getString("View.none.text"))) {
				job = new DeployJob("Deploying widget", destinationPath, deployerType);
				job.setUser(true);
				job.schedule();
				job.addJobChangeListener(new IJobChangeListener() {
					public void sleeping(IJobChangeEvent event) {}
					public void awake(IJobChangeEvent event) {
					}
					public void aboutToRun(IJobChangeEvent event) {
					}
					public void scheduled(IJobChangeEvent event) {
					}
					public void running(IJobChangeEvent event) {
					}
					public void done(IJobChangeEvent event) {
						/*synchronized (job) {
						job.notify();}*/
					}
				});		
			
		} else {
			MessageDialog.openInformation(shell, "Deploy error",
					" Invalid deployment preferences" + " dest path: " + destinationPath + "deployerType:" + deployerType + " store:" + store);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			DeployJob.projectList.clear();
			DeployJob.wgzList.clear();
			IStructuredSelection ss = (IStructuredSelection) selection;
			for (Iterator iter = ss.iterator(); iter.hasNext();) {
				Object obj = iter.next();
				if (obj instanceof IProject) {
					// multi project selection enabled
					DeployJob.projectList.add((IProject) obj);
				}
				if (obj instanceof IFile) {
					// multi wgz selection enabled
					IFile wgzFile = (IFile) obj;
					if(wgzFile.getName().endsWith("wgz")){
						DeployJob.wgzList.add((IFile) obj);}
				}
			}
		}
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}
	
	private static final class DeployJob extends Job {
		//private String widgetPackagedPath;
		private final String destination;
		private final String deployType;
		
		public static List<IProject> projectList = new ArrayList<IProject>();
		public static List<IFile> wgzList = new ArrayList<IFile>();
		
		private WrtPackageActionDelegate packagerAction = new WrtPackageActionDelegate();

		private DeployJob(String name, String deployDestination, String deployType) {
			super(name);
			//this.widgetPackagedPath = widgetPackagedPath;
			this.destination = deployDestination;
			this.deployType = deployType;
		}

		public IStatus run(IProgressMonitor monitor) {
			WRTStatusListener statusListener = new WRTStatusListener();
			IWidgetDeployer wd = WidgetDeployerFactory.createWidgetDeployer(deployType, statusListener);	
			IStatus result = new Status(IStatus.OK, Activator.PLUGIN_ID, 0, "", null);
			
			if (projectList != null && projectList.size() > 0) {
				for (IProject project : projectList) {				
					if (project != null) {
						/* package the files before deployment */
						boolean packageSuccess = packagerAction.packageProject(project);
						if (!packageSuccess) {
							continue;
						}
						String packagedPath;
						try {
							IPath wgzPath = new Path(project.getName() + ".wgz");
							IFile wgz = project.getFile(wgzPath);
							packagedPath = wgz.getLocation().toFile().getCanonicalFile().toString();
							try {
								result = wd.deploy(packagedPath, destination, monitor);
								if (result.isOK()) {
									job.setName("Deployment Completed");			
								} else if (result.getCode() == IStatus.CANCEL){
									job.setName("Deployment Cancelled");
								} 
							} catch (DeployException e) {
								result = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, e.getMessage(), e);
								Logging.log(Activator.getDefault(), result);
							}
						} catch (IOException e) {
							Activator.log(IStatus.ERROR, "Error deploying widget", e);
						}
					}
					}
				}
			if (wgzList != null && wgzList.size() > 0) {	
				for (IFile wgzFile : wgzList) {		
						File wgzF = new File(wgzFile.getLocation().toString());
						if(!wgzF.exists()){
							if (wgzList != null && wgzList.size() > 0) {
								IProject project = wgzFile.getProject();
									if (project != null) {
										boolean packageSuccess=packagerAction.packageProject(project);// check & handle if the file is deleted outside aptana
										if(!packageSuccess)
										{
								    	   continue;
										}
									}
								}
							}
							if (wgzFile.exists()) {						
								String packagedPath;
								try {
									packagedPath = wgzFile.getLocation().toFile().getCanonicalFile().toString();
									
									try {
										result = wd.deploy(packagedPath, destination, monitor);
										if (result.isOK()) {
											job.setName("Deployment Completed");
										} else if (result.getCode() == IStatus.CANCEL){
										    job.setName("Deployment Cancelled");
										} 
									} catch (DeployException e) {
										result = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, e.getMessage(), e);
										Logging.log(Activator.getDefault(), result);
									} 
								} catch (IOException e) {
									Activator.log(IStatus.ERROR, "Error deploying widget", e);
								}
							}
			  }
				}	
			statusListener.close();
			return result;
		}
	}
}
