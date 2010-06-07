package org.symbian.tools.wrttools.previewer.preview;

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
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.symbian.tools.wrttools.previewer.Images;
import org.symbian.tools.wrttools.previewer.PreviewerPlugin;

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

    protected IProject project;
    private Browser browser;
    private boolean toggleState = true;
    private final PreviewView previewView;
    private boolean needsRefresh = false;
    private Composite pane;

    public AbstractPreviewPage(IProject project, PreviewView previewView) {
        this.project = project;
        this.previewView = previewView;
    }

    protected void toggleRefresh() {
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
        pane = new Composite(parent, SWT.EMBEDDED);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        pane.setLayout(layout);
        addBrowser();
    }

    private void addBrowser() {
        browser = createBrowser(pane);
        browser.setLayoutData(new GridData(GridData.FILL_BOTH));
        browser.setUrl(getURI().toASCIIString());
        pane.layout();
    }

    protected abstract Browser createBrowser(Composite parent);

    private URI getURI() {
        return PreviewerPlugin.getDefault().getHttpPreviewer().previewProject(project, null);
    }

    @Override
    public Control getControl() {
        return pane;
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

    protected synchronized void refresh(boolean manual) {
        try {
            if (!isDisposed()) {
                final Control focusControl = browser.getDisplay().getFocusControl();
                browser.setUrl(browser.getUrl());
                refreshAction.setImageDescriptor(PreviewerPlugin.getImageDescriptor(Images.GREEN_SYNC));
                if (!manual && focusControl != null) {
                    asyncExec(new Runnable() {
                        public void run() {
                            focusControl.setFocus();
                        }
                    });
                }
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
        return toggleState ? "Disable preview autorefresh" : "Enable preview autorefresh";
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
            asyncExec(new Runnable() {
                public void run() {
                    browser.dispose();
                    addBrowser();
                }
            });
        }
    }
}
