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
package org.symbian.tools.tmw.previewer.internal;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.projects.ITMWProject;
import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntime;
import org.symbian.tools.tmw.previewer.core.IApplicationLayoutProvider;
import org.symbian.tools.tmw.previewer.core.IPreviewerExtensionsManager;
import org.symbian.tools.wrttools.previewer.PreviewerPlugin;

public class PreviewerExtensionsManagerImpl implements IPreviewerExtensionsManager {
    public static final class LazyProvider implements IApplicationLayoutProvider {
        private final IConfigurationElement element;
        private IApplicationLayoutProvider instance;

        public LazyProvider(IConfigurationElement element) {
            this.element = element;
        }

        public IPath getResourcePath(IFile file) {
            return getDelegate().getResourcePath(file);
        }

        private IApplicationLayoutProvider getDelegate() {
            if (instance == null) {
                try {
                    instance = (IApplicationLayoutProvider) element.createExecutableExtension("class");
                } catch (CoreException e) {
                    PreviewerPlugin.log(e);
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
    }

    private Map<IMobileWebRuntime, IApplicationLayoutProvider> providers;

    public IApplicationLayoutProvider getLayoutProvider(IProject project) {
        final ITMWProject p = TMWCore.create(project);
        if (p != null && p.getTargetRuntime() != null) {
            checkRegistry();
            return providers.get(p.getTargetRuntime());
        }
        return null;
    }

    private synchronized void checkRegistry() {
        if (providers == null) {
            providers = new HashMap<IMobileWebRuntime, IApplicationLayoutProvider>();
            final IConfigurationElement[] configuration = Platform.getExtensionRegistry().getConfigurationElementsFor(
                    PreviewerPlugin.PLUGIN_ID, "layoutProviders");
            for (IConfigurationElement element : configuration) {
                final String runtimeId = element.getAttribute("runtime-id");
                final String version = element.getAttribute("runtime-version");
                final IMobileWebRuntime runtime = TMWCore.getRuntimesManager().getRuntime(runtimeId, version);
                if (runtime != null) {
                    providers.put(runtime, new LazyProvider(element));
                } else {
                    PreviewerPlugin.log(String.format("Runtime %s:$s referenced from %s was not found", runtimeId,
                            version, element.getContributor().getName()), null);
                }
            }
        }
    }
}
