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

package org.symbian.tools.wrttools.core.exception;

import java.util.StringTokenizer;

public class CallStackParser {
    /**
     * Method to extract the class name, method name & line number from
     * the call stack.
     * @param pCallStack The call strack string
     * @return The exception Id.
     */
    protected static String parse( String pCallStack )
    {
        try {
            if( pCallStack == null )
                return "CallStackParser: Unknown Call Stack";

            String lineNumber = "";
            String text       = pCallStack;

            StringTokenizer st = new StringTokenizer( text, "\r\n" );
            // we want the second line for the classname and calling method
            // so throw away the first element
            st.nextElement();
            // and save the second
            text = ( String )st.nextElement();

            // find the line number
            int ix = text.lastIndexOf( ":" );
            if( ix != -1 ) {
                lineNumber = text.substring( ix + 1, text.length() - 1 );
            }

            // strip everything after the first found '('
            ix = text.indexOf( "(" );
            if( ix != -1 ) {
                text = text.substring( 0, ix );
            }

            // strip everything before the class name
            ix = text.indexOf( " " );
            if( ix != -1 ) {
                text = text.substring( ix + 1 );
            }

            // if we have a line number, append it
            if( lineNumber.length() > 0 ) {
                StringBuffer output = new StringBuffer( text );
                output.append( ":" );
                output.append( lineNumber );
                return output.toString();
            }

            return text;
        }
        catch( Exception e ) {
            return "<Unknown Exception Id>";
        }
    }
}
