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

import java.io.ByteArrayInputStream;
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
import org.symbian.tools.wrttools.previewer.PreviewerPlugin;
import org.symbian.tools.wrttools.previewer.http.HttpPreviewer;
import org.symbian.tools.wrttools.previewer.http.WebAppInterface;

public class DebuggerResourceProvider implements IResourceProvider {
    public static final String DEBUG_SESSION_ID_PARAMETER = "debugSessionId";

    public String[] getPaths() {
        return new String[] { HttpPreviewer.DEBUG_STARTING_PAGE, "__sym-debug" };
    }

    public InputStream getResourceStream(IProject project, IPath resource, Map<String, String[]> parameters)
            throws IOException, CoreException {
        if (resource.toString().equals(HttpPreviewer.DEBUG_STARTING_PAGE)) {
            URL url = FileLocator.find(PreviewerPlugin.getDefault().getBundle(), new Path(
                    PreviewerStaticResourceProvider.PREVIEW_START), null);
            if (url != null) {
                return url.openStream();
            }
        } else if (resource.segmentCount() == 2) {
            if ("index.html".equals(resource.segment(1))) {
                String[] sessionId = parameters.get(DEBUG_SESSION_ID_PARAMETER);
                if (sessionId != null && sessionId.length == 1) {
                    WebAppInterface.connectDebugger(project.getName(), sessionId[0]);
                }
                URL url = FileLocator.find(PreviewerPlugin.getDefault().getBundle(), new Path(
                        "http-content/wrtdebugger/debugger.htm"), null);
                if (url != null) {
                    return url.openStream();
                }
            } else if ("testConnection".equals(resource.segment(1))) {
                String[] sessionId = parameters.get(DEBUG_SESSION_ID_PARAMETER);
                if (sessionId != null & sessionId.length == 1) {
                    if (!WebAppInterface.isConnected(project.getName(), sessionId[0])) {
                        return null;
                    }
                }
                return new ByteArrayInputStream("Ok".getBytes());
            }
        }
        return null;
    }

    public void post(IProject project, IPath resource, Map<String, String[]> parameterMap, JSONObject object)
            throws IOException, CoreException {
        // TODO Auto-generated method stub

    }

}
