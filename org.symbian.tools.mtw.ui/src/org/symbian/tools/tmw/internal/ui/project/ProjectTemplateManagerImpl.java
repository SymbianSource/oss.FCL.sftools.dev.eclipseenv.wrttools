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
package org.symbian.tools.tmw.internal.ui.project;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntime;
import org.symbian.tools.tmw.ui.TMWCoreUI;
import org.symbian.tools.tmw.ui.project.IProjectTemplate;
import org.symbian.tools.tmw.ui.project.IProjectTemplateManager;
import org.symbian.tools.tmw.ui.project.ITemplateInstaller;

public class ProjectTemplateManagerImpl implements IProjectTemplateManager {
    private Map<IMobileWebRuntime, ITemplateInstaller> emptyProjects;
    private Map<IMobileWebRuntime, Map<String, String>> runtimeTemplateParameters;
    private Map<IMobileWebRuntime, IProjectTemplate[]> templates;

    public IProjectTemplate getDefaultTemplate(IMobileWebRuntime runtime) {
        final IProjectTemplate[] projectTemplates = getProjectTemplates(runtime);
        return projectTemplates != null && projectTemplates.length > 0 ? projectTemplates[0] : null;
    }

    public ITemplateInstaller getEmptyProjectTemplate(IMobileWebRuntime runtime) {
        if (emptyProjects == null) {
            readExtensions();
        }
        return emptyProjects.get(runtime);
    }

    public IProjectTemplate[] getProjectTemplates(IMobileWebRuntime runtime) {
        if (runtime == null) {
            return new IProjectTemplate[0];
        }
        if (templates == null) {
            templates = readExtensions();
        }
        final IProjectTemplate[] runtimeTemplates = templates.get(runtime);
        return runtimeTemplates == null ? new IProjectTemplate[0] : runtimeTemplates;
    }

    private Map<IMobileWebRuntime, IProjectTemplate[]> readExtensions() {
        runtimeTemplateParameters = new HashMap<IMobileWebRuntime, Map<String, String>>();
        emptyProjects = new HashMap<IMobileWebRuntime, ITemplateInstaller>();
        final Map<IMobileWebRuntime, Collection<IProjectTemplate>> map = new HashMap<IMobileWebRuntime, Collection<IProjectTemplate>>();
        final IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
                TMWCoreUI.PLUGIN_ID, "projectTemplate");
        for (IConfigurationElement element : elements) {
            if ("template".equals(element.getName())) {
                final ProjectTemplateImpl template = new ProjectTemplateImpl(element);
                final IMobileWebRuntime[] supportedRuntimes = template.getSupportedRuntimes();
                for (IMobileWebRuntime runtime : supportedRuntimes) {
                    Collection<IProjectTemplate> tmplts = map.get(runtime);
                    if (tmplts == null) {
                        tmplts = new HashSet<IProjectTemplate>();
                        map.put(runtime, tmplts);
                    }
                    tmplts.add(template);
                }
            } else if ("runtime-template".equals(element.getName())) {
                final String runtimeId = element.getAttribute("runtime-id");
                final String runtimeVersion = element.getAttribute("version");
                final IMobileWebRuntime runtime = TMWCore.getRuntimesManager().getRuntime(runtimeId, runtimeVersion);
                if (runtime != null) {
                    emptyProjects.put(runtime,
                            CompoundInstaller.combine(emptyProjects.get(runtime), element.getChildren()));
                    Map<String, String> params = runtimeTemplateParameters.get(runtime);
                    if (params == null) {
                        params = new TreeMap<String, String>();
                        runtimeTemplateParameters.put(runtime, params);
                    }
                    for (IConfigurationElement el : element.getChildren()) {
                        if ("default-parameter-value".equals(el.getName())) {
                            params.put(el.getAttribute("name"), el.getAttribute("value"));
                        }
                    }
                }
            }
        }
        final Map<IMobileWebRuntime, IProjectTemplate[]> res = new HashMap<IMobileWebRuntime, IProjectTemplate[]>(
                map.size());
        for (Map.Entry<IMobileWebRuntime, Collection<IProjectTemplate>> entry : map.entrySet()) {
            final Collection<IProjectTemplate> collection = entry.getValue();
            res.put(entry.getKey(), collection.toArray(new IProjectTemplate[collection.size()]));
        }
        return res;
    }

    public Map<String, String> getDefaultTemplateParameterValues(IMobileWebRuntime runtime) {
        if (runtimeTemplateParameters == null) {
            readExtensions();
        }
        Map<String, String> params = runtimeTemplateParameters.get(runtime);
        if (params == null) {
            return Collections.emptyMap();
        }
        return params;
    }
}
