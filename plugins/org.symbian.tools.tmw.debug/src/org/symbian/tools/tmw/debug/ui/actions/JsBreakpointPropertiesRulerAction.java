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
// Copyright (c) 2009 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.symbian.tools.tmw.debug.ui.actions;

import org.chromium.debug.core.model.ChromiumLineBreakpoint;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.ui.actions.RulerBreakpointAction;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.IUpdate;

/**
 * Action to bring up the breakpoint properties dialog.
 */
public class JsBreakpointPropertiesRulerAction extends RulerBreakpointAction implements IUpdate {
    private IBreakpoint breakpoint;

    public JsBreakpointPropertiesRulerAction(ITextEditor editor, IVerticalRulerInfo rulerInfo) {
        super(editor, rulerInfo);
        setText("Breakpoint Properties...");
    }

    @Override
    public void run() {
        if (getBreakpoint() != null) {
            PropertyDialogAction action = new PropertyDialogAction(getEditor().getEditorSite(),
                    new ISelectionProvider() {
                        public void addSelectionChangedListener(ISelectionChangedListener listener) {
                        }

                        public ISelection getSelection() {
                            return new StructuredSelection(getBreakpoint());
                        }

                        public void removeSelectionChangedListener(ISelectionChangedListener listener) {
                        }

                        public void setSelection(ISelection selection) {
                        }
                    });
            action.run();
        }
    }

    public void update() {
        breakpoint = null;
        IBreakpoint activeBreakpoint = getBreakpoint();
        if (activeBreakpoint != null && activeBreakpoint instanceof ChromiumLineBreakpoint) {
            breakpoint = activeBreakpoint;
        }
        setEnabled(breakpoint != null);
    }

}
