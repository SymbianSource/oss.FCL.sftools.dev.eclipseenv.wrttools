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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;

import org.symbian.tools.wrttools.core.deploy.device.DeviceListProvider;


/**
 * Progress Runnable Dialog for the searching of the devices.
 * Provides the functionality to cancel the operation.
 * @author avraina
 *
 */
public class ProgressRunnableDialog extends ProgressMonitorDialog {

	private IProgressMonitor progressMonitor;
	
	/**
	 * Constructor.
	 * @param parent
	 */
	public ProgressRunnableDialog(Shell parent) {
		super(parent);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.ProgressMonitorDialog#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell shell) {	
		super.configureShell(shell);
		//need to uncomment
		shell.setText(DeployMessages.getString("wrt.core.Deployer.searchdevice.dialog.title")); //$NON-NLS-1$
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.ProgressMonitorDialog#getProgressMonitor()
	 */
	public IProgressMonitor getProgressMonitor() {
		progressMonitor = new IProgressMonitor(){

			/* (non-Javadoc)
			 * @see org.eclipse.core.runtime.IProgressMonitor#beginTask(java.lang.String, int)
			 */
			public void beginTask(String name, int totalWork) {
				progressMonitor.beginTask(name, totalWork);
			}

			/* (non-Javadoc)
			 * @see org.eclipse.core.runtime.IProgressMonitor#done()
			 */
			public void done() {
				progressMonitor.done();
			}

			/* (non-Javadoc)
			 * @see org.eclipse.core.runtime.IProgressMonitor#internalWorked(double)
			 */
			public void internalWorked(double work) {
				progressMonitor.internalWorked(work);
			}

			/* (non-Javadoc)
			 * @see org.eclipse.core.runtime.IProgressMonitor#isCanceled()
			 */
			public boolean isCanceled() {
				return progressMonitor.isCanceled();
			}

			/* (non-Javadoc)
			 * @see org.eclipse.core.runtime.IProgressMonitor#setCanceled(boolean)
			 */
			public void setCanceled(boolean value) {
				if(value){
					DeviceListProvider.cancelSearch();
				} 				
			}

			/* (non-Javadoc)
			 * @see org.eclipse.core.runtime.IProgressMonitor#setTaskName(java.lang.String)
			 */
			public void setTaskName(String name) {
				progressMonitor.setTaskName(name);
			}

			/* (non-Javadoc)
			 * @see org.eclipse.core.runtime.IProgressMonitor#subTask(java.lang.String)
			 */
			public void subTask(String name) {
				progressMonitor.subTask(name);
			}

			/* (non-Javadoc)
			 * @see org.eclipse.core.runtime.IProgressMonitor#worked(int)
			 */
			public void worked(int work) {		
				progressMonitor.worked(work);
			}			
		};			
		return progressMonitor;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.ProgressMonitorDialog#cancelPressed()
	 */
	protected void cancelPressed() {
		progressMonitor.setCanceled(true);
		super.cancelPressed();
	}
}
