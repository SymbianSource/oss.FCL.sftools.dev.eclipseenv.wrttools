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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WRTStatusHandler {

    /**
     * The list of listeners added to the status handler.
     */
    private List<IWRTStatusListener> listeners = new ArrayList<IWRTStatusListener>();
    
    /**
     * Constructor
     */
    public WRTStatusHandler() {
    }

    /**
     * Adds the status listener to the handler. 
     * @param statusListener the listener which is added to the handler
     */
    public void addListener(IWRTStatusListener statusListener) {
        listeners.add(statusListener);
    }
    
    /**
     * Removes the status listener from the handler list.
     * @param statusListener the listener which needs to be removed from the handler.
     */
    public void removeListener(IWRTStatusListener statusListener) {
        listeners.remove(statusListener);
    }
        
    /**
     * Emit the status . This will basically emit the status from the listener.
     * If the status needs to be handled it will be emitted else nothing will happen.
     * @param status The status which needs to be handled.
     */
    public void emitStatus(WRTStatus status) {
       for (Iterator<IWRTStatusListener> iter = listeners.iterator(); iter.hasNext();) {
           IWRTStatusListener statusListener = (IWRTStatusListener) iter.next();
           if (statusListener.isStatusHandled(status)) {
        	   statusListener.emitStatus(status);
           }
       }
    }

    /**
     * Resets the existing status listeners.
     */
    public void reset() {
        listeners.clear();
    }
}
