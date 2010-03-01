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

public interface IWRTConstants {
	
	public enum StatusSourceType {
		VALIDATOR("IWRTConstants.validator"), //$NON-NLS-1$
		PACKAGER("IWRTConstants.packager"),  //$NON-NLS-1$
		DEPLOYER("IWRTConstants.deployer"), //$NON-NLS-1$
		CONVERTER("IWRTConstants.converter"), //$NON-NLS-1$
		CONVERTER_TODO("IWRTConstants.converter_todo");//$NON-NLS-1$

		
		private String name;
		StatusSourceType(String nameKey) {
			this.name = Messages.getString(nameKey);
		}
		public String getName() {
			return name;
		}
	};
	

	public static final int  VALIDATOR=50;
	public static final int  CONVERTOR=60;
	public static final int CONVERTOR_TODO = 65;
	public static final int  DEPLOYER=70;
	public static final int  PACKAGER=80;
	public static final int  ADDPROJECT=90;

	public static final String  IMAGE_REF="IMAGE-REF:|";
	
	//Report severity type
	public static final String  WARN = Messages.getString("IWRTConstants.report.warning");
	public static final String  ERROR = Messages.getString("IWRTConstants.report.error");
	public static final String  FATAL = Messages.getString("IWRTConstants.report.fatal");
	public static final String  INFO = Messages.getString("IWRTConstants.report.information");
	public static final String  SUCCESS = Messages.getString("IWRTConstants.report.success");
	public static final String  MANDATORY = Messages.getString("IWRTConstants.report.mandatory");
	public static final String  OPTIONAL = Messages.getString("IWRTConstants.report.optional");
	
	public static final int  MESSAGE_LIMIT=50;
	public static final int  VALIDATOR_MESSAGE_LIMIT=15;
	public static final int  CONVERTOR_MESSAGE_LIMIT=15;
	public static final int  DEPLOYER_MESSAGE_LIMIT=15;
	public static final int  PACKAGER_MESSAGE_LIMIT=15;
	
	public static final String	PROPERTIES_DIR="bin"; //$NON-NLS-1$
	
	public static final String WIDGET_FILE_EXTENSION = ".wgz"; //$NON-NLS-1$
	
	public static final String OS = System.getProperty("os.name"); //$NON-NLS-1$
	
	public static final String MAC_OS="Mac OS X"; //$NON-NLS-1$
	
	/**
	 * Locally stored Nokia DTD
	 */
	public static String NOKIA_PLIST_DTD = "/org/symbian/tools/wrttools/core/widgetmodel/plist-1.0.dtd";

	/**
	 * Nokia DTD in nfo.plist file 
	 */
	public static String NOKIA_DTD_SYS_ID = "http://www.nokia.com/DTDs/plist-1.0.dtd";
	public static String NOKIA_DTD_PUBLIC_ID = "-//Nokia//DTD PLIST 1.0//EN";
	/**
	 * Apple DTD in info.plist file
	 */
	public static String APPLE_DTD_SYS_ID = "http://www.apple.com/DTDs/PropertyList-1.0.dtd";
	public static String APPLE_DTD_PUBLIC_ID = "-//Apple Computer//DTD PLIST 1.0//EN";
	
	
	
}
