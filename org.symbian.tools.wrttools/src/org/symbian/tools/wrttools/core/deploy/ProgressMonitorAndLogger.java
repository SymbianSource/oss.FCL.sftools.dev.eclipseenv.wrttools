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
package org.symbian.tools.wrttools.core.deploy;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.core.status.IWRTStatusListener;
import org.symbian.tools.wrttools.core.status.WRTStatus;
import org.symbian.tools.wrttools.util.ProjectUtils;

public class ProgressMonitorAndLogger implements IWRTStatusListener {
    private final IProgressMonitor monitor;
    private final MultiStatus status;

    public ProgressMonitorAndLogger(IProgressMonitor monitor, MultiStatus status) {
        this.monitor = monitor;
        this.status = status;
    }

    public void emitStatus(WRTStatus status) {
        monitor.setTaskName(status.getStatusDescription().toString());
        this.status.add(new Status(IStatus.INFO, Activator.PLUGIN_ID, status.getStatusDescription().toString()));
    }

    public boolean isStatusHandled(WRTStatus status) {
        return true;
    }

    public void close() {
        // Do nothing
    }

    public boolean canPackageWithErrors(IProject project) {
        return ProjectUtils.canPackageWithErrors(project);
    }
}
