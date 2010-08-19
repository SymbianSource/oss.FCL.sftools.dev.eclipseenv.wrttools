/*******************************************************************************
 * Copyright (c) 2009 Symbian Foundation and/or its subsidiary(-ies).
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
 *******************************************************************************/
package org.symbian.tools.wrttools.previewer.http;

import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

public class HttpPreviewer {
    public static final String PREVIEW_STARTING_PAGE = "preview-frame.html";
    public static final String DEBUG_STARTING_PAGE = "debug-frame.html";

    public IFile getFileFromUrl(String name) {
        return WorkspaceResourcesServlet.getFileFromUrl(name);
    }

    public String getHttpUrl(IFile resource) {
        return WorkspaceResourcesServlet.getHttpUrl(resource);
    }

    public URI previewProject(IProject project, IPreviewStartupListener listener) {
        WebAppInterface.getInstance(); // Ensure server is up
        if (listener == null) {
            return WorkspaceResourcesServlet.getPreviewerStartingPage(project.getName());
        } else {
            return WebAppInterface.getInstance().prepareDebugger(project, listener);
        }
    }
}
