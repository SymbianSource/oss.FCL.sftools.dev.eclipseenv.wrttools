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

import org.symbian.tools.wrttools.core.exception.WRTException;

public class DeployException extends WRTException {

	private static final long serialVersionUID = -4322746430125745192L;

	/**
	 * @param cause
	 */
	public DeployException(Throwable cause) {
		super(cause);
	}

	public DeployException(String message) {
		super(message);
	}
}