/**
 * Copyright (c) 2010 Symbian Foundation and/or its subsidiary(-ies).
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
package org.symbian.tools.wrttools.util;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Util {

	public static String removeSpaces(String widgetName) {
		return widgetName != null ? widgetName.replace(" ", "") : null;
	}

	public static String removeNonAlphaNum(String projectName) {
		return projectName != null ? projectName.replaceAll("[^a-zA-Z0-9 ]", "") : null;
	}
public static void logEvent(Logger log, Level level, Throwable throwable)
	{
		if (level==Level.SEVERE)
		{
			log.severe(throwable.getLocalizedMessage());
			if(throwable.getCause()!=null)
			log.severe(throwable.getCause().toString());
			log.severe(throwable.getStackTrace().toString());
		}
		if (level==Level.WARNING)
		{
			log.warning(throwable.getLocalizedMessage());
			if(throwable.getCause()!=null)
			log.warning(throwable.getCause().toString());
			log.warning(throwable.getStackTrace().toString());
		}
		if (level==Level.INFO)
		{
			log.info(throwable.getLocalizedMessage());
			if(throwable.getCause()!=null)
			log.info(throwable.getCause().toString());
			log.info(throwable.getStackTrace().toString());
		}
		
	}
	
	public static String replaceChar(String input, char asciiOutChar, char asciiInChar){
		char x;
		int ascii ;
		String outString="";
		int outCharAscii= (asciiOutChar > 127) ? '?' : (char)(asciiOutChar & 0x7F);
		int inCharAscii= (asciiInChar > 127) ? '?' : (char)(asciiInChar & 0x7F);
		
		for (int i = 0; i < input.length(); i++) {
			 x = input.charAt(i);	
			 ascii = (x > 127) ? '?' : (char)(x & 0x7F);
			 
			 if(ascii==outCharAscii){
				 outString=outString+asciiInChar;
			 }else{
				 outString=outString+x; 
			 }
		
		}
		return outString;
	}
	
	  public static void showData(String s) {
		System.out.println(s);
	}
	  
	  public static void showData(List<String> listString, String header) {
		 
		  if(listString!=null&&listString.size()>0) {
			  System.out.println("--------"+header+"------");
		  for(String s:listString){
			System.out.println(s);
			}
		  }else {
			  System.out.println("--------Empty/Null "+header+"------");
		  }
		}


		@SuppressWarnings("restriction")
		/* Validation tests for both Windows & Mac OS */
		private static String commonValidate(String argName)
		{
			if (argName.length() == 0 ) {
				return ("Can not be empty");	    		 
			}
			
        	// filenames starting with dot are not valid for both Widget name & UID
	    	if ( argName.charAt(0) == '.' ) {
	    		return("Can not begin with a dot");
	    	}

	    	final char lastChar = argName.charAt(argName.length()-1);
			// filenames ending in dot are not valid for both Widget name & UID
			if (lastChar == '.') {
				return("Can not end with dot");
			}
			
			return null;
		}


		public static String validateWidgetName(String widgetName){    	
			String strError = null;			
			if ((strError = commonValidate(widgetName)) != null)
				return "Invalid Widget name. " + strError;			
			if (widgetName.indexOf("<") > -1 || widgetName.indexOf(">") > -1){
        		return("Invalid Widget name. Angle brackets are not allowed");
        	}
			final char lastChar = widgetName.charAt(widgetName.length()-1);
			// trailing or beginning space is not valid in filenames for Widget name
			if ((Character.isWhitespace(widgetName.charAt(0)) || Character.isWhitespace(lastChar))) {
				return("Invalid Widget name. Beginning or trailing spaces are not allowed");
			}

			if (widgetName.indexOf('\n') != -1 || widgetName.indexOf('\t') != -1 ) {
				return("Invalid Widget name. newline character is not allowed");
			}
			
	        return null;
		}

		public static String validateWidgetID(String widgetID)
		{
			String strError = null; 			
			if ((strError = commonValidate(widgetID)) != null)
				return "Invalid Widget identifier. " + strError;

			// file names with white spaces are not allowed for Widget Identifier (UID)
        	if (widgetID.indexOf(" ") > -1 ){
        		return("Invalid Widget identifier. Whitespaces are not allowed");
        	}
        	
        	if (widgetID.length() > 78 ) {
        		return("Invalid Widget identifier. Maximum string length exceeded");
        	}
        	
        	/* test invalid characters, allows only alphanumeric and '.' for UID*/
			String alphnum = ".0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
			for (int i = 0; i < widgetID.length(); i++)
				if (alphnum.indexOf(widgetID.charAt(i),0) == -1) {
					return("Invalid Widget identifier. Only alphanumeric or '.' is allowed");
				}

			if (widgetID.matches(".*[.]{2,}.*")) {				
				return("Invalid Widget identifier. Consecutive dots are not allowed");				
			}	        

			return null;
		}
}
