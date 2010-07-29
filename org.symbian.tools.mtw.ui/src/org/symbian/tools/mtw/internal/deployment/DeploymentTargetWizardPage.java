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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import org.symbian.tools.mtw.core.MTWCore;
import org.symbian.tools.mtw.core.runtimes.IPackager;
import org.symbian.tools.mtw.ui.MTWCoreUI;
import org.symbian.tools.mtw.ui.ProjectMemo;
import org.symbian.tools.mtw.ui.deployment.IDeploymentTarget;
import org.symbian.tools.mtw.ui.deployment.IDeploymentTargetType;
import org.symbian.tools.mtw.ui.deployment.ITargetDetailsPane;

public class DeploymentTargetWizardPage extends WizardPage implements ITargetDetailsPane.Context {
    private final DeployWizardContext context;
    private PageBook descriptions;
    private Control emptyness;
    private final Set<IDeploymentTarget> inited = new HashSet<IDeploymentTarget>();
    private TableViewer list;
    private final Map<IDeploymentTargetType, ITargetDetailsPane> panes = new HashMap<IDeploymentTargetType, ITargetDetailsPane>();
    private final IDeploymentTarget prev;

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
        data.height = 80;
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
        setControl(root);
    }

    protected void doBluetoothSearch(final Button search) {
        try {
            final ISelection sel = list.getSelection();
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
                            if (!sel.isEmpty()) {
                                list.setSelection(sel);
                                selectDeploymentTarget((DeploymentTargetWrapper) ((IStructuredSelection) sel)
                                        .getFirstElement());
                            } else {
                                DeploymentTargetWrapper[] deploymentTargets = context.getDeploymentTargets();
                                if (deploymentTargets.length == 0) {
                                    selectDeploymentTarget(null);
                                } else {
                                    list.setSelection(new StructuredSelection(deploymentTargets[0]));
                                    selectDeploymentTarget(deploymentTargets[0]);
                                }
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

    private IPackager getPackager() {
        return MTWCore.getDefault().getRuntimesManager().getPackager(context.getProject());
    }

    protected void selectDeploymentTarget(DeploymentTargetWrapper target) {
        if (target != null) {
            if (!inited.contains(target)) {
                target.init(context.getProject(), getPackager(),
                        MTWCoreUI.getMemo(context.getProject()).getMemo(target.getType().getId(), target));
                inited.add(target);
            }
            context.setTarget(target);

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
        }
        revalidate();
    }

    protected void toggleLogging(boolean logging) {
        context.setLogging(logging);
    }

    public void revalidate() {
        String error = null;
        String warning = !context.areTargetsReady() ? "Press \"Discover\" to find more deployment targets" : null;
        if (context.getTarget() == null) {
            error = "Select device or emulator to deploy the application";
        } else {
            final IStatus validate = panes.get(context.getTarget().getType()).validate();
            if (validate.getSeverity() == IStatus.ERROR) {
                error = validate.getMessage();
            } else if (validate.getSeverity() == IStatus.WARNING) {
                warning = validate.getMessage();
            }
        }
        setErrorMessage(error);
        setMessage(warning, IStatus.WARNING);
        setPageComplete(error == null);
    }

}
