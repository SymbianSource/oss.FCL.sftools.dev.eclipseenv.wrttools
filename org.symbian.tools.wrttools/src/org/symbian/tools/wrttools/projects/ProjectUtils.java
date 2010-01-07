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
package org.symbian.tools.wrttools.projects;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.wst.jsdt.internal.ui.wizards.buildpaths.BuildPathsBlock;
import org.eclipse.wst.validation.ValidationFramework;

public class ProjectUtils {
	public static IProject createWrtProject(String name, URI uri, IProgressMonitor monitor) throws CoreException {
		monitor.beginTask("Create project resources", 20);
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject project = workspace.getRoot().getProject(
				name);
		BuildPathsBlock.createProject(project, uri,
				new SubProgressMonitor(monitor, 10));

		BuildPathsBlock.addJavaNature(project, new SubProgressMonitor(monitor,
				10));

		// TODO: Build path, super type, etc.
		// BuildPathsBlock.flush(classPathEntries, javaScriptProject, superType,
		// monitor)

		ValidationFramework.getDefault().addValidationBuilder(project);

		monitor.done();
		return project;
	}
}
