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

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeBridge;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntime;

public class MobileRuntimesBridge implements IRuntimeBridge {
    private final Map<String, MobileRuntimeStub> stubs = new TreeMap<String, MobileRuntimeStub>();

    public Set<String> getExportedRuntimeNames() throws CoreException {
        final IMobileWebRuntime[] allRuntimes = TMWCore.getDefault().getRuntimesManager().getAllRuntimes();
        final Set<String> ids = new TreeSet<String>();
        for (IMobileWebRuntime runtime : allRuntimes) {
            ids.add(runtime.getId() + ":" + runtime.getVersion());
        }
        return ids;
    }

    public IStub bridge(String name) throws CoreException {
        if (!stubs.containsKey(name)) {
            final int ind = name.indexOf(":");
            if (ind > 0) {
                stubs.put(
                        name,
                        new MobileRuntimeStub(TMWCore.getDefault().getRuntimesManager()
                                .getRuntime(name.substring(0, ind), name.substring(ind + 1))));
            }
        }
        return stubs.get(name);
    }

}
