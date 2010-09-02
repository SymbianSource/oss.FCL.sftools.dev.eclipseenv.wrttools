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
import java.util.TreeMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.internal.runtimes.RuntimesManagerImpl.LazyProvider;
import org.symbian.tools.tmw.core.runtimes.IApplicationLayoutProvider;
import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntime;
import org.symbian.tools.tmw.core.utilities.CoreUtil;

public final class MobileWebRuntime implements IMobileWebRuntime {
    private final IConfigurationElement element;
    private IApplicationLayoutProvider layout;

    public MobileWebRuntime(IConfigurationElement configurationElement) {
        this.element = configurationElement;
        checkRegistry();
    }

    private synchronized void checkRegistry() {
        if (layout == null) {
            final IConfigurationElement[] configuration = Platform.getExtensionRegistry().getConfigurationElementsFor(
                    TMWCore.PLUGIN_ID, "runtimeAppLayout");
            for (IConfigurationElement configurationElement : configuration) {
                final String runtimeId = configurationElement.getAttribute("runtime-id");
                final String version = configurationElement.getAttribute("runtime-version");
                if (getId().endsWith(runtimeId) && getVersion().endsWith(version)) {
                    layout = new LazyProvider(configurationElement);
                    break;
                }
            }
            if (layout == null) {
                TMWCore.log("No layout provider for runtime %s:%s (from plugin %s)", getId(), getVersion(), element
                        .getContributor().getName());
            }
        }
    }

    public IConfigurationElement[] getComponentElements() {
        return element.getChildren("runtime-component");
    }

    public Map<String, String> getFixedFacets() {
        final Map<String, String> facets = new TreeMap<String, String>();
        final IConfigurationElement[] children = element.getChildren("fixed-facet");
        for (IConfigurationElement configurationElement : children) {
            facets.put(CoreUtil.notNull(configurationElement.getAttribute("id")),
                    configurationElement.getAttribute("version"));
        }
        return facets;
    }

    public String getId() {
        return element.getAttribute("component-id");
    }

    public IApplicationLayoutProvider getLayoutProvider() {
        return layout;
    }

    public String getName() {
        return element.getAttribute("name");
    }

    public String getVersion() {
        return element.getAttribute("component-version");
    }

    @Override
    public String toString() {
        return getId() + ":" + getVersion();
    }

}
