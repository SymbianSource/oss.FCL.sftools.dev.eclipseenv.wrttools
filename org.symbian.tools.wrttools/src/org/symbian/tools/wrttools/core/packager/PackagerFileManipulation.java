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

package org.symbian.tools.wrttools.core.packager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.symbian.tools.wrttools.core.exception.WRTException;

public class PackagerFileManipulation {
	
	private Logger logger = Logger.getLogger(getClass().getName());
	public PackagerFileManipulation() {
	}
	
	/**
	 * This method copies zip file from source folder to destination folder.
	 * @param sPath
	 * @param dPath
	 * @throws PackagerException 
	 * @throws PackageException 
	 * @throws IOException 
	 * @throws WRTException 
	 * @throws IOException 
	 */
	public void copyFile(String fromFileName, String toFileName) throws PackageException {
	    File fromFile = new File(fromFileName);
	    File toFile = new File(toFileName);
	    FileInputStream from = null;
	    FileOutputStream to = null;
	    
	    if (!fromFile.exists())
	    	logAndThrowErrors(Level.SEVERE, WRTPackagerConstants.LOG_SOURCE_NOT_FOUND,
	    			WRTPackagerConstants.ERR_SOURCE_NOT_FOUND);
	    if (fromFile.isDirectory())
	    	logAndThrowErrors(Level.SEVERE, WRTPackagerConstants.LOG_FILE_COPY_FAIL,
	    			WRTPackagerConstants.ERR_FILE_COPY_FAIL);
	    if (!fromFile.canRead())
	        logAndThrowErrors(Level.SEVERE, WRTPackagerConstants.LOG_UNREADABLE, 
	        		WRTPackagerConstants.ERR_UNREADABLE);
	    	
	    if (toFile.isDirectory())
	    	toFile = new File(toFile, fromFile.getName());

	    if (toFile.exists()) {
	    	if (!toFile.canWrite())
	    		logAndThrowErrors(Level.SEVERE, WRTPackagerConstants.LOG_NOT_WRITABLE,
	    				WRTPackagerConstants.ERR_NOT_WRITABLE);
	      
	    } else {
	    	String parent = toFile.getParent();
	    	if (parent == null)
	    		parent = System.getProperty(PackagerMessages.getString("PackagerFileManipulation.useDir")); //$NON-NLS-1$
	    	File dir = new File(parent);
	    	if (!dir.exists())
	    		logAndThrowErrors(Level.SEVERE, WRTPackagerConstants.LOG_DEST_NOT_FOUND,
	    				WRTPackagerConstants.ERR_DEST_NOT_FOUND);
	    	if (dir.isFile())
	    		logAndThrowErrors(Level.SEVERE, WRTPackagerConstants.LOG_FILE_COPY_FAIL,
	    			  WRTPackagerConstants.ERR_FILE_COPY_FAIL);
	    	if (!dir.canWrite())
	    		logAndThrowErrors(Level.SEVERE, WRTPackagerConstants.LOG_NOT_WRITABLE,
	    				WRTPackagerConstants.ERR_NOT_WRITABLE);
	    }

	    try {
	    	from = new FileInputStream(fromFile);
	    	to = new FileOutputStream(toFile);
	    	byte[] buffer = new byte[4096];
	    	int bytesRead;

	    	while ((bytesRead = from.read(buffer)) != -1)
	    		to.write(buffer, 0, bytesRead); // write
	    } catch (FileNotFoundException e) {
	    	logAndThrowErrors(Level.SEVERE, WRTPackagerConstants.LOG_READ_WRITE_FAIL,
	    			e.getMessage());
		} catch (IOException e) {
			logAndThrowErrors(Level.SEVERE, WRTPackagerConstants.LOG_READ_WRITE_FAIL,
	    			  e.getMessage());
		}
	    finally {
	      if (from != null)
			try {
				from.close();
			} catch (IOException e) {
				logAndThrowErrors(Level.SEVERE, WRTPackagerConstants.LOG_FILE_CLOSE_FAIL,
		    			  e.getMessage());
			}
	      if (to != null)
			try {
				to.close();
			} catch (IOException e) {
				logAndThrowErrors(Level.SEVERE, WRTPackagerConstants.LOG_FILE_CLOSE_FAIL,
		    			  e.getMessage());
			}
	    }
	}

	/**
	 * This method is used for logging and exception handling
	 * @param lev -- severity Level
	 * @param logMessage -- message to log
	 * @param errException -- exception to throw
	 * @throws PackagerException
	 */
	private void logAndThrowErrors(Level lev , String logMessage, String errException) throws PackageException {
		logger.log(lev, logMessage);
		throw new PackageException(errException);
	}
}
