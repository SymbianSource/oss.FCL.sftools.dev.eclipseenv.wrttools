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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.symbian.tools.wrttools.debug.internal.Activator;

public class WorkspaceResourcesServlet extends HttpServlet {
	private static final long serialVersionUID = -3217197074249607950L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(
				new Path(req.getPathInfo()));
		if (file.isAccessible()) {
			InputStream contents = null;
			try {
				contents = file.getContents();
				byte[] buf = new byte[4048];
				int i;
				while ((i = contents.read(buf)) >= 0) {
					resp.getOutputStream().write(buf, 0, i);
				}
			} catch (CoreException e) {
				Activator.log(e);
			} finally {
				if (contents != null) {
					contents.close();
				}
			}
		} else {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	public static String getHttpUrl(IResource file) {
		try {
			String path = WebappManager.WORKSPACE_RESOURCES_CONTEXT
					+ (file != null ? encode(file.getFullPath()) : "/");
			URL url = new URL("http", WebappManager.getHost(), WebappManager
					.getPort(), path);
			return url.toString();
		} catch (MalformedURLException e) {
			return file.getLocationURI().toString();
		}
	}

	private static String encode(IPath fullPath) {
		try {
			StringBuffer result = new StringBuffer();
			String[] segments = fullPath.segments();
			for (int i = 0; i < segments.length; i++) {
				String string = segments[i];
				result.append("/");
				// java.net.URLEncoder encodes " " as "+" while Chrome needs "%20"
				StringTokenizer tokenizer = new StringTokenizer(string, " ", false);
				while (tokenizer.hasMoreElements()) {
					result.append(URLEncoder.encode(tokenizer.nextToken(),
							"UTF-8"));
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
		try {
			String root = getHttpUrl(null);
			IFile file = null;
			if (name.startsWith(root)) {
				String fileName = name.substring(root.length());
				fileName = URLDecoder.decode(fileName, "UTF-8");
				final IPath path = new Path(fileName);
				file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
				if (!file.isAccessible()) {
					return null;
				}
			}
			return file;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}
