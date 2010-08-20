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

package org.symbian.tools.wrttools.core.packager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.util.ProjectUtils;

public class ExcludeFileAction implements IObjectActionDelegate {
	
	private final List<IFile> selectedFiles = new ArrayList<IFile>();
	private final List<IFolder> selectedFolders = new ArrayList<IFolder>();
	public ExcludeFileAction() {
		super();
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void run(IAction action) {
		for (IFile file : selectedFiles) {
            ProjectUtils.exclude(file);
		}
		
		for(IFolder folder : selectedFolders){
			try{
                ProjectUtils.exclude(folder);
				excludeFolder( folder);
			}
			catch (CoreException x) {
				Activator.log(IStatus.ERROR, "error setting exclude property on folder: "+folder.getName(), x);
			}
		}
		/*Refresh project tree when property is changed */ 
		PlatformUI.getWorkbench().getDecoratorManager().update("org.symbian.tools.wrttools.decorator"); 
	}
	
    @SuppressWarnings("rawtypes")
    public void selectionChanged(IAction action, ISelection selection) {
		selectedFiles.clear();
		selectedFolders.clear();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			for (Iterator iter = ss.iterator(); iter.hasNext();) {
				Object obj = iter.next();
				if (obj instanceof IFile) {
					
					IFile file = (IFile) obj;
					selectedFiles.add(file);
					
				}
				
				if(obj instanceof IFolder){
					IFolder folder = (IFolder) obj;
					selectedFolders.add(folder);
				}
			}
		}
	}
	
	
	/**
	 * 
	 * @param folder
	 * @throws CoreException
	 */

	private void excludeFolder(IFolder folder) throws CoreException{
		folder.accept(new IResourceVisitor() {
			public boolean visit(IResource resource)throws CoreException {
				if (resource instanceof IFile) {
					IFile file = (IFile) resource;
                    ProjectUtils.exclude(file);
				}
				else if (resource instanceof IFolder) {
					IFolder folder = (IFolder) resource;
                    ProjectUtils.exclude(folder);
				}
				return true;
			}
		});
	}	
}
