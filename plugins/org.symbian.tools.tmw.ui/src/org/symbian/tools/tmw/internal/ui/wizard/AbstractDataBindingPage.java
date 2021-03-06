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
package org.symbian.tools.tmw.internal.ui.wizard;


import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;

public abstract class AbstractDataBindingPage extends WizardPage {
    public AbstractDataBindingPage(WizardContext context,
            DataBindingContext bindingContext, String name, String title,
            ImageDescriptor image, String description) {
        super(name, title, image);
        setDescription(description);
    }

    protected boolean isActive() {
        return true;
    }
}
