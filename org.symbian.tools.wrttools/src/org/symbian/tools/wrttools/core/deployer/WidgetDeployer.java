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

import org.symbian.tools.wrttools.core.status.IWRTConstants;
import org.symbian.tools.wrttools.core.status.IWRTStatusListener;
import org.symbian.tools.wrttools.core.status.WRTStatus;
import org.symbian.tools.wrttools.core.status.WRTStatusHandler;

/**
 * Main Class for the widget Deployer.This implements the IWidgetDeployer. 
 * Different Deployer need to extend this class.This also contains the default
 * implementation for the actual widget deployer such as making a sure the widget
 * is validated before deploying and also if the widget is packaged correctly before
 * deploying.
 * @author avraina
 *
 */
public abstract class WidgetDeployer implements IWidgetDeployer{
	
	private WRTStatusHandler statusHandler;
	
	IWRTStatusListener statusListener;
	
	public WidgetDeployer() {
		setStatus();
	}

	/**
	 * The method is to check whether the widget project is validated.
	 * This should be done before calling the deploy method.
	 * @param fileName. The file name which needs to be validated.
	 * @return true if the project is validated else false.
	 */
	public boolean callValidator(String fileName){
		return false;
	}
	
	/**
	 * The method is to check whether the widget project is packaged.
	 * This should be done before calling the deploy method.
	 * @param fileName. The file name which need to be deployed.
	 * @return true if the project is packaged else false.
	 */
	public boolean callPackager(String fileName){
		return false;
	}
	
	/**
	 * The method returns the extension of the widget to be deployed.
	 * @return the extension of the widget.
	 */
	public String checkPackagedInput(){		
		return IWRTConstants.WIDGET_FILE_EXTENSION;	
	}
		
	protected void setStatus() {
		statusHandler = new WRTStatusHandler();
		statusHandler.addListener(getStatusListener());
	}

	/**
	 * Returns the associated status listner with the widget deployer
	 * @return status listener
	 */
	public IWRTStatusListener getStatusListener() {
		return statusListener;
	}

	/**
	 * Sets the status listner associated with the widget deployer
	 * @param statusListener the status listner to be associated.
	 */
	public void setStatusListener(IWRTStatusListener statusListener) {
		this.statusListener = statusListener;
	}
	
	/**
	 * Creates the status specific to the widget deployer
	 * @param statusDescription the description of the status
	 * @return the WRTStatus created
	 */
	protected void emitStatus(String statusDescription) {
		WRTStatus status = new WRTStatus();
		status.setStatusSource(IWRTConstants.StatusSourceType.DEPLOYER.name());
		status.setStatusDescription(statusDescription);		
		getStatusListener().emitStatus(status);
	}

    public boolean needsReport() {
        return true;
    }
}