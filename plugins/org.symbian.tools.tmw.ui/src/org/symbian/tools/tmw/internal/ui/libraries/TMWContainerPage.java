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
package org.symbian.tools.tmw.internal.ui.libraries;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.jsdt.core.IIncludePathEntry;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.wst.jsdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.wst.jsdt.ui.wizards.IJsGlobalScopeContainerPage;

@SuppressWarnings("restriction")
public class TMWContainerPage extends WizardPage implements IJsGlobalScopeContainerPage {
    private static final String CONTAINER_ID = "org.symbian.tools.mtw.core.mobileWebLibrary"; //$NON-NLS-1$

    public TMWContainerPage() {
        super("TMWLibraryWizzardPage"); //$NON-NLS-1$
        setTitle("Mobile Web Runtime Library");
    }

    public boolean finish() {
        return true;
    }

    public IIncludePathEntry getSelection() {
        return null;
    }

    public void setSelection(IIncludePathEntry containerEntry) {
    }

    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());
        DialogField field = new DialogField();

        field.setLabelText("Library added to a project");
        LayoutUtil.doDefaultLayout(composite, new DialogField[] { field }, false, SWT.DEFAULT, SWT.DEFAULT);
        Dialog.applyDialogFont(composite);
        setControl(composite);
        setDescription("Mobile application support");
    }

    public void initialize(IJavaScriptProject project, IIncludePathEntry[] currentEntries) {
        // nothing to initialize
    }

    public IIncludePathEntry[] getNewContainers() {
        IIncludePathEntry library = JavaScriptCore.newContainerEntry(new Path(CONTAINER_ID));
        return new IIncludePathEntry[] { library };
    }
}
