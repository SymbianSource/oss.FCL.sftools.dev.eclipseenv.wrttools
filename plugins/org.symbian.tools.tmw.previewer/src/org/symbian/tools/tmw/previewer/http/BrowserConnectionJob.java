/*******************************************************************************
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
 *******************************************************************************/
package org.symbian.tools.tmw.previewer.http;

import java.net.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.symbian.tools.tmw.previewer.PreviewerPlugin;

public class BrowserConnectionJob extends Job {
    private final IPreviewStartupListener listener;
    private final URI uri;

    private boolean ready = false;
    private boolean success = false;
    private String sId = null;

    public BrowserConnectionJob(IPreviewStartupListener listener, URI uri) {
        super("Connecting to Mobile Web debugger browser");
        this.listener = listener;
        this.uri = uri;
        setUser(false);
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        try {
            listener.browserRunning(uri, sId);
            synchronized (this) {
                success = true;
            }
        } catch (CoreException e) {
            synchronized (this) {
                success = false;
            }
            return e.getStatus();
        } finally {
            synchronized (this) {
                ready = true;
            }
        }
        return new Status(IStatus.OK, PreviewerPlugin.PLUGIN_ID, "");
    }

    public synchronized boolean isReady() {
        return ready;
    }

    public synchronized boolean isSuccess() {
        return success;
    }

    public void setSessionId(String sId) {
        this.sId = sId;
    }
}
