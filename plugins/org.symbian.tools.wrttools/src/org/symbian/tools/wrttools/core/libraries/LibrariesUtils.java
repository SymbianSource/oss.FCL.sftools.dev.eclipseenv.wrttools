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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.symbian.tools.wrttools.util.CoreUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@SuppressWarnings("restriction")
public class LibrariesUtils {
    public static void addJSToHtml(IProject project, String label, String[] js, String[] css) throws CoreException,
            IOException {
        if (js == null) {
            js = new String[0];
        }
        if (css == null) {
            css = new String[0];
        }
        if (js.length == 0 && css.length == 0) {
            return;
        }
        IModelManager modelManager = StructuredModelManager.getModelManager();
        IFile file = CoreUtil.getIndexFile(project);
        if (file != null & file.exists()) {
            IStructuredModel model = modelManager.getModelForEdit(file);
            try {
                if (model instanceof IDOMModel) {
                    ((IDOMModel) model).beginRecording(js, label);
                    boolean needsSave = false;
                    for (String jsLib : js) {
                        needsSave |= change(((IDOMModel) model).getDocument(), jsLib);
                    }
                    model.endRecording(js);
                    if (needsSave) {
                        model.save();
                    }
                }
            } finally {
                if (model != null) {
                    model.releaseFromEdit();
                }
            }
        }
    }

    private static boolean change(IDOMDocument document, String jsPath) {
        NodeList head = document.getElementsByTagName("head");
        if (head.getLength() == 1) {
            IDOMElement headNode = ((IDOMElement) head.item(0));
            NodeList elements = headNode.getElementsByTagName("script");
            boolean needToAdd = true;
            IDOMElement last = null;
            for (int i = 0; i < elements.getLength(); i++) {
                last = (IDOMElement) elements.item(i);
                String attribute = last.getAttribute("src");
                if (jsPath.equalsIgnoreCase(attribute)) {
                    needToAdd = false;
                    break;
                }
            }
            if (needToAdd) {
                Element element = document.createElement("script");
                element.setAttribute("language", "javascript");
                element.setAttribute("type", "text/javascript");
                element.setAttribute("src", jsPath);
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
