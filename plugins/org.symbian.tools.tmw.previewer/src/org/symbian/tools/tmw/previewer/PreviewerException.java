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
package org.symbian.tools.tmw.previewer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class PreviewerException extends CoreException {
    private static final long serialVersionUID = 5555595753039913542L;

    public PreviewerException(String message) {
        this(message, null);
    }
    
    public PreviewerException(String message, Exception exception) {
        super(new Status(IStatus.ERROR, PreviewerPlugin.PLUGIN_ID, message, exception));
    }

    public PreviewerException(Exception e) {
        this(null, e);
    }

    public PreviewerException(CoreException e) {
        super(e.getStatus());
    }
}
