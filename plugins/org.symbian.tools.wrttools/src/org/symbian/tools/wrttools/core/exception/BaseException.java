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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

public class BaseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2256229917601216943L;
	
	private Throwable m_nestedException;
    private String m_exceptionId;

    /**
     * Private initialization method.
     */
    private void init()
    {
        this.m_exceptionId = this.getExceptionId();
    }

    /**
     * Default constructor.
     */
    public BaseException()
    {
        this.init();
    }

    /**
     * Contructor that takes a message and a <code>Throwable</code> for nesting.
     * @param pMessage <code>String</code> message for the exception.
     * @param pNestedException <code>Throwable</code> object to nest.
     */
    public BaseException( String pMessage, Throwable pNestedException )
    {
        super( pMessage );

        this.init();

        this.m_nestedException = pNestedException;
    }

    /**
     * Contructor that takes a <code>Throwable</code> for nesting.
     * @param pNestedException <code>Throwable</code> object to nest.
     */
    public BaseException( Throwable pNestedException )
    {
        this.init();

        this.m_nestedException = pNestedException;
    }

    /**
     * Contructor that takes a message.
     * @param pMessage <code>String</code> message for the exception.
     */
    public BaseException( String pMessage )
    {
        super( pMessage );

        this.init();
    }

    /**
     * Method to extract the class name, method name & line number from the
     * call stack.
     * @return The exception Id.
     */
    private String getExceptionId()
    {
        return CallStackParser.parse( this.getExpStackTrace() );
    }

    /**
     * Internal helper method to serialize the stack to a
     * <code>String</code>.
     * @return <code>String</code> holding the serialized stack.
     */
    private String getExpStackTrace()
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream( stream );
        super.printStackTrace( ps );
        return stream.toString();
    }

    /**
     * Serializes the exception to a <code>String</code>.
     * @return String
     */
    public String toString()
    {
        StringBuffer retval = new StringBuffer( this.getClass().getName() );

        if( this.m_exceptionId != null ) {
            retval.append( "\r\nId: " );
            retval.append( this.m_exceptionId );
            retval.append( "\r\n" );
        }

        retval.append( "Message: " );
        retval.append( super.getMessage() );

        if( this.m_nestedException != null ) {
            retval.append( "\r\nNested Exception: " );
            retval.append( this.m_nestedException.toString() );
        }

        return retval.toString();
    }

    /**
     * Prints this <code>BaseException</code> and its backtrace and any nested
     * <code>Thowable</code>to the standard error stream.
     */
    public void printStackTrace()
    {
        super.printStackTrace();

        if( this.m_nestedException != null ) {
//            System.err.println( "Nested Exception--------------------------------" );
            this.m_nestedException.printStackTrace();
        }
    }

    /**
     * Prints this <code>BaseException</code> and its backtrace and any nested
     * <code>Thowable</code> to the specified <code>PrintStream</code>.
     * @param pStream <code>PrintStream</code> to write to.
     */
    public void printStackTrace( PrintStream pStream )
    {
        super.printStackTrace( pStream );

        if( this.m_nestedException != null ) {
            pStream.println( "Nested Exception--------------------------------" );
            this.m_nestedException.printStackTrace( pStream );
        }
    }

    /**
     * Prints this <code>BaseException</code> and its backtrace and any nested
     * <code>Thowable</code> to the specified <code>PrintWriter</code>.
     * @param pWriter <code>PrintWriter</code> to write to.
     */
    public void printStackTrace( PrintWriter pWriter )
    {
        super.printStackTrace( pWriter );

        if( this.m_nestedException != null ) {
            pWriter.println( "Nested Exception--------------------------------" );
            this.m_nestedException.printStackTrace( pWriter );
        }
    }
}
