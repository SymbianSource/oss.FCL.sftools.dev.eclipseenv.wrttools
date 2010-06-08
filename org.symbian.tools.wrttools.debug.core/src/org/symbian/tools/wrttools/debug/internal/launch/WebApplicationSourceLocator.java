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

import java.io.File;

import org.chromium.debug.core.model.StackFrame;
import org.chromium.sdk.Script;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant;
import org.symbian.tools.wrttools.debug.internal.Activator;
import org.symbian.tools.wrttools.debug.internal.model.ResourceManager;
import org.symbian.tools.wrttools.previewer.PreviewerPlugin;

public final class WebApplicationSourceLocator extends AbstractSourceLookupDirector implements ISourceLocator,
        ISourceLookupDirector {
    public static final class WebApplicationSourceLookupParticipant extends AbstractSourceLookupParticipant {
        private final ResourceManager resourceManager;

        public WebApplicationSourceLookupParticipant(ResourceManager resourceManager) {
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
                }
            }
            return null;
        }
    }

    private final ResourceManager resourceManager;

    public WebApplicationSourceLocator(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
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
        addParticipants(new ISourceLookupParticipant[] { new WebApplicationSourceLookupParticipant(resourceManager) });
    }
}