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
package org.symbian.tools.wrttools.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.navigator.ILinkHelper;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.symbian.tools.wrttools.Activator;

public class WrtNavigatorLinkHelper implements ILinkHelper {

    public void activateEditor(IWorkbenchPage aPage, IStructuredSelection aSelection) {
        IResource selection = filter(aSelection.toArray());
        if (selection == null) {
            return;
        }
        IEditorReference[] references = aPage.getEditorReferences();
        for (IEditorReference editorReference : references) {
            IEditorInput input;
            try {
                input = editorReference.getEditorInput();
                Object resource = input.getAdapter(IResource.class);
                if (resource != null && selection.equals(resource)) {
                    IWorkbenchPart part = editorReference.getPart(true);
                    if (part != null) {
                        aPage.activate(part);
                    }
                    return;
                }
            } catch (PartInitException e) {
                Activator.log(e);
            }
        }
    }

    private IResource filter(Object[] array) {
        IResource selection = null;
        for (Object object : array) {
            IResource resource = null;
            if (object instanceof IResource) {
                resource = (IResource) object;
            } else if (object instanceof IJavaScriptElement) {
                resource = ((IJavaScriptElement) object).getResource();
            }
            if (selection == null) {
                selection = resource;
            } else if (!selection.equals(resource)) {
                return null;
            }
        }
        return selection;
    }

    public IStructuredSelection findSelection(IEditorInput anInput) {
        final IFileEditorInput input = (IFileEditorInput) anInput.getAdapter(IFileEditorInput.class);
        IFile file = input.getFile();
        return new StructuredSelection(file);
    }
}
