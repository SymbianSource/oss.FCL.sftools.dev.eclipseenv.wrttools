/**
 * Copyright (c) 2009 Symbian Foundation and/or its subsidiary(-ies).
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

package org.symbian.tools.wrttools.core.deploy.device;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.symbian.tools.wrttools.core.deployer.WidgetDeployer;
import org.symbian.tools.wrttools.core.status.IWRTConstants;

/**
 * The class needed for the deployment of the widget to device using bluetooth.
 * 
 * @author avraina
 */
public class DeviceDeployer extends WidgetDeployer {

	public IStatus deploy(String fileName, String device, IProgressMonitor progressMonitor) {
		File inputFile = new File(fileName);
        // If the archive is directly deployed than directly deploy it
		// else deploy from the folder path.
		if (fileName.toLowerCase()
				.endsWith(IWRTConstants.WIDGET_FILE_EXTENSION)) {
            new BluetoothDeploymentJob(inputFile, device).schedule();
		}
        emitStatus("Background deployment job started");//$NON-NLS-1$
        return Status.OK_STATUS;
	}


    @Override
    public boolean needsReport() {
        return false;
    }
}
