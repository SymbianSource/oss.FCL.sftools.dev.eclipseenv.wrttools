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

public class WRTProjectFilesWizardPage extends AbstractDataBindingPage {
    private boolean isActive;
    protected final DataBindingContext bindingContext;
    protected final WizardContext context;

    public WRTProjectFilesWizardPage(WizardContext context,
            DataBindingContext bindingContext) {
        super(context, bindingContext, "WRTApplicationFiles",
                "Application Files", null, "Specify application file names");
        this.context = context;
        this.bindingContext = bindingContext;
    }

    protected void addTemplateControls(Composite root) {
        // Subclasses will override
    }

    public void createControl(Composite parent) {
        Composite root = new Composite(parent, SWT.NONE);
        WizardPageSupport.create(this, bindingContext);
        root.setLayout(new GridLayout(2, false));

        context.createLabel(root, "Name of main HTML:");
        context.createText(root, WizardContext.HTML_FILE, "HTML file name",
                bindingContext, this);
        context.createLabel(root, "");
        context.createLabel(root, "");
        context.createLabel(root, "Name of CSS file:");
        context.createText(root, WizardContext.CSS_FILE, "CSS file name",
                bindingContext, this);
        context.createLabel(root, "");
        context.createLabel(root, "");
        context.createLabel(root, "Name of JavaScript file:");
        context.createText(root, WizardContext.JS_FILE, "JavaScript file name",
                bindingContext, this);

        context.createLabel(root, "");
        Button homeScreen = new Button(root, SWT.CHECK);
        homeScreen.setText("Enable HomeScreen");

        context.createLabel(root, "");
        context.createLabel(root, "");

        IObservableValue view = SWTObservables.observeSelection(homeScreen);
        IObservableValue model = BeansObservables.observeValue(context,
                WizardContext.HOME_SCREEN);
        bindingContext.bindValue(view, model);

        addTemplateControls(root);

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
