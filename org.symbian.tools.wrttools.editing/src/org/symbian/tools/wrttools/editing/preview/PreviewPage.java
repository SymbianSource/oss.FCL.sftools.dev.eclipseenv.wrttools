package org.symbian.tools.wrttools.editing.preview;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.osgi.framework.Bundle;
import org.symbian.tools.wrttools.editing.Activator;
import org.symbian.tools.wrttools.editing.Images;

public class PreviewPage extends Page implements IPageBookViewPage, ISelectionProvider {
	private static final String XUL_RUNNER_PATH_PARAMETER = "org.eclipse.swt.browser.XULRunnerPath";
	
	private final IAction refreshAction = new Action("Refresh") {
		public void run() {
			refresh();
		};
	};
	private final IAction toggleRefresh = new Action("Toggle Refresh", IAction.AS_RADIO_BUTTON) {
		public void run() {
			toggleRefresh();
		};
	};

	private final IProject project;
	private Browser browser;
	private boolean toggleState = true;
	private final PreviewView previewView;
	private boolean needsRefresh = false;

	public PreviewPage(IProject project, PreviewView previewView) {
		this.project = project;
		this.previewView = previewView;
	}

	protected void toggleRefresh() {
		toggleState = !toggleState;
		toggleRefresh.setChecked(toggleState);
		previewView.setProjectAutorefresh(project, toggleState);
		toggleRefresh.setToolTipText(getToggleActionTooltip());
		if (toggleState && needsRefresh) {
			refresh();
		}
	}

	private synchronized void initMozilla() {
		if (System.getProperty(XUL_RUNNER_PATH_PARAMETER) == null) {
			Bundle bundle = Platform.getBundle("org.mozilla.xulrunner"); //$NON-NLS-1$
			if (bundle != null) {
				URL resourceUrl = bundle.getResource("xulrunner"); //$NON-NLS-1$
				if (resourceUrl != null) {
					try {
						URL fileUrl = FileLocator.toFileURL(resourceUrl);
						File file = new File(fileUrl.toURI());
						System.setProperty(XUL_RUNNER_PATH_PARAMETER, file
								.getAbsolutePath()); //$NON-NLS-1$
					} catch (IOException e) {
						// log the exception
					} catch (URISyntaxException e) {
						// log the exception
					}
				}
			}
		}
	}

	
	@Override
	public void createControl(Composite parent) {
		initMozilla();
		browser = new Browser(parent, SWT.MOZILLA);
		browser.setUrl(getURL());
	}

	private String getURL() {
		return project.getFile("wrt_preview_frame.html").getLocationURI().toASCIIString();
	}


	@Override
	public Control getControl() {
		return browser;
	}

	@Override
	public void setFocus() {
		browser.setFocus();
	}
	
	private boolean refreshScheduled = false;

	public synchronized void process(Collection<IFile> files) {
		if (!refreshScheduled && needsRefresh(files)) {
			asyncExec(new Runnable() {
				@Override
				public void run() {
					if (toggleState) {
						refresh();
					} else {
						needsRefresh = true;
						refreshAction.setImageDescriptor(Activator.getImageDescriptor(Images.RED_SYNC));
						refreshAction.setToolTipText("Refresh the preview browser (there are updated files)");
					}
				}
			});
			refreshScheduled = true;
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

	protected synchronized void refresh() {
		try {
			final Control focusControl = browser.getDisplay().getFocusControl();
			browser.refresh();
			refreshAction.setImageDescriptor(Activator
					.getImageDescriptor(Images.GREEN_SYNC));
			asyncExec(new Runnable() {
				@Override
				public void run() {
					focusControl.setFocus();
				}
			});
			refreshAction.setToolTipText("Refresh the preview browser");
			needsRefresh = false;
		} finally {
			refreshScheduled = false;
		}
	}

	@Override
	public void init(IPageSite pageSite) {
		super.init(pageSite);
		IToolBarManager toolBar = pageSite.getActionBars().getToolBarManager();
		refreshAction.setImageDescriptor(Activator.getImageDescriptor(Images.GREEN_SYNC));
		refreshAction.setToolTipText("Refresh the preview browser");
		toolBar.add(refreshAction);
		
		toggleState = previewView.getProjectAutorefresh(project);
		
		toggleRefresh.setImageDescriptor(Activator.getImageDescriptor(Images.YELLOW_SYNC));
		toggleRefresh.setToolTipText(getToggleActionTooltip());
		toggleRefresh.setChecked(toggleState);
		toolBar.add(toggleRefresh);
		
		pageSite.getActionBars().setGlobalActionHandler(ActionFactory.REFRESH.getId(), refreshAction);
		getSite().setSelectionProvider(this);
	}

	private String getToggleActionTooltip() {
		return toggleState ? "Disable preview autorefresh" : "Enable preview autorefresh";
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		// Do nothing
	}

	@Override
	public ISelection getSelection() {
		return new StructuredSelection(project);
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		// Do nothing
	}

	@Override
	public void setSelection(ISelection selection) {
		// Do nothing
	}
}
