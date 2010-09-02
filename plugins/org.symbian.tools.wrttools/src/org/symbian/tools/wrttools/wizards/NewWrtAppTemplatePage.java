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

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.symbian.tools.tmw.ui.project.AbstractNewApplicationWizardPage;
import org.symbian.tools.tmw.ui.project.IProjectTemplate;

/**
 * Entry for the basic template parameters.
 *
 * @author Eugene Ostroukhov (eugeneo@symbian.org)
 */
public class NewWrtAppTemplatePage extends AbstractNewApplicationWizardPage {

    public NewWrtAppTemplatePage() {
        this("WRTApplicationFiles", "Application Files", "Specify application file names");
    }

    protected NewWrtAppTemplatePage(String name, String title, String description) {
        super(name, title, description);
    }

    public final void createControl(Composite parent) {
        Composite root = new Composite(parent, SWT.NONE);
        WizardPageSupport.create(this, bindingContext);
        root.setLayout(new GridLayout(2, false));

        createLabel(root, "Name of main HTML:");
        createText(root, IProjectTemplate.CommonKeys.main_html, "HTML file name", bindingContext);
        createLabel(root, "");
        createLabel(root, "");
        createLabel(root, "Name of CSS file:");
        createText(root, IProjectTemplate.CommonKeys.main_css, "CSS file name", bindingContext);
        createLabel(root, "");
        createLabel(root, "");
        createLabel(root, "Name of JavaScript file:");
        createText(root, IProjectTemplate.CommonKeys.main_js, "JavaScript file name", bindingContext);

        createLabel(root, "");
        Button homeScreen = new Button(root, SWT.CHECK);
        homeScreen.setText("Enable HomeScreen");

        createLabel(root, "");
        createLabel(root, "");

        IObservableValue view = SWTObservables.observeSelection(homeScreen);
        IObservableValue model = context.getParameterObservable("homeScreen");
        bindingContext.bindValue(view, model);

        addTemplateControls(root);

        setControl(root);
    }

    protected void addTemplateControls(Composite root) {
        // Subclasses may override
    }
}
