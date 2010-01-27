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

import org.symbian.tools.wrttools.sdt.utils.noexport.Messages;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;


/**
 * Utilities for runtime checking of correctness.
 * 
 * @author eswartz
 *
 */
public class Check {
    /**
     * Report a failure.  This logs the failure and reports a dialog in debug mode.
     * 
     * @param message human-readable message
     * @param thr the exception to report 
     */
    public static void reportFailure(String message, Throwable thr) {
        try {
            IStatus status = Logging.newSimpleStatus(1 /* our caller */, 
                    IStatus.ERROR, 
                    message,
                    thr);
            Logging.log(UtilsPlugin.getDefault(), status);
            if (Platform.isRunning() && Platform.inDebugMode())
                Logging.showErrorDialog(Messages.getString("Check.RuntimeError"), null, status); //$NON-NLS-1$
        } catch (Throwable e) {
            // ignore: don't recursively fail
        }
    }
    
    /**
     * Verify an argument is not null else throw a runtime exception
     * 
     * It's not necessary to use this unless you're storing the object 
     * away for later use -- otherwise just deference it and let the runtime
     * throw for you!
     * 
     * @param obj
     * @throws NullPointerException
     */
    static public void checkArg(Object obj) {
        if (obj == null) {
            RuntimeException thr = new NullPointerException();
            reportFailure(Messages.getString("Check.ArgumentIsNull"), thr); //$NON-NLS-1$
            throw thr;
        }
    }


    /**
     * Verify an argument satisfies a condition else throw a runtime exception
     * 
     * @param condition test which must return true
     * @throws IllegalArgumentException
     */
    static public void checkArg(boolean condition) {
        if (!condition) {
            RuntimeException thr = new IllegalArgumentException();
            reportFailure(Messages.getString("Check.ArgumentIsInvalid"), thr); //$NON-NLS-1$
            throw thr;
        }
    }
    
    /**
     * Verify an invariant of object state holds else throw a runtime exception.
     * This is typically used to verify an object is internally consistent.
     * 
     * @param condition test which must return true
     * @throws IllegalStateException
     */
    static public void checkState(boolean condition) {
        if (!condition) {
            RuntimeException thr = new IllegalStateException();
            reportFailure(Messages.getString("Check.ObjectIsInconsistent"), thr); //$NON-NLS-1$
            throw thr;
        }
    }

    /**
     * Verify an invariant related to promised behavior holds else throw a runtime exception
     * This is typically used to ensure a client API works as expected.
     *  
     * @param condition test which must return true
     * @throws AssertionError
     */
    static public void checkContract(boolean condition) {
        if (!condition) {
            AssertionError thr = new AssertionError();
            reportFailure(Messages.getString("Check.ApiAssertionFailed"), thr); //$NON-NLS-1$
            throw thr;
        }
    }

    /**
     * Throw a runtime exception to indicate a test on an argument
     * failed due to another exception.
     * 
     * @param thr the throwable that resulted from an argument test
     * @throws IllegalArgumentException
     */
    public static void failedArg(Throwable thr) {
        IllegalArgumentException exc = new IllegalArgumentException();
        exc.initCause(thr);
        throw exc;
    }
}
