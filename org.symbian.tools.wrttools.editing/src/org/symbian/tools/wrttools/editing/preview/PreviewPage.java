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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.osgi.framework.Bundle;

public class PreviewPage extends Page implements IPageBookViewPage {
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

	public PreviewPage(IProject project) {
		this.project = project;
	}

	protected void toggleRefresh() {
		toggleState = !toggleState;
		toggleRefresh.setChecked(toggleState);
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

	public void process(Collection<IFile> files) {
		if (needsRefresh(files)) {
			getControl().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					if (toggleState) {
						refresh();
					}
				}
			});
		}
	}

	private boolean needsRefresh(Collection<IFile> files) {
		for (IFile iFile : files) {
			if (iFile.getProject().equals(project)) {
				return true;
			}
		}
		return false;
	}

	protected void refresh() {
		browser.refresh();
	}

	@Override
	public void init(IPageSite pageSite) {
		super.init(pageSite);
		IToolBarManager toolBar = pageSite.getActionBars().getToolBarManager();
		ImageDescriptor image = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED);
		refreshAction.setImageDescriptor(image);
		refreshAction.setDescription("Refresh the preview browser");
		refreshAction.setToolTipText("Refresh the preview browser");
		toolBar.add(refreshAction);
		
		toggleRefresh.setImageDescriptor(image);
		toggleRefresh.setDescription("Enable or disable automatic refresh");
		toggleRefresh.setToolTipText("Enable or disable automatic refresh");
		toggleRefresh.setChecked(toggleState);
		toolBar.add(toggleRefresh);
	}
}
