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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.json.simple.JSONObject;

public class WorkspaceResourceProvider implements IResourceProvider {
    public String[] getPaths() {
        return null;
    }

    public InputStream getResourceStream(IProject project, IPath resource, Map<String, String[]> parameters)
            throws IOException, CoreException {
        IFile file = project.getFile(resource);
        if (file.isAccessible()) {
            return file.getContents();
        } else {
            return null;
        }
    }

    public void post(IProject project, IPath resource, Map<String, String[]> parameterMap, JSONObject object)
            throws IOException, CoreException {
        // Do nothing
    }

}
