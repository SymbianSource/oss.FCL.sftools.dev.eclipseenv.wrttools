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
package org.symbian.tools.wrttools.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.PlatformUI;
import org.symbian.tools.wrttools.WRTStatusListener;
import org.symbian.tools.wrttools.core.packager.WrtPackageActionDelegate;
import org.symbian.tools.wrttools.util.ProjectUtils;

public class PackageApplicationHandler extends AbstractHandler implements IHandler {

    public Object execute(ExecutionEvent event) throws ExecutionException {
        IProject project = ProjectUtils.getProjectFromCommandContext(event);

        if (project != null) {
            PlatformUI.getWorkbench().saveAllEditors(true);
            if (project != null) {
                WRTStatusListener statusListener = new WRTStatusListener();
                new WrtPackageActionDelegate().packageProject(project, statusListener);
            }
        }
        return null;
    }

}
