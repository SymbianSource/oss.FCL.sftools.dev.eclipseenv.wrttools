package org.symbian.tools.wrttools.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.symbian.tools.wrttools.WRTProject;
import org.symbian.tools.wrttools.wizards.deploy.DeployWizard;

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
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

        IResource resource = null;

        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        if (activePart instanceof IEditorPart) {
            resource = (IResource) ((IEditorPart) activePart).getEditorInput().getAdapter(IResource.class);
        } else {
            ISelection selection = HandlerUtil.getCurrentSelection(event);
            if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
                Object[] array = ((IStructuredSelection) selection).toArray();
                if (array.length == 1 && array[0] instanceof IAdaptable) {
                    resource = (IResource) ((IAdaptable) array[0]).getAdapter(IResource.class);
                }
            }
        }
        if (resource != null) {
            window.getActivePage().saveAllEditors(true);
            WizardDialog dialog = new WizardDialog(window.getShell(), new DeployWizard(new WRTProject(resource
                    .getProject())));
            dialog.open();
        }

        return null;
    }
}
