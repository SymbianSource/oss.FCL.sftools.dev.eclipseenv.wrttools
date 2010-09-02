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
package org.symbian.tools.wrttools.product.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.wst.jsdt.internal.ui.wizards.NewJSWizard;
import org.eclipse.wst.jsdt.ui.actions.AbstractOpenWizardAction;

@SuppressWarnings("restriction")
public class NewJSFileAction extends AbstractOpenWizardAction implements IWorkbenchWindowActionDelegate {

    @Override
    protected INewWizard createWizard() throws CoreException {
        return new NewJSWizard();
    }

    public void dispose() {
        // TODO Auto-generated method stub

    }

    public void init(IWorkbenchWindow window) {
        // TODO Auto-generated method stub

    }

    public void run(IAction action) {
        run();
    }

    public void selectionChanged(IAction action, ISelection selection) {
        // TODO Auto-generated method stub

    }

}
