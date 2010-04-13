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
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.json.simple.JSONObject;
import org.symbian.tools.wrttools.previewer.PreviewerPlugin;
import org.symbian.tools.wrttools.previewer.preview.ProjectPreferencesManager;

public class PreferencesResourceProvider implements ResourceProvider {
    public String[] getPaths() {
        return new String[] { "preview/preferences.js" };
    }

    public InputStream getResourceStream(IProject project, IPath resource, Map<String, String> parameters)
            throws IOException, CoreException {
        Properties projectPreferences = ProjectPreferencesManager.getProjectProperties(project);
        String js = getJS(projectPreferences);
        try {
            return new ByteArrayInputStream(js.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            PreviewerPlugin.log(e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private String getJS(Properties projectPreferences) {
        StringBuilder builder = new StringBuilder("NOKIA.emulator.prefs = ");
        JSONObject jsonObject = new JSONObject();
        jsonObject.putAll(projectPreferences);
        builder.append(jsonObject.toJSONString()).append(";\n");
        return builder.toString();
    }

    public void post(IProject project, IPath resource, Map<String, String> parameterMap, JSONObject object)
            throws IOException, CoreException {
        String key = (String) object.get("key");
        String value = (String) object.get("value");
        Properties projectPreferences = ProjectPreferencesManager.getProjectProperties(project);
        String oldValue = (String) projectPreferences.get(key);
        if (oldValue != value && (oldValue == null || !oldValue.equals(value))) {
            projectPreferences.put(key, value);
            ProjectPreferencesManager.setProjectProperties(project, projectPreferences);
        }
    }

}
