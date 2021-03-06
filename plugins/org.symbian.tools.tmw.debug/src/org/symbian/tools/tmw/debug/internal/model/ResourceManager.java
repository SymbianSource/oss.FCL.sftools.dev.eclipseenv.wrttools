/*******************************************************************************
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
 *******************************************************************************/
// Copyright (c) 2009 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.
package org.symbian.tools.tmw.debug.internal.model;

import java.util.HashMap;
import java.util.Map;

import org.chromium.debug.core.model.VmResourceId;
import org.chromium.sdk.Script;
import org.eclipse.core.resources.IFile;
import org.symbian.tools.tmw.previewer.PreviewerPlugin;

/**
 * This object handles the mapping between {@link Script}s and their
 * corresponding resources inside Eclipse.
 */
public class ResourceManager {
    private final Map<IFile, Script> resourceToScript = new HashMap<IFile, Script>();
    private final Map<VmResourceId, IFile> scriptIdToResource = new HashMap<VmResourceId, IFile>();

    public synchronized void addScript(Script script) {
        IFile scriptFile = getResource(script);
        if (scriptFile == null) {
            scriptFile = getFile(script.getName());
            if (scriptFile != null) {
                VmResourceId scriptId = VmResourceId.forScript(script);
                resourceToScript.put(scriptFile, script);
                scriptIdToResource.put(scriptId, scriptFile);
            }
        }
    }

    public synchronized void clear() {
        resourceToScript.clear();
        scriptIdToResource.clear();
    }

    private IFile getFile(String name) {
        if (name == null) {
            return null;
        }
        IFile file = PreviewerPlugin.getDefault().getHttpPreviewer().getFileFromUrl(name);
        if (file != null && !file.isAccessible()) {
            file = null;
        }
        return file;
    }

    public synchronized IFile getResource(Script script) {
        return scriptIdToResource.get(VmResourceId.forScript(script));
    }

    public synchronized boolean scriptHasResource(Script script) {
        return getResource(script) != null;
    }

    public String translateResourceToScript(IFile resource) {
        return PreviewerPlugin.getDefault().getHttpPreviewer().getHttpUrl(resource);
    }

    public VmResourceId findVmResource(IFile resource) {
        final Script script = resourceToScript.get(resource);
        if (script != null) {
            return VmResourceId.forScript(script);
        } else {
            return null;
        }
    }

    public Script getScript(IFile file) {
        return resourceToScript.get(file);
    }
}
