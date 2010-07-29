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
package org.symbian.tools.mtw.internal.deployment.externalapp;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.IMemento;
import org.symbian.tools.mtw.core.MTWCore;
import org.symbian.tools.mtw.core.projects.IMTWProject;
import org.symbian.tools.mtw.core.runtimes.IMobileWebRuntime;
import org.symbian.tools.mtw.core.runtimes.IPackager;
import org.symbian.tools.mtw.ui.deployment.IDeploymentTarget;
import org.symbian.tools.mtw.ui.deployment.IDeploymentTargetType;

public class ExternalApplicationDeploymentType extends PlatformObject implements IDeploymentTargetType,
        IDeploymentTarget {

    private IMTWProject project;

    public IStatus deploy(IMTWProject project, IMobileWebRuntime runtime, IProgressMonitor monitor)
            throws CoreException {
        Program app = getExternalApp(project);
        IPackager packager = MTWCore.getDefault().getRuntimesManager().getPackager(project, runtime);
        File file = packager.packageApplication(project, runtime, monitor);
        if (file != null) {
            app.execute(file.toString());
            return new Status(
                    IStatus.OK,
                    MTWCore.PLUGIN_ID,
                    "Mobile web application was passed as an argument to external application. Please follow its prompts to complete deployment.");
        } else {
            return new Status(IStatus.ERROR, MTWCore.PLUGIN_ID, "Application packaging failed");
        }
    }

    public void discoverTargets(IProgressMonitor monitor) throws CoreException {
        // Do nothing
    }

    public IDeploymentTarget findTarget(IMTWProject project, String id) {
        if (getExternalApp(project) != null) {
            return this;
        } else {
            return null;
        }
    }

    public String getDescription() {
        return getExternalApp(project).getName();
    }

    private Program getExternalApp(IMTWProject project) {
        this.project = project;
        IPackager packager = MTWCore.getDefault().getRuntimesManager().getPackager(project);
        if (packager != null) {
            return Program.findProgram(packager.getFileType(project));
        } else {
            return null;
        }
    }

    public String getId() {
        return "mtw.externalapp";
    }

    public String getName() {
        return "Use external application";
    }

    public Program getProgram() {
        return getExternalApp(project);
    }

    public ISchedulingRule getSchedulingRule(IDeploymentTarget target) {
        return null;
    }

    public IDeploymentTarget[] getTargets(IMTWProject project) {
        if (getExternalApp(project) != null) {
            return new IDeploymentTarget[] { this };
        } else {
            return null;
        }
    }

    public void load(IMemento memento) {
        // Do nothing
    }

    public void save(IMemento memento) {
        // Do nothing
    }

    public boolean targetsDiscovered() {
        return true;
    }
}
