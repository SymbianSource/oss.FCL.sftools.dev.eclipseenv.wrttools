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
package org.symbian.tools.tmw.core.internal.runtimes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.projects.IMTWProject;
import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntime;
import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntimeManager;
import org.symbian.tools.tmw.core.runtimes.IPackager;

public final class RuntimesManagerImpl implements IMobileWebRuntimeManager {
    private Map<IMobileWebRuntime, Map<IMobileWebRuntime, IPackager>> packagers;
    private Map<String, IMobileWebRuntime> runtimes;

    public RuntimesManagerImpl() {
        collectRuntimes();
    }

    private void collectPackagers() {
        packagers = new HashMap<IMobileWebRuntime, Map<IMobileWebRuntime, IPackager>>();
        IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
                TMWCore.PLUGIN_ID, "packagers");
        for (IConfigurationElement element : elements) {
            IPackager packager = new LazyPackager(element);
            Map<IMobileWebRuntime, IPackager> map = packagers.get(packager.getTargetRuntime());
            if (map == null) {
                map = new HashMap<IMobileWebRuntime, IPackager>();
                packagers.put(packager.getTargetRuntime(), map);
            }
            map.put(packager.getSourceRuntime(), packager);
        }
    }

    private void collectRuntimes() {
        runtimes = new TreeMap<String, IMobileWebRuntime>();
        IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
                TMWCore.PLUGIN_ID, "runtimes");
        for (IConfigurationElement element : elements) {
            final MobileWebRuntime runtime = new MobileWebRuntime(element);
            runtimes.put(getInternalId(runtime.getId(), runtime.getVersion()), runtime);
        }
    }

    public IMobileWebRuntime[] getAllRuntimes() {
        if (runtimes == null) {
            collectRuntimes();
        }
        final Collection<IMobileWebRuntime> rts = runtimes.values();
        return rts.toArray(new IMobileWebRuntime[rts.size()]);
    }

    public IPackager getPackager(IMTWProject project) {
        return getPackager(project, project.getTargetRuntime());
    }

    public IPackager getPackager(IMTWProject project, IMobileWebRuntime runtime) {
        if (packagers == null) {
            collectPackagers();
        }
        final Map<IMobileWebRuntime, IPackager> map = packagers.get(runtime);
        if (map != null) {
            return map.get(project.getTargetRuntime());
        }
        return null;
    }

    public IMobileWebRuntime getRuntime(String id, String version) {
        if (runtimes == null) {
            collectRuntimes();
        }
        final IMobileWebRuntime runtime = runtimes.get(getInternalId(id, version));
        if (runtime == null) {
            TMWCore.log(String.format("Runtime %s@%s", id, version), new Exception());
        }
        return runtime;
    }

    private String getInternalId(String id, String version) {
        return id + ":" + version;
    }

}
