/**
 * Copyright (c) 2009 Symbian Foundation and/or its subsidiary(-ies).
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

package org.symbian.tools.wrttools.previewer.preview;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;

import org.mozilla.interfaces.nsIPrefBranch;
import org.mozilla.interfaces.nsIServiceManager;
import org.mozilla.xpcom.Mozilla;
import org.osgi.framework.Bundle;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;

//import com.aptana.ide.editor.html.BrowserExtensionLoader;
//import com.aptana.ide.editors.UnifiedEditorsPlugin;
//import com.aptana.ide.editors.unified.ContributedBrowser;
//import com.aptana.ide.editors.unified.ContributedOutline;
//import com.aptana.ide.editors.unified.DefaultBrowser;
import org.symbian.tools.wrttools.previewer.utils.FileUtils;
import org.symbian.tools.wrttools.previewer.Activator;

public class WidgetPreviewBrowser extends Page implements IPageBookViewPage {
	
	private static final String XUL_RUNNER_PATH_PARAMETER = "org.eclipse.swt.browser.XULRunnerPath";

	private ScrolledComposite outerComposite;

	private Composite innerComposite;

	private Browser browser;
	
	private String url;
	
	private PreviewSupport previewSupport;
	
	private boolean displayingPreview; // true if we're displaying the widget preview in the browser
	
	private nsIPrefBranch mozillaPrefs;
	
	private final IProject project;
	
	public WidgetPreviewBrowser(IProject project) {
		this.project = project;
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

	private String getURL() {
		return project.getFile("wrt_preview_frame.html").getLocationURI().toASCIIString();
	}


	@Override
	public Control getControl() {
		return browser;
	}


	public void createControl(Composite parent) {	
		
		// initialize mozilla browser
		initMozilla();
		browser = new Browser(parent, SWT.MOZILLA);
		browser.setUrl(getURL());

		outerComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		outerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		innerComposite = new Composite(outerComposite, SWT.NONE);
		innerComposite.setLayout(new GridLayout(1, true));
		innerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		String browserLabel = Activator.getDefault().getPreferenceStore().getString(
						WidgetPreviewPreferencePage.WIDGET_BROWSER_PREVIEW_PREFERENCE);
		IConfigurationElement element = null;
		
//TODO - remove Aptana specific Browser implementation
/*
		List<IConfigurationElement> browserList = WidgetPreviewPreferencePage.getBrowsers();
		for (int i = 0; i < browserList.size(); i++) {
			IConfigurationElement curr = (IConfigurationElement) browserList.get(i);
			String label = BrowserExtensionLoader.getBrowserLabel(curr);
			if (browserLabel.equals(label)) {
				element = curr;
				break;
			}
		}
		if (element != null) {
			try {
				browser = (ContributedBrowser) element.createExecutableExtension(UnifiedEditorsPlugin.CLASS_ATTR);
				browser.createControl(innerComposite);
			} catch (Exception e1) {
				Activator.log(IStatus.ERROR, "Error creating browser control", e1);
				browser = new DefaultBrowser();
				browser.createControl(innerComposite);
			}
		} else {
			browser = new DefaultBrowser();
			browser.createControl(innerComposite);
		}
		
		Object browserObject = browser.getUnderlyingBrowserObject();
*/
		
		//if (browserObject instanceof MozillaBrowser) {
			bypassSameOriginPolicy();
		//}

		outerComposite.setContent(innerComposite);

		outerComposite.setExpandHorizontal(true);
		outerComposite.setExpandVertical(true);
	}

	private void initializePreview(String url) {
		if (previewSupport == null) {
			IProject project = getCurrentProject(url);
			if (project != null) {
				previewSupport = new PreviewSupport(project);
			}
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aptana.ide.editors.unified.ContributedBrowser#refresh()
	 */
	public void refresh() {
		if (browser != null) {
			if (displayingPreview) {
				// setting the browser URL is potentially asynchronous.
				// do our other actions after that completes.
				browser.addProgressListener(new ProgressAdapter() {
					public void completed(ProgressEvent arg0) {
						browser.removeProgressListener(this);
						browser.refresh();
					}
				});
				try {
					IFile mainHtml = previewSupport.getMainHtml();
					if(mainHtml != null) {
						browser.setUrl(getPreviewHtmlURL());	
			
					} 
					else {
						IFile nopreview = previewSupport.getPreviewFolder().getFile("nopreview.html");
						if (nopreview != null) {
							browser.setUrl(nopreview.getLocation().toFile().toURI().toString());
						}
					}					
				} catch (CoreException e) {
					Activator.log(IStatus.ERROR, "Error setting preview URL", e);
				}
			}
		}
	}

	public void dispose() {
		if (previewSupport != null) {
			previewSupport.dispose();
		}
		if (outerComposite != null && !outerComposite.isDisposed()) {
			outerComposite.dispose();
		}
	}

//TODO
/*
    public Control getControl() {
 
		return outerComposite;
	}
*/
	
	@Override
	public void setFocus() {
		browser.setFocus();
	}
	
	private String getPreviewHtmlURL() {
		String result = null;
		try {
			IFile previewHtml = previewSupport.getPreviewFrameHtml();
			URL fileURL = previewHtml.getLocation().toFile().toURL();
			result = fileURL.toString();
		} catch (CoreException e) {
			Activator.log(IStatus.ERROR, "Error setting preview URL", e);
		} catch (IOException e) {
			Activator.log(IStatus.ERROR, "Error setting preview URL", e);
		}
		return result;
	}
	
	public void setURL(String url) {
		if (browser != null &&  
				!"about:blank".equalsIgnoreCase(url)) {
			
			initializePreview(url);
			applyAptanaProxySettingsToMozilla();

			try {
				URL tmp = new URL(url);
				String urlFile = tmp.getFile();
				IFile mainHtml = previewSupport.getMainHtml();
				
				String mainHtmlLocation = null;
				
				if(mainHtml != null) {
					mainHtmlLocation = mainHtml.getLocation().toString();
					
					if (urlFile != null && 
						(urlFile.contains(".tmp") ||
						 urlFile.equals(mainHtmlLocation))) {
						 
						this.url = getPreviewHtmlURL();
						browser.setUrl(this.url);
						displayingPreview = true;
					} 
				}else {
					IFile nopreview = previewSupport.getPreviewFolder().getFile("nopreview.html");
					if (nopreview != null) {
			
						browser.setUrl(nopreview.getLocation().toFile().toURI().toString());
					}
				}
			} catch (CoreException e) {
				Activator.log(IStatus.ERROR, "Error setting preview URL", e);
			} catch (IOException e) {
				Activator.log(IStatus.ERROR, "Error setting preview URL", e);
			}
		}
	}

	private void bypassSameOriginPolicy() {

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
			mozillaPrefs.setCharPref("capability.principal.codebase.p0.id", "file://");
			mozillaPrefs.setBoolPref("security.fileuri.strict_origin_policy", 0);
			
			
			

		} catch (Exception e) {
			Activator.log(IStatus.ERROR, "Error getting preferences", e);
		}
	}
	
	private void applyAptanaProxySettingsToMozilla() {
		
		IProxyService px = Activator.getProxyService();
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
			 Activator.log(IStatus.ERROR, "Proxy service returned null", e);
		 }
	}
	
	private IProject getCurrentProject(String url) {
		if (url != null) {
			File f =  null;
			try {
				f = new File(new URI(url));
				IPath path = FileUtils.convertToWorkspacePath(new Path(f.getCanonicalPath()), true, false);
				if (path != null) {
					IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot(); 
					IProject project = root.getProject(path.segment(0));
					if (project != null) {
						return project;
					}
				}
			} catch (Exception e) {
			}
		}
		ISelection selection = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		IEditorPart activeEditor = null;
		IProject project = null;
		IWorkbenchPage activePage = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (activePage != null) {
			activeEditor = activePage.getActiveEditor();
		}
		
		if(activeEditor != null){			
			IEditorInput editorInput = activeEditor.getEditorInput();
			IResource adapter = (IResource) editorInput.getAdapter(IResource.class);
			project = adapter.getProject();
		} else if (selection instanceof TreeSelection) {
			TreeSelection seTreeSelection = (TreeSelection)selection;
			Object obj = seTreeSelection.getFirstElement();
			if (obj instanceof IResource) {
				IResource r = (IResource) obj;
				project = r.getProject();
			}
		}
		return project;
	}

	public void back() {
		if(browser != null){
			browser.back();
		}
	}

	public void forward() {
		if(browser != null){
			browser.forward();
		}
	}

//TODO - remove Aptana specific browser implementation
/*
	public Object getUnderlyingBrowserObject()
	{
		Object result = null;
		if (browser != null) {
			result = browser.getUnderlyingBrowserObject();
		}
		return result;
	}
	

	@Override
	public void setOutline(ContributedOutline outline) {
		if (browser != null) {
			browser.setOutline(outline);
		}
	}
*/
}
