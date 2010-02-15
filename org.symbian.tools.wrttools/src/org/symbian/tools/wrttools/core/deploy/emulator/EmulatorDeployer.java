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

package org.symbian.tools.wrttools.core.deploy.emulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.symbian.tools.wrttools.core.deployer.DeployException;
import org.symbian.tools.wrttools.core.deployer.DeployerMessages;
import org.symbian.tools.wrttools.core.deployer.WidgetDeployer;
import org.symbian.tools.wrttools.core.status.IWRTConstants;

/**
 * The class needed for the deployment of the widget to emulator.
 * @author avraina
 */
public class EmulatorDeployer extends WidgetDeployer {
	
	private boolean isDeploySuccessful = false;

	public IStatus deploy(String fileName, String des, IProgressMonitor monitor) throws DeployException{				
		File inputFile = new File(fileName);
		File outputFile = new File(des);
		emitStatus(DeployerMessages.getString("Deployer.begin.msg")); //$NON-NLS-1$
		try {
			if(!outputFile.isDirectory()){
				outputFile.mkdir();
			}
		} catch (Exception e) {
			emitStatus(DeployerMessages.getString("Deployer.failed.err.msg")); //$NON-NLS-1$
			throw new DeployException(e);
		}
		
		// If the archive is directly deployed than directly deploy it
		// else deploy from the folder path.
		if(fileName.toLowerCase().endsWith(IWRTConstants.WIDGET_FILE_EXTENSION)){
			File out = new File(outputFile + "/" + inputFile.getName()); //$NON-NLS-1$
			deployWidget(inputFile, out);			
		} 
		if(isDeploySuccessful){				
			emitStatus(DeployerMessages.getString("Deployer.ends.msg")); //$NON-NLS-1$
		} else {
			emitStatus(DeployerMessages.getString("Deployer.failed.err.msg")); //$NON-NLS-1$
		}
		return Status.OK_STATUS;
	}
	
	// helper methods 
	
	/**
	 * Deploys the widget from the source to the destination path of the emulator.
	 * @param inputFile the actual widget path from where widget needs to be deployed.
	 * @param outputFile the path of the emulator where the widget will be deoplyed.
	 * @throws DeployException throw a DeployException if anything goes wrong.
	 */
	private void deployWidget(File inputFile, File outputFile) throws DeployException{
		try {
			String message = MessageFormat.format(DeployerMessages.getString("Deployer.inputfile.msg"), new Object[]{inputFile});//$NON-NLS-1$
			emitStatus(message);
			
			InputStream in = new FileInputStream(inputFile);
			OutputStream out  = new FileOutputStream(outputFile);
			int c;

			while ((c = in.read()) != -1)
				out.write(c);
			in.close();
			out.close();
			
			message = MessageFormat.format(DeployerMessages.getString("Deployer.outputfile.msg"), new Object[]{outputFile});//$NON-NLS-1$
			emitStatus(message);
			isDeploySuccessful= true;
		} catch (Exception e) {
			emitStatus(e.getMessage());
			isDeploySuccessful=false;
			emitStatus(DeployerMessages.getString("Deployer.failed.err.msg")); //$NON-NLS-1$
			throw new DeployException(e);
		}
	}
}
