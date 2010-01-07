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

public class WRTProjectDetailsWizardPage extends AbstractDataBindingPage {
	private boolean isActive;

	public WRTProjectDetailsWizardPage(WizardContext context, DataBindingContext bindingContext) {
		super(context, bindingContext, "WRTWidget", "New Symbian Widget Project", null, "Specify Symbian WRT widget details");
	}
	
	protected void addTemplateControls(Composite root) {
		// Subclasses will override
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		WizardPageSupport.create(this, bindingContext);
		root.setLayout(new GridLayout(2, false));
		createLabel(root, "Widget name:");
		
		createText(root, WizardContext.WIDGET_NAME, "widget name");
		
		createLabel(root, "");
		createLabel(root, "This will be the widget's display name on the device");
		createLabel(root, "");
		createLabel(root, "");
		createLabel(root, "Widget identifier:");

		createText(root, WizardContext.WIDGET_ID, "widget identifier");
		createLabel(root, "");
		createLabel(root, "This id should be unique for succesful installation of widget on the device");
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
