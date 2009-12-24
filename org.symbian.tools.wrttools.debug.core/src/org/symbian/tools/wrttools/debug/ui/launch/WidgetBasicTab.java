/*******************************************************************************
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
 *******************************************************************************/
package org.symbian.tools.wrttools.debug.ui.launch;

import java.util.LinkedList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.symbian.tools.wrttools.debug.internal.Activator;
import org.symbian.tools.wrttools.debug.internal.ChromeDebugUtils;
import org.symbian.tools.wrttools.debug.internal.IConstants;
import org.symbian.tools.wrttools.debug.internal.Images;

public class WidgetBasicTab extends AbstractLaunchConfigurationTab {
	private ComboViewer project;
	private boolean canSave;

	@Override
	public Image getImage() {
		return Images.getWrtIconImage();
	}

	public void createControl(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		FormLayout layout = new FormLayout();
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		layout.spacing = 5;
		root.setLayout(layout);

		Label label = new Label(root, SWT.NONE);
		label.setText("WRT Widget Project:");

		project = new ComboViewer(root, SWT.READ_ONLY);
		project.setContentProvider(new ArrayContentProvider());
		project.setLabelProvider(new WorkbenchLabelProvider());

		FormData formData = new FormData();
		formData.left = new FormAttachment(label);
		formData.right = new FormAttachment(100, 0);
		project.getControl().setLayoutData(formData);

		project.setInput(getWidgetProjects());
		project.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				setDirty(true);
				validate();
			}
		});
		setControl(root);
	}

	private IProject[] getWidgetProjects() {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
				.getProjects();
		LinkedList<IProject> filtered = new LinkedList<IProject>();
		for (IProject p : projects) {
			if (ChromeDebugUtils.isWidgetProject(p)) {
				filtered.add(p);
			}
		}
		return filtered.toArray(new IProject[filtered.size()]);
	}

	public String getName() {
		return "WRT Widget";
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		project.setSelection(getSelectedProject(configuration), true);
		validate();
		setErrorMessage(null);
	}

	private ISelection getSelectedProject(ILaunchConfiguration configuration) {
		ISelection selected = StructuredSelection.EMPTY;
		try {
			String projectName = configuration.getAttribute(
					IConstants.PROP_PROJECT_NAME, (String) null);
			if (projectName != null) {
				IProject p = ResourcesPlugin.getWorkspace().getRoot()
						.getProject(projectName);
				if (p.isAccessible()) {
					selected = new StructuredSelection(p);
				}
			}
		} catch (CoreException e) {
			Activator.log(e);
		}
		return selected;
	}

	@Override
	public boolean canSave() {
		return canSave;
	}

	private void validate() {
		String error = null;
		if (getWidgetProjects().length == 0) {
			error = "No WRT widget projects found in the workspace";
		} else if (project.getSelection().isEmpty()) {
			error = "Select WRT widget project to debug";
		} else if (ChromeDebugUtils.getChromeExecutible() == null) {
			error = "No Chrome browser configured in the preferences";
		}
		canSave = error == null;
		setErrorMessage(error);
		getLaunchConfigurationDialog().updateButtons();
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		ISelection selection = project.getSelection();
		if (selection.isEmpty()) {
			setDefaults(configuration);
		} else {
			IProject p = (IProject) ((IStructuredSelection) selection)
					.getFirstElement();
			configuration.setAttribute(IConstants.PROP_PROJECT_NAME, p
					.getName());
			configuration.setMappedResources(new IResource[] { p });
		}
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.removeAttribute(IConstants.PROP_PROJECT_NAME);
	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		return !getSelectedProject(launchConfig).isEmpty();
	}
}
