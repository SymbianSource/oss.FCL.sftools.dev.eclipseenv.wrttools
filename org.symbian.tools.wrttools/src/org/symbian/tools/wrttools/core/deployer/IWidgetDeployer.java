/* ============================================================================
 * Copyright © 2008 Nokia. All rights reserved.
 * This material, including documentation and any related computer
 * programs, is protected by copyright controlled by Nokia. All
 * rights are reserved. Copying, including reproducing, storing,
 * adapting or translating, any or all of this material requires the
 * prior written consent of Nokia. This material also contains
 * confidential information which may not be disclosed to others
 * without the prior written consent of Nokia.
 * ============================================================================*/

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

}
