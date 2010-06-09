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

package org.symbian.tools.wrttools.core.deployer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.symbian.tools.wrttools.core.status.IWRTStatusListener;

/**
 * The main interface for the Widget Project Deployment.The deployer will deploy the
 * widget to the different devices which can be an emulator, server etc. 
 * @author avraina
 *
 */
public interface IWidgetDeployer {

	/**
	 * The method will deploy the widget to the target.
	 * All the deployer tools must implement this method. Each deployer which 
	 * @param inputPath the widget path from where the widget needs to be deployed.
	 * @param destinationPath the destination path to which widget will be deployed. 
	 * This can be an emulator , server or any other device
	 * @return integer IStatus code. IStatus.OK for success, IStatus.CANCEL if the user canceled, IStatus.ERROR if an 
	 * error was caught and reported to the status listener.
	 * @throws DeployException throws a Deploy Exception if anything goes wrong while
	 * deployment is going on.
	 */
	public IStatus deploy(String inputPath, String destinationPath, 
						IProgressMonitor progressMonitor) throws DeployException;

	/**
	 * Sets the status listner associated with the widget deployer
	 * @param statusListener the status listner to be associated.
	 */
	public void setStatusListener(IWRTStatusListener statusListener);

    public boolean needsReport();

}
