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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.json.simple.JSONObject;
import org.symbian.tools.wrttools.previewer.PreviewerPlugin;
import org.symbian.tools.wrttools.util.CoreUtil;
import org.symbian.tools.wrttools.util.ProjectUtils;

public class ProjectIndexResourceProvider implements IResourceProvider {
    public static final String INDEX = "wrt_preview_main.html";
    private static final Pattern HEAD_TAG_PATTERN = Pattern.compile("<head(\\s*\\w*=\"(^\")*\")*\\s*>",
            Pattern.CASE_INSENSITIVE);
    private static final String SCRIPT = "<script language=\"JavaScript\" type=\"text/javascript\" src=\"preview/script/lib/loader.js\"></script>";

    public String[] getPaths() {
        return new String[] { INDEX };
    }

    public InputStream getResourceStream(IProject project, IPath resource, Map<String, String[]> parameters,
            String sessionId) throws IOException, CoreException {
        return getProjectIndexPage(project.getName());
    }

    private InputStream getProjectIndexPage(String projectName) throws IOException, CoreException {
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        if (project.isAccessible()) {
            String indexFileName = CoreUtil.getIndexFile(project);
            if (indexFileName != null) {
                final IFile file = CoreUtil.getFile(project, indexFileName);
                if (!ProjectUtils.isExcluded(file)) {
                    String string = CoreUtil.readFile(project, file);
                    if (string != null) {
                        Matcher matcher = HEAD_TAG_PATTERN.matcher(string);
                        if (matcher.find()) {
                            string = matcher.replaceFirst(matcher.group() + SCRIPT);
                        }
                        return new ByteArrayInputStream(string.getBytes("UTF-8"));
                    }
                }
            }
            PreviewerPlugin.print(String.format("Can not find main page (%s) in project %s.\n", indexFileName,
                    project.getName()));
        }
        return null;
    }

    public void post(IProject project, IPath resource, Map<String, String[]> parameterMap, JSONObject object,
            String sessionId) throws IOException, CoreException {
        // Do nothing
    }

}
