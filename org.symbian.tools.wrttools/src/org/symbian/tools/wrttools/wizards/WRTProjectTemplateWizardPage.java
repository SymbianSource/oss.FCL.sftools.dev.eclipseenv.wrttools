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
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
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

	public WRTProjectTemplateWizardPage(WizardContext context, DataBindingContext bindingContext) {
		super("WRTTemplate", "Application Template Selection", null);
		this.context = context;
		this.bindingContext = bindingContext;
		setDescription("Select template that will be used to populate your new project");
	}
	
	public void createControl(Composite parent) {
		ProjectTemplate[] allTemplates = ProjectTemplate.getAllTemplates();

		if (allTemplates.length == 1) {
			context.setTemplate(allTemplates[0]);
		}
		
		Composite composite = new Composite(parent, SWT.NONE);
		FormLayout layout = new FormLayout();
		layout.marginWidth = 5;
		composite.setLayout(layout);
		
		Label label = new Label(composite, SWT.NONE);
		label.setText("Choose a template");
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.top = new FormAttachment(0, 0);
		label.setLayoutData(data);
		
		templates = new TableViewer(composite, SWT.BORDER | SWT.SINGLE);
		FormData templatesData = new FormData();
		templatesData.top = new FormAttachment(label);
		templatesData.left = new FormAttachment(0, 0);
		templatesData.right = new FormAttachment(100, 0);
		templatesData.bottom = new FormAttachment(70, 0);
		templates.getControl().setLayoutData(templatesData);
		templates.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection)event.getSelection();
				final ProjectTemplate 
					template = (ProjectTemplate) selection.getFirstElement();
				refreshSelection(template);
			}
		});
		templates.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent arg0) {
				switchWizardPage();
			}
		});
		
		description = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT .READ_ONLY);
		FormData descriptionData = new FormData();
		descriptionData.top = new FormAttachment(templates.getControl(), 10);
		descriptionData.bottom = new FormAttachment(100, 0);
		descriptionData.left = new FormAttachment(0, 0);
		descriptionData.right = new FormAttachment(100, 0);
		description.setLayoutData(descriptionData);
		
		templates.setContentProvider(new ArrayContentProvider());
		templates.setLabelProvider(new ProjectTemplateLabelProvider());
		templates.setInput(allTemplates);
		
		setControl(composite);
		setPageComplete(false);

		IViewerObservableValue selection = ViewersObservables.observeSingleSelection(templates);
		IObservableValue property = BeansObservables.observeValue(context, WizardContext.TEMPLATE);
		
		bindingContext.bindValue(selection, property);
		if (context.getTemplate() != null) {
			refreshSelection(context.getTemplate());
		}
		setErrorMessage(null);
	}

	protected void switchWizardPage() {
		Display display = getShell().getDisplay();
		display.asyncExec(new Runnable() {
			public void run() {
				if (isPageComplete()) {
					IWizardPage nextPage = getWizard().getNextPage(WRTProjectTemplateWizardPage.this);
					getContainer().showPage(nextPage);
				}
			}
		});
	}

	protected void refreshSelection(ProjectTemplate template) {
		if (template != null) {
			setErrorMessage(null);
			setPageComplete(true);
			description.setText(template.getDescription());
		} else {
			setErrorMessage("Project template is not selected");
			setPageComplete(false);
			description.setText("");
		}
	}

	public ProjectTemplate getSelectedProjectTemplate() {
		IStructuredSelection selection = (IStructuredSelection) templates.getSelection();
		return (ProjectTemplate) selection.getFirstElement();
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		templates.getControl().setFocus();
	}
}
