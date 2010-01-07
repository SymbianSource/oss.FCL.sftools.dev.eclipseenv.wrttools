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
package org.symbian.tools.wrttools.util;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Helper {
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
}
