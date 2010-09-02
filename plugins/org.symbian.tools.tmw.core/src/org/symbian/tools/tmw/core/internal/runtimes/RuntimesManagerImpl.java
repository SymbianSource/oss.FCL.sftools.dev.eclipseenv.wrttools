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

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.projects.ITMWProject;
import org.symbian.tools.tmw.core.runtimes.IApplicationLayoutProvider;
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

    public IPackager getPackager(ITMWProject project) {
        return getPackager(project, project.getTargetRuntime());
    }

    public IPackager getPackager(ITMWProject project, IMobileWebRuntime runtime) {
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

    public static final class LazyProvider implements IApplicationLayoutProvider {
        private final IConfigurationElement element;
        private IApplicationLayoutProvider instance;

        public LazyProvider(IConfigurationElement configurationElement) {
            this.element = configurationElement;
        }

        public IPath getResourcePath(IFile file) {
            return getDelegate().getResourcePath(file);
        }

        private IApplicationLayoutProvider getDelegate() {
            if (instance == null) {
                try {
                    instance = (IApplicationLayoutProvider) element.createExecutableExtension("class");
                } catch (CoreException e) {
                    TMWCore.log(null, e);
                    instance = new IApplicationLayoutProvider() {

                        public IPath getResourcePath(IFile file) {
                            return null;
                        }

                        public InputStream getResourceFromPath(IProject project, IPath path) {
                            return null;
                        }

                        public IFile getWorkspaceFile(IProject project, IPath applicationPath) {
                            return null;
                        }

                        public IFile getIndexPage(IProject project) {
                            return null;
                        }
                    };
                }
            }
            return instance;
        }

        public InputStream getResourceFromPath(IProject project, IPath path) throws CoreException {
            return getDelegate().getResourceFromPath(project, path);
        }

        public IFile getWorkspaceFile(IProject project, IPath applicationPath) throws CoreException {
            return getDelegate().getWorkspaceFile(project, applicationPath);
        }

        public IFile getIndexPage(IProject project) {
            return getDelegate().getIndexPage(project);
        }
    }
}
