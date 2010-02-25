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
package org.symbian.tools.wrttools.wizards.projectimport;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.zip.ZipEntry;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.internal.wizards.datatransfer.ILeveledImportStructureProvider;
import org.eclipse.ui.internal.wizards.datatransfer.TarEntry;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.symbian.tools.wrttools.util.ProjectUtils;

@SuppressWarnings("restriction")
public class ArchivedProject implements ProjectRecord {
    public boolean hasConflicts;
    public int level;
    public Object parent;
    public Object projectArchiveFile;
    private String projectName;
    private final ILeveledImportStructureProvider provider;
    private IProjectDescription description;

    /**
     * @param file
     *            The Object representing the .project file
     * @param parent
     *            The parent folder of the .project file
     * @param level
     *            The number of levels deep in the provider the file is
     */
    public ArchivedProject(Object file, Object parent, int level, ILeveledImportStructureProvider structureProvider) {
        provider = structureProvider;
        this.projectArchiveFile = file;
        this.parent = parent;
        this.level = level;
        setProjectName();
    }

    /**
     * Get the name of the project
     * 
     * @return String
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * @return Returns the hasConflicts.
     */
    public boolean hasConflicts() {
        return hasConflicts;
    }

    /**
     * Set the name of the project based on the projectFile.
     */
    private void setProjectName() {
        try {
            InputStream stream = provider.getContents(projectArchiveFile);

            // If we can get a description pull the name from there
            if (stream == null) {
                if (projectArchiveFile instanceof ZipEntry) {
                    IPath path = new Path(((ZipEntry) projectArchiveFile).getName());
                    projectName = path.segment(path.segmentCount() - 2);
                } else if (projectArchiveFile instanceof TarEntry) {
                    IPath path = new Path(((TarEntry) projectArchiveFile).getName());
                    projectName = path.segment(path.segmentCount() - 2);
                }
            } else {
                description = ResourcesPlugin.getWorkspace().loadProjectDescription(stream);
                stream.close();
                projectName = description.getName();
            }
        } catch (CoreException e) {
            // no good couldn't get the name
        } catch (IOException e) {
            // no good couldn't get the name
        }
    }

    @SuppressWarnings("unchecked")
    public ImportOperation getImportOperation(IProject project, ILeveledImportStructureProvider structureProvider,
            IOverwriteQuery query) {
        // import from archive
        List<Object> fileSystemObjects = structureProvider.getChildren(parent);
        fileSystemObjects = ProjectUtils.filterExternalProjectEntries(fileSystemObjects);
        structureProvider.setStrip(level);
        return new ImportOperation(project.getFullPath(), structureProvider.getRoot(), structureProvider, query,
                fileSystemObjects);
    }

    public URI getLocationURI() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getProjectLabel() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setHasConflicts(boolean hasConflicts) {
        this.hasConflicts = hasConflicts;
    }
}
