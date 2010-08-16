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

import java.text.MessageFormat;
import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.symbian.tools.tmw.ui.project.INewApplicationWizardPage;
import org.symbian.tools.tmw.ui.project.IProjectTemplate;
import org.symbian.tools.tmw.ui.project.IProjectTemplateContext;
import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.util.CompoundValidator;

public class NewFlickrApplicationWizardPage extends WizardPage implements INewApplicationWizardPage {
    private static class NonEmptyStringValidator implements IValidator {
        private final String propertyName;

        public NonEmptyStringValidator(String propertyName) {
            this.propertyName = propertyName;
        }

        public IStatus validate(Object value) {
            if (value == null || value.toString().trim().length() == 0) {
                return new Status(IStatus.ERROR, Activator.PLUGIN_ID, MessageFormat.format("Field {0} is empty",
                        propertyName));
            }
            return Status.OK_STATUS;
        }
    }
    private DataBindingContext bindingContext;
    private final Collection<Binding> bindings = new LinkedList<Binding>();
    private IProjectTemplateContext context;

    public NewFlickrApplicationWizardPage() {
        super("FlickrFiles");
        setTitle("Application Files");
        setDescription("Specify application file names");
    }

    protected void addTemplateControls(Composite root) {
        createLabel(root, "Flickr URL:");
        createText(root, "flickrUrl", "Flickr URL", bindingContext);
        createLabel(root, "");
        createLabel(root, "");
    }

    public void createControl(Composite parent) {
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
        IObservableValue model = context.getParameterObservable(WizardContext.HOME_SCREEN);
        bindingContext.bindValue(view, model);

        addTemplateControls(root);

        setControl(root);
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

    public void init(IProjectTemplateContext context, DataBindingContext bindingContext) {
        this.context = context;
        this.bindingContext = bindingContext;
    }

    public void remove() {
        for (Binding binding : bindings) {
            bindingContext.removeBinding(binding);
        }
    }
}
