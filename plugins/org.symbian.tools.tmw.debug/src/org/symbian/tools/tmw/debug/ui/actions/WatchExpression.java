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
package org.symbian.tools.tmw.debug.ui.actions;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IExpressionManager;
import org.eclipse.debug.core.model.IWatchExpression;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.wst.jsdt.core.IJavaScriptUnit;
import org.eclipse.wst.jsdt.core.dom.AST;
import org.eclipse.wst.jsdt.core.dom.ASTNode;
import org.eclipse.wst.jsdt.core.dom.ASTParser;
import org.eclipse.wst.jsdt.core.dom.ASTVisitor;
import org.eclipse.wst.jsdt.core.dom.SimpleName;
import org.eclipse.wst.jsdt.internal.ui.JavaScriptPlugin;
import org.eclipse.wst.jsdt.internal.ui.javaeditor.WorkingCopyManager;
import org.symbian.tools.tmw.debug.internal.Activator;

@SuppressWarnings("restriction")
public class WatchExpression extends ActionDelegate implements IEditorActionDelegate {
    private final class FindNode extends ASTVisitor {
        private final int offset;
        private final int length;

        private ASTNode node;

        private FindNode(int offset, int length) {
            super(false);
            this.offset = offset;
            this.length = length;
        }

        @Override
        public boolean visit(SimpleName node) {
            checkNode(node);
            return true;
        }

        public void checkNode(ASTNode node) {
            final int end = node.getStartPosition() + node.getLength();
            if (node.getStartPosition() <= offset && end >= offset + length) {
                this.node = node;
            }
        }

        @Override
        public void postVisit(ASTNode node) {
            if (this.node == null) {
                checkNode(node);
            }
        }

        public ASTNode getNode() {
            return node;
        }
    }

    private IEditorPart targetEditor;

    public void setActiveEditor(IAction action, IEditorPart targetEditor) {
        this.targetEditor = targetEditor;
    }

    @Override
    public void run(IAction action) {
        ISelection selection = targetEditor.getEditorSite().getSelectionProvider().getSelection();
        if (selection instanceof ITextSelection) {
            final ITextSelection textSelection = (ITextSelection) selection;
            final WorkingCopyManager manager = JavaScriptPlugin.getDefault().getWorkingCopyManager();
            final IEditorInput editorInput = targetEditor.getEditorInput();
            final IJavaScriptUnit workingCopy = manager.getWorkingCopy(editorInput);
            final ASTParser parser = ASTParser.newParser(AST.JLS3);

            parser.setSource(workingCopy);
            final ASTNode ast = parser.createAST(null);
            final FindNode visitor = new FindNode(textSelection.getOffset(), textSelection.getLength());
            ast.accept(visitor);

            final ASTNode node = visitor.getNode();
            if (node != null) {
                final String jsString;
                if (node.getNodeType() == ASTNode.SIMPLE_NAME) {
                    switch (node.getParent().getNodeType()) {
                    case ASTNode.FIELD_ACCESS:
                    case ASTNode.FUNCTION_INVOCATION:
                        jsString = node.getParent().toString();
                        break;
                    default:
                        jsString = node.toString();
                        break;
                    }
                } else {
                    jsString = node.toString();
                }
                IExpressionManager expressionManager = DebugPlugin.getDefault().getExpressionManager();
                IWatchExpression expression = expressionManager.newWatchExpression(jsString);
                expressionManager.addExpression(expression);

                try {
                    targetEditor.getSite().getPage().showView(IDebugUIConstants.ID_EXPRESSION_VIEW);
                } catch (PartInitException e) {
                    Activator.log(e);
                }
            }
        }
    }

}
