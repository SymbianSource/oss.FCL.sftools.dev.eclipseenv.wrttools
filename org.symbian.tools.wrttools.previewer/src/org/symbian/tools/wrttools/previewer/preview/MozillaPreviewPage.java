package org.symbian.tools.wrttools.previewer.preview;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.mozilla.interfaces.nsIPrefBranch;
import org.mozilla.interfaces.nsIServiceManager;
import org.mozilla.xpcom.Mozilla;
import org.osgi.framework.Bundle;
import org.symbian.tools.wrttools.previewer.PreviewerPlugin;
import org.symbian.tools.wrttools.previewer.http.WebAppInterface;
import org.symbian.tools.wrttools.previewer.http.WebappManager;

public class MozillaPreviewPage extends AbstractPreviewPage {
	public static final String XUL_RUNNER_BUNDLE = "org.mozilla.xulrunner";
	
	private static final String XUL_RUNNER_PATH_PARAMETER = "org.eclipse.swt.browser.XULRunnerPath";
	private nsIPrefBranch mozillaPrefs;

	public MozillaPreviewPage(IProject project, PreviewView previewView) {
		super(project, previewView);
	}


	private synchronized void initMozilla() {
		if (System.getProperty(XUL_RUNNER_PATH_PARAMETER) == null) {
			Bundle bundle = Platform.getBundle(XUL_RUNNER_BUNDLE); //$NON-NLS-1$
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
	protected Browser createBrowser(Composite parent) {
		initMozilla();
		final Browser browser = new Browser(parent, SWT.MOZILLA);
		bypassSameOriginPolicy();
		applyProxySettings();
		return browser;
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
				PreviewerPlugin.log(x);
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
			String location = "http://" + WebappManager.getHost() + ":" + WebappManager.getPort();
			mozillaPrefs.setCharPref("capability.principal.codebase.p0.id", location);
			mozillaPrefs.setBoolPref("security.fileuri.strict_origin_policy", 0);
		} catch (Exception e) {
			PreviewerPlugin.log("Error getting preferences", e);
		}
	}


}
