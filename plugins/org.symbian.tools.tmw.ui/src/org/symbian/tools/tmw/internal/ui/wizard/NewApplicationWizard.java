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
package org.symbian.tools.tmw.internal.ui.wizard;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
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
import org.symbian.tools.tmw.core.runtimes.ProjectCreationConfigFactory;
import org.symbian.tools.tmw.ui.project.INewApplicationWizardPage;
import org.symbian.tools.tmw.ui.project.IProjectTemplate;

/**
 * @author Eugene Ostroukhov (eugeneo@symbian.org)
 */
public final class NewApplicationWizard extends ModifyFacetedProjectWizard implements INewWizard {
    private final PageContributions contributions = new PageContributions();
    private final DataBindingContext databindingContext = new DataBindingContext();
    private NewApplicationFacetsWizardPage facetsPage;
    private NewApplicationDetailsWizardPage firstPage;
    private IStructuredSelection selection;
    private IProjectTemplate template = null;
    private INewApplicationWizardPage[] templatePages = new INewApplicationWizardPage[0];
    private NewApplicationTemplateWizardPage templatesPage;
    private final WizardContext wizardContext = new WizardContext();
    private IWorkbench workbench;

    public NewApplicationWizard() {
        setShowFacetsSelectionPage(false);
    }

    public void addPages() {
        this.firstPage = createFirstPage();
        addPage(this.firstPage);
        final IFacetedProject project = getFacetedProject();
        final Set<IProjectFacetVersion> facets;
        if (project == null) {
            facets = Collections.emptySet();
        } else {
            facets = project.getProjectFacets();
        }
        facetsPage = new NewApplicationFacetsWizardPage(facets, getFacetedProjectWorkingCopy());
        addPage(facetsPage);
        templatesPage = new NewApplicationTemplateWizardPage(wizardContext, databindingContext);
        addPage(templatesPage);
        super.addPages();
    }

    public boolean canFinish() {
        return getNextPage(getContainer().getCurrentPage()) == null && this.firstPage.isPageComplete()
                && super.canFinish();
    }

    protected NewApplicationDetailsWizardPage createFirstPage() {
        firstPage = new NewApplicationDetailsWizardPage(wizardContext, databindingContext);
        return firstPage;
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
            Set<IProjectFacetVersion> facets = getCurrentFixedFacetVersions();
            facets.addAll(fpjwc.getProjectFacets());
            fpjwc.setProjectFacets(facets);
            fpjwc.setFixedProjectFacets(getCurrentFixedFacets());
        } else if (page == this.templatesPage) {
            Set<IProjectFacetVersion> facets = getCurrentFixedFacetVersions();
            facets.addAll(fpjwc.getProjectFacets());
            fpjwc.setProjectFacets(facets);
            fpjwc.setFixedProjectFacets(getCurrentFixedFacets());
        }
        final Collection<Action> actions = fpjwc.getProjectFacetActions();
        final Collection<Action> toReplace = new HashSet<IFacetedProject.Action>();
        for (Action action : actions) {
            if (action.getConfig() == ProjectCreationConfigFactory.CONFIG_STANDIN) {
                toReplace.add(action);
            }
        }
        for (Action action : toReplace) {
            fpjwc.setProjectFacetActionConfig(action.getProjectFacetVersion().getProjectFacet(), wizardContext);
        }

        IWizardPage nextPage = super.getNextPage(page);
        return nextPage;
    }

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
        final IWizardPage[] pages = new IWizardPage[base.length + 3 + templatePages.length];

        pages[0] = this.firstPage;
        pages[1] = this.templatesPage;
        pages[2] = this.facetsPage;
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

    /**
     * Returns the selection that this wizard was launched from.
     * 
     * @return the selection that this wizard was launched from
     * @since 1.4
     */
    public IStructuredSelection getSelection() {
        return this.selection;
    }

    /**
     * Returns the workbench that this wizard belongs to.
     * 
     * @return the workbench that this wizard belongs to
     * @since 1.4
     */
    public IWorkbench getWorkbench() {
        return this.workbench;
    }

    public void init(final IWorkbench workbench, final IStructuredSelection selection) {
        this.workbench = workbench;
        this.selection = selection;
    }

    @Override
    protected void performFinish(IProgressMonitor monitor) throws CoreException {
        monitor.beginTask("Preparing project", 100);
        super.performFinish(new SubProgressMonitor(monitor, 20));
        wizardContext.initialize(getFacetedProject().getProject(), new SubProgressMonitor(monitor, 80));
        monitor.done();
    }
}
