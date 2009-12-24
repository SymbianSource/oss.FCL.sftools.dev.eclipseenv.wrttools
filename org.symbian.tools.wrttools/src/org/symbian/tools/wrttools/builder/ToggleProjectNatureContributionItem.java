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
package org.symbian.tools.wrttools.builder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class ToggleProjectNatureContributionItem extends ContributionItem {

	public ToggleProjectNatureContributionItem() {
		// TODO Auto-generated constructor stub
	}
	
	public ToggleProjectNatureContributionItem(String id) {
		// TODO Auto-generated constructor stub
	}

	public void fill(Menu menu, int index) {
		final MenuItem menuItem = new MenuItem(menu, SWT.CHECK, index);
		menuItem.setText("Add/Remove propertiesAuditor project naute");
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				run();
			}
		});
		menu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				updateState(menuItem);
			}
		});
	}
	
	protected void updateState(MenuItem menuItem) {
		Collection<IProject> projects = getSelectedProjects();
		boolean enabled = projects.size() > 0;
		menuItem.setEnabled(enabled);
		menuItem.setSelection(enabled &&
				PropertiesAuditorNature.hasNature(projects.iterator().next()));
	}

	private Collection<IProject> getSelectedProjects() {
		// TODO Auto-generated method stub
		Collection<IProject> projects = new HashSet<IProject>();
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		ISelection selection = window.getActivePage().getSelection();
		if (selection instanceof IStructuredSelection) {
			for (Iterator<?> iter = ((IStructuredSelection) 
							selection).iterator(); iter.hasNext();) {
				// translate the selected object into a project
				Object elem = iter.next();
				if (!(elem instanceof IResource)) {
					if (!(elem instanceof IAdaptable))
					continue;
					elem = ((IAdaptable) elem).getAdapter(IResource.class);
					if (!(elem instanceof IResource))
						continue;
				}
				if (!(elem instanceof IProject)) {
					elem = ((IResource) elem).getProject();
					if (!(elem instanceof IProject))
						continue;
				}
				projects.add((IProject) elem);
			}
		}
		return projects;
	}

	protected void run() {
		// TODO Auto-generated method stub
		Collection<IProject> projects = getSelectedProjects();
		for (IProject project : projects) {
			/*
			 * The builder can be directly associated with a project
			 * without the need for a builder...
			 */
/*			if (false)
				toggleBuilder(project);
*/
			// ... or more typically a nature can be used
			// to associate a project with a builder
			if (true)
				toggleNature(project);
		}		
	}
	
	private void toggleNature(IProject project) {
		if (PropertiesAuditorNature.hasNature(project)) {
			PropertiesAuditorNature.removeNature(project);
		}
		else {
			PropertiesAuditorNature.addNature(project);
		}
	}
	
	private void toggleBuilder(final IProject project) {
    // If the project has the builder
	// then remove the builder and all audit markers
	if (PropertiesFileAuditor.hasBuilder(project)) {
		PropertiesFileAuditor.deleteAuditMarkers(project);
		PropertiesFileAuditor.removeBuilderFromProject(project);
	}
	// otherwise add the builder
	// and schedule an audit of the files
	else {
		PropertiesFileAuditor.addBuilderToProject(project);
		new Job("Properties File Audit") {
			protected IStatus run(IProgressMonitor monitor) {
			try {
				project.build(PropertiesFileAuditor.FULL_BUILD,
						PropertiesFileAuditor.BUILDER_ID, null, monitor);
			}
			catch (CoreException e) {
				// log the error
			}
			return Status.OK_STATUS;
		  }
	   }.schedule();
	}
  }
}
