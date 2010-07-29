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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.symbian.tools.mtw.ui.deployment.IDeploymentTarget;
import org.symbian.tools.mtw.ui.deployment.ITargetDetailsPane;

public class DefaultDeploymentTypePresentation implements ITargetDetailsPane {
    private Text text;

    public void init(Context page) {
    }

    public void setTarget(IDeploymentTarget target) {
        text.setText(target.getDescription());
    }

    public void createControl(Composite parent) {
        text = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY);
    }

    public Control getControl() {
        return text;
    }

    public IStatus validate() {
        return Status.OK_STATUS;
    }
}
