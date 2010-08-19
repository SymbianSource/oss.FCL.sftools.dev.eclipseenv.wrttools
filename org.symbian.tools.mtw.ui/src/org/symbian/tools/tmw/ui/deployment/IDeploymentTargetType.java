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
package org.symbian.tools.tmw.ui.deployment;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.symbian.tools.tmw.core.projects.ITMWProject;

/**
 * This interface is for deployment targets providers.
 * 
 * @author Eugene
 */
public interface IDeploymentTargetType {
    /**
     * Returns list of the targets that accept provided project. Project 
     * cannot be <code>null</code>.
     * @return List of the valid deployment targets. Both <code>null</code> and
     * empty array are a valid return value when there are no available targets.
     */
    IDeploymentTarget[] getTargets(ITMWProject project);

    /**
     * Discovers targets. This can be a long-running task and in most cases
     * will be triggered by the user.
     */
    void discoverTargets(IProgressMonitor monitor) throws CoreException;

    /**
     * Find target based on project and target ID. This method returns 
     * <code>null</code> if target with given ID cannot be found or if
     * the target does not accept project any longer.
     */
    IDeploymentTarget findTarget(ITMWProject project, String id);

    /**
     * Return <code>false</code> if user needs to trigger long-running 
     * discovery to see all potential deployment targets.
     */
    boolean targetsDiscovered();

    /**
     * @param target that will be used by a {@link org.eclipse.core.runtime.jobs.Job}
     * @return scheduling rule that will be used to properly schedule jobs to 
     * avoid resource access conflicts. Can be <code>null</code>
     */
    ISchedulingRule getSchedulingRule(IDeploymentTarget target);
}
