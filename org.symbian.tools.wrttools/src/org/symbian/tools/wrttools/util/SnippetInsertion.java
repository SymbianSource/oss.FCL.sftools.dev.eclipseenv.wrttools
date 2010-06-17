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
package org.symbian.tools.wrttools.util;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.common.snippets.ui.DefaultSnippetInsertion;

public class SnippetInsertion extends DefaultSnippetInsertion {

    @Override
    protected void doInsert(final IEditorPart editorPart, ITextEditor textEditor, IDocument document,
            ITextSelection textSelection) throws BadLocationException {
        super.doInsert(editorPart, textEditor, document, textSelection);
        editorPart.getSite().getShell().getDisplay().asyncExec(new Runnable() {
            public void run() {
                editorPart.setFocus();
            }
        });
    }
}
