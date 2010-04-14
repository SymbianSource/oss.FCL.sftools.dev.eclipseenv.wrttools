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
package org.symbian.tools.wrttools.debug.internal.launch;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.symbian.tools.wrttools.debug.internal.Activator;
import org.symbian.tools.wrttools.debug.internal.IConstants;
import org.symbian.tools.wrttools.previewer.PreviewerUtil;

public class ResourcesChangeListener implements IResourceChangeListener {
    public void resourceChanged(IResourceChangeEvent event) {
        if (event.getDelta() != null
                && !MessageDialogWithToggle.ALWAYS.equals(Activator.getDefault().getPreferenceStore().getString(
                        IConstants.PREF_SHOW_RESOURCE_CHANGE_ERROR))) {
            IFile[] changes = PreviewerUtil.getWebChanges(event.getDelta());
            if (changes.length > 0) {
                Display.getDefault().asyncExec(new Runnable() {
                    public void run() {
                        String message = "Debug browser is not updated when the files are updated. You may notice discrepancies between your workspace contents and debug session. You should either refresh the browser or restart debugging session to see the latest changes made to the workspace.";
                        MessageDialogWithToggle.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                                .getShell(), "WebRuntime Debugger", message,
                                "Do not show this warning on code changes", false, Activator.getDefault()
                                        .getPreferenceStore(), IConstants.PREF_SHOW_RESOURCE_CHANGE_ERROR);
                        ResourcesPlugin.getWorkspace().removeResourceChangeListener(ResourcesChangeListener.this);
                    }
                });
            }
        }
    }

}
