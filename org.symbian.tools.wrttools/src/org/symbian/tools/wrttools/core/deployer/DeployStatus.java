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

import org.symbian.tools.wrttools.core.status.WRTStatus;

/**
 * Raises the Deploy Status.
 * @author avraina
 *
 */
public class DeployStatus extends WRTStatus{

	/**
	 * @param statusSource 
	 * @param statusDescription
	 */
	public DeployStatus(String statusSource, String statusDescription) {
		super(statusSource, statusDescription);
	}
}
