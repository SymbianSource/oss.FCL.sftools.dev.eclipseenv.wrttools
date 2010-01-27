/* ============================================================================
 * Copyright © 2008 Nokia. All rights reserved.
 * This material, including documentation and any related computer
 * programs, is protected by copyright controlled by Nokia. All
 * rights are reserved. Copying, including reproducing, storing,
 * adapting or translating, any or all of this material requires the
 * prior written consent of Nokia. This material also contains
 * confidential information which may not be disclosed to others
 * without the prior written consent of Nokia.
 * ============================================================================*/

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
