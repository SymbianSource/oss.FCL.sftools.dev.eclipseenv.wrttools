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

package org.symbian.tools.wrttools.core.deploy.device;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;

import javax.bluetooth.BluetoothConnectionException;
import javax.microedition.io.Connector;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.ResponseCodes;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.core.deployer.DeployerMessages;
import org.symbian.tools.wrttools.core.deployer.IWidgetDeployerConstants;
import org.symbian.tools.wrttools.core.deployer.WidgetDeployer;
import org.symbian.tools.wrttools.core.status.IWRTConstants;

/**
 * The class needed for the deployment of the widget to device using bluetooth.
 * 
 * @author avraina
 */
public class DeviceDeployer extends WidgetDeployer {

	public IStatus deploy(String fileName, String device, IProgressMonitor progressMonitor) {
		File inputFile = new File(fileName);
		IStatus result = null;
		
		// If the archive is directly deployed than directly deploy it
		// else deploy from the folder path.
		if (fileName.toLowerCase()
				.endsWith(IWRTConstants.WIDGET_FILE_EXTENSION)) {
			result = deployWidget(inputFile, device, progressMonitor);
		}
		if (result.isOK()) {
			emitStatus(DeployerMessages.getString("Deployer.ends.msg"));//$NON-NLS-1$
			
		} else if (result.getCode() == IStatus.CANCEL){
			emitStatus(DeployerMessages.getString("Deployer.cancelled.msg"));
		} 
		return result;
	}

	/**
	 * Deploys the widget using the bluetooth.
	 * 
	 * @param inputWidget
	 *            the input widget which has to be deployed.
	 * @param device
	 *            the device to which the widget is deployed
	 * @param progressMonitor 
	 * @throws Exception
	 *             if the exception is throws
	 */
	private IStatus deployWidget(File inputWidget, String device, IProgressMonitor progressMonitor) {
		InputStream in = null;
		OutputStream os = null;
		Operation putOperation = null;
		ClientSession clientSession = null;
		IStatus result = null;
		try {
			if (!DeviceListProvider.isBloothToothConnected()) {
				String msg = DeployerMessages.getString("Deployer.bluetooth.notconnected.msg");
				emitStatus(msg);
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, msg, null);
			}

			String message = MessageFormat
					.format(
							DeployerMessages
									.getString("Deployer.searchservice.msg"), new Object[] { device });//$NON-NLS-1$
			emitStatus(message);
			String servicesFound = ServicesProvider.getServicesFound(device);
			if (servicesFound == null || servicesFound.length() < 1) {
				message = MessageFormat
						.format(
								DeployerMessages
										.getString("Deployer.servicenotfound.err.msg"), new Object[] { device });//$NON-NLS-1$
				emitStatus(message);

				message = MessageFormat
						.format(
								DeployerMessages
										.getString("Deployer.cannotdeploy.err.msg"), new Object[] { device });//$NON-NLS-1$
				emitStatus(message);
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, message, null);
			}

			message = MessageFormat
					.format(
							DeployerMessages
									.getString("Deployer.servicefound.msg"), new Object[] { device });//$NON-NLS-1$
			emitStatus(message);

			clientSession = (ClientSession) Connector.open(servicesFound);
			HeaderSet hsConnectReply = clientSession.connect(null);
			if (hsConnectReply.getResponseCode() != ResponseCodes.OBEX_HTTP_OK) {
				emitStatus(DeployerMessages
						.getString("Deployer.services.connect.err.msg"));//$NON-NLS-1$
			}

			emitStatus(DeployerMessages.getString("Deployer.begin.msg"));

			HeaderSet hsOperation = clientSession.createHeaderSet();
			
			if (progressMonitor.isCanceled()) {
				return new Status(IStatus.CANCEL, Activator.PLUGIN_ID, 0, "Deployment was canceled", null);
			}

			// Send widget to server
			in = new FileInputStream(inputWidget);
			message = MessageFormat
					.format(
							DeployerMessages
									.getString("Deployer.inputfile.msg"), new Object[] { inputWidget.getAbsolutePath() });//$NON-NLS-1$
			emitStatus(message);

			hsOperation.setHeader(HeaderSet.NAME, inputWidget.getName());
			hsOperation.setHeader(HeaderSet.TYPE,
					IWidgetDeployerConstants.WIDGET_FILE_TYPE);
			int size = (int)inputWidget.length();
			byte file[] = new byte[size];
			hsOperation.setHeader(HeaderSet.LENGTH, new Long(file.length));			

			// Create PUT Operation
			putOperation = clientSession.put(hsOperation);

			os = putOperation.openOutputStream();

			long start = System.currentTimeMillis();
			
            byte[] buf = new byte[16*1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                os.write(buf, 0, len);
    			if (progressMonitor.isCanceled()) {
    				putOperation.abort();
    				return new Status(IStatus.CANCEL, Activator.PLUGIN_ID, 0, "Deployment was canceled", null);
    			}
            }

            os.flush();
            os.close();			

            long elapsed = System.currentTimeMillis() - start;
            emitStatus("elapsed time: " + (double)elapsed/1000.0 + " seconds");
            
            int responseCode = putOperation.getResponseCode();
            if (responseCode == ResponseCodes.OBEX_HTTP_OK) {
            	message = MessageFormat.format(
            			DeployerMessages.getString("Deployer.outputfile.msg"), new Object[] { device });//$NON-NLS-1$
            	emitStatus(message);
            	result = Status.OK_STATUS;
            } else {
            	message = "Error during deployment, OBEX error: " + responseCode;
            	emitStatus(message);
            	result = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, message, null);
            }
            
		} catch (BluetoothConnectionException x) {
			String message = getExceptionMessage(x.getMessage());
			emitStatus(message);
		}
		catch (IOException e) {
			String message = getExceptionMessage(e.getMessage());
			emitStatus(message);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (putOperation != null) {
					putOperation.close();
				}
				if (clientSession != null) {
					clientSession.disconnect(null);
					clientSession.close();
				}
			} catch (IOException x) {
				Activator.log(IStatus.ERROR,
						"Error cleaning up BlueTooth deployment", x);
			}
		}
		if (result == null && clientSession == null) {
			String message = "An error occurred initiating a connection to the device. It may have been rejected by the user.";
			result = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, message, null);
		}
		return result;
	}
}
