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
package org.symbian.tools.tmw.previewer.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.projects.ITMWProject;
import org.symbian.tools.tmw.core.runtimes.IApplicationLayoutProvider;
import org.symbian.tools.tmw.previewer.PreviewerPlugin;

public class PreviewerUtil {
    public static final class ChangedResourcesCollector implements IResourceDeltaVisitor {
        public final Collection<IFile> files = new HashSet<IFile>();
        public final Collection<IProject> deleted = new HashSet<IProject>();
        public final Map<IProject, IPath> renamed = new HashMap<IProject, IPath>();

        public boolean visit(IResourceDelta delta) throws CoreException {
            IResource resource = delta.getResource();
            switch (resource.getType()) {
            case IResource.PROJECT:
                if (delta.getKind() == IResourceDelta.REMOVED) {
                    if ((delta.getFlags() & IResourceDelta.MOVED_TO) != 0) {
                        renamed.put(resource.getProject(), delta.getMovedToPath());
                    } else {
                        deleted.add((IProject) resource);
                    }
                    return false;
                }
                break;
            case IResource.FILE:
                final ITMWProject p = TMWCore.create(resource.getProject());
                if (p != null && p.getTargetRuntime() != null) {
                    IApplicationLayoutProvider layoutProvider = p.getTargetRuntime().getLayoutProvider();
                    if (layoutProvider != null) {
                        if (layoutProvider.getResourcePath((IFile) resource) != null) {
                            final boolean kind = delta.getKind() == IResourceDelta.ADDED
                                    | delta.getKind() == IResourceDelta.REMOVED;
                            final boolean flag = (delta.getFlags() & (IResourceDelta.CONTENT | IResourceDelta.ENCODING
                                    | IResourceDelta.LOCAL_CHANGED | IResourceDelta.REPLACED | IResourceDelta.SYNC)) != 0;
                            if (kind || flag) {
                                files.add((IFile) resource);
                            }
                        }
                    }
                }
                break;
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
