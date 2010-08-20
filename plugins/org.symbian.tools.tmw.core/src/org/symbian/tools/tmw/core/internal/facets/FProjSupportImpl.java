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
package org.symbian.tools.tmw.core.internal.facets;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.VersionFormatException;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.symbian.tools.tmw.core.projects.IFProjSupport;
import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntime;

public final class FProjSupportImpl implements IFProjSupport {
    public IRuntime getRuntime(IMobileWebRuntime runtime) {
        return RuntimeManager.getRuntime(getRuntimeId(runtime));
    }

    public Set<IProjectFacet> getFixedFacets(IMobileWebRuntime runtime) {
        final Set<IProjectFacet> facets = new HashSet<IProjectFacet>();
        for (IProjectFacetVersion facetVersion : getFixedFacetsVersions(runtime)) {
            facets.add(facetVersion.getProjectFacet());
        }
        return facets;
    }

    private IProjectFacetVersion getFacet(String id, String version) {
        final IProjectFacet projectFacet = ProjectFacetsManager.getProjectFacet(id);
        try {
            return version != null ? projectFacet.getVersion(version) : projectFacet.getLatestVersion();
        } catch (VersionFormatException e) {
            throw new RuntimeException(e);
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    public String getRuntimeId(IMobileWebRuntime runtime) {
        return runtime != null ? runtime.getId() + ":" + runtime.getVersion() : null;
    }

    public Set<IProjectFacetVersion> getFixedFacetsVersions(IMobileWebRuntime runtime) {
        final Set<IProjectFacetVersion> facets = new HashSet<IProjectFacetVersion>();
        facets.add(getFacet("wst.jsdt.web", null));
        facets.add(getFacet("tmw.core", null));
        //        facets.add(getFacet("wst.jsdt.web", "1.0"));
        return facets;
    }

    public IProjectFacet getTMWFacet() {
        return getFacet("tmw.core", null).getProjectFacet();
    }
}
