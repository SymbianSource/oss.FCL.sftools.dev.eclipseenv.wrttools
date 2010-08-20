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
package org.symbian.tools.tmw.previewer.http.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.json.simple.JSONObject;
import org.symbian.tools.tmw.previewer.PreviewerPlugin;
import org.symbian.tools.tmw.previewer.http.HttpPreviewer;

public class PreviewerStaticResourceProvider implements IResourceProvider {
    public static final String PREVIEW_PATH = "preview";
    public static final String PREVIEW_START = "/preview/wrt_preview.html";

    public String[] getPaths() {
        return new String[] { HttpPreviewer.PREVIEW_STARTING_PAGE, PREVIEW_PATH };
    }

    public InputStream getResourceStream(IProject project, IPath resource, Map<String, String[]> parameters,
            String sessionId)
            throws IOException {
        if (HttpPreviewer.PREVIEW_STARTING_PAGE.equals(resource.toString())
                || HttpPreviewer.DEBUG_STARTING_PAGE.equals(resource.toString())) {
            resource = new Path(PREVIEW_START);
        }
        URL url = FileLocator.find(PreviewerPlugin.getDefault().getBundle(), resource, null);
        if (url != null) {
            return url.openStream();
        } else {
            return null;
        }
    }

    public void post(IProject project, IPath resource, Map<String, String[]> parameterMap, JSONObject object,
            String sessionId)
            throws IOException, CoreException {
        // Do nothing
    }

}
