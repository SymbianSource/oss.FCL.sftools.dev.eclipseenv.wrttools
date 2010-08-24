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
package org.symbian.tools.wrttools.wizards;

import org.eclipse.swt.widgets.Composite;

public class NewRSSReaderApplicationWizardPage extends NewWrtAppTemplatePage {

    public NewRSSReaderApplicationWizardPage() {
        super("RssFiles", "RSS Application Settings", "Customize your RSS reader application project");
    }

    @Override
    protected void addTemplateControls(Composite root) {
        createLabel(root, "Feed URL:");
        createText(root, "feedUrl", "feed URL", bindingContext);
        createLabel(root, "Feed Name:");
        createText(root, "feedName", "feed name", bindingContext);
        createLabel(root, "");
        createLabel(root, "");
    }
}
