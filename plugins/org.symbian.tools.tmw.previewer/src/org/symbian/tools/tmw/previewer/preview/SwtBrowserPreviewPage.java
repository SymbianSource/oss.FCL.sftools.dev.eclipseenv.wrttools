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

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;

public class SwtBrowserPreviewPage extends AbstractPreviewPage implements IPreviewPage {

    public SwtBrowserPreviewPage(IProject project, PreviewView previewView) {
        super(project, previewView);
    }

    @Override
    protected Browser createBrowser(Composite parent) {
        return new Browser(parent, SWT.NONE);
    }
}
