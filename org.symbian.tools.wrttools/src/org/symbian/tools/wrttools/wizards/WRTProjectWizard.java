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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import java.io.*;
import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;
//import org.eclipse.ui.internal.ide.dialogs.IDEResourceInfoUtils;
//import org.symbian.tools.wrttools.wizards.IWizardData;


	/**
	 * This is a sample new wizard. Its role is to create a new file 
	 * resource in the provided container. If the container resource
	 * (a folder or a project) is selected in the workspace 
	 * when the wizard is opened, it will accept it as the target
	 * container. The wizard creates one file with the extension
	 * "mpe". If a sample multi-page editor (also available
	 * as a template) is registered for the same extension, it will
	 * be able to open it.
	 */

	@SuppressWarnings("restriction")
	public class WRTProjectWizard extends Wizard implements INewWizard {
		private WRTProjectWizardPage page;
		private ISelection selection;
		

		/**
		 * Constructor for SampleNewWizard.
		 */
		public WRTProjectWizard() {
			super();
			setNeedsProgressMonitor(true);
		}
		
		/**
		 * Adding the page to the wizard.
		 */

		@SuppressWarnings("restriction")
		public void addPages() {
			page = new WRTProjectWizardPage(selection);
			addPage(page);
		}

		/**
		 * This method is called when 'Finish' button is pressed in
		 * the wizard. We will create an operation and run it
		 * using wizard as execution context.
		 */
		public boolean performFinish() {
			final String containerName = page.getContainerName();
			final String fileName = page.getFileName();
			IRunnableWithProgress op = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException {
					try {
						doFinish(containerName, fileName, monitor);
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					} finally {
						monitor.done();
					}
				}
			};
			try {
				getContainer().run(true, false, op);
			} catch (InterruptedException e) {
				return false;
			} catch (InvocationTargetException e) {
				Throwable realException = e.getTargetException();
				MessageDialog.openError(getShell(), "Error", realException.getMessage());
				return false;
			}
			return true;
		}
		
		/**
		 * The worker method. It will find the container, create the
		 * file if missing or just replace its contents, and open
		 * the editor on the newly created file.
		 */

		private void doFinish(
			String containerName,
			String fileName,
			IProgressMonitor monitor)
			throws CoreException {
			// create a sample file
			monitor.beginTask("Creating " + fileName, 2);
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IResource resource = root.findMember(new Path(containerName));
			if (!resource.exists() || !(resource instanceof IContainer)) {
				throwCoreException("Container \"" + containerName + "\" does not exist.");
			}
			IContainer container = (IContainer) resource;
			final IFile file = container.getFile(new Path(fileName));
			try {
				InputStream stream = openContentStream();
				if (file.exists()) {
					file.setContents(stream, true, true, monitor);
				} else {
					file.create(stream, true, monitor);
				}
				stream.close();
			} catch (IOException e) {
			}
			monitor.worked(1);
			monitor.setTaskName("Opening file for editing...");
			getShell().getDisplay().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage page =
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					try {
						IDE.openEditor(page, file, true);
					} catch (PartInitException e) {
					}
				}
			});
			monitor.worked(1);
		}
		
		/**
		 * We will initialize file contents with a sample text.
		 */

		private InputStream openContentStream() {
			String contents =
				"functions checkError(message, resultList, divId, imgId) { } ";
			return new ByteArrayInputStream(contents.getBytes());
		}

		private void throwCoreException(String message) throws CoreException {
			IStatus status =
				new Status(IStatus.ERROR, "org.symbian.tools.wrttools", IStatus.OK, message, null);
			throw new CoreException(status);
		}

		/**
		 * We will accept the selection in the workbench to see if
		 * we can initialize from it.
		 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
		 */
		public void init(IWorkbench workbench, IStructuredSelection selection) {
			this.selection = selection;
		}
	}
