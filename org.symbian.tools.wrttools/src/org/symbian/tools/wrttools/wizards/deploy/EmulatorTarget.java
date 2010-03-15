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
import java.util.Map.Entry;

import org.eclipse.swt.graphics.Image;
import org.symbian.tools.wrttools.core.WRTImages;
import org.symbian.tools.wrttools.core.deploy.emulator.EmulatorDeployer;
import org.symbian.tools.wrttools.core.deployer.IWidgetDeployer;
import org.symbian.tools.wrttools.core.status.IWRTStatusListener;

public class EmulatorTarget extends DeploymentTarget {
    public EmulatorTarget(Entry<String, String> emulatorEntry) {
        super(emulatorEntry.getKey());
        addr = emulatorEntry.getValue();
    }

    @Override
    public String getDescription() {
        return MessageFormat.format("Emulator path: {0}", getAddr());
    }

    @Override
    public IWidgetDeployer createDeployer(IWRTStatusListener statusListener) {
        IWidgetDeployer widgetDeployer = new EmulatorDeployer();
        widgetDeployer.setStatusListener(statusListener);
        return widgetDeployer;
    }

    @Override
    public String getType() {
        return "emulator";
    }

    @Override
    public Image getIcon() {
        return WRTImages.getEmulatorImage();
    }

    public String getDeployMessage() {
        return "Application was copied to emulator Phone Memory.\nYou should manually install it using the File Manager before running.";
    }
}
