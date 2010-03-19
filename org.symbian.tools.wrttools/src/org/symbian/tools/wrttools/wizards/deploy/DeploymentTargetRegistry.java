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
package org.symbian.tools.wrttools.wizards.deploy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.core.deploy.device.DeployDeviceInfo;
import org.symbian.tools.wrttools.core.deploy.device.DeviceListProvider;
import org.symbian.tools.wrttools.core.deploy.emulator.EmulatorListProvider;
import org.symbian.tools.wrttools.core.deployer.DeployerMessages;

public class DeploymentTargetRegistry {
    private static DeploymentTargetRegistry INSTANCE;

    public synchronized static DeploymentTargetRegistry getRegistry() {
        if (INSTANCE == null) {
            INSTANCE = new DeploymentTargetRegistry();
        }
        return INSTANCE;
    }
    private final DeploymentTarget[] emulators;
    private DeploymentTarget[] phones = new DeploymentTarget[0];

    private DeploymentTargetRegistry() {
        HashMap<String, String> map = EmulatorListProvider.populateEmulators();
        Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
        emulators = new DeploymentTarget[map.size()];

        for (int i = 0; i < emulators.length; i++) {
            emulators[i] = new EmulatorTarget(iterator.next());
        }
    }

    private boolean didLookup = false;

    public synchronized void doSearch(IProgressMonitor monitor) throws CoreException {
        didLookup = true;
        monitor.beginTask("Bluetooth search", IProgressMonitor.UNKNOWN);
        if (DeviceListProvider.isBloothToothConnected()) {
            loadDevices(monitor);
        } else {
            throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, DeployerMessages
                    .getString("Deployer.bluetooth.notconnected.msg")));
        }

    }

    private DeploymentTarget findOrCreate(DeploymentTarget[] prev, DeployDeviceInfo next) {
        for (DeploymentTarget deploymentTarget : prev) {
            if (deploymentTarget instanceof PhoneTarget
                    && deploymentTarget.getName().equals(next.getDeviceName())) {
                ((PhoneTarget) deploymentTarget).update(next);
                return deploymentTarget;
            }
        }
        return new PhoneTarget(next);
    }

    public synchronized DeploymentTarget[] getDeploymentTargets() {
        ProgramTarget target = ProgramTarget.getInstance();
        DeploymentTarget[] all = new DeploymentTarget[emulators.length + phones.length + (target != null ? 1 : 0)];
        System.arraycopy(emulators, 0, all, 0, emulators.length);
        System.arraycopy(phones, 0, all, emulators.length, phones.length);
        if (target != null) {
            all[all.length - 1] = target;
        }
        return all;
    }

    /**
     * Runs a thread to load the devices present for the deployment.
     * Does not effect the loading of the UI.
     * @param device the device earlier saved .
     * @param monitor 
     */
    private void loadDevices(IProgressMonitor monitor) {
        List<DeployDeviceInfo> devices = DeviceListProvider.getDevices(monitor);
        Iterator<DeployDeviceInfo> iterator = devices.iterator();
        final DeploymentTarget[] prev = phones;
        phones = new DeploymentTarget[devices.size()];
        for (int i = 0; i < phones.length; i++) {
            phones[i] = findOrCreate(prev, iterator.next());
        }
    }

    public DeploymentTarget findTarget(String name, String type) {
        DeploymentTarget[] targets = getDeploymentTargets();
        for (DeploymentTarget deploymentTarget : targets) {
            if (deploymentTarget.getName().equals(name) && deploymentTarget.getType().equals(type)) {
                return deploymentTarget;
            }
        }
        if (type.equals(PhoneTarget.TYPE) && phones.length == 0) {
            DeploymentTarget target = new PhoneTarget(name);
            phones = new DeploymentTarget[] { target };
            return target;
        }
        return null;
    }

    public boolean didBluetoothLookup() {
        return didLookup;
    }

}
