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
package org.symbian.tools.tmw.internal.util;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.projects.ITMWProject;

public final class Util {
    public static String removeSpaces(String value) {
        return value != null ? value.trim().replace(" ", "") : "";
    }

    public static String neverNull(String string) {
        return string == null ? "" : string.trim();
    }

    private Util() {
        // No instantiation
    }

    public static ITMWProject getProjectFromCommandContext(ExecutionEvent event) {
        IResource resource = null;
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        if (activePart instanceof IEditorPart) {
            resource = (IResource) ((IEditorPart) activePart).getEditorInput().getAdapter(IResource.class);
        } else {
            ISelection selection = HandlerUtil.getCurrentSelection(event);
            if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
                Object[] array = ((IStructuredSelection) selection).toArray();
                if (array.length == 1 && array[0] instanceof IAdaptable) {
                    resource = (IResource) ((IAdaptable) array[0]).getAdapter(IResource.class);
                }
            }
        }
        if (resource != null) {
            IProject project = resource.getProject();
            return TMWCore.create(project);
        }
        return null;
    }
}
