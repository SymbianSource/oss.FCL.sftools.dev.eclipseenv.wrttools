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
package org.symbian.tools.tmw.previewer.preview;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.symbian.tools.tmw.previewer.PreviewerPlugin;

public final class ProjectPreferencesManager {
    private static IFile getPreferencesXml(IProject project) {
        return project.getFile(new Path(".settings").append(PreviewerPlugin.PLUGIN_ID).addFileExtension("xml"));
    }

    public static synchronized Properties getProjectProperties(IProject project) throws IOException, CoreException {
        Properties projectPreferences = new Properties();
        IFile xml = getPreferencesXml(project);
        if (!xml.isSynchronized(IResource.DEPTH_ZERO)) {
            xml.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
        }
        if (xml.isAccessible()) {
            InputStream contents = null;
            try {
                contents = xml.getContents();
                projectPreferences.loadFromXML(contents);
            } finally {
                if (contents != null) {
                    contents.close();
                }
            }
        }
        return projectPreferences;
    }

    public static synchronized void setProjectProperties(IProject project, Properties properties) throws IOException,
            CoreException {
        IFile xml = getPreferencesXml(project);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        properties.storeToXML(stream, null);
        stream.close();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(stream.toByteArray());
        if (xml.exists()) {
            xml.setContents(inputStream, IFile.KEEP_HISTORY, new NullProgressMonitor());
        } else {
            xml.create(inputStream, false, new NullProgressMonitor());
        }
        inputStream.close();
    }

    private ProjectPreferencesManager() {
        // No instantiation
    }
}
