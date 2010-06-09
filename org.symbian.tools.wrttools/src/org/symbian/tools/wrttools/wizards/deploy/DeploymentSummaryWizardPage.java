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
package org.symbian.tools.wrttools.wizards.deploy;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class DeploymentSummaryWizardPage extends WizardPage {
    private StringBuilder buffer = new StringBuilder(1000);
    private Text log;

    protected DeploymentSummaryWizardPage() {
        super("deploy");
        setTitle("WRT Application Deployment");
        setDescription("Please wait while deployment is in progress");
    }

    public void createControl(Composite parent) {
        log = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
        setControl(log);
    }

    public void log(String line) {
        synchronized (buffer) {
            buffer.append(line).append("\n");
        }
        log.getDisplay().asyncExec(new Runnable() {
            public void run() {
                synchronized (buffer) {
                    log.setText(buffer.toString());
                }
            }
        });
    }

    public void clear() {
        log.setText("");
        buffer = new StringBuilder();
    }
}
