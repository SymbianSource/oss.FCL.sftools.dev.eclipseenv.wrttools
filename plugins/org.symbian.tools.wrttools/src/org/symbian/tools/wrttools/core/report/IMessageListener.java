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

package org.symbian.tools.wrttools.core.report;

public interface IMessageListener {
	/**
	 * Tell if the Message should be listened by this listener,
	 * usually true for a solitary listener.  It would return false,
	 * e.g., if several listeners are attached and each has a particular 
	 * scope for status, and the given status doesn't match
	 * this listener's scope. 
	 */
	public boolean isMessageHandled(Message message);
	
    /**
     * publish  a message  this message should be handles by the client class
     * default way is to add to a list and pass the list to the report handler to 
     * generate report.
     * @param status
     */
    public void receiveMessage(Message message);
}
