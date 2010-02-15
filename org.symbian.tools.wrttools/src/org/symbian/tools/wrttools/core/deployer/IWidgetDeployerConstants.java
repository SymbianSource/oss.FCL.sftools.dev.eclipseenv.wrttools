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

/**
 * Interface to hold the constants for the Deployer.
 * @author avraina
 *
 */
public interface IWidgetDeployerConstants {
	
	/**
	 * Refers to the deployer with can either be Emulator or Server or Device.
	 * @author avraina
	 *
	 */
	public enum DeployerType {
		EMULATOR, SERVER, DEVICE
	};
	
	/**
	 * Widget file type.
	 */
	public static final String WIDGET_FILE_TYPE = "wgz";
}

