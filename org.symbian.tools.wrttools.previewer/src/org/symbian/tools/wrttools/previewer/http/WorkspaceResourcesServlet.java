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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.symbian.tools.wrttools.previewer.PreviewerException;
import org.symbian.tools.wrttools.previewer.PreviewerPlugin;
import org.symbian.tools.wrttools.previewer.http.handlers.DebuggerResourceProvider;
import org.symbian.tools.wrttools.previewer.http.handlers.PreviewerStaticResourceProvider;
import org.symbian.tools.wrttools.previewer.http.handlers.ProjectIndexResourceProvider;
import org.symbian.tools.wrttools.previewer.http.handlers.Providers;
import org.symbian.tools.wrttools.util.CoreUtil;

public class WorkspaceResourcesServlet extends HttpServlet {
    private static final Map<String, String> EXTENSION_CONTENT_TYPE = new TreeMap<String, String>();

    private static final long serialVersionUID = -3217197074249607950L;

    static {
        EXTENSION_CONTENT_TYPE.put("htm", "text/html");
        EXTENSION_CONTENT_TYPE.put("html", "text/html");
        EXTENSION_CONTENT_TYPE.put("xml", "text/xml");
        EXTENSION_CONTENT_TYPE.put("plist", "application/octet-stream");
        EXTENSION_CONTENT_TYPE.put("gif", "image/gif");
        EXTENSION_CONTENT_TYPE.put("jpg", "image/jpeg");
        EXTENSION_CONTENT_TYPE.put("jpeg", "image/jpeg");
        EXTENSION_CONTENT_TYPE.put("png", "image/png");
        EXTENSION_CONTENT_TYPE.put("css", "text/css");
        EXTENSION_CONTENT_TYPE.put("js", "application/x-javascript");
        EXTENSION_CONTENT_TYPE.put("mp3", "audio/x-mpeg");
    }

    private static String encode(String path) {
        try {
            StringBuffer result = new StringBuffer();
            String[] segments = new Path(path).segments();
            for (int i = 0; i < segments.length; i++) {
                String string = segments[i];
                result.append("/");
                // java.net.URLEncoder encodes " " as "+" while Chrome needs "%20"
                StringTokenizer tokenizer = new StringTokenizer(string, " ", false);
                while (tokenizer.hasMoreElements()) {
                    result.append(URLEncoder.encode(tokenizer.nextToken(), "UTF-8"));
                    if (tokenizer.hasMoreTokens()) {
                        result.append("%20");
                    }
                }
            }
            return result.toString();
        } catch (UnsupportedEncodingException e) {
            // Something is horribly wrong - JRE doesn't have UTF8?
            throw new RuntimeException(e);
        }
    }

    public static IFile getFileFromUrl(String name) {
        IPath path = getProjectRelativePath(name);
        if (path != null) {
            return getProjectResource(path);
        } else {
            return null;
        }
    }

    public static String getHttpUrl(IResource file) {
        String uri = getServerURIForResource(file != null ? file.getFullPath().toString() : "/", null).toASCIIString();
        if (uri != null) {
            return uri;
        } else {
            return file.getLocationURI().toString();
        }
    }

    public static String getMimeTypeByExtension(String extension) {
        if (extension != null) {
            return EXTENSION_CONTENT_TYPE.get(extension.toLowerCase());
        } else {
            return null;
        }
    }

    public static File getPreviewerResource(String name) {
        try {
            IPath path = getProjectRelativePath(name);
            if (path != null) {
                if (path.segmentCount() == 2 && HttpPreviewer.PREVIEW_STARTING_PAGE.equals(path.segment(1))) {
                    path = new Path(PreviewerStaticResourceProvider.PREVIEW_START);
                } else if (path.segmentCount() > 2
                        && PreviewerStaticResourceProvider.PREVIEW_PATH.equals(path.segment(1))) {
                    path = path.removeFirstSegments(1);
                } else {
                    return null;
                }
                URL pluginResource = FileLocator.find(PreviewerPlugin.getDefault().getBundle(), path, null);
                if (pluginResource != null) {
                    URL url = FileLocator.toFileURL(pluginResource);
                    if (url != null) {
                        return new File(url.getPath());
                    }
                }
            }
        } catch (IOException e) {
            PreviewerPlugin.log(e);
        }
        return null;
    }

