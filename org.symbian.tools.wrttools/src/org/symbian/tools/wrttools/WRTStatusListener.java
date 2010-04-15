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

package org.symbian.tools.wrttools;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.console.MessageConsoleStream;
import org.symbian.tools.wrttools.core.status.IWRTStatusListener;
import org.symbian.tools.wrttools.core.status.WRTStatus;
import org.symbian.tools.wrttools.util.ProjectUtils;

public class WRTStatusListener implements IWRTStatusListener {
	
	private final MessageConsoleStream consoleStream;
	private final boolean activateOnFirstStatus = true;
	private int statusCount;

	public WRTStatusListener() {
		consoleStream = ConsoleFactory.createStream();
	}
	
	public void close() {
		try {
			consoleStream.close();
		} catch (IOException x) {
			Activator.log(IStatus.ERROR, "Error closing console stream", x);
		}
	}

	public void emitStatus(WRTStatus status) {		
		Object description = status.getStatusDescription();
		if (description != null) {
			++statusCount;
			if (activateOnFirstStatus && statusCount == 1) {
				ConsoleFactory.activateConsole();
			}

			consoleStream.println(description.toString());
		}
	}

	public boolean isStatusHandled(WRTStatus status) {
		return true;
	}

    public boolean canPackageWithErrors(IProject project) {
        return ProjectUtils.canPackageWithErrors(project);
    }

}
