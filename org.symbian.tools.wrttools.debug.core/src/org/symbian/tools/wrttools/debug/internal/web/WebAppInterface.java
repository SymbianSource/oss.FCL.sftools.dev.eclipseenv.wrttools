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
package org.symbian.tools.wrttools.debug.internal.web;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.Job;
import org.symbian.tools.wrttools.debug.internal.Activator;
import org.symbian.tools.wrttools.debug.internal.launch.DebugConnectionJob;

public class WebAppInterface {
	private static WebAppInterface INSTANCE;

	public static void connectDebugger(String widget, String id) {
		getInstance().connect(widget, id);
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
		return getInstance().createAjaxUri(widget, id).toASCIIString();
	}

	public synchronized static WebAppInterface getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new WebAppInterface();
		}
		return INSTANCE;
	}

	public static String getUrl(String widget, String id) {
		return getInstance().complete(widget, id);
	}

	public static boolean isConnected(String widget, String id) {
		return getInstance().isJobComplete(widget, id);
	}

	private final Map<String, DebugConnectionJob> debuggerJobs = new TreeMap<String, DebugConnectionJob>();

	private WebAppInterface() {
		try {
			WebappManager.start("wrtbrowser");
		} catch (Exception e) {
			Activator.log(e);
		}
	}

	private synchronized String complete(String widget, String id) {
		IFile file = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(widget).getFile("wrt_preview_frame.html");
		if (file.isAccessible()) {
			return WorkspaceResourcesServlet.getHttpUrl(file);
		}
		return "";
	}

	private synchronized void connect(String widget, String id) {
		Job job = debuggerJobs.get(getId(widget, id));
		if (job != null) {
			job.schedule();
		}
	}

	private URI createAjaxUri(String widget, String id) {
		try {
			return createUri("connectionTest.jsp", widget, id);
		} catch (URISyntaxException e) {
			Activator.log(e);
			return null;
		}
	}

	private URI createUri(String page, String project, String session)
			throws URISyntaxException {
		URI uri = new URI("http", null, WebappManager.getHost(), WebappManager
				.getPort(), "/wrtdebugger/" + page, "widget=" + encode(project)
				+ "&session=" + session, null);
		return uri;
	}

	private String getId(String name, String session) {
		return name + "$" + session;
	}

	private synchronized boolean isJobComplete(String widget, String id) {
		DebugConnectionJob job = debuggerJobs.get(getId(widget, id));
		boolean isComplete = job == null || job.isConnected();
		return isComplete;
	}

	public synchronized URI prepareDebugger(IProject project,
			DebugConnectionJob job) {
		try {
			String session = Long.toHexString(System.currentTimeMillis());
			URI uri = createUri("debugger.jsp", project.getName(), session);
			if (job != null) {
				debuggerJobs.put(getId(project.getName(), session), job);
				job.setTabUri(uri);
			}
			return uri;
		} catch (URISyntaxException e) {
			Activator.log(e);
		}
		return null;
	}
}
