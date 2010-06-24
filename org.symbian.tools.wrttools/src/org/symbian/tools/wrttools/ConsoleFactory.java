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

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class ConsoleFactory implements IConsoleFactory {
	
	private static MessageConsole console;

	public void openConsole() {
		getConsole();
		console.activate();
	}
	
	public static MessageConsoleStream createStream() {
		getConsole();
		return console.newMessageStream();
	}
	
	public static void activateConsole() {
		getConsole();
		console.activate();
	}
	
	private synchronized static MessageConsole getConsole() {
		if (console == null) {
			console = new MessageConsole("TMW Console", null, true);
			IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
			consoleManager.addConsoles(new IConsole[]{console});
		}
		return console;
	}

}
