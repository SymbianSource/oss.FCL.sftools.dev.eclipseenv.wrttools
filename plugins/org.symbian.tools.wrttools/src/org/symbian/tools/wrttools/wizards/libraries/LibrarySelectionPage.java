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
package org.symbian.tools.wrttools.wizards.libraries;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.core.libraries.JSLibrary;

public class LibrarySelectionPage extends WizardPage implements IWizardPage {
    public final class CheckStateListener implements ICheckStateListener {
        public void checkStateChanged(CheckStateChangedEvent event) {
            JSLibrary library = (JSLibrary) event.getElement();
            if (selected != null && library.isInstalled(selected)) {
                event.getCheckable().setChecked(library, true);
            }
            validate();
        }
    }

    private IProject selected = null;
    private CheckboxTableViewer libraryList;

    public final class CheckStateProvider implements ICheckStateProvider {

        public boolean isChecked(Object element) {
            return selected != null && ((JSLibrary) element).isInstalled(selected);
        }

        public boolean isGrayed(Object element) {
            return selected != null && ((JSLibrary) element).isInstalled(selected);
        }

    }

    protected LibrarySelectionPage(IProject project) {
        super("libraryselectionpage");
        selected = project;
        setTitle("Add JavaScript Libraries");
        setDescription("Select project to add libraries to and select librarires to add");
    }

    public void createControl(Composite parent) {
        Composite root = new Composite(parent, SWT.NONE);
        root.setLayout(new GridLayout(2, false));
        Label label = new Label(root, SWT.NONE);
        label.setText("Project:");

        ComboViewer viewer = new ComboViewer(root, SWT.BORDER | SWT.READ_ONLY);

        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                ISelection selection = event.getSelection();
                if (selection instanceof IStructuredSelection) {
                    selectProject((IProject) ((IStructuredSelection) selection).getFirstElement());
                } else {
                    selectProject(null);
                }
            }
        });
        viewer.setLabelProvider(new WorkbenchLabelProvider());
        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setInput(Activator.getWrtProjects());

        viewer.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        libraryList = CheckboxTableViewer.newCheckList(root, SWT.SINGLE | SWT.BORDER);

        GridData data = new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1);
        libraryList.getControl().setLayoutData(data);

        libraryList.setContentProvider(new ArrayContentProvider());
        libraryList.setLabelProvider(new LibraryLabelProvider());
        libraryList.setCheckStateProvider(new CheckStateProvider());
        libraryList.addCheckStateListener(new CheckStateListener());
        libraryList.setInput(Activator.getJSLibraries());

        if (selected != null) {
            viewer.setSelection(new StructuredSelection(selected), true);
        }
        selectProject(selected);

        setControl(root);
        validate();
    }

    private void selectProject(IProject selected) {
        this.selected = selected;
        libraryList.getControl().setEnabled(selected != null);
        libraryList.refresh();
        validate();
    }

    private void validate() {
        boolean valid = false;
        if (selected != null) {
            Object[] checkedElements = libraryList.getCheckedElements();
            for (Object object : checkedElements) {
                if (!((JSLibrary) object).isInstalled(selected)) {
                    valid = true;
                    break;
                }
            }
        }
        setPageComplete(valid);
    }

    public IProject getProject() {
        return selected;
    }

    public JSLibrary[] getSelectedLibraries() {
        final Object[] checkedElements = libraryList.getCheckedElements();
        final Collection<JSLibrary> libraries = new HashSet<JSLibrary>();
        for (Object object : checkedElements) {
            final JSLibrary lib = (JSLibrary) object;
            if (!lib.isInstalled(selected)) {
                libraries.add(lib);
            }
        }
        return libraries.toArray(new JSLibrary[libraries.size()]);
    }

}
