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
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.json.simple.JSONObject;
import org.symbian.tools.tmw.previewer.PreviewerPlugin;

public class CommandResourceProvider implements IResourceProvider {

    public String[] getPaths() {
        return new String[] { "__sym_command" };
    }

    public InputStream getResourceStream(IProject project, IPath resource, Map<String, String[]> parameters,
            String sessionId)
            throws IOException, CoreException {
        if (resource.segmentCount() == 2) {
            PreviewerPlugin.getDefault().getCommandHandlerManager().handle(resource.segment(1), project.getName(),
                    parameters);
        }
        return null;
    }

    public void post(IProject project, IPath resource, Map<String, String[]> parameterMap, JSONObject object,
            String sessionId)
            throws IOException, CoreException {
        // Do nothing
    }

}
