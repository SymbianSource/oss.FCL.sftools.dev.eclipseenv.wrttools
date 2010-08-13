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
package org.symbian.tools.tmw.internal.ui.deployment.externalapp;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.symbian.tools.tmw.core.projects.IMTWProject;
import org.symbian.tools.tmw.ui.deployment.IDeploymentTarget;
import org.symbian.tools.tmw.ui.deployment.IDeploymentTargetType;

public class FilesystemDeploymentTarget implements IDeploymentTargetType {

    public FilesystemDeploymentTarget() {
        // TODO Auto-generated constructor stub
    }

    public IDeploymentTarget[] getTargets(IMTWProject project) {
        // TODO Auto-generated method stub
        return null;
    }

    public void discoverTargets(IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub

    }

    public IDeploymentTarget findTarget(IMTWProject project, String id) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean targetsDiscovered() {
        // TODO Auto-generated method stub
        return false;
    }

    public ISchedulingRule getSchedulingRule(IDeploymentTarget target) {
        // TODO Auto-generated method stub
        return null;
    }

}
