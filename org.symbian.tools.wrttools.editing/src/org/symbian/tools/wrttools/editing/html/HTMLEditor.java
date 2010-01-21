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
package org.symbian.tools.wrttools.editing.html;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

public class HTMLEditor extends StructuredTextEditor {
	@Override
	public void createPartControl(Composite parent) {
		Composite pane = new Composite(parent, SWT.NONE);
		pane.setLayout(new FormLayout());
		CTabFolder tabFolder = new CTabFolder(pane, SWT.NONE);
		CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
		ToolBar toolBar = new ToolBar(tabFolder, SWT.NONE);
		tabItem.setControl(toolBar);
		tabItem.setText("HTML");
		ToolItem item = new ToolItem(toolBar, SWT.PUSH);
		item.setText("Table");
		tabFolder.setSelection(tabItem);
		
		FormData data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		tabFolder.setLayoutData(data);
		
		Composite editor = new Composite(pane, SWT.BORDER);
		editor.setLayout(new FillLayout());
		data = new FormData();
		data.top = new FormAttachment(tabFolder, 0);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.bottom = new FormAttachment(100, 0);
		editor.setLayoutData(data);
		
		super.createPartControl(editor);
	}
}
