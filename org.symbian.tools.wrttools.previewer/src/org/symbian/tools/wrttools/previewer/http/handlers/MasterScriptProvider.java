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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.json.simple.JSONObject;
import org.symbian.tools.wrttools.previewer.PreviewerPlugin;

public class MasterScriptProvider implements IResourceProvider {
    private static final String[] FILES_CORE = { "loader.js", "widget.js", "systeminfo.js", "menu.js", "menuItem.js",
            "console.js" };
    private static final String PATH_DEVICE_JS = "preview/script/lib/device.js";
    private static final String PATH_LOADER_JS = "preview/script/lib/loader.js";
    private static final String[] FILES_SERVICES = { "AppManager.js", "Calendar.js", "Contact.js", "Landmarks.js",
            "Location.js", "Logging.js", "MediaManagement.js", "Messaging.js", "Sensor.js", "SysInfo.js" };

    private static final String[] FILES_SERVICES_DATA = { "appManager_data.js", "calendar_data.js", "contact_data.js",
            "landmarks_data.js", "location_data.js", "logging_data.js", "mediaManagement_data.js", "messaging_data.js",
            "sensor_data.js", "sysInfo_data.js" };
    private String WRT10;
    private String WRT11_SERVICES;

    public String[] getPaths() {
        return new String[] { PATH_LOADER_JS, PATH_DEVICE_JS };
    }

    public InputStream getResourceStream(IProject project, IPath resource, Map<String, String[]> parameters)
            throws IOException, CoreException {
        if (resource.equals(new Path(PATH_LOADER_JS))) {
            synchronized (this) {
                if (WRT10 == null) {
                    loadCoreAPI();
                }
            }
            return new ByteArrayInputStream((WRT10).getBytes("utf8"));
        } else if (resource.equals(new Path(PATH_DEVICE_JS))) {
            synchronized (this) {
                if (WRT11_SERVICES == null) {
                    loadWRT11Services();
                }
            }
            return new ByteArrayInputStream((WRT11_SERVICES).getBytes("utf8"));
        } else {
            return null;
        }
    }

    private void load(String base, String jsfile, StringBuilder builder) throws IOException {
        final IPath path = new Path(base).append(jsfile);
        InputStream stream = FileLocator.openStream(PreviewerPlugin.getDefault().getBundle(), path, false);
        if (stream != null) {
            try {
                builder.append(String.format("// Start \"%s\"\n", path.toOSString()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "utf8"));
                String sz;
                while ((sz = reader.readLine()) != null) {
                    builder.append(sz).append('\n');
                }
                builder.append(String.format("// End \"%s\"\n", path.toOSString()));
            } finally {
                stream.close();
            }
        } else {
            PreviewerPlugin.log("Missing JS file " + path.toOSString(), null);
        }

    }

    private void loadCoreAPI() {
        final StringBuilder builder = new StringBuilder();
        try {
            for (String jsfile : FILES_CORE) {
                load("/preview/script/lib/", jsfile, builder);
            }
        } catch (IOException e) {
            PreviewerPlugin.log(e);
        }
        WRT10 = builder.toString();
    }

    private void loadWRT11Services() {
        final StringBuilder builder = new StringBuilder();
        try {
            load("/preview/script/lib/", "device.js", builder);
            for (String jsfile : FILES_SERVICES) {
                load("/preview/script/lib/sapi", jsfile, builder);
            }
            for (String jsfile : FILES_SERVICES_DATA) {
                load("/preview/data", jsfile, builder);
            }
        } catch (IOException e) {
            PreviewerPlugin.log(e);
        }
        WRT11_SERVICES = builder.toString();
    }

    public void post(IProject project, IPath resource, Map<String, String[]> parameterMap, JSONObject object)
            throws IOException, CoreException {
        // Nothing to do
    }

}
