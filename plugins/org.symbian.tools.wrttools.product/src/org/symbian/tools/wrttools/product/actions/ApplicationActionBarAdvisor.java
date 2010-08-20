package org.symbian.tools.wrttools.product.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
    
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
		// TODO Auto-generated constructor stub
	}
	private IWorkbenchAction aboutAction;
    protected void makeActions(final IWorkbenchWindow window) {
        
	aboutAction = ActionFactory.ABOUT.create(window);
	register(aboutAction);
        
    }
    protected void fillMenuBar(IMenuManager menuBar) {
	//Help
	MenuManager helpMenu = new MenuManager("&Help",IWorkbenchActionConstants.M_HELP);
	menuBar.add(helpMenu);
	// About > Help
	helpMenu.add(new Separator());
	helpMenu.add(aboutAction);
        
    }
}