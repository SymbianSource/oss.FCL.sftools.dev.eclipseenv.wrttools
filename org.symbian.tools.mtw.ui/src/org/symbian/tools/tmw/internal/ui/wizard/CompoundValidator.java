package org.symbian.tools.tmw.internal.ui.wizard;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;


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
