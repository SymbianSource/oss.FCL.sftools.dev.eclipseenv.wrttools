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
package org.symbian.tools.wrttools.core.libraries;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.util.CoreUtil;
import org.symbian.tools.wrttools.util.ProjectUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@SuppressWarnings("restriction")
public class WRTKitInstaller implements IJSLibraryInstaller {
    private static final String JS_PATH = "WRTKit/WRTKit.js";

    public void install(IProject project, Map<String, String> parameters, IProgressMonitor monitor)
            throws CoreException, IOException {
        monitor.beginTask("Installing WRTKit library", 15);

        // 1. Copy resources
        IFolder folder = project.getFolder("WRTKit");
        
        if (folder != null && !folder.exists()) {
            folder.create(false, true, new SubProgressMonitor(monitor, 1));
        }
        InputStream zip = FileLocator.openStream(Activator.getDefault().getBundle(), new Path("/libraries/wrtkit.zip"),
                true);
        ProjectUtils.unzip(zip, folder, 0, "WRTKit", new SubProgressMonitor(monitor, 10));
        
        // 2. Register in main HTML
        IModelManager modelManager = StructuredModelManager.getModelManager();
        String indexFile = CoreUtil.getIndexFile(project);
        if (indexFile != null) {
            IFile file = project.getFile(indexFile);
            if (file != null & file.exists()) {
                IStructuredModel model = modelManager.getModelForEdit(file);
                try {
                    if (model instanceof IDOMModel) {
                        ((IDOMModel) model).beginRecording(this, "Adding WRTKit Library");
                        try {
                            if (change(((IDOMModel) model).getDocument())) {
                                model.save();
                            }
                        } finally {
                            model.endRecording(this);
                        }
                    }
                } finally {
                    if (model != null) {
                        model.releaseFromEdit();
                    }
                }
            }
        }
        

        monitor.done();
    }

    private boolean change(IDOMDocument document) {
        NodeList head = document.getElementsByTagName("head");
        if (head.getLength() == 1) {
            IDOMElement headNode = ((IDOMElement) head.item(0));
            NodeList elements = headNode.getElementsByTagName("script");
            boolean needToAdd = true;
            IDOMElement last = null;
            for (int i = 0; i < elements.getLength(); i++) {
                last = (IDOMElement) elements.item(i);
                String attribute = last.getAttribute("src");
                if (JS_PATH.equalsIgnoreCase(attribute)) {
                    needToAdd = false;
                    break;
                }
            }
            if (needToAdd) {
                Element element = document.createElement("script");
                element.setAttribute("language", "javascript");
                element.setAttribute("type", "text/javascript");
                element.setAttribute("src", JS_PATH);
                if (last != null && last.getNextSibling() != null) {
                    headNode.insertBefore(element, last.getNextSibling());
                } else {
                    headNode.appendChild(element);
                }
                FormatProcessorXML formatter = new FormatProcessorXML();
                formatter.formatNode(headNode);
                return true;
            }
        }
        return false;
    }

}
