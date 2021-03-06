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
package org.symbian.tools.tmw.debug.internal;

import org.chromium.debug.core.model.ChromiumLineBreakpoint;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.ui.IEditorInput;
import org.eclipse.wst.html.core.text.IHTMLPartitions;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.ui.internal.provisional.extensions.ISourceEditingTextTools;
import org.eclipse.wst.sse.ui.internal.provisional.extensions.breakpoint.IBreakpointProvider;
import org.symbian.tools.tmw.debug.internal.launch.ChromeToolsWorkspaceBridge;

@SuppressWarnings("restriction")
public class HtmlBreakpointProvider implements IBreakpointProvider {

    public IStatus addBreakpoint(IDocument document, IEditorInput input, int lineNumber, int offset)
            throws CoreException {
        boolean hasScript = hasJavaScript(document, lineNumber);

        if (hasScript) {
            ChromiumLineBreakpoint breakpoint = new ChromiumLineBreakpoint(getResource(input), lineNumber,
                    ChromeToolsWorkspaceBridge.DEBUG_MODEL_ID);
            DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(breakpoint);
        }
        return Status.OK_STATUS;
    }

    private boolean hasJavaScript(IDocument document, int lineNumber) {
        try {
            if (document instanceof IStructuredDocument) {
                IStructuredDocument doc = (IStructuredDocument) document;
                int lineOffset = doc.getLineOffset(lineNumber - 1);
                int lineLength = doc.getLineLength(lineNumber - 1);
                ITypedRegion[] computePartitioning = doc.computePartitioning(lineOffset, lineLength);
                for (ITypedRegion region : computePartitioning) {
                    if (IHTMLPartitions.SCRIPT.equals(region.getType())
                            || IHTMLPartitions.SCRIPT_EVENTHANDLER.equals(region.getType())) {
                        return true;
                    }
                }
            }
        } catch (BadLocationException e) {
            Activator.log(e);
        }
        return false;
    }

    public IResource getResource(IEditorInput input) {
        return (IResource) input.getAdapter(IResource.class);
    }

    public void setSourceEditingTextTools(ISourceEditingTextTools tools) {
        // Do nothing
    }

}
