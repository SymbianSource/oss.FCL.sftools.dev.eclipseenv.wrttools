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
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class WRTProjectFilesWizardPage extends AbstractDataBindingPage {

	public WRTProjectFilesWizardPage(WizardContext context, DataBindingContext bindingContext) {
		super(context, bindingContext, "WRTApplicationFiles", "Application Files", null, "Specify application file names");
	}

	public void createControl(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		WizardPageSupport.create(this, bindingContext);
		root.setLayout(new GridLayout(2, false));

		createLabel(root, "Name of main HTML:");
		createText(root, WizardContext.HTML_FILE, "HTML file name");
		createLabel(root, "Name of CSS file:");
		createText(root, WizardContext.CSS_FILE, "CSS file name");
		createLabel(root, "Name of JavaScript file:");
		createText(root, WizardContext.JS_FILE, "JavaScript file name");
		
		setControl(root);
	}

}
