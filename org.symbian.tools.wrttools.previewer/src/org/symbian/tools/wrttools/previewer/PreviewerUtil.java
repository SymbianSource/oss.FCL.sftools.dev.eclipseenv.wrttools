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
package org.symbian.tools.wrttools.previewer;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.symbian.tools.wrttools.util.ProjectUtils;

public class PreviewerUtil {
    public static final class ChangedResourcesCollector implements IResourceDeltaVisitor {
        public final Collection<IFile> files = new HashSet<IFile>();
        public final Collection<IProject> deleted = new HashSet<IProject>();
        public final Map<IProject, IPath> renamed = new HashMap<IProject, IPath>();

        public boolean visit(IResourceDelta delta) throws CoreException {
            IResource resource = delta.getResource();
            if (resource.getType() == IResource.PROJECT) {
                if (delta.getKind() == IResourceDelta.REMOVED) {
                    if ((delta.getFlags() & IResourceDelta.MOVED_TO) != 0) {
                        renamed.put(resource.getProject(), delta.getMovedToPath());
                    } else {
                        deleted.add((IProject) resource);
                    }
                    return false;
                }
            } else if (isRelevantResource(resource)) {
                if (delta.getKind() == IResourceDelta.ADDED | delta.getKind() == IResourceDelta.REMOVED) {
                    if (!ProjectUtils.isExcluded(resource)) {
                        files.add((IFile) resource);
                    }
                } else if ((delta.getFlags() & (IResourceDelta.CONTENT | IResourceDelta.ENCODING
                        | IResourceDelta.LOCAL_CHANGED | IResourceDelta.REPLACED | IResourceDelta.SYNC)) != 0) {
                    if (!ProjectUtils.isExcluded(resource)) {
                        files.add((IFile) resource);
                    }
                } else if (delta.getMarkerDeltas().length != 0) {
                    for (IMarkerDelta markerDelta : delta.getMarkerDeltas()) {
                        if (markerDelta.getType().equals(ProjectUtils.EXCLUDE_MARKER_ID)) {
                            files.add((IFile) resource);
                            break;
                        }
                    }
                }
            }
            return true;
        }
    }

    public static boolean isRelevantResource(IResource resource) {
        if (resource.exists() && resource.getType() == IResource.FILE) {
            return !resource.getFullPath().segment(1).equalsIgnoreCase("preview")
                    && !"wgz".equalsIgnoreCase(resource.getFileExtension())
                    && !(PreviewerPlugin.PLUGIN_ID + ".xml").equals(resource.getName());
        }
        return false;
    }

    public static IFile[] getWebChanges(final IResourceDelta delta) {
        ChangedResourcesCollector visitor = collectResourceChanges(delta);
        final Collection<IFile> files = visitor.files;
        return files.toArray(new IFile[files.size()]);
    }

    public static ChangedResourcesCollector collectResourceChanges(final IResourceDelta delta) {
        ChangedResourcesCollector visitor = new ChangedResourcesCollector();
        try {
            delta.accept(visitor);
        } catch (CoreException e) {
            PreviewerPlugin.log(e);
        }
        return visitor;
    }
}
