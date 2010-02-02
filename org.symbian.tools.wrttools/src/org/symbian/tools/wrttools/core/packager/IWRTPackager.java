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

import java.io.IOException;
import java.util.List;

import org.symbian.tools.wrttools.core.exception.WRTException;

/**
 * Main Interface for Packaging.
 * @author Nutan 
 *
 */
public interface IWRTPackager   {

	/**
	 * This method is used to check user Input for Packaging (A project folder or .Zip file)
	 * Returns 0 for file type .zip and 1 for Project folder and 2 for any thing else
	 * @return  int returns project input type format
	 * @throws WRTException 
	 */
	public int checkInputType() throws PackageException;
	
	/**
	 * This method will convert validated folder into .zip file
	 * Method implementation using java.util.zip package
	 * This method will rename the file from .zip extension to .wgz
	 * @throws PackageException 
	 * @throws IOException 
	 */
	public void packageWidget(String sPath, String dPath, List<String> fileList) throws PackageException ;
	
}
