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
	
	protected String[] exceptionCodes  = new String[] {"OBEX_HTTP_UNSUPPORTED_TYPE", "OBEX_HTTP_FORBIDDEN"}; //$NON-NLS-1$ //$NON-NLS-2$
	
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
	
	/**
	 * Returns the customized methods from the exception error code. If it
	 * matches it returns the customized message else returns the exception itself
	 * @param message exception message
	 * @return the customized message
	 */
	protected String getExceptionMessage(String message) {
		
		if(message.contains(exceptionCodes[0])){
			return DeployerMessages.getString("Deployer.device.notsupport.err.msg"); //$NON-NLS-1$
		} else if (message.contains(exceptionCodes[1])){
			return DeployerMessages.getString("Deployer.device.rejected.err.msg"); //$NON-NLS-1$
		}		
		return message;
	}
}