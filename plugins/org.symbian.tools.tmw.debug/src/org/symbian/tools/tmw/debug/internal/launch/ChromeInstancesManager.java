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

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.symbian.tools.tmw.debug.internal.Activator;
import org.symbian.tools.tmw.debug.internal.ChromeDebugUtils;
import org.symbian.tools.wrttools.util.CoreUtil;

public class ChromeInstancesManager {
    private static final String[] CHROME_ARGS = { "%s", "--remote-shell-port=%d", // Here we will set port
            "--user-data-dir=%s", // Here we will set profile folder so user settings have no effect
            "--disk-cache-dir=%s", // We don't care
            "--disable-web-security", // Widgets can use network now
            "--disable-extenions", // Use standard UI, should also improve speed and stability
            "--activate-on-launch", // Bring to front on Mac
            "--disable-geolocation", // Use our own Geolocation (needed to emulate Geolocation in phonegap) 
            "--disable-local-storage", // Disable local storage (needed to emulate Geolocation in phonegap) 
            "--disable-session-storage", // Disable local storage (needed to emulate Geolocation in phonegap) 
            "--no-default-browser-check", // Our users don't need this nagging
            "--disable-hang-monitor", // Fix for Bug 2682 - The debugger should disable "unresponsive" error message from chrome
            "--no-first-run", // We don't care
            "--app=%s" // Here we will have widget URI as --app argument
    };
    private final Map<Object, Process> chromes = Collections.synchronizedMap(new HashMap<Object, Process>());

    private static final int EXECUTIBLE_INDEX = 0;
    private static final int PORT_INDEX = 1;
    private static final int USER_DATA_INDEX = 2;
    private static final int CACHE_INDEX = 3;
    private static final int URL_INDEX = CHROME_ARGS.length - 1;

    private int profileNum = 0;

    public void startChrome(final Object key, int port, final String url) throws CoreException {
        final String browserExecutable = ChromeDebugUtils.getChromeExecutible();
        if (browserExecutable == null) {
            throw createCoreException("No Chrome browser available", null);
        }

        String[] commandline = new String[CHROME_ARGS.length];
        System.arraycopy(CHROME_ARGS, 0, commandline, 0, CHROME_ARGS.length);
        commandline[EXECUTIBLE_INDEX] = String.format(CHROME_ARGS[EXECUTIBLE_INDEX], browserExecutable);
        commandline[PORT_INDEX] = String.format(CHROME_ARGS[PORT_INDEX], port);
        commandline[USER_DATA_INDEX] = String.format(CHROME_ARGS[USER_DATA_INDEX], getChromeProfilePath());
        commandline[CACHE_INDEX] = String.format(CHROME_ARGS[CACHE_INDEX], getChromeCachePath());
        commandline[URL_INDEX] = String.format(CHROME_ARGS[URL_INDEX], url);
        
        // 2. Start Chrome
        try {
            Process process = Runtime.getRuntime().exec(commandline);
            chromes.put(key, process);
        } catch (IOException e) {
            StringBuilder builder = new StringBuilder();
            for (String string : commandline) {
                builder.append(" ").append(string);
            }
            throw createCoreException("Cannot execute: {0}", builder.toString().trim(), e);
        }

    }

    private String getChromeCachePath() {
        return getChromeSpecialDir(".chromecache");
    }

    /**
     * Terminate Chrome instance associated with given key.
     */
    public synchronized void stopChrome(final Object key) {

    }

    public synchronized void forgetChrome(final Object key) {

    }

    public synchronized void shutdown() {
        String dir = getChromeSpecialDir("");
        File directory = new File(dir);
        File[] files = directory.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory() && pathname.getName().matches("\\.chr\\d{3}")
                        && !isChromeRunning(pathname);
            }
        });
        boolean removedAll = true;
        for (File file : files) {
            removedAll = delete(file) && removedAll;
        }
        if (removedAll) {
            String cachePath = getChromeCachePath();
            delete(new File(cachePath));
        }

    }

    private boolean delete(File file) {
        if (!file.exists()) {
            return true;
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (!delete(f)) {
                    return false;
                }
            }
        }
        return file.delete();
    }

    private CoreException createCoreException(String pattern, String arg, Throwable exeption) {
        return createCoreException(MessageFormat.format(pattern, arg), exeption);
    }

    private CoreException createCoreException(String message, Throwable exeption) {
        return new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, exeption));
    }

    private String getChromeProfilePath() {
        while (true) {
            String dir = getChromeSpecialDir(String.format(".chr%03d", profileNum++));
            if (!new File(dir).exists()) {
                return dir;
            }
        }
    }

    private boolean isChromeRunning(File pathname) {
        File file = new File(pathname, "Default/Cookies");
        try {
            // Note: it is ok to delete cookies file - if it can be deleted then Chrome is not running.
            // If Chrome is running then we will not be able to delete the file.
            // We do not need to preserve state between launches. So it is ok to delete coockies.
            return file.exists() && !file.delete();
        } catch (Exception e) {
            Activator.log(e);
            return true;
        }
    }

    private String getChromeSpecialDir(String subdir) {
        IPath location = ResourcesPlugin.getWorkspace().getRoot().getLocation();
        if (CoreUtil.isLinux() && location.toString().length() > 50) {
            location = new Path(System.getProperty("user.home")).append(".wrtdebug");
        }
        return location.append(subdir).toOSString();
    }
}
