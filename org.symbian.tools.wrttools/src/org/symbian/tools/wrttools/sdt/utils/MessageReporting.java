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
package org.symbian.tools.wrttools.sdt.utils;

import org.eclipse.core.runtime.IStatus;

import java.util.*;

/**
 * This class provides a means for reporting user-visible
 * messages (such as script errors, component errors, etc.)
 * by indirecting them through a set of listeners.
 * This is opposed to Logging which is intended for developer-
 * related issues.
 * 
 * @author eswartz
 *
 */
public class MessageReporting {

    static List listeners = new ArrayList();
    
    private MessageReporting() {
    }

    public static void addListener(IMessageListener handler) {
        listeners.add(handler);
    }
    
    public static void removeListener(IMessageListener handler) {
        listeners.remove(handler);
    }
        
    /**
     * @param msg
     */
    public static void emitMessage(IMessage msg) {
    	boolean handled = false;
       for (Iterator iter = listeners.iterator(); iter.hasNext();) {
           IMessageListener handler = (IMessageListener) iter.next();
           if (handler.isHandlingMessage(msg)) {
        	   handler.emitMessage(msg);
        	   handled = true;
           }
       }
       if (!handled) {
    	   Logging.log(UtilsPlugin.getDefault(),
    			   Logging.newStatus(UtilsPlugin.getDefault(), msg.getSeverity(), msg.getMessage()));
       }
    }
    
    public static void emit(int severity, MessageLocation loc, String key, String locMsg, Object[] args) {
        IMessage msg = new Message(severity, loc,
                key, locMsg, args);
        emitMessage(msg);
    }

    public static void emit(MessageLocation loc, String key, String locMsg, IStatus status) {
        IMessage msg = new Message(status.getSeverity(), loc,
                key, locMsg);
        emitMessage(msg);
    }

    /**
     * 
     */
    public static void reset() {
        listeners.clear();
    }
}
