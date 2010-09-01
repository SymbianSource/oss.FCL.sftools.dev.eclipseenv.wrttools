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
package org.symbian.tools.wrttools.core.libraries;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetDetector;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.symbian.tools.tmw.core.utilities.CoreUtil;

public class WrtKitFacetDetector extends ProjectFacetDetector {
    @Override
    public void detect(IFacetedProjectWorkingCopy fpjwc, IProgressMonitor monitor) throws CoreException {
        if (CoreUtil.hasTypes(fpjwc.getProject(), "UIManager", "NavigationButton", "NotificationPopup")) {
            final IProjectFacet facet = ProjectFacetsManager.getProjectFacet("symbian.wrtkit");
            final IProjectFacetVersion latestVersion = facet.getLatestVersion();
            fpjwc.addProjectFacet(latestVersion);
            fpjwc.setProjectFacetActionConfig(facet, null);
        }
    }

}
