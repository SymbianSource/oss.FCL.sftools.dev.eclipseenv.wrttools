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
package org.symbian.tools.wrttools.previewer.preview;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.symbian.tools.wrttools.previewer.PreviewerPlugin;
import org.symbian.tools.wrttools.util.ProjectUtils;

public class RefreshJob extends Job {
    private static final class ChangedResourcesCollector implements IResourceDeltaVisitor {
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
        return resource.getType() == IResource.FILE && !resource.getFullPath().segment(1).equalsIgnoreCase("preview")
                && !"wgz".equalsIgnoreCase(resource.getFileExtension());
    }

    private final IResourceDelta delta;
    private final PreviewView view;

    public RefreshJob(IResourceDelta delta, PreviewView view) {
        super("Refresh preview browser");
        this.delta = delta;
        this.view = view;
        setRule(ResourcesPlugin.getWorkspace().getRoot());
        setSystem(true);
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        ChangedResourcesCollector visitor = new ChangedResourcesCollector();
        try {
            delta.accept(visitor);
        } catch (CoreException e) {
            PreviewerPlugin.log(e);
        }
        for (Entry<IProject, IPath> entry : visitor.renamed.entrySet()) {
            view.projectRenamed(entry.getKey(), entry.getValue());
        }
        if (visitor.files.size() > 0) {
            view.refreshPages(visitor.files);
        }
        return Status.OK_STATUS;
    }

}
