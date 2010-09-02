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
package org.symbian.tools.tmw.ui.navigator;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;
import org.eclipse.wst.jsdt.ui.IContextMenuConstants;
import org.eclipse.wst.jsdt.ui.actions.IJavaEditorActionDefinitionIds;
import org.eclipse.wst.jsdt.ui.actions.JdtActionConstants;
import org.eclipse.wst.jsdt.ui.actions.MoveAction;
import org.eclipse.wst.jsdt.ui.actions.RenameAction;
import org.eclipse.wst.jsdt.ui.actions.SelectionDispatchAction;

public class RenameMoveActionProvider extends CommonActionProvider {
    private SelectionDispatchAction fMoveAction;
    private SelectionDispatchAction fRenameAction;

    private final Collection<IAction> fActions = new HashSet<IAction>();

    public void fillActionBars(IActionBars actionBars) {
        if (fActions.size() > 0) {
            actionBars.setGlobalActionHandler(JdtActionConstants.RENAME, fRenameAction);
            actionBars.setGlobalActionHandler(ActionFactory.RENAME.getId(), fRenameAction);

            actionBars.setGlobalActionHandler(JdtActionConstants.MOVE, fMoveAction);
            actionBars.setGlobalActionHandler(ActionFactory.MOVE.getId(), fMoveAction);
        }
    }

    public void fillContextMenu(IMenuManager menu) {
        menu.appendToGroup(IContextMenuConstants.GROUP_REORGANIZE, new Separator());
        for (IAction action : fActions) {
            menu.appendToGroup(IContextMenuConstants.GROUP_REORGANIZE, action);
        }
    }

    public void init(ICommonActionExtensionSite site) {
        ICommonViewerWorkbenchSite workbenchSite = null;
        if (site.getViewSite() instanceof ICommonViewerWorkbenchSite) {
            workbenchSite = (ICommonViewerWorkbenchSite) site.getViewSite();
        }

        // we only initialize the refactor group when in a view part
        // (required for the constructor)
        if (workbenchSite != null) {
            if (workbenchSite.getPart() != null && workbenchSite.getPart() instanceof IViewPart) {
                IViewPart viewPart = (IViewPart) workbenchSite.getPart();
                IWorkbenchPartSite s = viewPart.getSite();

                fRenameAction = new RenameAction(s);
                initUpdatingAction(fRenameAction, workbenchSite.getSelectionProvider(), workbenchSite
                        .getSelectionProvider().getSelection(), IJavaEditorActionDefinitionIds.RENAME_ELEMENT);
                fMoveAction = new MoveAction(s);
                initUpdatingAction(fMoveAction, workbenchSite.getSelectionProvider(), workbenchSite
                        .getSelectionProvider().getSelection(), IJavaEditorActionDefinitionIds.RENAME_ELEMENT);
            }
        }
    }

    private void initUpdatingAction(SelectionDispatchAction action, ISelectionProvider provider, ISelection selection,
            String actionDefinitionId) {
        action.setActionDefinitionId(actionDefinitionId);
        action.update(selection);
        if (provider != null) {
            provider.addSelectionChangedListener(action);
        }
        fActions.add(action);
    }

    public void setContext(ActionContext context) {
        // Do nothing
    }
}
