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
package org.symbian.tools.mtw.core.internal.runtimes;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.symbian.tools.mtw.core.MTWCore;
import org.symbian.tools.mtw.core.projects.IMTWProject;
import org.symbian.tools.mtw.core.runtimes.IMobileWebRuntime;
import org.symbian.tools.mtw.core.runtimes.IMobileWebRuntimeManager;
import org.symbian.tools.mtw.core.runtimes.IPackager;

public final class RuntimesManagerImpl implements IMobileWebRuntimeManager {
    private final Map<String, IMobileWebRuntime> runtimes = new TreeMap<String, IMobileWebRuntime>();
    private final Map<IMobileWebRuntime, Map<IMobileWebRuntime, IPackager>> packagers = new HashMap<IMobileWebRuntime, Map<IMobileWebRuntime, IPackager>>();

    public RuntimesManagerImpl() {
        collectRuntimes();
        collectPackagers();
    }

    private void collectPackagers() {
        IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
                MTWCore.PLUGIN_ID, "packagers");
        for (IConfigurationElement element : elements) {
            final String target = element.getAttribute("targetRuntime");
            final IMobileWebRuntime targetRuntime = getRuntime(target);
            if (targetRuntime == null) {
                MTWCore.log("Runtime %s referenced from plugin %s was not found", target, element
                        .getDeclaringExtension().getNamespaceIdentifier());
                break;
            }

            final String source = element.getAttribute("sourceRuntime");
            IMobileWebRuntime sourceRuntime;
            if (source == null) {
                sourceRuntime = targetRuntime;
            } else {
                sourceRuntime = getRuntime(source);
                if (sourceRuntime == null) {
                    MTWCore.log("Runtime %s referenced from plugin %s was not found", source, element
                            .getDeclaringExtension().getNamespaceIdentifier());
                    break;
                }
            }
            IPackager packager = new LazyPackager(element);
            Map<IMobileWebRuntime, IPackager> map = packagers.get(targetRuntime);
            if (map == null) {
                map = new HashMap<IMobileWebRuntime, IPackager>();
                packagers.put(targetRuntime, map);
            }
            map.put(sourceRuntime, packager);
        }
    }

    private void collectRuntimes() {
        IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
                MTWCore.PLUGIN_ID, "runtimes");
        for (IConfigurationElement element : elements) {
            final MobileWebRuntime runtime = new MobileWebRuntime(element);
            runtimes.put(runtime.getId(), runtime);
        }
    }

    public IPackager getPackager(IMTWProject project) {
        return getPackager(project, project.getTargetRuntime());
    }

    public IPackager getPackager(IMTWProject project, IMobileWebRuntime runtime) {
        final Map<IMobileWebRuntime, IPackager> map = packagers.get(runtime);
        if (map != null) {
            return map.get(project.getTargetRuntime());
        }
        return null;
    }

    public IMobileWebRuntime getRuntime(String id) {
        return runtimes.get(id);
    }

}
