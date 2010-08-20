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

/**
 * The main class for the WRT status raiser.This is defined the status which can be raised
 * from various sources e.g validator, packager or deployer . The status contains the 
 * source from it was raised and the corresponding description about the status which is
 * like some information.
 * 
 * @author avraina
 * 
 */
public class WRTStatus {
	/**
	 * The source of the status, e.g. validator , packaging or deployer.
	 */
	private String statusSource;
	/**
	 * Status description.
	 */
	private String statusDescription;
	
	/**
	 * Default Constructor.
	 */
	public WRTStatus(){
		
	}

	/**
	 * Create a new <code>WRTStatus</code> object.
	 * @param statusSource The source of the status. E.g. validator, deployer or packaging.
	 * @param statusDescription The status description.
	 */
	public WRTStatus(final String statusSource, final String statusDescription) {
		setStatusSource(statusSource);
		setStatusDescription(statusDescription);
	}
	/**
	 * Returns the source of the status.The source can be validator, deployer or packaging etc.
	 * @return the statusSource
	 */
	public String getStatusSource() {
		return statusSource;
	}

	/**
	 * Sets the source of the status.Source can be from the status is to be emitted.
	 * @param source the statusSource to set
	 */
	public void setStatusSource(String source) {
		statusSource = source;
	}

	/**
	 * Returns the status description. This the information which status needs to emit.
	 * @return the statusDescription the description of the source.
	 */
	public Object getStatusDescription() {
		return statusDescription;
	}

	/**
	 * Set the description for the status.This is the information which status will emit.
	 * @param subject the statusDescription to set
	 */
	public void setStatusDescription(String information) {
		statusDescription = information;
	}
}
