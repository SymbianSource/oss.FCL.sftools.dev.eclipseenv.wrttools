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

package org.symbian.tools.wrttools.core.packager;

import org.symbian.tools.wrttools.core.exception.WRTException;

public class PackageException extends WRTException {

	/**
	 * Default serial version ID 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Default constructor.
	 */
	public PackageException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public PackageException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public PackageException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public PackageException(Throwable cause) {
		super(cause);
	}

}
