package org.symbian.tools.wrttools.product.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.wst.css.ui.internal.wizard.NewCSSWizard;
import org.eclipse.wst.jsdt.ui.actions.AbstractOpenWizardAction;

public class NewCSSFileAction extends AbstractOpenWizardAction implements
		IWorkbenchWindowActionDelegate {

	@SuppressWarnings("restriction")
	@Override
	protected INewWizard createWizard() throws CoreException {
		return new NewCSSWizard();
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
