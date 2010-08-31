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
package org.symbian.tools.tmw.core.internal.projects;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.projects.IProjectSetupConfig;
import org.symbian.tools.tmw.core.projects.ITMWProject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@SuppressWarnings("restriction")
public class LibraryAdditionConfigObject implements IProjectSetupConfig {

    public void initialize(IProject project, IProgressMonitor monitor) {
        // Do nothing
    }

    public IFile addFile(IProject project, IPath name, InputStream contents, IProgressMonitor monitor)
            throws CoreException {
        IFile file = project.getFile(name);
        if (!file.exists()) {
            createContainers(file.getParent());
            file.create(contents, false, monitor);
        }
        return file;
    }

    private void createContainers(IContainer container) throws CoreException {
        if (container.getType() == IResource.FOLDER && !container.exists()) {
            createContainers(container.getParent());
            ((IFolder) container).create(false, true, new NullProgressMonitor());
        }
    }

    public void addIncludedJsFile(IProject project, IFile file) {
        // It is highly likely this code is extremely specific to Symbian web runtime.
        // Please open a bug and clarify the requirements
        final ITMWProject tmwProject = TMWCore.create(project);
        if (tmwProject != null && tmwProject.getTargetRuntime() != null) {
            final IFile page = tmwProject.getTargetRuntime().getLayoutProvider().getIndexPage(tmwProject.getProject());
            if (page != null && page.exists()) {
                IStructuredModel model = null;
                try {
                    model = StructuredModelManager.getModelManager().getModelForEdit(page);
                    if (model instanceof IDOMModel) {
                        final IDOMDocument document = ((IDOMModel) model).getDocument();
                        final String path = file.getProjectRelativePath().makeRelative().toString();
                        final NodeList scripts = document.getElementsByTagName("script");
                        boolean hasScript = false;
                        for (int i = 0; i < scripts.getLength(); i++) {
                            if (path.equals(((Element) scripts.item(i)).getAttribute("src"))) {
                                hasScript = true;
                                break;
                            }
                        }
                        if (!hasScript) {
                            final NodeList heads = document.getElementsByTagName("head");
                            if (heads.getLength() > 0) {
                                Element el = document.createElement("script");
                                el.setAttribute("type", "text/javascript");
                                el.setAttribute("src", path);
                                heads.item(0).appendChild(el);
                                model.save();
                            }
                        }
                    }
                } catch (IOException e) {
                    TMWCore.log(null, e);
                } catch (CoreException e) {
                    TMWCore.log(null, e);
                } finally {
                    if (model != null) {
                        model.releaseFromEdit();
                    }
                }
            }
        }
    }
}
