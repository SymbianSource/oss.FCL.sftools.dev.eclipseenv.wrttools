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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeBridge;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentType;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentVersion;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntime;

/**
 * Integrates mobile web runtime with the faceted project runtime framework.
 * 
 * @author Eugene Ostroukhov (eugeneo@symbian.org)
 */
@SuppressWarnings("unchecked")
public class MobileRuntimeStub implements IRuntimeBridge.IStub {
    private final IMobileWebRuntime runtime;
    private final Map<String, String> properties = new TreeMap<String, String>();

    public MobileRuntimeStub(IMobileWebRuntime runtime) {
        this.runtime = runtime;
        properties.put("localized-name", runtime.getName());
    }

    private IRuntimeComponent getComponent(String string, String version) {
        final IRuntimeComponentType componentType = RuntimeManager.getRuntimeComponentType(string);
        final IRuntimeComponentVersion v = componentType.getVersion(version);
        return RuntimeManager.createRuntimeComponent(v, Collections.EMPTY_MAP);
    }

    public List<IRuntimeComponent> getRuntimeComponents() {
        final List<IRuntimeComponent> components = new LinkedList<IRuntimeComponent>();
        components.add(getComponent(runtime.getId(), runtime.getVersion()));
        components.add(getComponent("tmw.core", "1.0"));
        for (IConfigurationElement element : ((MobileWebRuntime) runtime).getComponentElements()) {
            components.add(getComponent(element.getAttribute("id"), element.getAttribute("version")));
        }
        return components;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

}
