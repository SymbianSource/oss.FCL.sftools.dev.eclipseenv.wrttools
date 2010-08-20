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
package org.symbian.tools.tmw.previewer.http;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.resources.IProject;
import org.symbian.tools.tmw.previewer.PreviewerPlugin;

public class WebAppInterface {
    private static WebAppInterface INSTANCE;

    public static void connectDebugger(String widget, String id, String sId) {
        if (PreviewerPlugin.TRACE_WEBAPP) {
            System.out.println("Connecting debugger");
        }
        getInstance().connect(widget, id, sId);
    }

    public static String decode(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encode(String project) {
        try {
            return URLEncoder.encode(project, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getAjaxUri(String widget, String id) {
        if (PreviewerPlugin.TRACE_WEBAPP) {
            System.out.println("getAjaxUri");
        }
        return getInstance().createAjaxUri(widget, id).toASCIIString();
    }

    public synchronized static WebAppInterface getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WebAppInterface();
        }
        return INSTANCE;
    }

    public static String getUrl(String widget, String id) {
        return getInstance().complete(widget, id).toASCIIString();
    }

    public static boolean isConnected(String widget, String id) {
        if (PreviewerPlugin.TRACE_WEBAPP) {
            System.out.println("isConnected");
        }
        return getInstance().isJobComplete(widget, id);
    }

    public static boolean isSuccessful(String widget, String id) {
        if (PreviewerPlugin.TRACE_WEBAPP) {
            System.out.println("isSuccessful");
        }
        return getInstance().isConnectionSuccessful(widget, id);
    }

    private final Map<String, BrowserConnectionJob> debuggerJobs = new TreeMap<String, BrowserConnectionJob>();

    private WebAppInterface() {
        try {
            WebappManager.start("wrtbrowser");
        } catch (Exception e) {
            PreviewerPlugin.log(e);
        }
    }

    private synchronized URI complete(String widget, String id) {
        return WorkspaceResourcesServlet.getPreviewerStartingPage(widget);
    }

    private synchronized void connect(String widget, String id, String sId) {
        if (!isConnected(widget, id)) {
            BrowserConnectionJob listener = debuggerJobs.get(getId(widget, id));
            listener.setSessionId(sId);
            listener.schedule(250);
        }
    }

    private URI createAjaxUri(String widget, String id) {
        try {
            return createUri("connectionTest.jsp", widget, id);
        } catch (URISyntaxException e) {
            PreviewerPlugin.log(e);
            return null;
        }
    }

    private URI createUri(String page, String project, String session) throws URISyntaxException {
        URI uri = new URI("http", null, WebappManager.getHost(), WebappManager.getPort(), "/wrtdebugger/" + page,
                "widget=" + encode(project) + "&session=" + session, null);
        return uri;
    }

    private String getId(String name, String session) {
        return name + "$" + session;
    }

    private boolean isJobComplete(String widget, String id) {
        BrowserConnectionJob job = debuggerJobs.get(getId(widget, id));
        return job.isReady();
    }

    private boolean isConnectionSuccessful(String widget, String id) {
        BrowserConnectionJob job = debuggerJobs.get(getId(widget, id));
        return job.isSuccess();
    }

    public synchronized URI prepareDebugger(IProject project, IPreviewStartupListener listener) {
        String session = Long.toHexString(System.currentTimeMillis());
        URI uri = WorkspaceResourcesServlet.getDebugStartingPage(project, session);
        if (listener != null) {
            debuggerJobs.put(getId(project.getName(), session), new BrowserConnectionJob(listener, uri));
        }
        return uri;
    }
}
