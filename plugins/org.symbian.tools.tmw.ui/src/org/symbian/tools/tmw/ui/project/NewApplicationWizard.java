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
package org.symbian.tools.tmw.ui.project;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.ui.ModifyFacetedProjectWizard;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.projects.IFProjSupport;
import org.symbian.tools.tmw.core.projects.IProjectSetupConfig;
import org.symbian.tools.tmw.internal.ui.wizard.NewApplicationDetailsWizardPage;
import org.symbian.tools.tmw.internal.ui.wizard.NewApplicationFacetsWizardPage;
import org.symbian.tools.tmw.internal.ui.wizard.NewApplicationTemplateWizardPage;
import org.symbian.tools.tmw.internal.ui.wizard.PageContributions;
import org.symbian.tools.tmw.internal.ui.wizard.WizardContext;

/**
 * This is the wizard that guides through new mobile application project creation.
 * 
 * @author Eugene Ostroukhov (eugeneo@symbian.org)
 */
public final class NewApplicationWizard extends ModifyFacetedProjectWizard implements INewWizard {
    public static final String ID = "org.symbian.tools.tmw.newproject";

    private final PageContributions contributions = new PageContributions();
    private final DataBindingContext databindingContext = new DataBindingContext();
    private IWizardPage[] staticPages;
    private NewApplicationDetailsWizardPage firstPage;
    private IProjectTemplate template = null;
    private INewApplicationWizardPage[] templatePages = new INewApplicationWizardPage[0];
    private final WizardContext wizardContext = new WizardContext();

    public NewApplicationWizard() {
        setShowFacetsSelectionPage(false);
    }

    public void addPages() {
        firstPage = new NewApplicationDetailsWizardPage(wizardContext, databindingContext);
        addPage(this.firstPage);
        final IFacetedProject project = getFacetedProject();
        final Set<IProjectFacetVersion> facets;
        if (project == null) {
            facets = Collections.emptySet();
        } else {
            facets = project.getProjectFacets();
        }
        final IWizardPage facetsPage = new NewApplicationFacetsWizardPage(facets, getFacetedProjectWorkingCopy());
        addPage(facetsPage);
        final IWizardPage templatesPage = new NewApplicationTemplateWizardPage(wizardContext, databindingContext);
        addPage(templatesPage);
        staticPages = new IWizardPage[3];
        staticPages[0] = firstPage;
        staticPages[1] = templatesPage;
        staticPages[2] = facetsPage;
        super.addPages();
    }

    public boolean canFinish() {
        return getNextPage(getContainer().getCurrentPage()) == null && this.firstPage.isPageComplete()
                && super.canFinish();
    }

    @Override
    public IWizardPage getNextPage(final IWizardPage page) {
        final IFacetedProjectWorkingCopy fpjwc = getFacetedProjectWorkingCopy();
        if (page == this.firstPage) {
            fpjwc.setProjectName(getProjectName());
            fpjwc.setProjectLocation(getProjectLocation());
            final IFProjSupport fprojSupport = TMWCore.getFProjSupport();
            IRuntime runtime = fprojSupport.getRuntime(wizardContext.getRuntime());
            fpjwc.setTargetedRuntimes(Collections.singleton(runtime));
            fpjwc.setPrimaryRuntime(runtime);
        }
        final Collection<Action> actions = fpjwc.getProjectFacetActions();
        final Collection<Action> toReplace = new HashSet<IFacetedProject.Action>();
        for (Action action : actions) {
            if (action.getConfig() instanceof IProjectSetupConfig) {
                toReplace.add(action);
            }
        }
        for (Action action : toReplace) {
            fpjwc.setProjectFacetActionConfig(action.getProjectFacetVersion().getProjectFacet(), wizardContext);
        }
        final IWizardPage nextPage = super.getNextPage(page);
        synchronized (currentFacets) {
            if (nextPage instanceof NewApplicationFacetsWizardPage && !reentry) {
                try {
                    reentry = true;
                    final IFacetedProjectWorkingCopy projectWorkingCopy = getFacetedProjectWorkingCopy();
                    final Set<IProjectFacetVersion> facets = projectWorkingCopy.getProjectFacets();
                    final Set<IProjectFacet> fixed = projectWorkingCopy.getFixedProjectFacets();
                    currentFacets.retainAll(facets);
                    for (IProjectFacetVersion facetVersion : facets) {
                        if (!fixed.contains(facetVersion.getProjectFacet())) {
                            currentFacets.add(facetVersion);
                        }
                    }
                    final Set<IProjectFacetVersion> f = getCurrentFixedFacetVersions();
                    f.addAll(currentFacets);
                    fpjwc.setProjectFacets(f);
                    fpjwc.setFixedProjectFacets(getCurrentFixedFacets());
                } finally {
                    reentry = false;
                }
            }
        }
        return nextPage;
    }

