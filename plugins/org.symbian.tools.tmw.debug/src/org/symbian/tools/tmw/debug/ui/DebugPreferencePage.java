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
package org.symbian.tools.tmw.debug.ui;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.symbian.tools.tmw.debug.internal.Activator;
import org.symbian.tools.tmw.debug.internal.ChromeDebugUtils;
import org.symbian.tools.tmw.debug.internal.IConstants;

public class DebugPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

    private Button check;

    public DebugPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Mobile Web debugger settings");
	}

	@Override
	protected void createFieldEditors() {
        DirectoryFieldEditor editor = new DirectoryFieldEditor(IConstants.PREF_NAME_CHROME_LOCATION,
                "Chrome Install Location:",
                getFieldEditorParent()) {
            @Override
            protected boolean doCheckState() {
                if (super.doCheckState()) {
                    String message = validate(getStringValue());
                    setErrorMessage(message);
                    return message == null;
                } else {
                    return false;
                }
            }
        };
        editor.setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
        editor.setEmptyStringAllowed(false);
		addField(editor);

        check = new Button(getFieldEditorParent(), SWT.CHECK);
        check.setText("Show warning dialog when resources in the debugged project were changed");
        check.setLayoutData(new GridData(GridData.BEGINNING, GridData.END, false, false, 3, 1));

        check.setSelection(!MessageDialogWithToggle.ALWAYS.equals(getPreferenceStore().getString(
                IConstants.PREF_SHOW_RESOURCE_CHANGE_ERROR)));
	}


    protected String validate(String newValue) {
        String error = null;
        if (newValue == null || newValue.trim().length() == 0) {
            error = "Chrome install location is not specified";
        } else if (!new File(newValue).exists()) {
            error = String.format("%s does not exist", newValue);
        } else if (ChromeDebugUtils.getExecutablePath(newValue) == null) {
            error = String.format("%s does not contain Chrome executable", newValue);
        }
        return error;
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
