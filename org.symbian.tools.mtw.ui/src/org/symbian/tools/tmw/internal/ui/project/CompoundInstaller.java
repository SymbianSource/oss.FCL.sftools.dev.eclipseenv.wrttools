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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.symbian.tools.tmw.internal.util.DelegateRunnable;
import org.symbian.tools.tmw.ui.project.IProjectTemplateContext;
import org.symbian.tools.tmw.ui.project.ITemplateInstaller;

public class CompoundInstaller implements ITemplateInstaller {
    private final class InstallerFiles {
        private final ITemplateInstaller installer;
        private final Collection<IPath> paths;

        public InstallerFiles(Collection<IPath> paths, ITemplateInstaller installer) {
            this.paths = paths;
            this.installer = installer;
        }
    }

    public static ITemplateInstaller combine(ITemplateInstaller installer, IConfigurationElement[] elements) {
        final ITemplateInstaller[] installers = getChildren(installer);
        final Collection<ITemplateInstaller> installerCollection = new ArrayList<ITemplateInstaller>(installers.length
                + elements.length);
        installerCollection.addAll(Arrays.asList(installers));
        installerCollection.addAll(fromExtensions(elements));
        return new CompoundInstaller(installerCollection.toArray(new ITemplateInstaller[installerCollection.size()]));
    }

    public static ITemplateInstaller combine(ITemplateInstaller installer1, ITemplateInstaller installer2) {
        final ITemplateInstaller[] children1 = getChildren(installer1);
        final ITemplateInstaller[] children2 = getChildren(installer2);
        final ITemplateInstaller[] result = new ITemplateInstaller[children1.length + children2.length];
        System.arraycopy(children1, 0, result, 0, children1.length);
        System.arraycopy(children2, 0, result, children1.length, children2.length);
        return new CompoundInstaller(result);
    }

    private static Collection<? extends ITemplateInstaller> fromExtensions(IConfigurationElement[] elements) {
        final Collection<ITemplateInstaller> result = new LinkedList<ITemplateInstaller>();
        for (IConfigurationElement element : elements) {
            if ("installer".equals(element.getName())) {
                result.add(new LazyInstaller(element));
            } else if ("archive".equals(element.getName())) {
                result.add(new ZipInstaller(element));
            }
        }
        return result;
    }

    private static ITemplateInstaller[] getChildren(ITemplateInstaller installer) {
        final ITemplateInstaller[] installers;
        if (installer == null) {
            installers = new ITemplateInstaller[0];
        } else if (installer instanceof CompoundInstaller) {
            installers = ((CompoundInstaller) installer).installers;
        } else {
            installers = new ITemplateInstaller[] { installer };
        }
        return installers;
    }

    private final Collection<InstallerFiles> files = new LinkedList<CompoundInstaller.InstallerFiles>();
    private final ITemplateInstaller[] installers;

    private CompoundInstaller(ITemplateInstaller[] installers) {
        this.installers = installers;
    }

    public void cleanup() {
        for (ITemplateInstaller installer : installers) {
            installer.cleanup();
        }
    }

    public void copyFiles(IPath[] files, IProgressMonitor monitor) throws CoreException {
        monitor.beginTask("Copying files", files.length * 10);
        final List<IPath> list = Arrays.asList(files);
        for (InstallerFiles fs : this.files) {
            final ITemplateInstaller installer = fs.installer;
            fs.paths.retainAll(list);
            IPath[] array = fs.paths.toArray(new IPath[fs.paths.size()]);
            installer.copyFiles(array, new SubProgressMonitor(monitor, array.length * 10));
        }
        monitor.done();
    }

    public IPath[] getFiles() throws CoreException {
        final Collection<IPath> paths = new HashSet<IPath>();
        for (ITemplateInstaller installer : installers) {
            final IPath[] f = installer.getFiles();
            final Collection<IPath> c = new HashSet<IPath>(Arrays.asList(f));
            c.removeAll(paths);
            files.add(new InstallerFiles(c, installer));
            paths.addAll(c);
        }
        return paths.toArray(new IPath[paths.size()]);
    }

    public void prepare(IProject project, IProjectTemplateContext context) {
        for (ITemplateInstaller installer : installers) {
            installer.prepare(project, context);
        }
    }

    public IRunnableWithProgress getPostCreateAction() {
        final Collection<IRunnableWithProgress> runnables = new LinkedList<IRunnableWithProgress>();
        for (ITemplateInstaller installer : installers) {
            final IRunnableWithProgress action = installer.getPostCreateAction();
            if (action != null) {
                runnables.add(action);
            }
        }
        return runnables.size() > 0 ? new DelegateRunnable(runnables) : null;
    }
}
