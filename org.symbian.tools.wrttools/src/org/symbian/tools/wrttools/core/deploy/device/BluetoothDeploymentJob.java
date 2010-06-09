/**
 * Copyright (c) 2010 Symbian Foundation and/or its subsidiary(-ies).
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

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.LinkedList;

import javax.bluetooth.BluetoothConnectionException;
import javax.microedition.io.Connector;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.ResponseCodes;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.statushandlers.StatusManager;
import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.core.deploy.BluetoothRule;
import org.symbian.tools.wrttools.core.deployer.DeployerMessages;
import org.symbian.tools.wrttools.core.deployer.IWidgetDeployerConstants;

public class BluetoothDeploymentJob extends Job {
    protected String[] exceptionCodes = new String[] { "OBEX_HTTP_UNSUPPORTED_TYPE", "OBEX_HTTP_FORBIDDEN" }; //$NON-NLS-1$ //$NON-NLS-2$

    private final Collection<IStatus> statuses = new LinkedList<IStatus>();
    private String message = "Deployment was successful";
    private final File inputWidget;
    private final String device;

    public BluetoothDeploymentJob(final File inputWidget, final String device) {
        super(String.format("Deploy %s to %s", inputWidget.getName(), device));
        this.device = device;
        this.inputWidget = inputWidget;
        setRule(BluetoothRule.INSTANCE);
        setUser(true);
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        deployWidget(inputWidget, device, monitor);
        MultiStatus multiStatus = new MultiStatus(Activator.PLUGIN_ID, 0, statuses.toArray(new IStatus[0]), message, null);
        if (multiStatus.getSeverity() != IStatus.ERROR && multiStatus.getSeverity() != IStatus.WARNING) {
            StatusManager.getManager().handle(multiStatus, StatusManager.SHOW);
        }
        return multiStatus;
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
    private void deployWidget(File inputWidget, String device, IProgressMonitor progressMonitor) {
        progressMonitor.beginTask("Deploying application", IProgressMonitor.UNKNOWN);
        InputStream in = null;
        OutputStream os = null;
        Operation putOperation = null;
        ClientSession clientSession = null;
        try {
            if (!DeviceListProvider.isBloothToothConnected()) {
                String msg = DeployerMessages.getString("Deployer.bluetooth.notconnected.msg");
                emitStatus(IStatus.ERROR, msg, progressMonitor);
                return;
            }

            String message = MessageFormat.format(
                    DeployerMessages.getString("Deployer.searchservice.msg"), new Object[] { device });//$NON-NLS-1$
            emitStatus(IStatus.OK, message, progressMonitor);
            String servicesFound = ServicesProvider.getServicesFound(device);
            if (servicesFound == null || servicesFound.length() < 1) {
                message = MessageFormat.format(
                        DeployerMessages.getString("Deployer.servicenotfound.err.msg"), new Object[] { device });//$NON-NLS-1$
                emitStatus(IStatus.ERROR, message, progressMonitor);
                return;
            }

            message = MessageFormat.format(
                    DeployerMessages.getString("Deployer.servicefound.msg"), new Object[] { device });//$NON-NLS-1$
            emitStatus(IStatus.OK, message, progressMonitor);

            clientSession = (ClientSession) Connector.open(servicesFound);
            HeaderSet hsConnectReply = clientSession.connect(null);
            if (hsConnectReply.getResponseCode() != ResponseCodes.OBEX_HTTP_OK) {
                emitStatus(IStatus.OK, DeployerMessages.getString("Deployer.services.connect.err.msg"), progressMonitor);//$NON-NLS-1$
            }

            emitStatus(IStatus.OK, DeployerMessages.getString("Deployer.begin.msg"), progressMonitor);

            HeaderSet hsOperation = clientSession.createHeaderSet();

            if (progressMonitor.isCanceled()) {
                emitStatus(IStatus.CANCEL, "Deployment was canceled", progressMonitor);
                return;
            }

            // Send widget to server
            in = new FileInputStream(inputWidget);
            message = MessageFormat
                    .format(DeployerMessages.getString("Deployer.inputfile.msg"), new Object[] { inputWidget.getAbsolutePath() });//$NON-NLS-1$
            emitStatus(IStatus.OK, message, progressMonitor);

            hsOperation.setHeader(HeaderSet.NAME, inputWidget.getName());
            hsOperation.setHeader(HeaderSet.TYPE, IWidgetDeployerConstants.WIDGET_FILE_TYPE);
            int size = (int) inputWidget.length();
            byte file[] = new byte[size];
            hsOperation.setHeader(HeaderSet.LENGTH, new Long(file.length));

            // Create PUT Operation
            putOperation = clientSession.put(hsOperation);

            os = putOperation.openOutputStream();

            long start = System.currentTimeMillis();

            byte[] buf = new byte[16 * 1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                os.write(buf, 0, len);
                if (progressMonitor.isCanceled()) {
                    putOperation.abort();
                    emitStatus(IStatus.CANCEL, "Deployment was canceled", progressMonitor);
                    return;
                }
            }

            os.flush();
            os.close();

            long elapsed = System.currentTimeMillis() - start;
            emitStatus(IStatus.OK, "elapsed time: " + elapsed / 1000.0 + " seconds", progressMonitor);

            int responseCode = putOperation.getResponseCode();
            if (responseCode == ResponseCodes.OBEX_HTTP_OK) {
                message = MessageFormat.format(
                        DeployerMessages.getString("Deployer.outputfile.msg"), new Object[] { device });//$NON-NLS-1$
                emitStatus(IStatus.OK, message, progressMonitor);
            } else {
                message = "Error during deployment, OBEX error: " + responseCode;
                emitStatus(IStatus.ERROR, message, progressMonitor);
            }

        } catch (BluetoothConnectionException x) {
            String message = getExceptionMessage(x.getMessage());
            emitStatus(IStatus.ERROR, message, progressMonitor);
        } catch (IOException e) {
            String message = getExceptionMessage(e.getMessage());
            emitStatus(IStatus.ERROR, message, progressMonitor);
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
            } catch (EOFException eof) {
                // EOFException is now caught 
                // Ignore the error since deployment has already completed
                //Activator.log(IStatus.ERROR, "EOF encountered while cleaning up Bluetooth deployment", eof);
            } catch (IOException x) {
                Activator.log(IStatus.ERROR, "Error cleaning up BlueTooth deployment", x);
            }
        }
        return;
    }

    /**
     * Creates the status specific to the widget deployer
     * @param statusDescription the description of the status
     * @param monitor TODO
     * @return the WRTStatus created
     */
    protected void emitStatus(int severity, String statusDescription, IProgressMonitor monitor) {
        statuses.add(new Status(severity, Activator.PLUGIN_ID, statusDescription));
        monitor.setTaskName(statusDescription);
        if (severity != IStatus.OK) {
            message = statusDescription;
        }
    }

    /**
     * Returns the customized methods from the exception error code. If it
     * matches it returns the customized message else returns the exception itself
     * @param message exception message
     * @return the customized message
     */
    protected String getExceptionMessage(String message) {

        if (message.contains(exceptionCodes[0])) {
            return DeployerMessages.getString("Deployer.device.notsupport.err.msg"); //$NON-NLS-1$
        } else if (message.contains(exceptionCodes[1])) {
            return DeployerMessages.getString("Deployer.device.rejected.err.msg"); //$NON-NLS-1$
        }
        return message;
    }
}
