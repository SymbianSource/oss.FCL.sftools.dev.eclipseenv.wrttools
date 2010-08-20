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
package org.symbian.tools.wrttools.debug.internal.launch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.chromium.debug.core.model.StackFrame;
import org.chromium.sdk.Script;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant;
import org.symbian.tools.tmw.previewer.PreviewerPlugin;
import org.symbian.tools.tmw.previewer.http.WebappManager;
import org.symbian.tools.wrttools.debug.internal.Activator;
import org.symbian.tools.wrttools.debug.internal.model.ResourceManager;

public final class WebApplicationSourceLocator extends AbstractSourceLookupDirector implements ISourceLocator,
        ISourceLookupDirector {
    public static final class WebApplicationSourceLookupParticipant extends AbstractSourceLookupParticipant {
        private final ResourceManager resourceManager;
        private final WebApplicationSourceLocator locator;

        public WebApplicationSourceLookupParticipant(WebApplicationSourceLocator locator,
                ResourceManager resourceManager) {
            this.locator = locator;
            this.resourceManager = resourceManager;
        }

        public String getSourceName(Object object) throws CoreException {
            Script script = null;
            if (object instanceof StackFrame) {
                script = ((StackFrame) object).getCallFrame().getScript();
            } else if (object instanceof Script) {
                script = (Script) object;
            } else {
                System.out.println("Source lookup request for " + object.getClass());
            }
            if (script != null) {
                IFile resource = resourceManager.getResource(script);
                if (resource != null) {
                    return resource.getProjectRelativePath().toString();
                } else {
                    return locator.fileUrl(script);
                }
            }
            return null;
        }
    }

    private final ResourceManager resourceManager;

    public WebApplicationSourceLocator(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public String fileUrl(Script script) throws CoreException {
        String name = script.getName();
        try {
            URI uri = new URI(name);
            if ("http".equals(uri.getScheme()) && WebappManager.getHost().equals(uri.getHost())
                    && WebappManager.getPort() == uri.getPort()) {
                final IPath stateLocation = Activator.getDefault().getStateLocation();
                final IPath path = stateLocation.append("systemlibraries")
                        .append(new Path(uri.getPath()).lastSegment());
                final File file = path.toFile();
                file.getParentFile().mkdirs();
                final String source = script.getSource();
                final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                try {
                    writer.write(source);
                } finally {
                    writer.close();
                }
                return new Path(file.getParentFile().getName()).append(file.getName()).toString();
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                    "Cannot load web runtime system libraries", e));
        }
        return null;
    }

    public Object getSourceElement(IStackFrame stackFrame) {
		if (stackFrame instanceof StackFrame == false) {
			return null;
		}
		StackFrame jsStackFrame = (StackFrame) stackFrame;

		Script script = jsStackFrame.getCallFrame().getScript();
		if (script == null) {
			return null;
		}

		IFile resource = resourceManager.getResource(script);
		if (resource != null) {
			return resource;
		} else {
			File file = PreviewerPlugin.getDefault().getHttpPreviewer().getLocalFile(script.getName());
            if (file != null) {
				try {
					return EFS.getStore(file.toURI());
				} catch (CoreException e) {
					Activator.log(e);
				}
			}
		}
		return null;
	}

    public void initializeParticipants() {
        addParticipants(new ISourceLookupParticipant[] { new WebApplicationSourceLookupParticipant(this,
                resourceManager) });
    }
}