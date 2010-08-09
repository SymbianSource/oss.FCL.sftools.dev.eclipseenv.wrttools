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
package org.symbian.tools.tmw.ui.commands;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.projects.IMTWProject;
import org.symbian.tools.tmw.ui.UIUtils;
import org.symbian.tools.tmw.ui.deployment.DeployWizard;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class DeployHandler extends AbstractHandler {
    /**
     * The constructor.
     */
    public DeployHandler() {
    }

    /**
     * the command has been executed, so extract extract the needed information
     * from the application context.
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

        final IMTWProject project = UIUtils.getProjectFromCommandContext(event);
        if (project != null) {
            window.getActivePage().saveAllEditors(true);
            if (validate(project, window)) {
                new WizardDialog(window.getShell(), new DeployWizard(project)).open();
            }
        }

        return null;
    }

    private boolean validate(final IMTWProject project, final IWorkbenchWindow window) {
        final boolean[] retvalue = { false };
        final ProgressMonitorDialog dialog = new ProgressMonitorDialog(window.getShell());
        try {
            dialog.run(true, true, new IRunnableWithProgress() {
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    retvalue[0] = project.validate(monitor);
                }
            });
        } catch (InvocationTargetException e) {
            TMWCore.log(null, e);
        } catch (InterruptedException e) {
            TMWCore.log(null, e);
        }
        if (!retvalue[0]) {
            retvalue[0] = MessageDialog.openQuestion(window.getShell(), "Deploying Mobile Web Project",
                    String.format("Project %s has errors. Are you sure you want to deploy it?", project.getName()));
            if (!retvalue[0]) {
                try {
                    window.getActivePage().showView(IPageLayout.ID_PROBLEM_VIEW);
                } catch (PartInitException e) {
                    TMWCore.log(null, e);
                }
            }
        }
        return retvalue[0];
    }

}
