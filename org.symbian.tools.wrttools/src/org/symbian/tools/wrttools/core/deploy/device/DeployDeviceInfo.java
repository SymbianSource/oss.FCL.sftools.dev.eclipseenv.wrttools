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

import org.symbian.tools.wrttools.sdt.utils.Check;

/**
 * Holds the information about the Device to be used for the deployment
 * through bluetooth
 * @author avraina
 *
 */
public class DeployDeviceInfo {

	private String deviceName;
	private String deviceBlueToothAddress;
	
	public DeployDeviceInfo(String deviceName, String deviceBlueToothAddress) {
		Check.checkArg(deviceName);
		Check.checkArg(deviceBlueToothAddress);
		this.deviceName = deviceName;
		this.deviceBlueToothAddress = deviceBlueToothAddress;
	}	
	
	/**
	 * Returns the device name.
	 * @return the address
	 */
	public String getDeviceName() {
		return deviceName;
	}

	/**
	 * Returns the bluetooth address of the device
	 * @return the address
	 */
	public String getDeviceBlueToothAddress() {
		return deviceBlueToothAddress;
	}
}
