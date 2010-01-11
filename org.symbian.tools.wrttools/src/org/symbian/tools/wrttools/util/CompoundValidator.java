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

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.symbian.tools.wrttools.wizards.AbstractDataBindingPage.NonEmptyStringValidator;


public class CompoundValidator implements IValidator {
	private final IValidator[] validators;

	public CompoundValidator(IValidator ... validators) {
		this.validators = validators;
	}
	
	public CompoundValidator(NonEmptyStringValidator validator,
			IValidator[] validators) {
		this.validators = new IValidator[validators.length + 1];
		this.validators[0] = validator;
		System.arraycopy(validators, 0, this.validators, 1, validators.length);
	}

	@Override
	public IStatus validate(Object value) {
		IStatus status = Status.OK_STATUS;
		
		for (IValidator validator : validators) {
			IStatus s = validator.validate(value);
			switch (s.getSeverity()) {
			case IStatus.ERROR:
				return s;
			case IStatus.WARNING:
				status = s;
			}
		}
		return status;
	}

}
