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
package org.symbian.tools.mtw.internal.deployment.targets;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.symbian.tools.mtw.ui.MTWCoreUI;
import org.symbian.tools.mtw.ui.deployment.IDeploymentTarget;
import org.symbian.tools.mtw.ui.deployment.ITargetDetailsPane;

public class LocalFileSystemPane implements ITargetDetailsPane {
    private Composite root;
    private Text location;
    private FilesystemDeploymentTarget target;
    private Context page;

    public void init(Context page) {
        this.page = page;
    }

    public void setTarget(IDeploymentTarget target) {
        this.target = (FilesystemDeploymentTarget) target;
        IPath path = this.target.getPath();
        String string;
        if (path == null) {
            string = "";
        } else {
            string = path.toOSString();
        }
        location.setText(string);
        page.revalidate();
    }

    public void createControl(Composite parent) {
        root = new Composite(parent, SWT.NONE);
        root.setLayout(new GridLayout(2, false));
        Label label = new Label(root, SWT.NONE);
        label.setText("File name:");
        GridDataFactory.generate(label, 2, 1);
        location = new Text(root, SWT.BORDER);
        location.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        location.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                page.revalidate();
            }
        });
        final Button button = new Button(root, SWT.NONE);
        button.setText("Browse...");
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                browse();
            }
        });
    }

    protected void browse() {
        final FileDialog dialog = new FileDialog(location.getShell(), SWT.SAVE);
        dialog.setText("Select Application Package Location");
        dialog.setFileName(target.getDefaultName());
        final IPath path = target.getPath();
        if (path != null) {
            dialog.setFilterPath(path.removeLastSegments(1).toOSString());
        }
        String newPath = dialog.open();
        if (newPath != null) {
            location.setText(newPath);
            page.revalidate();
        }
    }

    public IStatus validate() {
        target.setPath(null);
        String path = location.getText();
        if (path.trim().length() == 0) {
            return new Status(IStatus.ERROR, MTWCoreUI.PLUGIN_ID, "File name must be specified");
        } else {
            final File file = new File(path);
            if (!file.getParentFile().isDirectory()) {
                return new Status(IStatus.ERROR, MTWCoreUI.PLUGIN_ID, String.format("%s is not a valid folder",
                        file.getParent()));
            } else {
                IStatus status = ResourcesPlugin.getWorkspace().validateName(file.getName(), IResource.FILE);
                if (status.getSeverity() == IStatus.ERROR) {
                    return status;
                } else {
                    Path p = new Path(path.trim());
                    target.setPath(p);
                    if (p.toFile().exists()) {
                        return new Status(IStatus.WARNING, MTWCoreUI.PLUGIN_ID,
                                "Target file already exists. It will be overwritten.");
                    } else {
                        return status;
                    }
                }
            }
        }
    }

    public Control getControl() {
        return root;
    }

}
