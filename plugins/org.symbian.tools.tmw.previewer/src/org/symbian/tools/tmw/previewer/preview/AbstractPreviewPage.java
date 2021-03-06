/*******************************************************************************
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
 *******************************************************************************/
package org.symbian.tools.tmw.previewer.preview;

import java.net.URI;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.symbian.tools.tmw.core.utilities.CoreUtil;
import org.symbian.tools.tmw.previewer.Images;
import org.symbian.tools.tmw.previewer.PreviewerPlugin;

public abstract class AbstractPreviewPage extends Page implements IPreviewPage, ISelectionProvider {
    private final IAction refreshAction = new Action("Refresh") {
        public void run() {
            refresh(true);
        };
    };
    private final IAction toggleRefresh = new Action("Toggle Refresh", IAction.AS_RADIO_BUTTON) {
        public void run() {
            toggleRefresh();
        };
    };
    private IProject project;
    private Browser browser;
    private boolean toggleState = true;
    private final PreviewView previewView;
    private boolean needsRefresh = false;

    public AbstractPreviewPage(IProject project, PreviewView previewView) {
        this.project = project;
        this.previewView = previewView;
    }

    protected synchronized void toggleRefresh() {
        toggleState = !toggleState;
        toggleRefresh.setChecked(toggleState);
        previewView.setProjectAutorefresh(project, toggleState);
        toggleRefresh.setToolTipText(getToggleActionTooltip());
        if (toggleState && needsRefresh) {
            refresh(true);
        }
    }

    @Override
    public void createControl(Composite parent) {
        browser = createBrowser(parent);
        browser.setLayoutData(new GridData(GridData.FILL_BOTH));
        browser.setUrl(getURI().toASCIIString());
    }

    protected abstract Browser createBrowser(Composite parent);

    private URI getURI() {
        return PreviewerPlugin.getDefault().getHttpPreviewer().previewProject(project, null);
    }

    @Override
    public Control getControl() {
        return browser;
    }

    @Override
    public void setFocus() {
        if (browser != null && !browser.isDisposed()) {
            browser.setFocus();
        }
    }

    private boolean refreshScheduled = false;

    public synchronized void process(Collection<IFile> files) {
        if (!isDisposed() && !refreshScheduled && needsRefresh(files)) {
            asyncExec(new Runnable() {
                public void run() {
                    refreshBrowser();
                }
            });
            refreshScheduled = true;
        }
    }

    protected void promptIfNeeded() {
        if (toggleState) {
            toggleState = previewView.promptUserToToggle(project, toggleState);
            toggleRefresh.setChecked(toggleState);
        }
    }

    private void asyncExec(Runnable runnable) {
        getControl().getDisplay().asyncExec(runnable);
    }

    private boolean needsRefresh(Collection<IFile> files) {
        for (IFile iFile : files) {
            if (iFile.getProject().equals(project)) {
                return true;
            }
        }
        return false;
    }

    protected synchronized void refresh(final boolean manual) {
        try {
            if (!isDisposed()) {
                final Control focusControl = browser.getDisplay().getFocusControl();
                if (manual && CoreUtil.isMac()) {
                    browser.getParent().forceFocus();
                }
                browser.setUrl(getURI().toASCIIString());
                refreshAction.setImageDescriptor(PreviewerPlugin.getImageDescriptor(Images.GREEN_SYNC));
                asyncExec(new Runnable() {
                    public void run() {
                        if (!manual && focusControl != null && !focusControl.isDisposed()) {
                            focusControl.setFocus();
                        } else if (manual) {
                            browser.forceFocus();
                        }
                    }
                });
                refreshAction.setToolTipText("Refresh the preview browser");
            }
            needsRefresh = false;
        } finally {
            refreshScheduled = false;
        }
    }

    @Override
    public void init(IPageSite pageSite) {
        super.init(pageSite);
        IToolBarManager toolBar = pageSite.getActionBars().getToolBarManager();

        contributeToToolbar(toolBar);

        refreshAction.setImageDescriptor(PreviewerPlugin.getImageDescriptor(Images.GREEN_SYNC));
        refreshAction.setToolTipText("Refresh the preview browser");
        toolBar.add(refreshAction);

        toggleState = previewView.getProjectAutorefresh(project);

        toggleRefresh.setImageDescriptor(PreviewerPlugin.getImageDescriptor(Images.YELLOW_SYNC));
        toggleRefresh.setToolTipText(getToggleActionTooltip());
        toggleRefresh.setChecked(toggleState);
        toolBar.add(toggleRefresh);

        pageSite.getActionBars().setGlobalActionHandler(ActionFactory.REFRESH.getId(), refreshAction);
        getSite().setSelectionProvider(this);
    }

    protected void contributeToToolbar(IToolBarManager toolBar) {
        // Do nothing here
    }

    private String getToggleActionTooltip() {
        if (toggleState) {
            return "Disable preview autorefresh";
        } else {
            return "Enable preview autorefresh";
        }
    }

    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        // Do nothing
    }

    public ISelection getSelection() {
        return new StructuredSelection(project);
    }

    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        // Do nothing
    }

    public void setSelection(ISelection selection) {
        // Do nothing
    }

    public boolean isDisposed() {
        return browser != null && browser.isDisposed();
    }

    private synchronized void refreshBrowser() {
        if (toggleState) {
            promptIfNeeded();
        }
        if (toggleState) {
            refresh(false);
        } else {
            needsRefresh = true;
            refreshAction.setImageDescriptor(PreviewerPlugin.getImageDescriptor(Images.RED_SYNC));
            refreshAction.setToolTipText("Refresh the preview browser (there are updated files)");
        }
    }

    public IProject getProject() {
        return project;
    }

    public synchronized void projectRenamed(IPath newPath) {
        if (!isDisposed()) {
            project = ResourcesPlugin.getWorkspace().getRoot().getProject(newPath.lastSegment());
        }
    }
}
