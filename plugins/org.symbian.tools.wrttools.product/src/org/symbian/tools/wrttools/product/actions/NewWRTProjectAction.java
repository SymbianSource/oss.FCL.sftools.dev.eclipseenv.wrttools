package org.symbian.tools.wrttools.product.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.wst.jsdt.ui.actions.AbstractOpenWizardAction;
import org.symbian.tools.tmw.ui.project.NewApplicationWizard;

public class NewWRTProjectAction extends AbstractOpenWizardAction implements
		IWorkbenchWindowActionDelegate {

	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub

	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	protected INewWizard createWizard() throws CoreException {
        return new NewApplicationWizard();
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean doCreateProjectFirstOnEmptyWorkspace(Shell shell) {
		return true;
	}
}
