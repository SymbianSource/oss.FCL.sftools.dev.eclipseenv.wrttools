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

import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFramework;
import org.symbian.tools.tmw.core.ITMWConstants;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.projects.IMTWProject;
import org.symbian.tools.tmw.core.projects.IMTWProjectProvider;

public class MTWFacetedProjectProvider implements IMTWProjectProvider {
    private final Map<IProject, IMTWProject> projects = new WeakHashMap<IProject, IMTWProject>();

    public IMTWProject create(IProject project) {
        if (!projects.containsKey(project)) {
            projects.put(project, new TMWFacetedProject(project));
        }
        return projects.get(project);
    }

    public boolean isSupportedProject(IProject project) {
        try {
            return FacetedProjectFramework.hasProjectFacet(project, ITMWConstants.CORE_FACET);
        } catch (CoreException e) {
            TMWCore.log(null, e);
            return false;
        }
    }

}
