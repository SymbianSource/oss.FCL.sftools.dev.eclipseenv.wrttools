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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

import java.io.File;

/**
 * A reference to a location in a file.  It contains a source,
 * a line, and a column (the latter both 1-based).
 * <p>
 * The source is presumably a file, but you can use
 * whatever is needed.  The source should implement
 * toString() in an appropriate manner for reporting
 * the source reference as a string. 
 * 
 * @author eswartz
 *
 */
public class MessageLocation {
    /** The full path of the offending resource */
    IPath fullPath;
    /** The line number where this node starts (1-based) */
    int lineNumber;
    /** The character offset of the column where the node starts (1-based) */ 
    int columnNumber;

    /**
     * Create a new source reference
     * @param project the project (may not be null)
     * @param line line (1-based)
     * @param column column (1-based)
     */
    public MessageLocation(IProject project, int line, int column) {
        Check.checkArg(project);
        Check.checkArg(project.getLocation() != null);
        this.fullPath = project.getLocation();
        lineNumber = line;
        columnNumber = column;
    }
    
    /**
     * Create a new source reference
     * @param fullpath the full filesystem path to the offending resource (may not be null)
     * @param line line (1-based)
     * @param column column (1-based)
     */
    public MessageLocation(IPath fullpath, int line, int column) {
        Check.checkArg(fullpath);
        this.fullPath = fullpath;
        lineNumber = line;
        columnNumber = column;
    }
    
    /**
     * @param path the full filesystem path to the offending resource (may not be null)
    */
    public MessageLocation(IPath path) {
        this(path, 0, 0);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof MessageLocation))
            return false;
        MessageLocation other = (MessageLocation) obj;
        return fullPath.equals(other.fullPath)
        && lineNumber == other.lineNumber
        && columnNumber == other.columnNumber;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return fullPath.hashCode()
        ^ (lineNumber << 16)
        ^ (columnNumber << 8)
        ^ 0x18283782;
    }
     
    public int getColumnNumber() {
        return columnNumber;
    }
    
    public int getLineNumber() {
        return lineNumber;
    }
    
    /** 
     * Return full path in filesystem 
     */
    public IPath getLocation() {
        return fullPath;
    }
    
    /**
     * Map the given full path to a path in the current workspace.
     * @param path
     * @return the workspace-relative file
     */
    private IPath pathToWorkspace(IPath path) {
    	return FileUtils.convertToWorkspacePath(path);
    }

    /**
     * Get the workspace-relative path, or null if
     * not resolvable to the workspace
     */
    public IPath getPath() {
        return pathToWorkspace(fullPath);
    }
    
    public String toString() {
    	IPath path = getPath();
    	if (path == null)
    		path = fullPath;
        return path+":"+lineNumber+":"+columnNumber;     //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public String toShortString() {
        return new File(fullPath.toOSString()).getName()+":"+lineNumber;     //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Set the full path
     */
    public void setLocation(IPath newPath) {
        this.fullPath = newPath;
    }
    
}
