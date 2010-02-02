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

public class ReportException extends BaseException {
	/**
	 *  @author Sailaja Duvvuri
	 */
	private static final long serialVersionUID = -1598160901494205202L;

	/**
	 * Contructor that takes a <code>Throwable</code> for nesting.
	 * @param pNestedException <code>Throwable</code> object to nest.
	 */
	public ReportException(Throwable pNestedException) {
		super(pNestedException);
	}

	/**
	 *
	 * @param pMessage
	 * @param pNestedException
	 */
	public ReportException(String pMessage, Throwable pNestedException) {
		super(pMessage, pNestedException);
	}

	/**
	 *
	 * @param pMessage
	 */
	public ReportException(String pMessage) {
		super(pMessage);
	}
}