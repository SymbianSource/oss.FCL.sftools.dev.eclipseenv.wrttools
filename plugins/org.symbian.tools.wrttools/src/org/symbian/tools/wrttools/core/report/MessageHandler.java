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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Message Handler contains list of Listener. It publish all the messages generated 
 * to all the registered listeners. 
 *   
 * @author Sailaja Duvvuri
 */

public class MessageHandler {

	private Logger log = Logger.getLogger(getClass().getName());
	private boolean fileTypeZip = false;

	public boolean isFileTypeZip() {
		return fileTypeZip;
	}

	public void setFileTypeZip(boolean fileTypeZip) {
		this.fileTypeZip = fileTypeZip;
	}
	
	  /**
     * The list of listeners added to the message handler.
     */
    private List<IMessageListener> listeners = new ArrayList<IMessageListener>();

    /**
     * Register the message listener to the handler. 
     * @param messageListener the listener which is added to the handler
     */
    public void registerListener(IMessageListener messageListener) {
        listeners.add(messageListener);
    }
    
    /**
     * Removes the message listener from the handler list.
     * @param messageListener the listener which needs to be removed from the handler.
     */
    public void removeListener(IMessageListener messageListener) {
        listeners.remove(messageListener);
    }
        
    /**
     * Publish  the message from the listener.
     * Message needs to be handled in the client side .
     * @param message The message  needs to be handled.
     */
    public void publishMessage(Message message) {
       for (Iterator<IMessageListener> iter = listeners.iterator(); iter.hasNext();) {
    	   IMessageListener messageListener = (IMessageListener) iter.next();
           if (messageListener.isMessageHandled(message)) {
        	   messageListener.receiveMessage(message);

           }
       }
    }

    /**
     * Resets the existing message listeners.
     */
    public void reset() {
        listeners.clear();
    }

}
