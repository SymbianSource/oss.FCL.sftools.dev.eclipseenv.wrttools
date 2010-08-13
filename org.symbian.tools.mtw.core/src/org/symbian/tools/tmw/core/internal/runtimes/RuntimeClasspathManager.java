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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.IVersion;
import org.eclipse.wst.common.project.facet.core.IVersionExpr;
import org.eclipse.wst.common.project.facet.core.util.internal.VersionExpr;
import org.eclipse.wst.common.project.facet.core.util.internal.Versionable;
import org.eclipse.wst.jsdt.core.IIncludePathEntry;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.internal.projects.LazyIncludePathProvider;
import org.symbian.tools.tmw.core.internal.projects.StaticIncludePathProvider;
import org.symbian.tools.tmw.core.projects.IFacetIncludePathProvider;
import org.symbian.tools.tmw.core.projects.IMTWProject;
import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntime;

@SuppressWarnings("restriction")
public class RuntimeClasspathManager {
    private static final class VersionedEntry<T> {
        protected final IVersionExpr versionExpression;
        protected final T entry;

        public VersionedEntry(String versionExpression, T entry) {
            if (versionExpression == null) {
                this.versionExpression = null;
            } else {
                IVersionExpr expr;
                try {
                    expr = new VersionExpr<Version>(new VersionableObject(), versionExpression, null);
                } catch (CoreException e) {
                    expr = null;
                    TMWCore.log(null, e);
                }
                this.versionExpression = expr;
            }
            this.entry = entry;
        }

        public boolean matches(String version) {
            if (versionExpression == null) { // Any version
                return true;
            }
            if (version == null) { // Unspecified
                return false;
            }
            return versionExpression.check(new Version(version));
        }
    }

    private static final class VersionableObject extends Versionable<Version> {
        @Override
        public String getPluginId() {
            return TMWCore.PLUGIN_ID;
        }

        @Override
        public String createVersionNotFoundErrMsg(String verstr) {
            return "Version not found";
        }
    }

    private static final class Version implements IVersion {
        private final String version;

        public Version(String version) {
            this.version = version;
        }

        public int compareTo(Object o) {
            return version.compareTo(((IVersion) o).getVersionString());
        }

        public String getVersionString() {
            return version;
        }
    }

    private final Map<String, Collection<VersionedEntry<Map<String, Collection<VersionedEntry<IFacetIncludePathProvider[]>>>>>> providers = new HashMap<String, Collection<VersionedEntry<Map<String, Collection<VersionedEntry<IFacetIncludePathProvider[]>>>>>>();
    private final boolean ready = false;

    public IIncludePathEntry[] getProjectClasspathEntries(IFacetedProject project) {
        collectProviders();
        final IMTWProject p = TMWCore.create(project.getProject());
        if (p != null) {
            final IMobileWebRuntime runtime = p.getTargetRuntime();
            final Map<String, Collection<VersionedEntry<IFacetIncludePathProvider[]>>> facetToEntry;
            if (runtime != null) {
                facetToEntry = join(getMatchingEntries(runtime.getId(), runtime.getVersion(), providers));
            } else {
                facetToEntry = join(getMatchingEntries(null, null, providers));
            }
            final Collection<IFacetIncludePathProvider[]> entries = getMatchingEntries(null, null, facetToEntry);
            final Set<IProjectFacetVersion> facets = project.getProjectFacets();
            for (IProjectFacetVersion facet : facets) {
                entries.addAll(getMatchingEntries(facet.getProjectFacet().getId(), facet.getVersionString(),
                        facetToEntry));
            }
            final Collection<IIncludePathEntry> pathEntries = new HashSet<IIncludePathEntry>();
            for (IFacetIncludePathProvider[] providers : entries) {
                for (IFacetIncludePathProvider provider : providers) {
                    pathEntries.addAll(Arrays.asList(provider.getEntries(p)));
                }
            }
            return pathEntries.toArray(new IIncludePathEntry[pathEntries.size()]);
        }
        return new IIncludePathEntry[0];
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private synchronized void collectProviders() {
        if (!ready) {
            final IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
                    TMWCore.PLUGIN_ID, "runtimeIncludePath");
            final Map<String, Collection<VersionedEntry<IFacetIncludePathProvider[]>>> entries = collectFaceletEntries(elements);
            final VersionedEntry<Map<String, Collection<VersionedEntry<IFacetIncludePathProvider[]>>>> entry = new VersionedEntry(
                    null, entries);
            providers.put(null, Collections.singleton(entry));
            for (IConfigurationElement element : elements) {
                if ("runtime-include-path".equals(element.getName())) {
                    final String id = element.getAttribute("id");
                    Collection<VersionedEntry<Map<String, Collection<VersionedEntry<IFacetIncludePathProvider[]>>>>> versions = providers
                            .get(id);
                    if (versions == null) {
                        versions = new LinkedList<RuntimeClasspathManager.VersionedEntry<Map<String, Collection<VersionedEntry<IFacetIncludePathProvider[]>>>>>();
                        providers.put(id, versions);
                    }
                    final Map<String, Collection<VersionedEntry<IFacetIncludePathProvider[]>>> facetProviders = new HashMap<String, Collection<VersionedEntry<IFacetIncludePathProvider[]>>>();
                    final IFacetIncludePathProvider[] collection = collectProviders(element.getChildren());
                    final VersionedEntry<IFacetIncludePathProvider[]> ent = new VersionedEntry(null, collection);
                    facetProviders.put(null, Collections.singleton(ent));
                    facetProviders.putAll(collectFaceletEntries(element.getChildren()));
                    final VersionedEntry<Map<String, Collection<VersionedEntry<IFacetIncludePathProvider[]>>>> ver = new VersionedEntry<Map<String, Collection<VersionedEntry<IFacetIncludePathProvider[]>>>>(
                            element.getAttribute("version"), facetProviders);
                    versions.add(ver);
                }
            }
        }
    }

