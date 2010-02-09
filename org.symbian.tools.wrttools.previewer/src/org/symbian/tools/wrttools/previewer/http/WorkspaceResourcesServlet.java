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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import org.symbian.tools.wrttools.util.CoreUtil;

public class WorkspaceResourcesServlet extends HttpServlet {
	private static final String PREVIEW_START = "/preview/wrt_preview.html";
	private static final String METADATA_FILE = "Info.plist";
	private static final String PREVIEW_PATH = "preview";
	private static final String STARTING_PAGE = "preview-frame.html";
	private static final String INDEX_PAGE = "wrt_preview_main.html";
	private static final long serialVersionUID = -3217197074249607950L;

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
						PREVIEW_START));
			} else if (INDEX_PAGE.equals(relativePath.segment(0))) {
				return getProjectIndexPage(path.segment(0));
			}
		} else if (PREVIEW_PATH.equals(relativePath.segment(0))) {
			return getPluginResourceStream(relativePath.makeAbsolute());
		}
		return null;
	}

	private InputStream getProjectIndexPage(String projectName)
			throws IOException, CoreException {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				projectName);
		if (project.isAccessible()) {
			String indexFileName = CoreUtil.getIndexFileName(CoreUtil.readFile(project, METADATA_FILE));
			if (indexFileName != null) {
				String string = CoreUtil.readFile(project, indexFileName);
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
		Path p = new Path(resourcePath);
		if (p.segmentCount() > 1) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(p.segment(0));
			try {
				if (p.removeFirstSegments(1).toString().equals(
						CoreUtil.getIndexFileName(CoreUtil.readFile(project,
								METADATA_FILE)))) {
					return getServerURIForResource(new Path(p.segment(0))
							.append(INDEX_PAGE).makeAbsolute().toString());
				}
			} catch (Exception e1) {
				PreviewerPlugin.log(e1);
			}
		}
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
		IPath path = getProjectRelativePath(name);
		if (path != null) {
			return getProjectResource(path);
		} else {
			return null;
		}
	}

	private static IPath getProjectRelativePath(String uri) {
		IPath path = null;
		try {
			String root = getHttpUrl(null);
			if (uri != null && uri.startsWith(root)) {
				String fileName = uri.substring(root.length());
				fileName = URLDecoder.decode(fileName, "UTF-8");
				path = new Path(fileName);
				if (path.segmentCount() == 2 && INDEX_PAGE.equals(path.lastSegment())) {
					IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(path.segment(0));
					path = new Path(project.getName()).append(CoreUtil.getIndexFileName(CoreUtil.readFile(project, METADATA_FILE)));
				}
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (CoreException e) {
			PreviewerPlugin.log(e);
		} catch (IOException e) {
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

	public static String getPreviewerStartingPage(String widget) {
		return getServerURIForResource(new Path(widget).append(STARTING_PAGE)
				.makeAbsolute().toString());
	}

	public static File getPreviewerResource(String name) {
		try {
			IPath path = getProjectRelativePath(name);
			if (path != null) {
				if (path.segmentCount() == 2
						&& STARTING_PAGE.equals(path.segment(1))) {
					path = new Path(PREVIEW_START);
				} else if (path.segmentCount() > 2
						&& PREVIEW_PATH.equals(path.segment(1))) {
					path = path.removeFirstSegments(1);
				} else {
					return null;
				}
				URL pluginResource = FileLocator.find(PreviewerPlugin
						.getDefault().getBundle(), path, null);
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
}
