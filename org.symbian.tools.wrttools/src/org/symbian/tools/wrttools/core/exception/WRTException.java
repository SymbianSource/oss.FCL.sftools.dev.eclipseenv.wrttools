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

package org.symbian.tools.wrttools.core.exception;

/**
 * The main class for the widget exception.
 * Specific modules need to inherit the class and customize their corresponding
 * exceptions.
 * @author avraina
 *
 */
public class WRTException extends Exception {

	private static final long serialVersionUID = -1450794313853330624L;

	/**
	 * Default constructor.
	 */
	public WRTException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public WRTException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public WRTException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public WRTException(Throwable cause) {
		super(cause);
	}
}