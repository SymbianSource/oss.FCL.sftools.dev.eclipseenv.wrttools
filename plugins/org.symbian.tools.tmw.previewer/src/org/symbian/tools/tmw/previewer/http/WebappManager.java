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
package org.symbian.tools.tmw.previewer.http;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.http.jetty.JettyConfigurator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.symbian.tools.tmw.previewer.PreviewerPlugin;

/**
 * Copy from the WS Explorer
 *
 * @author Eugene Ostroukhov
 *
 */
public final class WebappManager {
    public static final String WORKSPACE_RESOURCES_CONTEXT = "/preview";
    public static final String STATIC_RESOURCES_CONTEXT = "/tmwdebugger";
    public static final String WEB_CONTENT_ROOT = "/http-content";

    private static String host;
    private static int port = -1;
    private static final int AUTO_SELECT_JETTY_PORT = 0;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void start(String webappName) throws Exception {
        Dictionary d = new Hashtable();

        d.put("http.port", Integer.valueOf(getPortParameter())); //$NON-NLS-1$

        // set the base URL
        d.put("other.info", "org.symbian.wst.debugger"); //$NON-NLS-1$ //$NON-NLS-2$

        // suppress Jetty INFO/DEBUG messages to stderr
        Logger.getLogger("org.mortbay").setLevel(Level.WARNING); //$NON-NLS-1$

        JettyConfigurator.startServer(webappName, d);
        checkBundle();
        Bundle bundle = PreviewerPlugin.getDefault().getBundle();
        HttpService service = (HttpService) bundle.getBundleContext().getService(getServiceReference());
        HttpContext httpContext = service.createDefaultHttpContext();
        service.registerServlet(WORKSPACE_RESOURCES_CONTEXT, new WorkspaceResourcesServlet(), new Hashtable(),
                httpContext);
    }

    /*
     * Ensures that the bundle with the specified name and the highest available
     * version is started and reads the port number
     */
    private static void checkBundle() throws InvalidSyntaxException, BundleException {
        Bundle bundle = Platform.getBundle("org.eclipse.equinox.http.registry"); //$NON-NLS-1$if (bundle != null) {
        if (bundle.getState() == Bundle.RESOLVED) {
            bundle.start(Bundle.START_TRANSIENT);
        }
        if (port == -1) {
            ServiceReference reference = getServiceReference();
            Object assignedPort = reference.getProperty("http.port"); //$NON-NLS-1$
            port = Integer.parseInt((String) assignedPort);
        }
    }

    private static ServiceReference getServiceReference() throws InvalidSyntaxException {
        Bundle bundle = PreviewerPlugin.getDefault().getBundle();
        // Jetty selected a port number for us
        ServiceReference[] reference = bundle.getBundleContext().getServiceReferences(
                "org.osgi.service.http.HttpService", "(other.info=org.symbian.wst.debugger)"); //$NON-NLS-1$ //$NON-NLS-2$
        return reference[0];
    }

    public static void stop(String webappName) throws CoreException {
        try {
            JettyConfigurator.stopServer(webappName);
        } catch (Exception e) {
            PreviewerPlugin.log(e);
        }
    }

    public static int getPort() {
        return port;
    }

    /*
     * Get the port number which will be passed to Jetty
     */
    private static int getPortParameter() {
        if (port == -1) {
            return AUTO_SELECT_JETTY_PORT;
        }
        return port;
    }

    public static String getHost() {
        if (host == null) {
            host = "127.0.0.1";
        }
        return host;
    }

    private WebappManager() {
    }

}
