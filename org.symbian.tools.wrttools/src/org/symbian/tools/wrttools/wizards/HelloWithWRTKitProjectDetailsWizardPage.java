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
import org.eclipse.swt.widgets.Composite;

public class HelloWithWRTKitProjectDetailsWizardPage extends WRTProjectDetailsWizardPage {

	public HelloWithWRTKitProjectDetailsWizardPage(WizardContext context,
			DataBindingContext bindingContext) {
		super(context, bindingContext);
	}
	
	@Override
	protected void addTemplateControls(Composite root) {
	}

	public static final class Factory implements IWizardPageFactory { 
		public WRTProjectDetailsWizardPage createPage(WizardContext context,
				DataBindingContext bindingContext) {
			return new HelloWithWRTKitProjectDetailsWizardPage(context, bindingContext);
		}
	}
}
