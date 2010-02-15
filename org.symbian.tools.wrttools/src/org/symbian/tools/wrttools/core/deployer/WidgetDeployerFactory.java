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

import org.symbian.tools.wrttools.core.deploy.device.DeviceDeployer;
import org.symbian.tools.wrttools.core.deploy.emulator.EmulatorDeployer;
import org.symbian.tools.wrttools.core.status.IWRTStatusListener;

/**
 * Factory class which returns the emulator based on the input provided.
 * @author avraina
 *
 */
public class WidgetDeployerFactory {
	
	/**
	 * @param deployerType
	 * @return
	 */
	public static IWidgetDeployer createWidgetDeployer(String deployerType, IWRTStatusListener statusListener){
		int ordinal = IWidgetDeployerConstants.DeployerType.valueOf(deployerType.toUpperCase()).ordinal();
		IWidgetDeployer widgetDeployer;
		
		switch(ordinal){		
		case 0: 
			widgetDeployer = new EmulatorDeployer();
			widgetDeployer.setStatusListener(statusListener);
			return widgetDeployer;
				
		case 2:
			widgetDeployer = new DeviceDeployer();
			widgetDeployer.setStatusListener(statusListener);
			return widgetDeployer;
		}
		return null;
	}
}
