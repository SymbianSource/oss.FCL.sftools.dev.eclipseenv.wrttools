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
package org.symbian.tools.tmw.internal.ui.project;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.symbian.tools.tmw.ui.TMWCoreUI;

public class PrepareProjectJob extends Job {
    private final IProject project;
    private final IRunnableWithProgress action;

    public PrepareProjectJob(IProject project, IRunnableWithProgress action) {
        super(String.format("Preparing project %s", project.getName()));
        this.project = project;
        this.action = action;
        setUser(false);
        setRule(ResourcesPlugin.getWorkspace().getRoot());
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask("Prepare project files", 200);
        try {
            touchFiles();
        } catch (CoreException e) {
            TMWCoreUI.log(e);
        }
        if (action != null) {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    try {
                        action.run(new NullProgressMonitor());
                    } catch (InvocationTargetException e) {
                        TMWCoreUI.log(e);
                    } catch (InterruptedException e) {
                        TMWCoreUI.log(e);
                    }
                }
            });
        }
        monitor.done();
        return Status.OK_STATUS;
    }

    protected void touchFiles() throws CoreException {
        try {
            Thread.sleep(4000); // This is plain wrong. But we have concurrency issues in JSDT/Eclipse build system
        } catch (InterruptedException e) {
            TMWCoreUI.log(e);
        }
        project.accept(new IResourceVisitor() {
            public boolean visit(IResource resource) throws CoreException {
                if (resource.isAccessible() && resource.getType() == IResource.FILE
                        && resource.getFileExtension().equals("js")) {
                    resource.touch(new NullProgressMonitor());
                }
                return true;
            }
        });
        try {
            Thread.sleep(1000); // This is plain wrong. But we have concurrency issues in JSDT/Eclipse build system
        } catch (InterruptedException e) {
            TMWCoreUI.log(e);
        }
        project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
    }

}
