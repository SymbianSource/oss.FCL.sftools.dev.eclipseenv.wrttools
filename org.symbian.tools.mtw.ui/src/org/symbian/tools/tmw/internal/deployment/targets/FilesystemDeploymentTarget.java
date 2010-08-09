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
package org.symbian.tools.tmw.internal.deployment.targets;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.ui.IMemento;
import org.symbian.tools.tmw.core.projects.IMTWProject;
import org.symbian.tools.tmw.core.runtimes.IPackager;
import org.symbian.tools.tmw.ui.TMWCoreUI;
import org.symbian.tools.tmw.ui.deployment.IDeploymentTarget;
import org.symbian.tools.tmw.ui.deployment.IDeploymentTargetType;

public class FilesystemDeploymentTarget extends PlatformObject implements IDeploymentTargetType, IDeploymentTarget {
    private String defaultName;
    private IPath path;

    public IStatus deploy(IMTWProject project, IPackager packager, IProgressMonitor monitor) throws CoreException {
        final File file = packager.packageApplication(project, monitor);
        try {
            final InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
            final OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(path.toFile()));
            try {
                final byte[] buffer = new byte[16384];
                int read = -1;
                while ((read = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, read);
                }
            } finally {
                inputStream.close();
                outputStream.close();
            }
        } catch (IOException e) {
            throw new CoreException(
                    new Status(IStatus.ERROR, TMWCoreUI.PLUGIN_ID, "Failed to copy application file", e));
        } finally {
            file.delete();
        }
        return Status.OK_STATUS;
    }

    public void discoverTargets(IProgressMonitor monitor) throws CoreException {
        // Do nothing
    }

    public IDeploymentTarget findTarget(IMTWProject project, String id) {
        return getId().equals(id) ? this : null;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public String getDescription() {
        return "Copies application package to a location in the local filesystem";
    }

    public String getId() {
        return "mtw.filesystem";
    }

    public String getName() {
        return "Local filesystem";
    }

    public IPath getPath() {
        return path;
    }

    public ISchedulingRule getSchedulingRule(IDeploymentTarget target) {
        return null;
    }

    public IDeploymentTarget[] getTargets(IMTWProject project) {
        return new IDeploymentTarget[] { this };
    }

    public void init(IMTWProject project, IPackager packager, IMemento memento) {
        defaultName = new Path(project.getName()).addFileExtension(packager.getFileType(project)).toOSString();
        path = null;
        String string = memento != null ? memento.getString("path") : null;
        if (string == null) {
            string = TMWCoreUI.getDefault().getPreferenceStore().getString("path");
            if (string != null) {
                final IPath p = Path.fromPortableString(string);
                if (p.toFile().isDirectory()) {
                    this.path = p.append(defaultName);
                }
            }
        } else {
            path = Path.fromPortableString(string);
            if (!path.removeLastSegments(1).toFile().isDirectory()) {
                path = null;
            }
        }
    }

    public void save(IMemento memento) {
        memento.putString("path", path.toPortableString());
        TMWCoreUI.getDefault().getPreferenceStore().setValue("path", path.removeLastSegments(1).toPortableString());
    }

    public void setPath(IPath path) {
        this.path = path;
    }

    public boolean targetsDiscovered() {
        return true;
    }

}