    private boolean reentry = false;
    private final Collection<IProjectFacetVersion> currentFacets = new HashSet<IProjectFacetVersion>();

    private Set<IProjectFacet> getCurrentFixedFacets() {
        final Set<IProjectFacetVersion> fixedFacets = getCurrentFixedFacetVersions();
        final Set<IProjectFacet> facets = new HashSet<IProjectFacet>();
        for (IProjectFacetVersion facet : fixedFacets) {
            facets.add(facet.getProjectFacet());
        }
        return facets;
    }

    private Set<IProjectFacetVersion> getCurrentFixedFacetVersions() {
        final IFProjSupport fprojSupport = TMWCore.getFProjSupport();
        Set<IProjectFacetVersion> facets = new HashSet<IProjectFacetVersion>(
                fprojSupport.getFixedFacetsVersions(wizardContext.getRuntime()));
        if (wizardContext.getTemplate() != null) {
            facets.addAll(Arrays.asList(wizardContext.getTemplate().getRequiredFacets()));
        }
        return facets;
    }

    public IWizardPage[] getPages() {
        final IProjectTemplate current = wizardContext.getTemplate();
        if (template != current) {
            for (INewApplicationWizardPage page : templatePages) {
                page.remove();
                page.dispose();
            }
            if (current == null) {
                template = null;
                templatePages = new INewApplicationWizardPage[0];
            } else {
                template = current;
                templatePages = contributions.createPages(template.getId());
                for (INewApplicationWizardPage page : templatePages) {
                    page.setWizard(this);
                    page.init(wizardContext, databindingContext);
                }
            }
        }
        final IWizardPage[] base = super.getPages();
        final IWizardPage[] pages = new IWizardPage[staticPages.length + templatePages.length];
        System.arraycopy(staticPages, 0, pages, 0, 3);
        if (templatePages.length > 0) {
            System.arraycopy(templatePages, 0, pages, 3, templatePages.length);
        }
        System.arraycopy(base, 0, pages, 2, base.length);

        return pages;
    }

    protected IPath getProjectLocation() {
        return firstPage.getLocationPath();
    }

    protected String getProjectName() {
        return wizardContext.getProjectName();
    }

    public void init(final IWorkbench workbench, final IStructuredSelection selection) {
        // Do nothing
    }

    @Override
    protected void performFinish(IProgressMonitor monitor) throws CoreException {
        monitor.beginTask("Preparing project", 300);
        super.performFinish(new SubProgressMonitor(monitor, 20));
        wizardContext.initialize(getFacetedProject().getProject(), new SubProgressMonitor(monitor, 80));
        getFacetedProject().getProject().build(IncrementalProjectBuilder.CLEAN_BUILD,
                new SubProgressMonitor(monitor, 100));
        getFacetedProject().getProject().build(IncrementalProjectBuilder.FULL_BUILD,
                new SubProgressMonitor(monitor, 100));
        monitor.done();
    }
}
