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
package org.symbian.tools.tmw.core.internal.projects;

import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentVersion;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.projects.IMTWProject;
import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntime;

public class TMWFacetedProject implements IMTWProject {
    private final IProject project;

    public TMWFacetedProject(IProject project) {
        this.project = project;
    }

    public String getName() {
        return project.getName();
    }

    public IProject getProject() {
        return project;
    }

    public IMobileWebRuntime getTargetRuntime() {
        try {
            final IFacetedProject fproj = ProjectFacetsManager.create(project);
            final IRuntime runtime = fproj.getPrimaryRuntime();
            if (runtime != null) {
                final List<IRuntimeComponent> components = runtime.getRuntimeComponents();
                if (!components.isEmpty()) {
                    final IRuntimeComponentVersion version = components.get(0).getRuntimeComponentVersion();
                    return TMWCore.getRuntimesManager()
                            .getRuntime(version.getRuntimeComponentType().getId(), version.getVersionString());
                }
            }
        } catch (CoreException e) {
            TMWCore.log(null, e);
        }
        return null;
    }

    public boolean validate(IProgressMonitor monitor) {
        try {
            project.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
            IMarker[] markers = project.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);

            boolean hasErrors = false;
            for (IMarker marker : markers) {
                if (marker.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO) == IMarker.SEVERITY_ERROR) {
                    hasErrors = true;
                    break;
                }
            }
            return !hasErrors;
        } catch (CoreException e1) {
            // Proofing from coding exceptions in JSDT
            TMWCore.log(null, e1);
        }
        return false;
    }

}
