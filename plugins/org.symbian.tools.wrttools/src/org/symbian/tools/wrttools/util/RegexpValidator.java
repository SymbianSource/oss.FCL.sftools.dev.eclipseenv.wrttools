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

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.symbian.tools.wrttools.Activator;

public class RegexpValidator implements IValidator {
    private final String errorMessage;
    private final Pattern pattern;
    private final boolean match;

    public RegexpValidator(String pattern, String errorMessage, boolean match) {
        this.errorMessage = errorMessage;
        this.match = match;
        this.pattern = Pattern.compile(pattern);
    }

    public IStatus validate(Object value) {
        String string = value.toString();
        Matcher matcher = pattern.matcher(string);
        if (match && !matcher.matches()) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, MessageFormat.format(errorMessage, string));
        } else if (!match && matcher.find()) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, MessageFormat.format(errorMessage,
                    string.substring(matcher.start(), matcher.end())));
        } else {
            return Status.OK_STATUS;
        }
    }

}
