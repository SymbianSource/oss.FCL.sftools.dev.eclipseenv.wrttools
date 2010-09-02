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

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMText;
import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.util.CoreUtil;
import org.symbian.tools.wrttools.util.ProjectUtils;
import org.symbian.tools.wrttools.util.Util;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@SuppressWarnings("restriction")
public class ProjectRenameParticipant extends RenameParticipant {
    private IProject project;

    @Override
    protected boolean initialize(Object element) {
        if (element instanceof IProject && (ProjectUtils.hasWrtNature((IProject) element))) {
            project = (IProject) element;
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return "Mobile Web Project Rename Participant";
    }

    @Override
    public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) {
        return null;
    }

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException {
        final IFile file = project.getFile(CoreUtil.METADATA_FILE);
        if (file.isAccessible()) {
            IStructuredModel model = null;
            try {
                model = StructuredModelManager.getModelManager().getModelForRead(file);
                if (model != null) {
                    final TextEdit change = getAttributesChange(model);
                    if (change != null) {
                        IFile newFile = ResourcesPlugin.getWorkspace().getRoot()
                                .getFile(new Path(getArguments().getNewName()).append(file.getProjectRelativePath()));
                        TextFileChange fileChange = new TextFileChange("Rename application", newFile);
                        fileChange.setEdit(change);
                        return fileChange;
                    } else {
                        return null;
                    }
                }
            } catch (IOException e) {
                throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Cannot rename project", e));
            } finally {
                if (model != null) {
                    model.releaseFromRead();
                }
            }
        }
        return null;
    }

    private TextEdit getAttributesChange(IStructuredModel model) {
        MultiTextEdit edit = new MultiTextEdit();
        IDOMDocument document = ((IDOMModel) model).getDocument();
        Element element = document.getDocumentElement();
        if (element != null) {
            NodeList dict = element.getElementsByTagName("dict");
            if (dict.getLength() == 1) {
                NodeList children = dict.item(0).getChildNodes();
                for (int i = 0; i < children.getLength() - 2; i++) { // Note - we only care about name-value pairs, so we do not to iterate to last element. Also remember there's text node between elements
                    final String key = getKey(children.item(i));
                    if ("DisplayName".equals(key)) {
                        final IDOMText text = getTextNode(children.item(i + 2));
                        if (text != null) {
                            final String val = text.getNodeValue();
                            if (val.trim().equals(project.getName())) {
                                edit.addChild(new ReplaceEdit(text.getStartOffset() + val.indexOf(project.getName()),
                                        project.getName().length(), getArguments().getNewName()));
                            }
                        }
                    } else if ("Identifier".equals(key)) {
                        final IDOMText text = getTextNode(children.item(i + 2));
                        if (text != null) {
                            final String val = text.getNodeValue();
                            final String idPart = Util.removeSpaces(project.getName());
                            int offset = val.indexOf(idPart);
                            if (offset >= 0) {
                                edit.addChild(new ReplaceEdit(text.getStartOffset() + offset, idPart.length(), Util
                                        .removeSpaces(getArguments().getNewName())));
                            }
                        }
                    }
                }
            }
        }
        return edit.getChildrenSize() > 0 ? edit : null;
    }

    private IDOMText getTextNode(final Node value) {
        if (value.getNodeType() == Node.ELEMENT_NODE && "string".equals(value.getNodeName())) {
            NodeList childNodes = value.getChildNodes();
            if (childNodes.getLength() == 1 && childNodes.item(0).getNodeType() == Node.TEXT_NODE) {
                return (IDOMText) childNodes.item(0);
            }
        }
        return null;
    }

    private String getKey(final Node node) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            if ("key".equals(node.getNodeName())) {
                return getNodeValue(node);
            }
        }
        return null;
    }

    private String getNodeValue(final Node node) {
        final NodeList childNodes = node.getChildNodes();
        if (childNodes.getLength() == 1) {
            final Node contents = childNodes.item(0);
            if (contents.getNodeType() == Node.TEXT_NODE) {
                return contents.getNodeValue().trim();
            }
        }
        return null;
    }

}
