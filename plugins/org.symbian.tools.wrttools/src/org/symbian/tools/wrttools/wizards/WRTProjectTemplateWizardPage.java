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
import org.eclipse.jface.databinding.viewers.IViewerObservableValue;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.symbian.tools.wrttools.core.ProjectTemplate;

public class WRTProjectTemplateWizardPage extends WizardPage {
    public class ProjectTemplateLabelProvider extends LabelProvider {

        @Override
        public Image getImage(Object element) {
            return ((ProjectTemplate) element).getIcon();
        }

        @Override
        public String getText(Object element) {
            return ((ProjectTemplate) element).getName();
        }
    }

    private TableViewer templates;
    private Text description;
    private final WizardContext context;
    private final DataBindingContext bindingContext;

    public WRTProjectTemplateWizardPage(WizardContext context,
            DataBindingContext bindingContext) {
        super("Create a New Mobile Web Application");
        setTitle("Create a New Mobile Web Application");
        this.context = context;
        this.bindingContext = bindingContext;
        setDescription("Select project name and template that will be used to populate");
    }

    public void createControl(Composite parent) {
        ProjectTemplate[] allTemplates = ProjectTemplate.getAllTemplates();

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
                true, true));
        FormLayout layout = new FormLayout();
        layout.marginWidth = 5;
        composite.setLayout(layout);

        templates = new TableViewer(composite, SWT.BORDER | SWT.SINGLE);
        FormData templatesData = new FormData();
        templatesData.top = new FormAttachment(0, 0);
        templatesData.left = new FormAttachment(0, 0);
        templatesData.right = new FormAttachment(40, -2);
        templatesData.bottom = new FormAttachment(100, -8);
        templates.getControl().setLayoutData(templatesData);
        templates.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event
                        .getSelection();
                final ProjectTemplate template = (ProjectTemplate) selection
                        .getFirstElement();
                refreshSelection(template);
            }
        });
        templates.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent arg0) {
                switchWizardPage();
            }
        });

        description = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP
                | SWT.READ_ONLY);
        FormData descriptionData = new FormData();
        descriptionData.top = new FormAttachment(0, 0);
        descriptionData.bottom = new FormAttachment(100, -8);
        descriptionData.left = new FormAttachment(templates.getControl(), 5);
        descriptionData.right = new FormAttachment(100, 0);
        descriptionData.width = 50;
        description.setLayoutData(descriptionData);

        templates.setContentProvider(new ArrayContentProvider());
        templates.setLabelProvider(new ProjectTemplateLabelProvider());
        templates.setSorter(new ViewerSorter() {
            @Override
            public int category(Object element) {
                return Integer.valueOf(((ProjectTemplate) element).getOrder());
            }
        });
        templates.setInput(allTemplates);

        setPageComplete(false);

        IViewerObservableValue selection = ViewersObservables
                .observeSingleSelection(templates);
        IObservableValue property = BeansObservables.observeValue(context,
                WizardContext.TEMPLATE);

        bindingContext.bindValue(selection, property);
        if (context.getTemplate() != null) {
            refreshSelection(context.getTemplate());
        }
        setErrorMessage(null);
        setControl(composite);
    }

    protected void switchWizardPage() {
        Display display = getShell().getDisplay();
        display.asyncExec(new Runnable() {
            public void run() {
                if (isPageComplete()) {
                    IWizardPage nextPage = getWizard().getNextPage(
                            WRTProjectTemplateWizardPage.this);
                    getContainer().showPage(nextPage);
                }
            }
        });
    }

    protected void refreshSelection(ProjectTemplate template) {
        if (template != null) {
            description.setText(template.getDescription());
        } else {
            description.setText("");
        }
        validatePage();
    }

    protected boolean validatePage() {
        if (templates.getSelection().isEmpty()) {
            setErrorMessage("Project template is not selected");
            setPageComplete(false);
            return false;
        } else {
            setErrorMessage(null);
            setPageComplete(true);
            return true;
        }
    }

    public ProjectTemplate getSelectedProjectTemplate() {
        IStructuredSelection selection = (IStructuredSelection) templates
                .getSelection();
        return (ProjectTemplate) selection.getFirstElement();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        templates.getControl().setFocus();
    }
}
