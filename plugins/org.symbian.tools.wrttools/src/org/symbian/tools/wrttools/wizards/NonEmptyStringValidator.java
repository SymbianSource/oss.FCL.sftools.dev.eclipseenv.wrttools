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
package org.symbian.tools.wrttools.wizards;

import java.text.MessageFormat;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.symbian.tools.wrttools.Activator;

public class NonEmptyStringValidator implements IValidator {
	private final String propertyName;
    private final AbstractDataBindingPage page;

	public NonEmptyStringValidator(String propertyName, AbstractDataBindingPage page) {
		this.propertyName = propertyName;
        this.page = page;
	}

	public IStatus validate(Object value) {
        if (page != null && page.isActive()) {
			if (value == null || value.toString().trim().length() == 0) {
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						MessageFormat.format("Field {0} is empty",
								propertyName));
			}
		}
		return Status.OK_STATUS;
	}
}