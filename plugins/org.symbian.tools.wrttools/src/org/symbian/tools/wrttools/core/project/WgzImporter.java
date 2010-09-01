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
package org.symbian.tools.wrttools.core.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntime;
import org.symbian.tools.tmw.ui.project.AbstractZippedApplicationImporter;
import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.WRTProject;
import org.symbian.tools.wrttools.util.CoreUtil;

public class WgzImporter extends AbstractZippedApplicationImporter {

    public IMobileWebRuntime getApplicationRuntime(File file) {
        if (file.isFile()) {
            ZipInputStream stream = null;
            try {
                stream = new ZipInputStream(new FileInputStream(file));
                ZipEntry entry = null;
                while ((entry = stream.getNextEntry()) != null) {
                    if (!entry.isDirectory()) {
                        IPath path = new Path(entry.getName());
                        if (path.segmentCount() == 2 && "Info.plist".equalsIgnoreCase(path.lastSegment())) {
                            return TMWCore.getRuntimesManager().getRuntime(WRTProject.WRT11_RUNTIME,
                                    WRTProject.WRT11_VERSION);
                        }
                    }
                }
            } catch (IOException e) {
                Activator.log(e);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        Activator.log(e);
                    }
                }
            }
        }
        return null;
    }

    public Map<String, String> getFileFilters() {
        Map<String, String> filters = new TreeMap<String, String>();
        filters.put("*.wgz", "WRT Application Archive (*.wgz)");
        filters.put("*.zip", "Zip Archive (*.zip)");
        return filters;
    }

    public IFile[] getFilesToOpen(IProject project) {
        try {
            return new IFile[] { CoreUtil.getIndexFile(project) };
        } catch (CoreException e) {
            Activator.log(e);
            return null;
        }
    }

    public IProjectFacetVersion[] getConfiguredFacets(File file) {
        return null;
    }

    @Override
    protected IPath getApplicationRelativePath(ZipEntry entry) {
        return new Path(entry.getName()).removeFirstSegments(1);
    }
}
