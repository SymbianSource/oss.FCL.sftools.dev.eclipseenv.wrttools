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

import org.symbian.tools.wrttools.sdt.utils.noexport.Messages;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;

import java.text.MessageFormat;


/**
 * A message.
 * <p>
 * A message is identified by a key, a location,
 * and a severity.
 * <p>
 * When constructing a message, the key is the name of a
 * property (in this plugin's messages.properties file),
 * which is used to format a string with the optional arguments.
 * <p>
 * The key is retained to identify the message later (e.g.
 * for unit tests).
 * 
 * @author eswartz
 *
 */
public class Message implements IMessage {

    /** Message severity (INFO, WARNING, ERROR) */
    public int severity;
    /** Location associated with message */
    public MessageLocation ref;
    /** Message key */
    public String key;
    /** Expanded and formatted message */
    public String text;
    
    /**
     * Create a message
     * @param severity (Message.xxx)
     * @param ref
     * @param key message key
     * @param locMsg localized string (unformatted)
     * @param args
     */
    public Message(int severity, MessageLocation ref, String key, String locMsg, Object[] args) {
        Check.checkArg(key);
        Check.checkArg(locMsg);
        Check.checkArg(ref);
        this.severity = severity;
        this.ref = ref;
        this.key = key;
        this.text = MessageFormat.format(locMsg, args);
    }

    /**
     * Create a message
     * @param severity (Message.xxx)
     * @param ref
     * @param key message key
     * @param locMsg localized string (unformatted)
     * @param arg
     */
    public Message(int severity, MessageLocation ref, String key, String locMsg, Object arg) {
        Check.checkArg(key);
        Check.checkArg(locMsg);
        Check.checkArg(ref);
        this.severity = severity;
        this.ref = ref;
        this.key = key;
        if (arg == null)
            arg = ""; //$NON-NLS-1$
        this.text = MessageFormat.format(locMsg, new Object[] { arg });
    }

    /**
     * Create a message
     * @param severity (Message.xxx)
     * @param ref
     * @param key message key
     * @param locMsg localized string (unformatted)
     */
    public Message(int severity, MessageLocation ref, String key, String locMsg) {
        Check.checkArg(key);
        Check.checkArg(locMsg);
        Check.checkArg(ref);
        this.severity = severity;
        this.ref = ref;
        this.key = key;
        this.text = locMsg;
    }

    public String getSeverityString() {
        if (severity == INFO) return ""; //$NON-NLS-1$
        else if (severity == WARNING) return Messages.getString("Message.Warning"); //$NON-NLS-1$
        else return Messages.getString("Message.Error"); //$NON-NLS-1$
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof Message))
            return false;
        Message other = (Message) obj;
        return other.severity == severity
            && ((ref == null && other.ref == null)
                || (ref != null && other.ref != null && other.ref.equals(ref)))
            && key.equals(other.key)
            && text.equals(other.text);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return (severity << 20)
             ^ (ref != null ? ref.hashCode() : 0)
             ^ key.hashCode()
             ^ text.hashCode()
             ^ 0x49482721;
    }
    
    public String toString() {
        return (ref != null ? ref + ": " : "")  //$NON-NLS-1$ //$NON-NLS-2$
            + getSeverityString() 
            + text;
    }

    /* (non-Javadoc)
     * @see com.nokia.sdt.component.sourcegen.ISourceGenProblem#getSeverity()
     */
    public int getSeverity() {
        return severity;
    }

    /* (non-Javadoc)
     * @see com.nokia.sdt.utils.IMessage#getMessageLocation()
     */
    public MessageLocation getMessageLocation() {
        return ref;
    }
    
    /* (non-Javadoc)
     * @see com.nokia.sdt.utils.IMessage#getLocation()
     */
    public IPath getLocation() {
        return ref != null ? ref.getLocation() : null;
    }
    
    /* (non-Javadoc)
     * @see com.nokia.sdt.utils.IMessage#getPath()
     */
    public IPath getPath() {
        return ref != null ? ref.getPath() : null;
    }
    
    /* (non-Javadoc)
     * @see com.nokia.sdt.component.sourcegen.ISourceGenProblem#getLineNumber()
     */
    public int getLineNumber() {
        return ref != null ? ref.lineNumber : -1;
    }

    /* (non-Javadoc)
     * @see com.nokia.sdt.sourcegen.ISourceGenMessage#getColumnNumber()
     */
    public int getColumnNumber() {
        return ref != null ? ref.columnNumber : -1;
    }
    
    /* (non-Javadoc)
     * @see com.nokia.sdt.component.sourcegen.ISourceGenProblem#getMessage()
     */
    public String getMessage() {
        return text;
    }

    /* (non-Javadoc)
     * @see com.nokia.sdt.component.sourcegen.ISourceGenProblem#getMessageKey()
     */
    public String getMessageKey() {
        return key;
    }
    
    /**
     * Create a problem marker for the message.
     * @return
     */
    public IMarker createMarker(IResource resource, String modelMarkerType) {
    	try {
			IMarker marker = resource.createMarker(modelMarkerType);
			
			marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
			switch (getSeverity()) {
			case IStatus.ERROR:
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				break;
			case IStatus.WARNING:
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
				break;
			case IStatus.INFO:
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
				break;
			}
			
			MessageLocation location = getMessageLocation();
			
			// remove superfluous tabs and newlines that might be in the message (bug 3152)
			String message = getMessage().replaceAll("[\t\r\n]+", " "); //$NON-NLS-1$ //$NON-NLS-2$
			
			// no resource was located in the workspace, but perhaps
			// there is info available
			if (resource == ResourcesPlugin.getWorkspace().getRoot() && location != null && getLineNumber() != 0) {
				marker.setAttribute(IMarker.MESSAGE, 
						"(" + location.toShortString() +") " + message); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else
				marker.setAttribute(IMarker.MESSAGE, message);
	
			if (getLineNumber() != 0)
				marker.setAttribute(IMarker.LINE_NUMBER, getLineNumber());
	    	
			return marker;
		} catch (CoreException e) {
			Logging.log(UtilsPlugin.getDefault(),
					Logging.newStatus(UtilsPlugin.getDefault(), e));
			return null;
		}

    }
}
