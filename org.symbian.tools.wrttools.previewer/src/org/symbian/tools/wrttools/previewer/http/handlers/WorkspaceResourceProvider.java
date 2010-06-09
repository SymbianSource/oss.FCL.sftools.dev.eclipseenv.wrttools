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
package org.symbian.tools.wrttools.previewer.http.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.json.simple.JSONObject;
import org.symbian.tools.wrttools.previewer.PreviewerPlugin;
import org.symbian.tools.wrttools.util.ProjectUtils;

public class WorkspaceResourceProvider implements IResourceProvider {
    public String[] getPaths() {
        return null;
    }

    public InputStream getResourceStream(IProject project, IPath resource, Map<String, String[]> parameters,
            String sessionId)
            throws IOException, CoreException {
        IFile file = getFile(project, resource);
        if (file != null) {
            return file.getContents();
        } else {
            PreviewerPlugin.print(String.format(
                    "%s was not found in the workspace. It was requested by the previewer.\n", resource.toString()));
            return null;
        }
    }

    private IFile getFile(IProject project, IPath resource) throws CoreException {
        final IFile file = project.getFile(resource);
        if (!file.isAccessible()) {
            IContainer container = project;
            for (int i = 0; i < resource.segmentCount() - 1; i++) {
                String name = resource.segment(i).toLowerCase();
                container = (IContainer) getResource(container, name, false);
                if (container == null) {
                    return null;
                }
            }
            return (IFile) getResource(container, resource.lastSegment().toLowerCase(), true);
        } else {
            return file;
        }
    }

    private IResource getResource(final IContainer container, final String name, final boolean isFile) throws CoreException {
        IResource[] members = container.members();
        for (IResource resource : members) {
            if (resource.getName().toLowerCase().equals(name)) {
                if (resource.isAccessible() && ((resource.getType() == IResource.FILE) == isFile)
                        && !ProjectUtils.isExcluded(resource)) {
                    return resource;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    public void post(IProject project, IPath resource, Map<String, String[]> parameterMap, JSONObject object,
            String sessionId)
            throws IOException, CoreException {
        // Do nothing
    }

}
