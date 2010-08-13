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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.symbian.tools.tmw.ui.TMWCoreUI;
import org.symbian.tools.tmw.ui.project.IProjectTemplateContext;
import org.symbian.tools.tmw.ui.project.ITemplateInstaller;

public final class LazyInstaller implements ITemplateInstaller {
    private final class NullInstaller implements ITemplateInstaller {
        public void cleanup() {
            // Do nothing
        }

        public void copyFiles(IPath[] files, IProgressMonitor monitor) {
            // Do nothing
        }

        public IPath[] getFiles() throws CoreException {
            return new IPath[0];
        }

        public void prepare(IProject project, IProjectTemplateContext context) {
            // Do nothing
        }
    }
    private final IConfigurationElement element;
    private ITemplateInstaller installer;

    public LazyInstaller(IConfigurationElement element) {
        this.element = element;
    }

    public void cleanup() {
        getInstaller().cleanup();
    }

    public void copyFiles(IPath[] files, IProgressMonitor monitor) throws CoreException {
        getInstaller().copyFiles(files, monitor);
    }

    public IPath[] getFiles() throws CoreException {
        return getInstaller().getFiles();
    }

    private ITemplateInstaller getInstaller() {
        if (installer == null) {
            try {
                installer = (ITemplateInstaller) element.createExecutableExtension("class");
            } catch (CoreException e) {
                TMWCoreUI.log(e);
                installer = new NullInstaller();
            }
        }
        return installer;
    }

    public void prepare(IProject project, IProjectTemplateContext context) {
        getInstaller().prepare(project, context);
    }
}
