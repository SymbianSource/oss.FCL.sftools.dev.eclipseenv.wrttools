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
package org.symbian.tools.tmw.core.projects;

import java.util.Set;

import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntime;

/**
 * This is a bridge between Faceted Project framework and TMW project frameworks.
 * 
 * @author Eugene Ostroukhov (eugeneo@symbian.org)
 */
public interface IFProjSupport {
    /**
     * Converts TMW runtime to FProj runtime.
     */
    IRuntime getRuntime(IMobileWebRuntime runtime);

    /**
     * @param runtime TMW runtime
     * @return facets that are always "on"
     */
    Set<IProjectFacet> getFixedFacets(IMobileWebRuntime runtime);

    /**
     * @return ID of the FProj runtime that corresponds to TMW runtime
     */
    String getRuntimeId(IMobileWebRuntime runtime);

    /**
     * @return same list as {@link IFProjSupport#getFixedFacets(IMobileWebRuntime)}
     */
    Set<IProjectFacetVersion> getFixedFacetsVersions(IMobileWebRuntime runtime);

    /**
     * @return core TMW facet
     */
    IProjectFacet getTMWFacet();

    /**
     * @return mobile web runtime that corresponds to FProj runtime
     */
    IMobileWebRuntime getTMWRuntime(IRuntime runtime);
}
