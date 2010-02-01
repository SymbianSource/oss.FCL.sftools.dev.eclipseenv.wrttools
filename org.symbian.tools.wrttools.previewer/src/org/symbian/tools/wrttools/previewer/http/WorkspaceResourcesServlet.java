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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
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
import org.symbian.tools.wrttools.previewer.PreviewerPlugin;

public class WorkspaceResourcesServlet extends HttpServlet {
	private static final String STARTING_PAGE = "preview-frame.html";
	private static final String INDEX_PAGE = "wrt_preview_main.html";
	private static final long serialVersionUID = -3217197074249607950L;

	private static final Pattern HTML_FILE_NAME_PROPERTY = Pattern
			.compile("<key>\\s*MainHTML\\s*</key>\\s*<string>\\s*(.*)\\s*</string>", Pattern.CASE_INSENSITIVE);
	private static final Pattern HEAD_TAG_PATTERN = Pattern.compile("<head(\\s*\\w*=\"(^\")*\")*\\s*>", Pattern.CASE_INSENSITIVE);
	private static final String SCRIPT = "<script language=\"JavaScript\" type=\"text/javascript\" src=\"preview/script/lib/loader.js\"></script>";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		IPath path = new Path(req.getPathInfo());
		InputStream contents = null;
		try {
			contents = getSpecialResource(path);
			if (contents == null) {
				contents = getWorkspaceResourceContents(path);
			}
			if (contents != null) {
				copyData(contents, resp.getOutputStream());
			} else {
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (CoreException e) {
			PreviewerPlugin.log(e);
		} finally {
			if (contents != null) {
				contents.close();
			}
		}
	}

	private InputStream getSpecialResource(IPath path) throws IOException,
			CoreException {
		IPath relativePath = path.removeFirstSegments(1);
		if (relativePath.segmentCount() == 1) {
			if (STARTING_PAGE.equals(relativePath.segment(0))) {
				return getPluginResourceStream(new Path(
						"/preview/wrt_preview.html"));
			} else if (INDEX_PAGE.equals(relativePath.segment(0))) {
				return getProjectIndexPage(path.segment(0));
			}
		} else if ("preview".equals(relativePath.segment(0))) {
			return getPluginResourceStream(relativePath.makeAbsolute());
		}
		return null;
	}

	private InputStream getProjectIndexPage(String projectName)
			throws IOException, CoreException {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				projectName);
		if (project.isAccessible()) {
			String indexFileName = getIndexFileName(project);
			if (indexFileName != null) {
				String string = readFile(project, indexFileName);
				if (string != null) {
					Matcher matcher = HEAD_TAG_PATTERN.matcher(string);
					if (matcher.find()) {
						string = matcher.replaceFirst(matcher.group() + SCRIPT);
					}
					return new ByteArrayInputStream(string.getBytes("UTF-8"));
				}
			}
		}
		return null;
	}

	private String getIndexFileName(IProject project) throws CoreException,
			UnsupportedEncodingException, IOException {
		String buffer = readFile(project, "Info.plist");
		if (buffer != null) {
			Matcher matcher = HTML_FILE_NAME_PROPERTY.matcher(buffer);
			if (matcher.find()) {
				return matcher.group(1);
			}
		}
		return null;
	}

	private String readFile(IProject project, String fileName)
			throws CoreException, UnsupportedEncodingException, IOException {
		IFile file = getFile(project, fileName);
		if (file.isAccessible()) {
			InputStream contents = file.getContents();
			final BufferedReader reader = new BufferedReader(
					new InputStreamReader(contents, file.getCharset()));
			StringBuffer buffer = new StringBuffer();
			try {
				int c = 0;
				char[] buf = new char[4096];
				while ((c = reader.read(buf)) > 0) {
					buffer.append(buf, 0, c);
				}
				return buffer.toString();
			} finally {
				reader.close();
			}
		}
		return null;
	}

	private IFile getFile(IProject project, String fileName) throws CoreException {
		String n = fileName.toLowerCase();
		IResource[] members = project.members();
		for (IResource iResource : members) {
			if (iResource.getType() == IResource.FILE
					&& n.equals(iResource.getName().toLowerCase())
					&& iResource.isAccessible()) {
				return (IFile) iResource;
			}
		}
		return null;
	}

	private InputStream getPluginResourceStream(IPath path) throws IOException {
		URL url = FileLocator.find(PreviewerPlugin.getDefault().getBundle(),
				path, null);
		if (url != null) {
			return url.openStream();
		} else {
			return null;
		}
	}

	private void copyData(InputStream contents, OutputStream ouput)
			throws IOException {
		byte[] buf = new byte[4048];
		int i;
		while ((i = contents.read(buf)) >= 0) {
			ouput.write(buf, 0, i);
		}
	}

	private InputStream getWorkspaceResourceContents(IPath path)
			throws CoreException {
		InputStream contents = null;
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		if (file.isAccessible()) {
			contents = file.getContents();
		}
		return contents;
	}

	public static String getHttpUrl(IResource file) {
		String uri = getServerURIForResource(file != null ? file.getFullPath()
				.toString() : "/");
		if (uri != null) {
			return uri;
		} else {
			return file.getLocationURI().toString();
		}
	}

	private static String getServerURIForResource(String resourcePath) {
		String uri = null;
		try {
			String path = WebappManager.WORKSPACE_RESOURCES_CONTEXT
					+ resourcePath;
			URL url = new URL("http", WebappManager.getHost(), WebappManager
					.getPort(), encode(path));
			uri = url.toString();
		} catch (MalformedURLException e) {
			uri = null;
		}
		return uri;
	}

	private static String encode(String path) {
		try {
			StringBuffer result = new StringBuffer();
			String[] segments = new Path(path).segments();
			for (int i = 0; i < segments.length; i++) {
				String string = segments[i];
				result.append("/");
				// java.net.URLEncoder encodes " " as "+" while Chrome needs
				// "%20"
				StringTokenizer tokenizer = new StringTokenizer(string, " ",
						false);
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

	public static String getPreviewerStartingPage(String widget) {
		return getServerURIForResource(new Path(widget).append(STARTING_PAGE)
				.makeAbsolute().toString());
	}
}
