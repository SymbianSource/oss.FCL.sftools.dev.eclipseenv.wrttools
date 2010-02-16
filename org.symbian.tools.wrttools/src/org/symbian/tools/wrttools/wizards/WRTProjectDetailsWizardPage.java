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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.symbian.tools.wrttools.util.RegexpValidator;

public class WRTProjectDetailsWizardPage extends AbstractDataBindingPage {
	private boolean isActive;

	public WRTProjectDetailsWizardPage(WizardContext context, DataBindingContext bindingContext) {
		super(context, bindingContext, "WRTApp", "Application Details", null, "Specify application details");
	}
	
	protected void addTemplateControls(Composite root) {
		// Subclasses will override
	}
	
	public void createControl(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		WizardPageSupport.create(this, bindingContext);
		root.setLayout(new GridLayout(2, false));
		createLabel(root, "Application name:");
		
		createText(root, WizardContext.WIDGET_NAME, "application name", new RegexpValidator("[^\\w\\. ]", "Application name cannot contain {0} character", false));
		
		createLabel(root, "");
		createLabel(root, "This will be the application display name on the device");
		createLabel(root, "");
		createLabel(root, "");
		createLabel(root, "Widget identifier:");

		createText(root, WizardContext.WIDGET_ID, "applicatoin identifier", new RegexpValidator("[\\w]*(\\.\\w[\\w]*)*", "{0} is not a valid applicatoin ID", true));
		createLabel(root, "");
		createLabel(root, "This id should be unique for succesful installation of application on the device");
		createLabel(root, "");
		createLabel(root, "");
		
		addTemplateControls(root);
		
		createLabel(root, "");
		Button homeScreen = new Button(root, SWT.CHECK);
		homeScreen.setText("Enable HomeScreen");
		
		IObservableValue view = SWTObservables.observeSelection(homeScreen);
		IObservableValue model = BeansObservables.observeValue(context, WizardContext.HOME_SCREEN);
		bindingContext.bindValue(view, model);
		
		setControl(root);
	}

	@Override
	protected boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
}
