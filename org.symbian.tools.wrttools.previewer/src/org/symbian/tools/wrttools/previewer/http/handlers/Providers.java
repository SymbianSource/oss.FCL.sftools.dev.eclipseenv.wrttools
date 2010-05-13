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
import java.util.TreeMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.json.simple.JSONObject;
import org.symbian.tools.wrttools.previewer.PreviewerException;

/**
 * Path is usually in the form /preview/{project name}/{path}
 * 
 * There are following spacial cases:
 * - Static previewer resource from plugin is returned when {path} begins with
 *   "preview" or is "preview-frame.html"
 * - Preferences resource is "preview/preferences.js"
 * - Index page (main HTML page of the WRT application) is always wrt_preview_main.html
 * - Debugger commands are submitted to URL __sym_command
 * 
 * All other URLs return workspace resources.
 */
public class Providers {
    private final Map<String, IResourceProvider> HANDLERS = new TreeMap<String, IResourceProvider>();
    private final IResourceProvider defaultHandler = new WorkspaceResourceProvider();
    public Providers() {
        addPaths(new MasterScriptProvider());
        addPaths(new PreviewerStaticResourceProvider());
        addPaths(new PreferencesResourceProvider());
        addPaths(new ProjectIndexResourceProvider());
        addPaths(new CommandResourceProvider());
        addPaths(new DebuggerResourceProvider());
    }

    private void addPaths(IResourceProvider handler) {
        for (String path : handler.getPaths()) {
            HANDLERS.put(path, handler);
        }
    }

    public InputStream get(String url, Map<String, String[]> parameters) throws PreviewerException {
        final IProject project = getProject(url);
        final IPath resource = new Path(url).removeFirstSegments(1);
        final IResourceProvider provider = getHandlerForPath(resource);
        try {
            return provider.getResourceStream(project, resource, parameters);
        } catch (IOException e) {
            throw new PreviewerException(e);
        } catch (CoreException e) {
            throw new PreviewerException(e);
        }
    }

    private IResourceProvider getHandlerForPath(IPath resource) {
        IResourceProvider provider = null;
        IPath mapping = resource;
        while (mapping.segmentCount() > 0) {
            provider = HANDLERS.get(mapping.toString());
            if (provider != null) {
                break;
            }
            mapping = mapping.removeLastSegments(1);
        }
        if (provider == null) {
            provider = defaultHandler;
        }
        return provider;
    }

    private IProject getProject(String url) throws PreviewerException {
        final IPath path = new Path(url);
        if (path.segmentCount() < 2) {
            throw new PreviewerException(String.format("Invalid path: %s", url));
        }
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(path.segment(0));
        if (!project.isAccessible()) {
            throw new PreviewerException(String.format("Project %s does not exist or is not open", project.getName()));
        }
        return project;
    }

    public IResourceProvider getHandlerForPostPath(String url) {
        return null;
    }

    public void post(String url, Map<String, String[]> parameterMap, JSONObject object) throws PreviewerException {
        final IProject project = getProject(url);
        final IPath resource = new Path(url).removeFirstSegments(1);
        final IResourceProvider provider = getHandlerForPath(resource);
        try {
            provider.post(project, resource, parameterMap, object);
        } catch (IOException e) {
            throw new PreviewerException(e);
        } catch (CoreException e) {
            throw new PreviewerException(e);
        }
    }
}
