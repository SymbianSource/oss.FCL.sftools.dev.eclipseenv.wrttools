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
package org.symbian.tools.wrttools.wizards;

import java.text.MessageFormat;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.util.CompoundValidator;

public abstract class AbstractDataBindingPage extends WizardPage {
	public class NonEmptyStringValidator implements IValidator {
		private final String propertyName;

		public NonEmptyStringValidator(String propertyName) {
			this.propertyName = propertyName;
		}

		@Override
		public IStatus validate(Object value) {
			if (isActive()) {
				if (value == null || value.toString().trim().length() == 0) {
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							MessageFormat.format("Field {0} is empty",
									propertyName));
				}
			}
			return Status.OK_STATUS;
		}
	}

	protected final WizardContext context;
	protected final DataBindingContext bindingContext;

	public AbstractDataBindingPage(WizardContext context,
			DataBindingContext bindingContext, String name, String title,
			ImageDescriptor image, String description) {
		super(name, title, image);
		this.context = context;
		this.bindingContext = bindingContext;
		setDescription(description);
	}

	protected boolean isActive() {
		return true;
	}

	protected Text createText(Composite root, String property,
			String propertyName, IValidator... validators) {
		return createText(root, BeansObservables
				.observeValue(context, property), propertyName, validators);
	}

	protected Text createTextForExt(Composite root, String property,
			String propertyName) {
		IObservableMap map = BeansObservables.observeMap(context, "extensions");
		IObservableValue entry = Observables.observeMapEntry(map, property,
				String.class);
		return createText(root, entry, propertyName);
	}

	private Text createText(Composite root, IObservableValue model,
			String propertyName, IValidator... validators) {
		Text text = new Text(root, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ISWTObservableValue view = SWTObservables.observeText(text, SWT.Modify);
		UpdateValueStrategy strategy = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		NonEmptyStringValidator validator = new NonEmptyStringValidator(
				propertyName);
		strategy.setBeforeSetValidator(validators.length == 0 ? validator
				: new CompoundValidator(validator, validators));
		bindingContext.bindValue(view, model, strategy, null);
		return text;
	}

	protected static void createLabel(Composite root, String text) {
		Label label = new Label(root, SWT.NONE);
		label.setText(text);
	}
}
