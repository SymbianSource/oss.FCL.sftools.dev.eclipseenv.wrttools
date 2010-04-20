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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.jface.databinding.viewers.IViewerObservableSet;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.core.libraries.JSLibrary;
import org.symbian.tools.wrttools.wizards.WizardContext;

public class WRTProjectLibraryWizardPage extends WizardPage {
    public static final class LibraryCheckStateListener implements ICheckStateListener {
        private final WizardContext context;

        public LibraryCheckStateListener(WizardContext context) {
            this.context = context;
        }

        public void checkStateChanged(CheckStateChangedEvent event) {
            if (!event.getChecked() && context.isRequiredLibrary((JSLibrary) event.getElement())) {
                event.getCheckable().setChecked(event.getElement(), true);
            }
        }

    }
    private CheckboxTableViewer list;
    private final WizardContext context;
    private final DataBindingContext bindingContext;

    public WRTProjectLibraryWizardPage(WizardContext context, DataBindingContext bindingContext) {
        super("ProjectLibraries", "WRT Project Libraries", null);
        this.context = context;
        this.bindingContext = bindingContext;
        setDescription("Select libraries to add to your project");
    }

    public void createControl(Composite parent) {
        list = CheckboxTableViewer.newCheckList(parent, SWT.BORDER);
        list.setLabelProvider(new LibraryLabelProvider());
        list.setContentProvider(new ArrayContentProvider());
        list.setInput(Activator.getJSLibraries());
        list.addCheckStateListener(new LibraryCheckStateListener(context));
        IViewerObservableSet ui = ViewersObservables.observeCheckedElements(list, JSLibrary.class);
        IObservableSet model = BeansObservables.observeSet(context, WizardContext.LIBRARIES);
        bindingContext.bindSet(model, ui);
        setControl(list.getControl());
    }

}
