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
package org.symbian.tools.tmw.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.projects.ITMWProject;
import org.symbian.tools.tmw.ui.deployment.IDeploymentTarget;
import org.symbian.tools.tmw.ui.deployment.IDeploymentTargetType;

public class ProjectMemo {
    private static final String TARGET_TYPE = "typeId";
    private static final String TARGET = "targetId";
    private static final String TARGET_CONFIGURATION = "target";
    private static final String MEMO_TYPE = "deployment";

    private final ITMWProject project;
    private XMLMemento memento;

    public ProjectMemo(ITMWProject project) {
        this.project = project;
    }

    public synchronized void setDeploymentTarget(String providerId, IDeploymentTarget target) {
        try {
            checkMemento();
            memento.putString(TARGET_TYPE, providerId);
            memento.putString(TARGET, target.getId());
            IMemento child = null;
            IMemento[] children = memento.getChildren(TARGET_CONFIGURATION);
            for (IMemento memento : children) {
                if (providerId.equals(memento.getString(TARGET_TYPE))
                        && target.getId().equals(memento.getString(TARGET))) {
                    child = memento;
                }
            }
            if (child == null) {
                child = memento.createChild(TARGET_CONFIGURATION);
                child.putString(TARGET_TYPE, providerId);
                child.putString(TARGET, target.getId());
            }
            target.save(child);
            saveMemento();
        } catch (CoreException e) {
            TMWCoreUI.log(e);
        } catch (IOException e) {
            TMWCoreUI.log(e);
        }
    }

    private void saveMemento() throws IOException, CoreException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        final Writer writer = new OutputStreamWriter(os);
        memento.save(writer);
        writer.close();
        final InputStream stream = new ByteArrayInputStream(os.toByteArray());
        IFile file = getMemoFile();
        if (file.exists()) {
            file.setContents(stream, IFile.KEEP_HISTORY, null);
        } else {
            file.create(stream, false, null);
        }
    }

    private IFile getMemoFile() {
        return project.getProject().getFile(new Path(".settings").append(TMWCoreUI.PLUGIN_ID).addFileExtension("xml"));
    }

    private void checkMemento() throws CoreException, IOException {
        IFile memoFile = getMemoFile();
        if (memoFile.exists()) {
            memento = XMLMemento.createReadRoot(new InputStreamReader(memoFile.getContents(), memoFile.getCharset()));
        } else {
            memento = XMLMemento.createWriteRoot(MEMO_TYPE);
        }
    }

    public IDeploymentTarget getPreviousDeploymentTarget() {
        try {
            checkMemento();
            String type = memento.getString(TARGET_TYPE);
            if (type != null) {
                final IDeploymentTargetType provider = TMWCoreUI.getDefault().getDeploymentTypesRegistry()
                        .getType(type);
                if (provider != null) {
                    IDeploymentTarget target = provider.findTarget(project, memento.getString(TARGET));
                    if (target != null) {
                        return target;
                    }
                }
            }
        } catch (CoreException e) {
            TMWCoreUI.log(e);
        } catch (IOException e) {
            TMWCoreUI.log(e);
        }
        return null;
    }

    public IMemento getMemo(String targetType, IDeploymentTarget target) {
        if (memento != null) {
            IMemento[] children = memento.getChildren(TARGET_CONFIGURATION);
            for (IMemento memento : children) {
                if (targetType.equals(memento.getString(TARGET_TYPE))
                        && target.getId().equals(memento.getString(TARGET))) {
                    return memento;
                }
            }
        }
        return null;
    }

    public void setAttribute(String name, String value) {
        try {
            checkMemento();
            memento.putString(name, value);
            saveMemento();
        } catch (CoreException e) {
            TMWCoreUI.log(e);
        } catch (IOException e) {
            TMWCoreUI.log(e);
        }
    }

    public String getAttribute(String name) {
        try {
            checkMemento();
        } catch (CoreException e) {
            TMWCore.log(null, e);
        } catch (IOException e) {
            TMWCore.log(null, e);
        }
        if (memento != null) {
            return memento.getString(name);
        }
        return null;
    }

}
