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

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.ui.ModifyFacetedProjectWizard;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.projects.IFProjSupport;

/**
 * @author Eugene Ostroukhov (eugeneo@symbian.org)
 */
public final class NewApplicationWizard extends ModifyFacetedProjectWizard implements INewWizard {
    private final DataBindingContext databindingContext = new DataBindingContext();
    private NewApplicationDetailsWizardPage firstPage;
    private IStructuredSelection selection;
    private final WizardContext wizardContext = new WizardContext();
    private IWorkbench workbench;
    private FacetsSelectionPage facetsPage;
    private NewApplicationTemplateWizardPage templatesPage;

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
        facetsPage = new FacetsSelectionPage(facets, getFacetedProjectWorkingCopy());
        addPage(facetsPage);
        templatesPage = new NewApplicationTemplateWizardPage(wizardContext, databindingContext);
        addPage(templatesPage);
        super.addPages();
    }

    public boolean canFinish() {
        return this.firstPage.isPageComplete() && super.canFinish();
    }

    protected NewApplicationDetailsWizardPage createFirstPage() {
        firstPage = new NewApplicationDetailsWizardPage(wizardContext, databindingContext);
        return firstPage;
    }

    @Override
    public IWizardPage getNextPage(final IWizardPage page) {
        if (page == this.firstPage) {
            final IFacetedProjectWorkingCopy fpjwc = getFacetedProjectWorkingCopy();
            fpjwc.setProjectName(getProjectName());
            fpjwc.setProjectLocation(getProjectLocation());
            final IFProjSupport fprojSupport = TMWCore.getFProjSupport();
            IRuntime runtime = fprojSupport.getRuntime(wizardContext.getRuntime());
            fpjwc.setTargetedRuntimes(Collections.singleton(runtime));
            fpjwc.setPrimaryRuntime(runtime);
            fpjwc.setProjectFacets(fprojSupport.getFixedFacetsVersions(wizardContext.getRuntime()));
            fpjwc.setFixedProjectFacets(fprojSupport.getFixedFacets(wizardContext.getRuntime()));
            fpjwc.setProjectFacetActionConfig(fprojSupport.getTMWFacet(), wizardContext);
        }

        IWizardPage nextPage = super.getNextPage(page);
        return nextPage;
    }

    public IWizardPage[] getPages() {
        final IWizardPage[] base = super.getPages();
        final IWizardPage[] pages = new IWizardPage[base.length + 3];

        pages[0] = this.firstPage;
        pages[1] = this.templatesPage;
        pages[2] = this.facetsPage;
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

    public boolean performFinish() {
        super.performFinish();
        return true;
    }
}
