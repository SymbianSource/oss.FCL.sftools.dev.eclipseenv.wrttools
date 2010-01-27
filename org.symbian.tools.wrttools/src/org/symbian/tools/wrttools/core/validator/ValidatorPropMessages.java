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

package org.symbian.tools.wrttools.core.validator;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.symbian.tools.wrttools.util.Util;

public class ValidatorPropMessages {
	private static final String BUNDLE_NAME = "com.nokia.wrt.core.validator.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);
	private static Logger log = Logger.getLogger("com.nokia.wrt.core.validator.ValidatorPropMessages");

	private ValidatorPropMessages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			 Util.logEvent(log, Level.INFO, e);				
			return '!' + key + '!';
		}
	}
	
	public static void main(String[] args) {
		
//		  System.out.println("    "+ ValidatorPropMessages.getString("plist.File.Not.Present"));
	
	}
}
