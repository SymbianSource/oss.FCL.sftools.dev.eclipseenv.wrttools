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
package org.symbian.tools.wrttools.wizards.projectimport;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.symbian.tools.wrttools.util.ProjectUtils;

public class AptanaProjectsImportWizard extends Wizard implements
		IImportWizard, INewWizard, IExecutableExtension {

	private AptanaProjectLocationWizardPage mainPage;
	private IConfigurationElement config;

	public AptanaProjectsImportWizard() {
		setWindowTitle("Import Aptana Project");
		setNeedsProgressMonitor(true);
	}

	@Override
	public boolean performCancel() {
		mainPage.performCancel();
		return true;
	}

	@Override
	public boolean performFinish() {
		if (mainPage.createProjects()) {
			BasicNewProjectResourceWizard.updatePerspective(config);
			List<IProject> projects = mainPage.getCreatedProjects();
				ProjectUtils.focusOn(projects.toArray(new IProject[projects.size()]));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void addPages() {
		mainPage = new AptanaProjectLocationWizardPage();
		addPage(mainPage);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// Do nothing
	}

	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		this.config = config;
	}
}
