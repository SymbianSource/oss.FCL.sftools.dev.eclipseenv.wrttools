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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.internal.wizards.datatransfer.DataTransferMessages;
import org.eclipse.ui.internal.wizards.datatransfer.ILeveledImportStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.symbian.tools.wrttools.util.CoreUtil;
import org.symbian.tools.wrttools.util.ProjectUtils;

@SuppressWarnings("restriction")
public class ArchivedProject implements ProjectRecord {
    public boolean hasConflicts;
    public int level;
    public Object parent;
    public Object dotProject;
    private String projectName;
    private final ILeveledImportStructureProvider provider;
    private IProjectDescription description;
    private final Object infoPlist;

    /**
     * @param file
     *            The Object representing the .project file
     * @param parent
     *            The parent folder of the .project file
     * @param level
     *            The number of levels deep in the provider the file is
     */
    public ArchivedProject(Object infoPlist, Object dotProject, Object parent, int level,
            ILeveledImportStructureProvider structureProvider) {
        this.infoPlist = infoPlist;
        this.dotProject = dotProject;
        provider = structureProvider;
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
            if (dotProject != null) {
                InputStream stream = provider.getContents(dotProject);

                // If we can get a description pull the name from there
                if (stream != null) {
                    description = ResourcesPlugin.getWorkspace().loadProjectDescription(stream);
                    stream.close();
                    projectName = description.getName();
                }
            } else {
                InputStream stream = provider.getContents(infoPlist);
                if (stream != null) {
                    String manifest = CoreUtil.read(new BufferedReader(new InputStreamReader(stream)));
                    projectName = CoreUtil.getApplicationName(manifest);
                }
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
        return null;
    }

    public String getProjectLabel() {
        String path = provider.getLabel(parent);
        return NLS.bind(DataTransferMessages.WizardProjectsImportPage_projectLabel, projectName, path);
    }

    public void setHasConflicts(boolean hasConflicts) {
        this.hasConflicts = hasConflicts;
    }
}
