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
package org.symbian.tools.tmw.ui.project;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.symbian.tools.tmw.internal.ui.wizard.CompoundValidator;
import org.symbian.tools.tmw.internal.ui.wizard.NonEmptyStringValidator;

/**
 * Boilerplate code to make it easier creating such wizard pages.
 *
 * @author Eugene Ostroukhov (eugeneo@symbian.org)
 */
public abstract class AbstractNewApplicationWizardPage extends WizardPage implements INewApplicationWizardPage {
    protected DataBindingContext bindingContext;
    protected final Collection<Binding> bindings = new LinkedList<Binding>();
    protected IProjectTemplateContext context;

    public AbstractNewApplicationWizardPage(String name, String title, String description) {
        super(name);
        setTitle(title);
        setDescription(description);
    }

    public void init(IProjectTemplateContext context, DataBindingContext bindingContext) {
        this.context = context;
        this.bindingContext = bindingContext;
    }

    public void remove() {
        for (Binding binding : bindings) {
            bindingContext.removeBinding(binding);
        }
    }

    protected void createLabel(Composite root, String text) {
        Label label = new Label(root, SWT.NONE);
        label.setText(text);
    }

    protected Text createText(Composite root, String property, String propertyName, DataBindingContext bindingContext,
            IValidator... validators) {
        Text text = new Text(root, SWT.BORDER);
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        ISWTObservableValue view = SWTObservables.observeText(text, SWT.Modify);
        UpdateValueStrategy strategy = new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);
        NonEmptyStringValidator validator = new NonEmptyStringValidator(propertyName);
        strategy.setBeforeSetValidator(validators.length == 0 ? validator
                : new CompoundValidator(validator, validators));
        bindings.add(bindingContext.bindValue(view, context.getParameterObservable(property), strategy, null));
        return text;
    }
}