    public static URI getPreviewerStartingPage(String widget) {
        return getServerURIForResource(new Path(widget).append(HttpPreviewer.PREVIEW_STARTING_PAGE).makeAbsolute()
                .toString(), null);
    }

    private static IPath getProjectRelativePath(String uri) {
        IPath path = null;
        try {
            String root = getHttpUrl(null);
            if (uri != null && uri.startsWith(root)) {
                String fileName = uri.substring(root.length());
                fileName = URLDecoder.decode(fileName, "UTF-8");
                path = new Path(fileName);
                if (path.segmentCount() == 2 && ProjectIndexResourceProvider.INDEX.equals(path.lastSegment())) {
                    IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(path.segment(0));
                    path = new Path(project.getName()).append(CoreUtil.getIndexFile(project));
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (CoreException e) {
            PreviewerPlugin.log(e);
        }
        return path;
    }

    private static IFile getProjectResource(IPath path) {
        IFile file;
        file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
        if (!file.isAccessible()) {
            return null;
        } else {
            return file;
        }
    }

    private static URI getServerURIForResource(String resourcePath, String debugSessionId) {
        Path p = new Path(resourcePath);
        if (p.segmentCount() > 1) {
            IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(p.segment(0));
            try {
                if (p.removeFirstSegments(1).toString().equals(CoreUtil.getIndexFile(project))) {
                    return getServerURIForResource(new Path(p.segment(0)).append(ProjectIndexResourceProvider.INDEX)
                            .makeAbsolute().toString(), debugSessionId);
                }
            } catch (Exception e1) {
                PreviewerPlugin.log(e1);
            }
        }
        try {
            String path = encode(WebappManager.WORKSPACE_RESOURCES_CONTEXT + resourcePath);
            path += debugSessionId == null ? "" : (String.format("?%s=%s",
                    DebuggerResourceProvider.DEBUG_SESSION_ID_PARAMETER, debugSessionId));
            URL url = new URL("http", WebappManager.getHost(), WebappManager.getPort(), path);
            return url.toURI();
        } catch (MalformedURLException e) {
            PreviewerPlugin.log(e);
        } catch (URISyntaxException e) {
            PreviewerPlugin.log(e);
        }
        return null;
    }

    private final Providers providers = new Providers();

    private void copyData(InputStream contents, OutputStream ouput) throws IOException {
        byte[] buf = new byte[4048];
        int i;
        while ((i = contents.read(buf)) >= 0) {
            ouput.write(buf, 0, i);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long time = 0;
        if (PreviewerPlugin.TRACE_SERVLET) {
            time = System.currentTimeMillis();
        }
        try {
            InputStream stream = providers.get(req.getPathInfo(), req.getParameterMap(), req.getSession().getId());
            String mimeType = getMimeTypeByExtension(new Path(req.getPathInfo()).getFileExtension());
            if (mimeType != null) {
                resp.setContentType(mimeType);
            }
            if (stream != null) {
                copyData(stream, resp.getOutputStream());
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (PreviewerException e) {
            PreviewerPlugin.log(e);
            throw new ServletException(e);
        }
        if (PreviewerPlugin.TRACE_SERVLET) {
            System.out.println(MessageFormat.format("Resource {0} was downloaded in {1}", req.getPathInfo(), System
                    .currentTimeMillis()
                    - time));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletInputStream inputStream = req.getInputStream();
        byte[] buff = new byte[1024];
        StringBuilder builder = new StringBuilder();
        int l;
        while ((l = inputStream.read(buff)) > 0) {
            builder.append(new String(buff, 0, l, "UTF-8"));
        }
        inputStream.close();
        JSONObject object = null;
        try {
            Object parseResult = new JSONParser().parse(builder.toString());
            if (parseResult instanceof JSONObject) {
                object = (JSONObject) parseResult;
            }
        } catch (ParseException e) {
            PreviewerPlugin.log(e);
            throw new ServletException(e);
        }
        try {
            providers.post(req.getPathInfo(), req.getParameterMap(), object, req.getSession().getId());
        } catch (PreviewerException e) {
            throw new ServletException(e);
        }
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    public static URI getDebugStartingPage(String project, String session) {
        return getServerURIForResource(new Path(project).append(HttpPreviewer.DEBUG_STARTING_PAGE).makeAbsolute()
                .toString(), session);
    }
}
