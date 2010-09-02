/*******************************************************************************
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
 *******************************************************************************/
package org.symbian.tools.tmw.debug.internal.launch;

import java.io.IOException;
import java.net.ServerSocket;

import org.symbian.tools.tmw.debug.internal.Activator;

public abstract class PortPolicy {
    private static class NewPortEachTime extends PortPolicy {
        @Override
        protected int getPort() {
            return getOpenPort();
        }
    }

    public static final PortPolicy INSTANCE;
    static {
        INSTANCE = new NewPortEachTime();
    }

    public static synchronized int getPortNumber() {
        return INSTANCE.getPort();
    }

    public int getOpenPort() {
        try {
            final ServerSocket socket = new ServerSocket(0);
            int port = socket.getLocalPort();
            socket.close();
            return port;
        } catch (IOException e) {
            Activator.log(e);
            return 7222;
        }
    }

    protected abstract int getPort();
}
