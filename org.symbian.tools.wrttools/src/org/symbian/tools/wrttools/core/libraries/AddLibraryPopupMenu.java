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
package org.symbian.tools.wrttools.core.libraries;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.symbian.tools.wrttools.Activator;

public class AddLibraryPopupMenu extends Action implements IMenuCreator, MenuListener {
    public static class LibrariesPopupMenu extends ActionContributionItem {
        public LibrariesPopupMenu() {
            super(new AddLibraryPopupMenu());
        }

        @Override
        public void fill(Menu parent, int index) {
            IProject project = getSelectedProject();
            getAction().setEnabled(project != null && hasLibrariesToInstall(project));
            super.fill(parent, index);
        }
    }

    private Menu menu;

    public AddLibraryPopupMenu() {
        super("Add JavaScript Library", AS_DROP_DOWN_MENU);
        setMenuCreator(this);
    }

    protected static boolean hasLibrariesToInstall(IProject project) {
        JSLibrary[] libraries = Activator.getJSLibraries();
        for (JSLibrary library : libraries) {
            if (!library.isInstalled(project)) {
                return true;
            }
        }
        return false;
    }

    public void dispose() {
        // TODO Auto-generated method stub

    }

    public Menu getMenu(Control parent) {
        return null;
    }

    public Menu getMenu(Menu parent) {
        if (menu != null) {
            menu.dispose();
        }
        menu = new Menu(parent);
        menu.addMenuListener(this);
        return menu;
    }

    public void menuHidden(MenuEvent e) {
        // TODO Auto-generated method stub

    }

    public void menuShown(MenuEvent e) {
        IProject p = getSelectedProject();
        if (p != null) {
            addActions(p);
        }
    }

    protected static IProject getSelectedProject() {
        IProject p = null;
        ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
                .getSelection();
        if (!selection.isEmpty() && (selection instanceof IStructuredSelection)
                && ((IStructuredSelection) selection).size() == 1) {
            Object el = ((IStructuredSelection) selection).getFirstElement();
            if (el instanceof IAdaptable) {
                p = (IProject) ((IAdaptable) el).getAdapter(IProject.class);
            }
        }
        return p;
    }

    private void addActions(IProject p) {
        IJavaScriptProject jsProject = JavaScriptCore.create(p);
        JSLibrary[] jsLibraries = Activator.getJSLibraries();
        for (JSLibrary jsLibrary : jsLibraries) {
            if (!jsLibrary.isInstalled(jsProject.getProject())) {
                createMenuItem(menu, jsLibrary, jsProject);
            }
        }
    }

    private void createMenuItem(Menu menu2, final JSLibrary jsLibrary, final IJavaScriptProject jsProject) {
        MenuItem item = new MenuItem(menu2, SWT.PUSH);
        item.setText(jsLibrary.getName());
        item.setImage(jsLibrary.getImage());
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Map<String, String> empty = Collections.emptyMap();
                try {
                    jsLibrary.install(jsProject.getProject(), empty, new NullProgressMonitor());
                } catch (CoreException e1) {
                    Activator.log(e1);
                } catch (IOException e1) {
                    Activator.log(e1);
                }
            }
        });
    }
}
