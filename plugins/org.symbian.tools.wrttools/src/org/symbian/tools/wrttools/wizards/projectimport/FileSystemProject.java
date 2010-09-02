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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.internal.wizards.datatransfer.DataTransferMessages;
import org.eclipse.ui.internal.wizards.datatransfer.ILeveledImportStructureProvider;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.util.CoreUtil;
import org.symbian.tools.wrttools.util.ProjectUtils;

@SuppressWarnings("restriction")
public class FileSystemProject implements ProjectRecord {
    private boolean hasConflicts;
    private String projectName;
    private final File location;

    /**
     * Create a record for a project based on the info in the file.
     *
     * @param file
     */
    public FileSystemProject(File location) {
        this.location = location;
        setProjectName();
    }

    @SuppressWarnings("unchecked")
    public ImportOperation getImportOperation(IProject project, ILeveledImportStructureProvider structureProvider,
            IOverwriteQuery query) {
        File importSource = location;
        if (!importSource.equals(project.getLocation().toFile())) {
            List<Object> filesToImport = ProjectUtils.filterExternalProjectEntries(FileSystemStructureProvider.INSTANCE
                    .getChildren(importSource));
            ImportOperation operation = new ImportOperation(project.getFullPath(), importSource,
                    FileSystemStructureProvider.INSTANCE, query, filesToImport);
            operation.setOverwriteResources(true); // need to overwrite
            // .project, .classpath
            // files
            operation.setCreateContainerStructure(false);
            return operation;
        } else {
            return null;
        }
    }

    /**
     * Gets the label to be used when rendering this project record in the UI.
     *
     * @return String the label
     * @since 3.4
     */
    public String getProjectLabel() {
        return NLS.bind(DataTransferMessages.WizardProjectsImportPage_projectLabel, projectName, location
                .getAbsolutePath());
    }

    public String getProjectName() {
        return projectName;
    }

    public boolean hasConflicts() {
        return hasConflicts;
    }

    /**
     * Returns whether the given project description file path is in the
     * default location for a project
     *
     * @param path
     *            The path to examine
     * @return Whether the given path is the default location for a project
     */
    private boolean isDefaultLocation(IPath path) {
        // The project description file must at least be within the project,
        // which is within the workspace location
        if (path.segmentCount() < 1) {
            return false;
        }
        return path.removeLastSegments(1).toFile().equals(Platform.getLocation().toFile());
    }

    public void setHasConflicts(boolean hasConflicts) {
        this.hasConflicts = hasConflicts;
    }

    /**
     * Set the name of the project based on the projectFile.
     */
    private void setProjectName() {
        try {
            // If we don't have the project name try again
            if (projectName == null) {
                IPath path = new Path(location.getAbsolutePath());
                IPath desc = path.append(IProjectDescription.DESCRIPTION_FILE_NAME);
                // if the file is in the default location, use the directory
                // name as the project name
                if (isDefaultLocation(path)) {
                    projectName = path.segment(path.segmentCount() - 1);
                } else if (!desc.toFile().isFile() && !isDefaultLocation(path)) {
                    try {
                        FileInputStream input = new FileInputStream(path.append(CoreUtil.METADATA_FILE).toFile());
                        String manifest = CoreUtil.read(new InputStreamReader(input));
                        projectName = CoreUtil.getApplicationName(manifest);
                    } catch (IOException e) {
                        Activator.log(e);
                        projectName = path.segment(path.segmentCount() - 1);
                    }
                } else {
                    IProjectDescription description = ResourcesPlugin.getWorkspace().loadProjectDescription(desc);
                    projectName = description.getName();
                }

            }
        } catch (CoreException e) {
            // no good couldn't get the name
        }
    }
}
