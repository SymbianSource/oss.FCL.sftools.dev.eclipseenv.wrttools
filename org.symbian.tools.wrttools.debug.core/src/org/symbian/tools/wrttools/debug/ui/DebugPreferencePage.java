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
package org.symbian.tools.wrttools.debug.ui;

import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.symbian.tools.wrttools.debug.internal.Activator;
import org.symbian.tools.wrttools.debug.internal.IConstants;

public class DebugPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

    private Button check;

    public DebugPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("TMW debugger settings");
	}

	@Override
	protected void createFieldEditors() {
		DirectoryFieldEditor editor = new DirectoryFieldEditor("chrome", "Chrome Install Location:", getFieldEditorParent());
		editor.setPreferenceName(IConstants.PREF_NAME_CHROME_LOCATION);
		addField(editor);

        check = new Button(getFieldEditorParent(), SWT.CHECK);
        check.setText("Show warning dialog when resources in the debugged project were changed");
        check.setLayoutData(new GridData(GridData.BEGINNING, GridData.END, false, false, 3, 1));

        check.setSelection(!MessageDialogWithToggle.ALWAYS.equals(getPreferenceStore().getString(
                IConstants.PREF_SHOW_RESOURCE_CHANGE_ERROR)));
	}

    @Override
    protected void performDefaults() {
        super.performDefaults();
        check.setSelection(false);
    }

    @Override
    public boolean performOk() {
        if (check.getSelection()) {
            getPreferenceStore().setToDefault(IConstants.PREF_SHOW_RESOURCE_CHANGE_ERROR);
        } else {
            getPreferenceStore().setValue(IConstants.PREF_SHOW_RESOURCE_CHANGE_ERROR, MessageDialogWithToggle.ALWAYS);
        }
        return super.performOk();
    }

	public void init(IWorkbench workbench) {
		// Do nothing
	}
}
