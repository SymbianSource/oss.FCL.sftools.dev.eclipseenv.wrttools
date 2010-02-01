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

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.List;
import org.symbian.tools.wrttools.builder.PropertiesFileAuditor;


public class PropertiesAuditorNature implements IProjectNature {

	private IProject project;
	private static final String NATURE_ID = "org.eclipse.wst.jsdt.core.jsNature";
	
	public void configure() throws CoreException {
		// TODO Auto-generated method stub
		PropertiesFileAuditor.addBuilderToProject(project); 
		new Job("Properties File Audit") {
			protected IStatus run(IProgressMonitor monitor) {
				try {
					project.build(
							PropertiesFileAuditor.FULL_BUILD,
							PropertiesFileAuditor.BUILDER_ID,
							null,
							monitor);
				}
				catch (CoreException e) {
					// log the logError e
				}
				return Status.OK_STATUS;
			}
		}.schedule();

	}

	public void deconfigure() throws CoreException {
		// TODO Auto-generated method stub
		PropertiesFileAuditor.removeBuilderFromProject(project);
		PropertiesFileAuditor.deleteAuditMarkers(project);
	}

	public IProject getProject() {
		// TODO Auto-generated method stub
		return project;
	}

	public void setProject(IProject project) {
		// TODO Auto-generated method stub
		this.project = project;

	}
	
	public static void addNature(IProject project){
		// Cannot modify closed projects
		if (!project.isOpen())
			return;
		// Get the description
		IProjectDescription description;
		try {
			description = project.getDescription();
		}
		catch (CoreException e) {
			// log the error
			return;
		}
		// Determine if the project already has the nature
		ArrayList<String> newIds = new ArrayList<String>();
		newIds.addAll(Arrays.asList(description.getNatureIds()));
		int index = newIds.indexOf(NATURE_ID);
		if (index != -1) 
			return;
		
		// Add the nature
		newIds.add(NATURE_ID);
		description.setNatureIds(newIds.toArray(new String[newIds.size()]));
		
		// save the description
		try {
			project.setDescription(description, null);
		}
		catch (CoreException e) {
			// log the error
		}
	}

	public static boolean hasNature(IProject project) {
		// TODO Auto-generated method stub
		try {
			return project.isOpen() && project.hasNature(NATURE_ID);
		}
		catch (CoreException e) {
			// log the error
			return false;
		}
	}
	
	public static void removeNature(IProject project) {
		// Cannot modify closed projects
		if (!project.isOpen())
			return;
		
		// Get the description
		IProjectDescription description;
		try {
			description = project.getDescription();
		}
		catch (CoreException e) {
			// log the error
			return;
		}
		
		// Determine if the project has the nature
		ArrayList<String> newIds = new ArrayList<String>();
		newIds.addAll(Arrays.asList(description.getNatureIds()));
		int index = newIds.indexOf(NATURE_ID);
		
		if (index == -1)
			return;
		
		// Remove the nature
		newIds.remove(index);
		description.setNatureIds(newIds.toArray(
				new String[newIds.size()]));
		
		// save the description
		try {
			project.setDescription(description, null);
		}
		catch (CoreException e) {
			// log the error
		}
	}
}
