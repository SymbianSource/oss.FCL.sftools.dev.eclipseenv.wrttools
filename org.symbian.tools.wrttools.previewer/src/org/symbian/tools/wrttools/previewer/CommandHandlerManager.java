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
package org.symbian.tools.wrttools.previewer;

import java.text.MessageFormat;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;

public class CommandHandlerManager {

    private Map<String, IPreviewerCommandHandler> handlers = null;

    public void handle(String commandName, String projectName, Map<String, String[]> parameters) {
        final IPreviewerCommandHandler commandHandler = getCommandMap().get(commandName);
        if (commandHandler == null) {
            PreviewerPlugin.log(MessageFormat.format("Command {0} is not handled", commandName), null);
        } else {
            commandHandler.handle(commandName, projectName, parameters);
        }
    }

    private synchronized Map<String, IPreviewerCommandHandler> getCommandMap() {
        if (handlers == null) {
            handlers = new TreeMap<String, IPreviewerCommandHandler>();
            IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
                    PreviewerPlugin.PLUGIN_ID, "commands");
            for (IConfigurationElement element : elements) {
                final String name = element.getAttribute("name");
                try {
                    handlers.put(name, (IPreviewerCommandHandler) element.createExecutableExtension("handler"));
                } catch (InvalidRegistryObjectException e) {
                    PreviewerPlugin.log(MessageFormat.format("Command: {0}, handler: {1}", name, element
                            .getAttribute("handler")), e);
                } catch (CoreException e) {
                    PreviewerPlugin.log(MessageFormat.format("Command: {0}, handler: {1}", name, element
                            .getAttribute("handler")), e);
                }
            }
        }
        return handlers;
    }

}
