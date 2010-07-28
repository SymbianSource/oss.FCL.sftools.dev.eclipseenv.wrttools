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
package org.symbian.tools.mtw.ui;

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
import org.symbian.tools.mtw.core.projects.IMTWProject;
import org.symbian.tools.mtw.internal.deployment.DeploymentTargetProviderRegistry;
import org.symbian.tools.mtw.ui.deployment.IDeploymentTarget;
import org.symbian.tools.mtw.ui.deployment.IDeploymentTargetProvider;

public class ProjectMemo {
    private static final String DEPLOYMENT_PROVIDER = "providerId";
    private static final String TARGET = "targetId";
    private static final String TARGET_CONFIGURATION = "target";
    private static final String MEMO_TYPE = "deployment";

    private final IMTWProject project;
    private XMLMemento memento;

    public ProjectMemo(IMTWProject project) {
        this.project = project;
    }

    public synchronized void setDeploymentTarget(String providerId, IDeploymentTarget target) {
        try {
            checkMemento();
            memento.putString(DEPLOYMENT_PROVIDER, providerId);
            memento.putString(TARGET, target.getId());
            final IMemento child = memento.createChild(TARGET_CONFIGURATION);
            target.save(child);
            saveMemento();
        } catch (CoreException e) {
            MTWCoreUI.log(e);
        } catch (IOException e) {
            MTWCoreUI.log(e);
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
        return project.getProject().getFile(new Path(".settings").append(MTWCoreUI.PLUGIN_ID).addFileExtension("xml"));
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
            String type = memento.getString(DEPLOYMENT_PROVIDER);
            if (type != null) {
                final IDeploymentTargetProvider provider = DeploymentTargetProviderRegistry.getInstance().getProvider(
                        type);
                if (provider != null) {
                    IDeploymentTarget target = provider.findTarget(project, memento.getString(TARGET));
                    if (target != null) {
                        IMemento child = memento.getChild(TARGET_CONFIGURATION);
                        if (child != null) {
                            target.load(child);
                        }
                    }
                }
            }
        } catch (CoreException e) {
            MTWCoreUI.log(e);
        } catch (IOException e) {
            MTWCoreUI.log(e);
        }
        return null;
    }

}
