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

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.symbian.tools.tmw.core.projects.IMTWProject;
import org.symbian.tools.tmw.ui.ConsoleFactory;
import org.symbian.tools.tmw.ui.TMWCoreUI;
import org.symbian.tools.tmw.ui.deployment.IDeploymentTarget;
import org.symbian.tools.tmw.ui.deployment.IDeploymentTargetType;

import com.intel.bluetooth.BlueCoveImpl;

/**
 * Discovers Bluetooth-enabled devices. This code is generic and will not 
 * perform any checks if the discovered device can run application being 
 * deployed.
 * 
 * @author Eugene Ostroukhov (eugeneo@symbian.org)
 */
public class BluetoothTargetType implements IDeploymentTargetType {
    private final class TargetDiscoveryListener implements DiscoveryListener {
        final Object inquiryCompletedEvent;
        boolean isCanceled;
        final Map<String, BluetoothTarget> prevTargets;
        final IProgressMonitor progressMonitor;

        private TargetDiscoveryListener(Map<String, BluetoothTarget> previousTargets, Object inquiryCompletedEvent,
                IProgressMonitor progressMonitor) {
            this.prevTargets = previousTargets;
            this.inquiryCompletedEvent = inquiryCompletedEvent;
            this.progressMonitor = progressMonitor;
        }

        private void checkCanceled() {
            if (!isCanceled && progressMonitor != null) {
                if (progressMonitor.isCanceled()) {
                    try {
                        LocalDevice.getLocalDevice().getDiscoveryAgent().cancelInquiry(listener);
                    } catch (BluetoothStateException e) {
                        TMWCoreUI.log(e);
                    }
                }
            }
        }

        public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
            try {
                final String name = btDevice.getFriendlyName(false);
                if (name != null && name.length() > 0) {
                    final BluetoothTarget target = prevTargets.get(name);
                    if (target != null) {
                        target.setAddress(btDevice);
                        targets.put(name, target);
                    } else {
                        targets.put(name, new BluetoothTarget(name, btDevice, BluetoothTargetType.this));
                    }
                    checkCanceled();
                }
            } catch (BluetoothStateException e) {
                TMWCoreUI.log(e.getMessage(), e);
            } catch (IOException e) {
                TMWCoreUI.log(e.getMessage(), e);
            }
        }

        public void inquiryCompleted(int discType) {
            synchronized (inquiryCompletedEvent) {
                inquiryCompletedEvent.notifyAll();
            }
        }

        public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
        }

        public void serviceSearchCompleted(int transID, int respCode) {
        }
    }

    private static PrintStream savedSysOut;
    private boolean discovered = false;
    private TargetDiscoveryListener listener;
    private Map<String, BluetoothTarget> targets = new TreeMap<String, BluetoothTarget>();

    public BluetoothTargetType() {
        // set parameters for BlueCove
        String param = Integer.toString(65 * 1024);
        System.setProperty("bluecove.obex.mtu", param);
        BlueCoveImpl.setConfigProperty("bluecove.obex.mtu", param);
    }

    public void discoverTargets(IProgressMonitor monitor) throws CoreException {
        if (!isBloothToothConnected()) {
            throw new CoreException(new Status(IStatus.ERROR, TMWCoreUI.PLUGIN_ID, "Bluetooth is not available"));
        }
        monitor.beginTask("Discovering Bluetooth devices", IProgressMonitor.UNKNOWN);
        final Object inquiryCompletedEvent = new Object();
        final Map<String, BluetoothTarget> previousTargets = targets;
        targets = new TreeMap<String, BluetoothTarget>();

        listener = new TargetDiscoveryListener(previousTargets, inquiryCompletedEvent, monitor);

        synchronized (inquiryCompletedEvent) {
            boolean started;
            try {
                started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listener);
                if (started) {
                    inquiryCompletedEvent.wait();
                    discovered = true;
                }
            } catch (BluetoothStateException e) {
                TMWCoreUI.log(e.getMessage(), e);
            } catch (InterruptedException e) {
                TMWCoreUI.log(e.getMessage(), e);
            }
        }
        monitor.done();
    }

    /** Toggle BlueCove logging
     */
    public void enableBlueCoveDiagnostics(boolean enable) {
        System.setProperty("bluecove.debug", Boolean.valueOf(enable).toString());
        BlueCoveImpl.instance().enableNativeDebug(enable);
        if (enable) {
            System.setOut(new PrintStream(ConsoleFactory.createStream()));
        } else {
            System.setOut(savedSysOut);
        }
    }

    public IDeploymentTarget findTarget(IMTWProject project, String id) {
        if (!isBloothToothConnected()) {
            return null;
        }
        if (discovered) {
            return targets.get(id);
        } else {
            BluetoothTarget target = targets.get(id);
            if (target == null) {
                target = new BluetoothTarget(id, null, this);
                targets.put(id, target);
            }
            return target;
        }
    }

    public IDeploymentTarget[] getTargets(IMTWProject project) {
        if (targets != null) {
            final Collection<BluetoothTarget> values = targets.values();
            return values.toArray(new IDeploymentTarget[values.size()]);
        } else {
            return null;
        }
    }

    /**
     * Check whether the bluetooth is on or not.
     * 
     * @return whether the device is on.
     */
    public boolean isBloothToothConnected() {
        return LocalDevice.isPowerOn();
    }

    public boolean targetsDiscovered() {
        return discovered;
    }

    public ISchedulingRule getSchedulingRule(IDeploymentTarget target) {
        return BluetoothRule.INSTANCE;
    }
}
