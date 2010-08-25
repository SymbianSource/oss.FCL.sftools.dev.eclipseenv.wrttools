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
package org.symbian.tools.wrttools.core.project;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.projects.ITMWProject;
import org.symbian.tools.tmw.ui.ProjectMemo;
import org.symbian.tools.tmw.ui.project.IProjectTemplateContext;
import org.symbian.tools.tmw.ui.project.ITemplateInstaller;

public class SetResolution implements ITemplateInstaller {
    private IProject project;

    public void prepare(IProject project, IProjectTemplateContext context) {
        this.project = project;
    }

    public void cleanup() {
        project = null;
    }

    public IPath[] getFiles() throws CoreException {
        return new IPath[0];
    }

    public void copyFiles(IPath[] files, IProgressMonitor monitor) throws CoreException {
        // Do nothing
    }

    public IRunnableWithProgress getPostCreateAction() {
        final IProject p = project;
        return new IRunnableWithProgress() {
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                ITMWProject tmw = TMWCore.create(p);
                new ProjectMemo(tmw).setAttribute("resolution", "360x640");
            }
        };
    }

}
