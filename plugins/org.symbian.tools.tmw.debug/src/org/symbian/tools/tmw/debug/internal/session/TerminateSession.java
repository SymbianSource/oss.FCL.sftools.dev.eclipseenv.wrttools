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
package org.symbian.tools.tmw.debug.internal.session;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.symbian.tools.tmw.debug.internal.Activator;
import org.symbian.tools.tmw.debug.internal.IConstants;
import org.symbian.tools.tmw.debug.internal.launch.WidgetLaunchDelegate;
import org.symbian.tools.tmw.previewer.IPreviewerCommandHandler;

public class TerminateSession implements IPreviewerCommandHandler {
    @SuppressWarnings("rawtypes")
    public void handle(String commandName, String projectName, Map parameters) {
        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
        for (ILaunch launch : launchManager.getLaunches()) {
            try {
                if (!launch.isTerminated()) {
                    ILaunchConfiguration lc = launch.getLaunchConfiguration();
                    if (WidgetLaunchDelegate.ID.equals(lc.getType().getIdentifier())
                            && projectName.equals(lc.getAttribute(IConstants.PROP_PROJECT_NAME, ""))) {
                        launch.terminate();
                    }
                }
            } catch (CoreException e) {
                Activator.log(e);
            }
        }
    }

}
