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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;

/**
 * This encapsulates generic information about a message
 * that can be reported to multiple sources (problems view,
 * etc).
 * 
 * @author eswartz
 *
 */
public interface IMessage {
    static public final int INFO = IStatus.INFO;
    static public final int WARNING = IStatus.WARNING;
    static public final int ERROR = IStatus.ERROR;
    
    /** Get the severity
     * @see #INFO
     * @see #WARNING
     * @see #ERROR 
     */
    int getSeverity();
    
    /** Get the message location */
    MessageLocation getMessageLocation();
    
    /** Get the offending file (full path) or null */
    IPath getLocation();
    
    /** Get the offending file (workspace path) or null */
    IPath getPath();
    
    /** Get the line number */
    int getLineNumber();
    
    /** Get the column number */
    int getColumnNumber();
    
    /** Get the message (localized) */
    String getMessage();

    /** Get the message key, i.e. the non-localized identifier for the message,
     * for use in testing */
    String getMessageKey();

    /** Create (and attach) a problem marker */
    IMarker createMarker(IResource resource, String modelMarkerType);

}
