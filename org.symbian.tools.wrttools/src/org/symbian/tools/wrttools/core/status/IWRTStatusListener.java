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

package org.symbian.tools.wrttools.core.status;

public interface IWRTStatusListener {
	/**
	 * Tell if the status should be emitted by this listener,
	 * usually true for a solitary listener.  It would return false,
	 * e.g., if several listeners are attached and each has a particular 
	 * scope for status, and the given status doesn't match
	 * this listener's scope. 
	 */
	public boolean isStatusHandled(WRTStatus status);
	
    /**
     * Emit a status if #isStatusHandled() returned true.
     * @param status
     */
    public void emitStatus(WRTStatus status);
}