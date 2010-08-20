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
package org.symbian.tools.tmw.ui.deployment.bluetooth;

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
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.ResponseCodes;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.ui.IMemento;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.projects.ITMWProject;
import org.symbian.tools.tmw.core.runtimes.IPackager;
import org.symbian.tools.tmw.ui.deployment.IDeploymentTarget;

public class BluetoothTarget extends PlatformObject implements IDeploymentTarget {
    private static final UUID OBEX_OBJECT_PUSH = new UUID(0x1105);
    private String serviceURL;
    private RemoteDevice device;
    protected String[] exceptionCodes = new String[] { "OBEX_HTTP_UNSUPPORTED_TYPE", "OBEX_HTTP_FORBIDDEN" };
    private String message = "Deployment was successful. Please follow on-screen instructions to complete application deployment on your device.";
    private final String name;
    private final BluetoothTargetType provider;
    private final Collection<IStatus> statuses = new LinkedList<IStatus>();

    public BluetoothTarget(String name, RemoteDevice device, BluetoothTargetType provider) {
        this.name = name;
        this.device = device;
        this.provider = provider;
    }

    public IStatus deploy(ITMWProject project, IPackager packager, IProgressMonitor monitor) throws CoreException {
        message = "Deployment was successful. Please follow on-screen instructions to complete application deployment on your device.";
        statuses.clear();
        monitor.beginTask(String.format("Deploying application %s to %s", project.getName(), name),
                IProgressMonitor.UNKNOWN);
        if (packager == null) {
            return new Status(IStatus.ERROR, TMWCore.PLUGIN_ID, String.format(
                    "No packager found for project %s with runtime %s", project.getName(), project.getTargetRuntime()));
        }
        final File application = packager.packageApplication(project, new SubProgressMonitor(monitor, 100));
        try {
            deployWidget(application, packager.getFileType(project), new SubProgressMonitor(monitor, 10));
        } finally {
            application.delete();
        }
        monitor.done();
        MultiStatus multiStatus = new MultiStatus(TMWCore.PLUGIN_ID, 0, message, null);
        for (IStatus status : statuses) {
            multiStatus.add(status);
        }
        return multiStatus;
    }

    private void deployWidget(File inputWidget, String fileType, IProgressMonitor progressMonitor) throws CoreException {
        if (device == null) {
            provider.discoverTargets(new SubProgressMonitor(progressMonitor, 10));
            if (device == null) {
                throw new CoreException(new Status(IStatus.ERROR, TMWCore.PLUGIN_ID, String.format(
                        "Device %s is not available", name)));
            }
        }
        progressMonitor.beginTask("Deploying application", IProgressMonitor.UNKNOWN);
        InputStream in = null;
        OutputStream os = null;
        Operation putOperation = null;
        ClientSession clientSession = null;
        try {
            if (!provider.isBloothToothConnected()) {
                String msg = "Bluetooth is either disabled or not present in the system.";
                emitStatus(IStatus.ERROR, msg, progressMonitor);
                return;
            }

            String message = MessageFormat.format("Searching for the service for the selected device \"{0}\"",
                    new Object[] { getName() });
            emitStatus(IStatus.OK, message, progressMonitor);
            String servicesFound = getServicesFound();
            if (servicesFound == null || servicesFound.length() < 1) {
                message = MessageFormat.format("Cannot find service to the device \"{0}\"", new Object[] { getName() });
                emitStatus(IStatus.ERROR, message, progressMonitor);
                return;
            }

            message = MessageFormat.format("Service for the device \"{0}\" found", new Object[] { getName() });
            emitStatus(IStatus.OK, message, progressMonitor);

            clientSession = (ClientSession) Connector.open(servicesFound);
            HeaderSet hsConnectReply = clientSession.connect(null);
            if (hsConnectReply.getResponseCode() != ResponseCodes.OBEX_HTTP_OK) {
                emitStatus(IStatus.OK, "Failed to connect to the service", progressMonitor);
            }

            emitStatus(IStatus.OK, "Deployment Started", progressMonitor);

            HeaderSet hsOperation = clientSession.createHeaderSet();

            if (progressMonitor.isCanceled()) {
                emitStatus(IStatus.CANCEL, "Deployment was canceled", progressMonitor);
                return;
            }

            // Send widget to server
            in = new FileInputStream(inputWidget);
            message = MessageFormat.format("Deploying file from {0}", new Object[] { inputWidget.getAbsolutePath() });
            emitStatus(IStatus.OK, message, progressMonitor);

            hsOperation.setHeader(HeaderSet.NAME, inputWidget.getName());
            hsOperation.setHeader(HeaderSet.TYPE, fileType);
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
                message = MessageFormat.format("File deployed to {0}", new Object[] { getName() });
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
                TMWCore.log("Error cleaning up BlueTooth deployment", x);
            }
        }
        return;
    }

    protected void emitStatus(int severity, String statusDescription, IProgressMonitor monitor) {
        statuses.add(new Status(severity, TMWCore.PLUGIN_ID, statusDescription));
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
            return "Device does not support the widget deployment";
        } else if (message.contains(exceptionCodes[1])) {
            return "Deployment rejected by the device";
        }
        return message;
    }

    public String getId() {
        return getName();
    }

    public String getName() {
        return name;
    }

    private String getServicesFound() {
        try {
            serviceURL = "";
            UUID serviceUUID = OBEX_OBJECT_PUSH;

            final Object serviceSearchCompletedEvent = new Object();

            DiscoveryListener listener = new DiscoveryListener() {

                public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
                }

                public void inquiryCompleted(int discType) {
                }

                public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
                    try {
                        for (int i = 0; i < servRecord.length; i++) {
                            if (servRecord[i].getHostDevice().getFriendlyName(false).equals(name)) {
                                serviceURL = servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT,
                                        false);
                            }
                        }
                    } catch (BluetoothStateException e) {
                        TMWCore.log(e.getMessage(), e);
                    } catch (IOException e) {
                        TMWCore.log(e.getMessage(), e);
                    }
                }

                public void serviceSearchCompleted(int transID, int respCode) {
                    synchronized (serviceSearchCompletedEvent) {
                        serviceSearchCompletedEvent.notifyAll();
                    }
                }

            };

            UUID[] searchUuidSet = new UUID[] { serviceUUID };
            int[] attrIDs = new int[] { 0x0100 }; // Service name

            synchronized (serviceSearchCompletedEvent) {
                LocalDevice.getLocalDevice().getDiscoveryAgent()
                        .searchServices(attrIDs, searchUuidSet, device, listener);
                serviceSearchCompletedEvent.wait();
            }

        } catch (IOException e) {
            TMWCore.log("Error in Bluetooth service discovery", e);
        } catch (InterruptedException e) {
            TMWCore.log("Error in Bluetooth service discovery", e);
        }
        return serviceURL;
    }

    public void init(ITMWProject project, IPackager packager, IMemento memento) {
        // nothing
    }

    public void save(IMemento memento) {
        // nothing
    }

    public void setAddress(RemoteDevice device) {
        this.device = device;
    }

    public boolean isDiscovered() {
        return device != null;
    }

    public String getDescription() {
        return device == null ? "This device was remembered from past sessions and may not be available" : String
                .format("Remote device with address %s", device.getBluetoothAddress());
    }
}
