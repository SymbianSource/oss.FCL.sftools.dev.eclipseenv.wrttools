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

import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.symbian.tools.wrttools.previewer.PreviewerUtil;
import org.symbian.tools.wrttools.previewer.PreviewerUtil.ChangedResourcesCollector;

public class RefreshJob extends Job {
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
        final ChangedResourcesCollector visitor = PreviewerUtil.collectResourceChanges(delta);
        for (Entry<IProject, IPath> entry : visitor.renamed.entrySet()) {
            view.projectRenamed(entry.getKey(), entry.getValue());
        }
        if (visitor.files.size() > 0) {
            view.refreshPages(visitor.files);
        }
        return Status.OK_STATUS;
    }

}
