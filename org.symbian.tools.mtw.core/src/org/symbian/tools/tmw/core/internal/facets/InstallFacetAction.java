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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.jsdt.core.IIncludePathEntry;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.JavaScriptCore;

public class InstallFacetAction implements IDelegate {

    public void execute(IProject project, IProjectFacetVersion fv, Object config, IProgressMonitor monitor)
            throws CoreException {
        final IJavaScriptProject jsProject = JavaScriptCore.create(project);
        final IIncludePathEntry[] rawIncludepath = jsProject.getRawIncludepath();
        final Path path = new Path("tmw.coreLibrary");
        final int originalEntryCount = rawIncludepath.length;

        IIncludePathEntry[] newIncludepath = new IIncludePathEntry[originalEntryCount + 1];
        System.arraycopy(rawIncludepath, 0, newIncludepath, 0, originalEntryCount);
        for (IIncludePathEntry entry : rawIncludepath) {
            if (entry.getPath().equals(path)) {
                newIncludepath = null;
                break;
            }
        }
        if (newIncludepath != null) {
            final IIncludePathEntry newContainerEntry = JavaScriptCore.newContainerEntry(path, false);
            newIncludepath[originalEntryCount] = newContainerEntry;
            jsProject.setRawIncludepath(newIncludepath, monitor);
        }
    }

}
