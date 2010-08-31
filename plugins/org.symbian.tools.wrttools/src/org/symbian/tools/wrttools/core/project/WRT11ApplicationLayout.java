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
package org.symbian.tools.wrttools.core.project;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.symbian.tools.tmw.core.runtimes.IApplicationLayoutProvider;
import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.util.CoreUtil;
import org.symbian.tools.wrttools.util.ProjectUtils;

public class WRT11ApplicationLayout implements IApplicationLayoutProvider {
    private static final Pattern HEAD_TAG_PATTERN = Pattern.compile("<head(\\s*\\w*=\"(^\")*\")*\\s*>",
            Pattern.CASE_INSENSITIVE);
    public static final String INDEX = "wrt_preview_main.html";
    private static final String SCRIPT = "<script language=\"JavaScript\" "
            + "type=\"text/javascript\" src=\"preview/script/lib/loader.js\"></script>";

    public String[] getPaths() {
        return new String[] { INDEX };
    }

    private IFile getProjectIndexPage(String projectName) throws IOException, CoreException {
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        if (project.isAccessible()) {
            final IFile file = CoreUtil.getIndexFile(project);
            if (file != null && !ProjectUtils.isExcluded(file)) {
                return file;
            }
            Activator.log(String.format("Can not find main page in project %s.\n", project.getName()), null);
        }
        return null;
    }

    private InputStream patchIndexFile(final IFile file) throws CoreException, UnsupportedEncodingException {
        String string = CoreUtil.readFile(file);
        if (string != null) {
            Matcher matcher = HEAD_TAG_PATTERN.matcher(string);
            if (matcher.find()) {
                string = matcher.replaceFirst(matcher.group() + SCRIPT);
            }
            return new ByteArrayInputStream(string.getBytes("UTF-8"));
        }
        return null;
    }

    public InputStream getResourceFromPath(IProject project, IPath path) throws CoreException {
        final IFile file = getWorkspaceFile(project, path);
        if (file == null) {
            return null;
        }
        if (path.segmentCount() == 1 && INDEX.equals(path.segment(0))) {
            try {
                return patchIndexFile(file);
            } catch (IOException e) {
                throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                        "Can't access preview index file", e));
            }
        }
        return file.getContents();
    }

    public IPath getResourcePath(IFile file) {
        try {
            if (file != null && file.getProjectRelativePath().equals(CoreUtil.getIndexFile(file.getProject()))) {
                return new Path(INDEX);
            }
        } catch (CoreException e) {
            Activator.log(e);
        }
        return file != null && file.exists() && isFileIncluded(file) ? file.getProjectRelativePath().makeRelative()
                : null;
    }

    private boolean isFileIncluded(IFile file) {
        return !ProjectUtils.isExcluded(file);
    }

    // WRT runtime is case-insensitive so we need to properly process files in a case-insensitive way
    private IFile tryCaseInsensitiveSearch(IContainer container, IPath path) throws CoreException {
        if (path.segmentCount() == 0) {
            return null;
        }
        final String segment = path.segment(0);
        final IResource[] members = container.members();
        for (IResource resource : members) {
            if (resource.getName().equalsIgnoreCase(segment)) {
                if (resource.getType() == IResource.FILE) {
                    return (IFile) (path.segmentCount() == 1 ? resource : null);
                } else {
                    return tryCaseInsensitiveSearch((IContainer) resource, path.removeFirstSegments(1));
                }
            }
        }
        return null;
    }

    public IFile getWorkspaceFile(IProject project, IPath path) throws CoreException {
        if (path.segmentCount() == 1 && INDEX.equals(path.segment(0))) {
            try {
                return getProjectIndexPage(project.getName());
            } catch (IOException e) {
                throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Can't find project index file",
                        e));
            }
        }
        IFile file = project.getFile(path);
        if (!file.exists()) {
            try {
                file = tryCaseInsensitiveSearch(project, path);
            } catch (CoreException e) {
                Activator.log(e);
            }
        }
        return (file != null && file.exists()) && isFileIncluded(file) ? file : null;
    }

    public IFile getIndexPage(IProject project) {
        try {
            return CoreUtil.getIndexFile(project);
        } catch (CoreException e) {
            Activator.log(e);
            return null;
        }
    }

}
