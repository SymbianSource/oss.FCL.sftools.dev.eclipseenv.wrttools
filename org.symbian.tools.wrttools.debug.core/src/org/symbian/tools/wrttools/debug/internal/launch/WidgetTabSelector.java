/*******************************************************************************
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
 *******************************************************************************/
package org.symbian.tools.wrttools.debug.internal.launch;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.chromium.debug.core.model.TabSelector;
import org.chromium.sdk.Browser.TabConnector;
import org.chromium.sdk.Browser.TabFetcher;

public class WidgetTabSelector implements TabSelector {
	private final URI uri;
	private TabConnector connector;

	public WidgetTabSelector(URI uri) {
		this.uri = uri;
	}
	
	public TabConnector selectTab(TabFetcher tabFetcher) throws IOException {
		// Give it time to start the process/tab. 5 retries, 500 ms inbetween.
		for (int i = 0; i < 5; i++) {
			List<? extends TabConnector> tabs = tabFetcher.getTabs();
			for (TabConnector tabConnector : tabs) {
				String url = tabConnector.getUrl();
				try {
					if (uri.toURL().equals(new URL(url))) {
						connector = tabConnector;
						return tabConnector;
					}
				} catch (MalformedURLException e) {
					// Ignore - fails because of "chrome" protocol, we should ignore these tabs anyways
				}
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// Ignore
			}
		}
		return null;
	}
	
	public TabConnector getConnector() {
		return connector;
	}
}
