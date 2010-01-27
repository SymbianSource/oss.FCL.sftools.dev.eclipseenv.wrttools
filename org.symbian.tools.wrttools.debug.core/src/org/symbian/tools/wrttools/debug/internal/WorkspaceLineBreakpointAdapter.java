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
package org.symbian.tools.wrttools.debug.internal;

import org.chromium.debug.core.model.LineBreakpointAdapter;
import org.chromium.debug.core.model.VProjectWorkspaceBridge;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.jsdt.internal.ui.javaeditor.JavaEditor;

public class WorkspaceLineBreakpointAdapter extends LineBreakpointAdapter {
	@SuppressWarnings("restriction")
	@Override
	protected ITextEditor getEditor(IWorkbenchPart part) {
		if (part instanceof JavaEditor) {
			return (ITextEditor) part;
		} else {
			return null;
		}
	}

    @Override
    protected String getDebugModelId() {
      return VProjectWorkspaceBridge.DEBUG_MODEL_ID;
    }
}
