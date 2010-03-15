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

import java.text.MessageFormat;

import org.eclipse.swt.graphics.Image;
import org.symbian.tools.wrttools.core.WRTImages;
import org.symbian.tools.wrttools.core.deploy.device.DeployDeviceInfo;
import org.symbian.tools.wrttools.core.deploy.device.DeviceDeployer;
import org.symbian.tools.wrttools.core.deployer.IWidgetDeployer;
import org.symbian.tools.wrttools.core.status.IWRTStatusListener;

public class PhoneTarget extends DeploymentTarget {
    public static final String TYPE = "bluetooth";
    private boolean discovered;

    public PhoneTarget(DeployDeviceInfo deviceInfo) {
        super(deviceInfo.getDeviceName());
        addr = deviceInfo.getDeviceBlueToothAddress();
        discovered = true;
    }

    public PhoneTarget(String name) {
        super(name);
        discovered = false;
    }

    @Override
    public String getDescription() {
        if (isResolved()) {
            return MessageFormat.format("Bluetooth address: {0}", getAddr());
        } else {
            return "This deviced was used in the previous session and might not be available";
        }
    }

    @Override
    public IWidgetDeployer createDeployer(IWRTStatusListener statusListener) {
        IWidgetDeployer widgetDeployer = new DeviceDeployer();
        widgetDeployer.setStatusListener(statusListener);
        return widgetDeployer;
    }

    public void update(DeployDeviceInfo info) {
        addr = info.getDeviceBlueToothAddress();
        discovered = true;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public boolean isResolved() {
        return discovered;
    }

    @Override
    public Image getIcon() {
        return WRTImages.getBluetoothImage();
    }

    @Override
    public String getDeployMessage() {
        return "The application was sent to your phone.\nYou need to complete the installation on your phone before running the application.";
    }
}
