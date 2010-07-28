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
package org.symbian.tools.mtw.internal.deployment;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.util.Policy;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.PageBook;
import org.symbian.tools.mtw.ui.MTWCoreUI;
import org.symbian.tools.mtw.ui.ProjectMemo;
import org.symbian.tools.mtw.ui.deployment.IDeploymentTarget;
import org.symbian.tools.mtw.ui.deployment.IDeploymentTargetType;
import org.symbian.tools.mtw.ui.deployment.ITargetDetailsPane;

public class DeploymentTargetWizardPage extends WizardPage {
    private final DeployWizardContext context;
    private TableViewer list;
    private final IDeploymentTarget prev;
    private PageBook descriptions;
    private final Map<IDeploymentTargetType, ITargetDetailsPane> panes = new HashMap<IDeploymentTargetType, ITargetDetailsPane>();
    private Control emptyness;

    public DeploymentTargetWizardPage(DeployWizardContext context, ProjectMemo memo) {
        super("TargetPage", "Select Deployment Target", null);
        this.context = context;
        prev = memo.getPreviousDeploymentTarget();
        setDescription(MessageFormat.format("Select emulator or device to deploy WRT application ''{0}''", context
                .getProject().getProject().getName()));
    }

    public void createControl(Composite parent) {
        Composite root = new Composite(parent, SWT.NONE);

        root.setLayout(new FormLayout());

        list = new TableViewer(root, SWT.BORDER);
        list.setContentProvider(new ArrayContentProvider());
        list.setLabelProvider(new WorkbenchLabelProvider());
        list.setSorter(new ViewerSorter() {
            @Override
            public int category(Object element) {
                return ((DeploymentTargetWrapper) element).getCategory();
            }
        });
        list.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = ((IStructuredSelection) event.getSelection());
                DeploymentTargetWrapper target = (DeploymentTargetWrapper) selection.getFirstElement();
                selectDeploymentTarget(target);
            }
        });
        list.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                if (getWizard().performFinish()) {
                    IWizardContainer container = getWizard().getContainer();
                    if (container instanceof WizardDialog) {
                        ((WizardDialog) container).close();
                    }
                }
            }
        });
        final Button search = new Button(root, SWT.NONE);
        search.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                doBluetoothSearch(search);
            }
        });
        search.setText("Discover");
        search.setImage(MTWCoreUI.getImages().getDiscoverButtonIcon());

        descriptions = new PageBook(root, SWT.BORDER);
        emptyness = new Composite(descriptions, SWT.NONE);

        FormData data = new FormData();
        data.left = new FormAttachment(0, 5);
        data.right = new FormAttachment(100, -5);
        data.bottom = new FormAttachment(100, -5);
        data.height = 50;
        descriptions.setLayoutData(data);

        data = new FormData();
        data.top = new FormAttachment(0, 5);
        data.right = new FormAttachment(100, -5);
        search.setLayoutData(data);

        data = new FormData();
        data.left = new FormAttachment(0, 5);
        data.top = new FormAttachment(0, 5);
        data.bottom = new FormAttachment(descriptions, -10);
        data.right = new FormAttachment(search, -10);

        list.getControl().setLayoutData(data);

        list.setInput(context.getDeploymentTargets());

        setPageComplete(false);
        if (prev != null) {
            list.setSelection(new StructuredSelection(prev));
        }

        if (!context.areTargetsReady()) {
            setMessage("Press \"Discover\" to find more deployment targets", IStatus.WARNING);
        }

        setControl(root);
    }

    protected void toggleLogging(boolean logging) {
        context.setLogging(logging);
    }

    protected void doBluetoothSearch(final Button search) {
        try {
            getContainer().run(true, true, new IRunnableWithProgress() {
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask("Searching for Bluetooth devices", IProgressMonitor.UNKNOWN);
                    try {
                        context.doSearch(new SubProgressMonitor(monitor, 100));
                    } catch (CoreException e) {
                        Policy.getStatusHandler().show(e.getStatus(), "WRT Application Deployment");
                    }
                    monitor.done();
                    search.getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            list.setInput(context.getDeploymentTargets());
                            ISelection selection = list.getSelection();
                            if (selection.isEmpty()) {
                                selectDeploymentTarget(null);
                            } else {
                                selectDeploymentTarget((DeploymentTargetWrapper) ((IStructuredSelection) selection)
                                        .getFirstElement());
                            }
                        }
                    });
                }
            });
        } catch (InvocationTargetException e) {
            MTWCoreUI.log(e);
        } catch (InterruptedException e) {
            MTWCoreUI.log(e);
        }
    }

    protected void selectDeploymentTarget(DeploymentTargetWrapper target) {
        if (target != null) {
            context.setTarget(target);
            setMessage(null);
            setErrorMessage(null);
            setPageComplete(true);

            IDeploymentTargetType type = target.getType();
            ITargetDetailsPane pane = panes.get(type);
            if (pane == null) {
                pane = MTWCoreUI.getDefault().getPresentations().createDetailsPane(type);
                pane.createControl(descriptions);
                pane.init(this);
                panes.put(type, pane);
            }
            pane.setTarget(target.getActualTarget());
            descriptions.showPage(pane.getControl());
        } else {
            descriptions.showPage(emptyness);
            context.setTarget(null);
            setPageComplete(false);
            setMessage(null);
            setErrorMessage("Select device or emulator to deploy the application");
        }
    }

}
