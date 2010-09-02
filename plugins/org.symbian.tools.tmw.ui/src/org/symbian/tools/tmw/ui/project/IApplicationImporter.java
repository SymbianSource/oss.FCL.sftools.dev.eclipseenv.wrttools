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
package org.symbian.tools.tmw.ui.project;

import java.io.File;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntime;

/**
 * Classes implementing this interface help create projects from application
 * archives.
 *
 * @author Eugene Ostroukhov (eugeneo@symbian.org)
 */
public interface IApplicationImporter {
    /**
     * Importer can support only one runtime (i.e. several extensions need to
     * be contributed for several runtimes).
     *
     * @param file application package
     * @return runtime that created project should target. <code>null</code> if
     * this importer can't import the file.
     */
    IMobileWebRuntime getApplicationRuntime(File file);

    /**
     * @return filters that users can use to navigate filesystem. They are
     * returned as "file extension-description" pairs
     * @see org.eclipse.swt.widgets.FileDialog#setFilterExtensions(String[])
     * @see org.eclipse.swt.widgets.FileDialog#setFilterNames(String[])
     */
    Map<String, String> getFileFilters();

    /**
     * @param file application package
     * @return collection of the enabled facets. These facets will be "locked"
     * on the project
     */
    IProjectFacetVersion[] getConfiguredFacets(File file);

    /**
     * Extracts files from application archive to workspace project
     *
     * @param file mobile web application archive
     * @param runtime mobile web runtime
     * @param project workspace project
     * @param monitor progress monitor
     * @throws CoreException this exception signals failure to extract files
     */
    void extractFiles(File file, IMobileWebRuntime runtime, IProject project, IProgressMonitor monitor)
            throws CoreException;

    /**
     * @param project newly imported application project
     * @return collection of the files to open
     */
    IFile[] getFilesToOpen(IProject project);
}
