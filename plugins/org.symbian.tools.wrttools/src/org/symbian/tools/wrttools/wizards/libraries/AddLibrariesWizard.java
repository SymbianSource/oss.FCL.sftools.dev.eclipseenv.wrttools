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
package org.symbian.tools.wrttools.wizards.libraries;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.core.libraries.JSLibrary;

public class AddLibrariesWizard extends Wizard {
    private final IProject project;
    private LibrarySelectionPage page;

    public AddLibrariesWizard(ISelection currentSelection) {
        IProject p = null;
        if (!currentSelection.isEmpty() && currentSelection instanceof IStructuredSelection) {
            Object sel = ((IStructuredSelection) currentSelection).getFirstElement();
            if (sel instanceof IAdaptable) {
                IResource res = (IResource) ((IAdaptable) sel).getAdapter(IResource.class);
                if (res != null) {
                    p = res.getProject();
                }
            }
        }
        project = p;
    }

    @Override
    public void addPages() {
        page = new LibrarySelectionPage(project);
        setWindowTitle("Add JavaScript Libraries");
        addPage(page);
    }

    @Override
    public boolean performFinish() {
        final IProject p = page.getProject();
        final JSLibrary[] libraries = page.getSelectedLibraries();
        try {
            getContainer().run(true, false, new IRunnableWithProgress() {
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask("Installing libraries", 50 * libraries.length);
                    for (JSLibrary jsLibrary : libraries) {
                        try {
                            jsLibrary.install(p, new HashMap<String, String>(), new SubProgressMonitor(monitor, 50));
                        } catch (CoreException e) {
                            Activator.log(e);
                        } catch (IOException e) {
                            Activator.log(e);
                        }
                    }
                }
            });
        } catch (InvocationTargetException e) {
            Activator.log(e);
        } catch (InterruptedException e) {
            Activator.log(e);
        }
        return true;
    }
}
