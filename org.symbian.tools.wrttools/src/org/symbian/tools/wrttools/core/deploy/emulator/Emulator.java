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
package org.symbian.tools.wrttools.core.deploy.emulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IMemento;
import org.symbian.tools.mtw.core.projects.IMTWProject;
import org.symbian.tools.mtw.core.runtimes.IPackager;
import org.symbian.tools.mtw.ui.deployment.IDeploymentTarget;
import org.symbian.tools.wrttools.Activator;

public class Emulator extends PlatformObject implements IDeploymentTarget {
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Emulator other = (Emulator) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (path == null) {
            if (other.path != null) {
                return false;
            }
        } else if (!path.equals(other.path)) {
            return false;
        }
        return true;
    }

    private final String id;
    private final String path;

    public Emulator(String id, String path) {
        this.id = id;
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return id;
    }

    public String getDescription() {
        return path;
    }

    public IStatus deploy(IMTWProject project, IPackager packager, IProgressMonitor monitor)
            throws CoreException {
        final File application = packager.packageApplication(project, monitor);
        final File outputFile = new File(path);
        if (!outputFile.isDirectory()) {
            outputFile.mkdir();
        }

        final File out = new File(outputFile + "/" + application.getName()); //$NON-NLS-1$
        deployWidget(application, out);
        return new Status(
                IStatus.OK,
                Activator.PLUGIN_ID,
                "Application was successfully deployed to emulator. You will need to complete installation process using your emulator UI.");
    }

    public void save(IMemento memento) {
        // Do nothing
    }

    public void load(IMemento memento) {
        // Do nothing
    }

    // helper methods 

    /**
     * Deploys the widget from the source to the destination path of the emulator.
     * @param inputFile the actual widget path from where widget needs to be deployed.
     * @param outputFile the path of the emulator where the widget will be deoplyed.
     */
    private void deployWidget(File inputFile, File outputFile) throws CoreException {
        try {
            InputStream in = new FileInputStream(inputFile);
            OutputStream out = new FileOutputStream(outputFile);
            int c;

            while ((c = in.read()) != -1) {
                out.write(c);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Application deployment failed", e));
        }
    }
}
