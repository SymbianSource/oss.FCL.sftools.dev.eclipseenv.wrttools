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
package org.symbian.tools.tmw.core.internal.runtimes;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.jsdt.core.IIncludePathEntry;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.IJsGlobalScopeContainer;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.projects.IMTWProject;
import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntime;

public class MobileRuntimeLibraryContainer implements IJsGlobalScopeContainer {
    private final IPath containerPath;
    private final IJavaScriptProject project;

    public MobileRuntimeLibraryContainer(IJavaScriptProject project, IPath containerPath) {
        this.project = project;
        this.containerPath = containerPath;
    }

    public IIncludePathEntry[] getIncludepathEntries() {
        final IMTWProject proj = TMWCore.create(project.getProject());
        if (proj != null) {
            try {
                IFacetedProject facetedProject = ProjectFacetsManager.create(project.getProject(), false,
                        new NullProgressMonitor());
                if (facetedProject != null) {
                    return TMWCore.getDefault().getClasspathManager().getProjectClasspathEntries(facetedProject);
                }
            } catch (CoreException e) {
                TMWCore.log(null, e);
            }
        }
        return new IIncludePathEntry[0];
    }

    public String getDescription() {
        final IMTWProject proj = TMWCore.create(project.getProject());
        if (proj != null) {
            final IMobileWebRuntime targetRuntime = proj.getTargetRuntime();
            if (targetRuntime != null) {
                return String.format("%s API", targetRuntime.getName());
            }
        }
        return "Mobile Web Runtime Library";
    }

    public int getKind() {
        return K_SYSTEM;
    }

    public IPath getPath() {
        return containerPath;
    }

    public String[] resolvedLibraryImport(String realImport) {
        return new String[] { realImport };
    }

}
