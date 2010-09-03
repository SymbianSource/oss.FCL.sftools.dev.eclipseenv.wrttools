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
package org.symbian.tools.wrttools.product.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.internal.ui.actions.DebugLastAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.symbian.tools.tmw.core.TMWCore;

@SuppressWarnings("restriction")
public class DebugAction extends DebugLastAction {
    @Override
    public void run(IAction action) {
        boolean hasTmwProjects = false;
        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        for (IProject project : projects) {
            if (project.isAccessible() && TMWCore.create(project) != null) {
                hasTmwProjects = true;
                break;
            }
        }
        if (hasTmwProjects) {
            super.run(action);
        } else {
            MessageDialog.openInformation(getShell(), "Cannot launch debugger",
                    "There are no open projects in your workspace");
        }
    }
}