    private IFacetIncludePathProvider[] collectProviders(IConfigurationElement[] children) {
        final Collection<IFacetIncludePathProvider> providers = new LinkedList<IFacetIncludePathProvider>();
        for (IConfigurationElement element : children) {
            if ("include-path-entry".equals(element.getName())) {
                providers.add(new StaticIncludePathProvider(element));
            } else if ("include-path-provider".equals(element.getName())) {
                providers.add(new LazyIncludePathProvider(element));
            }
        }
        return providers.toArray(new IFacetIncludePathProvider[providers.size()]);
    }

    public Map<String, Collection<VersionedEntry<IFacetIncludePathProvider[]>>> collectFaceletEntries(
            IConfigurationElement[] elements) {
        final Map<String, Collection<VersionedEntry<IFacetIncludePathProvider[]>>> faceletsToProviders = new HashMap<String, Collection<VersionedEntry<IFacetIncludePathProvider[]>>>();
        for (IConfigurationElement element : elements) {
            if ("facet-include-path".equals(element.getName())) {
                final IFacetIncludePathProvider[] providers = collectProviders(element.getChildren());
                final VersionedEntry<IFacetIncludePathProvider[]> versionedEntry = new VersionedEntry<IFacetIncludePathProvider[]>(
                        element.getAttribute("version"), providers);
                final String id = element.getAttribute("facelet-id");
                Collection<VersionedEntry<IFacetIncludePathProvider[]>> provs = faceletsToProviders.get(id);
                if (provs == null) {
                    provs = new LinkedList<RuntimeClasspathManager.VersionedEntry<IFacetIncludePathProvider[]>>();
                    faceletsToProviders.put(id, provs);
                }
                provs.add(versionedEntry);
            }
        }
        return faceletsToProviders;
    }

    private <T, V> Map<T, Collection<V>> join(Collection<Map<T, Collection<V>>> maps) {
        final Map<T, Collection<V>> res = new HashMap<T, Collection<V>>();
        for (Map<T, Collection<V>> map : maps) {
            for (Map.Entry<T, Collection<V>> entry : map.entrySet()) {
                if (res.containsKey(entry.getKey())) {
                    res.get(entry.getKey()).addAll(entry.getValue());
                } else {
                    res.put(entry.getKey(), new LinkedList<V>(entry.getValue()));
                }
            }
        }
        return res;
    }

    private <T> Collection<T> getMatchingEntries(String id, String version,
            Map<String, Collection<VersionedEntry<T>>> map) {
        final Collection<VersionedEntry<T>> entries = new LinkedList<VersionedEntry<T>>();
        Collection<VersionedEntry<T>> versionedEntries = map.get(null);
        if (versionedEntries != null) {
            entries.addAll(versionedEntries);
        }
        if (id != null) {
            versionedEntries = map.get(id);
            if (versionedEntries != null) {
                entries.addAll(versionedEntries);
            }
        }
        final Collection<T> res = new LinkedList<T>();
        for (VersionedEntry<T> versionedEntry : entries) {
            if (versionedEntry.matches(version)) {
                res.add(versionedEntry.entry);
            }
        }
        return res;
    }
}
