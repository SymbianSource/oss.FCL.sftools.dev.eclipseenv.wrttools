package org.symbian.tools.wrttools.previewer.preview;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
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
import org.mozilla.interfaces.nsIPrefBranch;
import org.mozilla.interfaces.nsIServiceManager;
import org.mozilla.xpcom.Mozilla;
import org.osgi.framework.Bundle;
import org.symbian.tools.wrttools.previewer.PreviewerPlugin;
import org.symbian.tools.wrttools.previewer.Images;
import org.symbian.tools.wrttools.previewer.http.WebAppInterface;
import org.symbian.tools.wrttools.previewer.http.WebappManager;

public class PreviewPage extends Page implements IPageBookViewPage, ISelectionProvider {
	private static final String XUL_RUNNER_PATH_PARAMETER = "org.eclipse.swt.browser.XULRunnerPath";
	private nsIPrefBranch mozillaPrefs;

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
		bypassSameOriginPolicy();
		applyProxySettings();
		browser.setUrl(getURI().toASCIIString());
	}

	private URI getURI() {
		return PreviewerPlugin.getDefault().getHttpPreviewer().previewProject(project);
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
						refreshAction.setImageDescriptor(PreviewerPlugin.getImageDescriptor(Images.RED_SYNC));
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
			refreshAction.setImageDescriptor(PreviewerPlugin
					.getImageDescriptor(Images.GREEN_SYNC));
			if (focusControl != null) {
				asyncExec(new Runnable() {
					@Override
					public void run() {
						focusControl.setFocus();
					}
				});
			}
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
	
	private void applyProxySettings() {
		
		IProxyService px = PreviewerPlugin.getDefault().getProxyService();
		if(px != null){			 
			boolean proxyEnabled = px.isProxiesEnabled();

			boolean systemProxy = px.isSystemProxiesEnabled();
			if( proxyEnabled && !systemProxy){
				IProxyData pd = px.getProxyData(IProxyData.HTTP_PROXY_TYPE);
				if (pd !=null &&mozillaPrefs != null) {				
					String host= pd.getHost();
					int port = pd.getPort();
					if(host !=null && port != -1){
						mozillaPrefs.setIntPref("network.proxy.type", 1);
						mozillaPrefs.setCharPref("network.proxy.http", host);
						mozillaPrefs.setIntPref("network.proxy.http_port", port);
					}
				}
			 }
		}
		else{
			 Exception e= new Exception();
			 PreviewerPlugin.log("Proxy service returned null", e);
		 }
	}
	
	private void bypassSameOriginPolicy() {
		WebAppInterface.getInstance();
		try{
			nsIServiceManager servMgr = null;
			try {
				servMgr = Mozilla.getInstance().getServiceManager();
				if (servMgr == null) return;
			} catch (Exception x) {
				// known to throw NullPointException on Mac OS when you're not using 
				// Mozilla. We don't want to pollute the error log with this
				return;
			}
			
			mozillaPrefs = (nsIPrefBranch) servMgr.getServiceByContractID(
											"@mozilla.org/preferences-service;1", 
											nsIPrefBranch.NS_IPREFBRANCH_IID );		

			mozillaPrefs.setBoolPref("signed.applets.codebase_principal_support", 1);

			mozillaPrefs.setCharPref("capability.policy.default.XMLDocument.getElementsByTagName", "allAccess");
			mozillaPrefs.setCharPref("capability.policy.default.CDATASection.nodeValue", "allAccess");

			mozillaPrefs.setCharPref("capability.policy.default.HTMLCollection.length", "allAccess");
			mozillaPrefs.setCharPref("capability.policy.default.HTMLCollection.item", "allAccess");

			mozillaPrefs.setCharPref("capability.policy.default.*.nodeValue", "allAccess");
			mozillaPrefs.setCharPref("capability.policy.default.*.nodeType", "allAccess");
			mozillaPrefs.setCharPref("capability.policy.default.*.nodeName", "allAccess");
			mozillaPrefs.setCharPref("capability.policy.default.*.nextSibling", "allAccess");
			mozillaPrefs.setCharPref("capability.policy.default.*.previousSibling", "allAccess");
			mozillaPrefs.setCharPref("capability.policy.default.*.attributes", "allAccess");
			mozillaPrefs.setCharPref("capability.policy.default.*.childNodes", "allAccess");
			mozillaPrefs.setCharPref("capability.policy.default.*.firstChild", "allAccess");
			mozillaPrefs.setCharPref("capability.policy.default.*.getAttribute", "allAccess");
			mozillaPrefs.setCharPref("capability.policy.default.*.getElementsByTagName", "allAccess");
			mozillaPrefs.setCharPref("capability.policy.default.*.lastChild", "allAccess");
			mozillaPrefs.setCharPref("capability.policy.default.*.parentNode", "allAccess");
			mozillaPrefs.setCharPref("capability.policy.default.*.tagName", "allAccess");

			mozillaPrefs.setCharPref("capability.policy.default.XMLDocument.documentElement", "allAccess");
			mozillaPrefs.setCharPref("capability.policy.default.XMLDocument.getElementsByTagName", "allAccess");
			mozillaPrefs.setCharPref("capability.policy.default.XMLHttpRequest.channel", "allAccess");
			mozillaPrefs.setCharPref("capability.policy.default.XMLHttpRequest.open", "allAccess");
			mozillaPrefs.setCharPref("capability.policy.default.XMLHttpRequest.responseText", "allAccess");
			mozillaPrefs.setCharPref("capability.policy.default.XMLHttpRequest.responseXML", "allAccess");
			mozillaPrefs.setCharPref("capability.policy.default.XMLHttpRequest.send", "allAccess");
			mozillaPrefs.setCharPref("capability.policy.default.XMLHttpRequest.setRequestHeader", "allAccess");
			/* to over-ride the internet security dialog when preview browser tries to access local hard drive */
			mozillaPrefs.setCharPref("capability.principal.codebase.p0.granted", "UniversalXPConnect  UniversalBrowserRead");
			String location = "http://127.0.0.1:" + WebappManager.getPort();
			mozillaPrefs.setCharPref("capability.principal.codebase.p0.id", location);
			mozillaPrefs.setBoolPref("security.fileuri.strict_origin_policy", 0);
		} catch (Exception e) {
			PreviewerPlugin.log("Error getting preferences", e);
		}
	}

}
