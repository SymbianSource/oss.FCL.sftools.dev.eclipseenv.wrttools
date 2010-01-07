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
package org.symbian.tools.wrttools.wizards;

import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;

public class RssReaderProjectDetailsWizardPage extends
		WRTProjectDetailsWizardPage {

	public RssReaderProjectDetailsWizardPage(WizardContext context,
			DataBindingContext bindingContext) {
		super(context, bindingContext);
	}
	
	@Override
	protected void addTemplateControls(Composite root) {
		Map<String, String> extensions = context.getExtensions();
		extensions.put("feedUrl", "http://twitter.com/statuses/user_timeline/21138778.rss");
		extensions.put("feedName", "Symbian Foundation on Twitter");
		
		createLabel(root, "Feed URL:");
		createTextForExt(root, "feedUrl", "feed URL");
		createLabel(root, "Feed Name:");
		createTextForExt(root, "feedName", "feed name");
		createLabel(root, "");
		createLabel(root, "");
	}
	
	public static final class Factory implements IWizardPageFactory {
		@Override
		public WRTProjectDetailsWizardPage createPage(WizardContext context,
				DataBindingContext bindingContext) {
			return new RssReaderProjectDetailsWizardPage(context, bindingContext);
		}
		
	}

}
