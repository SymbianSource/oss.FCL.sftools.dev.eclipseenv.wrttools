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
package org.symbian.tools.tmw.internal.ui.wizard;

import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.projects.IFProjSupport;
import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntime;

public class RuntimeFacetsFilter extends ViewerFilter {
    private final IFacetedProjectWorkingCopy fpjwc;

    public RuntimeFacetsFilter(IFacetedProjectWorkingCopy fpjwc) {
        this.fpjwc = fpjwc;
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        final IFProjSupport support = TMWCore.getFProjSupport();
        final IMobileWebRuntime webRuntime = support.getTMWRuntime(fpjwc.getPrimaryRuntime());
        if (webRuntime != null) {
            final Set<IProjectFacet> facets = support.getFixedFacets(webRuntime);
            return !facets.contains(element);
        }
        return true;
    }

}
