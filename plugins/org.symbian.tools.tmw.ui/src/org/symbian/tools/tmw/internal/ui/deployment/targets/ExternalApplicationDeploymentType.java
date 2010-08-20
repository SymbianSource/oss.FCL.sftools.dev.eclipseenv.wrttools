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
package org.symbian.tools.tmw.internal.ui.deployment.targets;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.IMemento;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.projects.ITMWProject;
import org.symbian.tools.tmw.core.runtimes.IPackager;
import org.symbian.tools.tmw.ui.deployment.IDeploymentTarget;
import org.symbian.tools.tmw.ui.deployment.IDeploymentTargetType;

public class ExternalApplicationDeploymentType extends PlatformObject implements IDeploymentTargetType,
        IDeploymentTarget {

    private ITMWProject project;

    public IStatus deploy(ITMWProject project, IPackager packager, IProgressMonitor monitor)
            throws CoreException {
        Program app = getExternalApp(project);
        File file = packager.packageApplication(project, monitor);
        if (file != null) {
            app.execute(file.toString());
            return new Status(
                    IStatus.OK,
                    TMWCore.PLUGIN_ID,
                    "Mobile web application was passed as an argument to external application. Please follow its prompts to complete deployment.");
        } else {
            return new Status(IStatus.ERROR, TMWCore.PLUGIN_ID, "Application packaging failed");
        }
    }

    public void discoverTargets(IProgressMonitor monitor) throws CoreException {
        // Do nothing
    }

    public IDeploymentTarget findTarget(ITMWProject project, String id) {
        if (getExternalApp(project) != null) {
            return this;
        } else {
            return null;
        }
    }

    public String getDescription() {
        return getExternalApp(project).getName();
    }

    private Program getExternalApp(ITMWProject project) {
        this.project = project;
        IPackager packager = TMWCore.getRuntimesManager().getPackager(project);
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

    public IDeploymentTarget[] getTargets(ITMWProject project) {
        if (getExternalApp(project) != null) {
            return new IDeploymentTarget[] { this };
        } else {
            return null;
        }
    }

    public void init(ITMWProject project, IPackager packager, IMemento memento) {
        // Do nothing
    }

    public void save(IMemento memento) {
        // Do nothing
    }

    public boolean targetsDiscovered() {
        return true;
    }
}
