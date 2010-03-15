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

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.symbian.tools.wrttools.core.deployer.DeployException;
import org.symbian.tools.wrttools.core.deployer.IWidgetDeployer;
import org.symbian.tools.wrttools.core.deployer.WidgetDeployer;
import org.symbian.tools.wrttools.core.status.IWRTStatusListener;

public class ProgramTarget extends DeploymentTarget {
    public class Deployer extends WidgetDeployer {
        public IStatus deploy(String inputPath, String destinationPath, IProgressMonitor progressMonitor)
                throws DeployException {
            emitStatus(MessageFormat.format("Running {0}", program.getName()));
            boolean b = program.execute(inputPath);
            System.out.println(b);
            return Status.OK_STATUS;
        }
    }

    private static ProgramTarget instance;
    
    private final Program program;
    private Image image;

    private ProgramTarget(Program program) {
        super(program.getName());
        this.program = program;
    }

    public static ProgramTarget getInstance() {
        if (instance == null) {
            Program program = Program.findProgram("wgz");
            if (program != null) {
                instance = new ProgramTarget(program);
            }
        }
        return instance;
    }

    @Override
    public IWidgetDeployer createDeployer(IWRTStatusListener statusListener) {
        Deployer deployer = new Deployer();
        deployer.setStatusListener(statusListener);
        return deployer;
    }

    @Override
    public String getDescription() {
        return "External application will be used to deploy the widget";
    }

    @Override
    public String getType() {
        return "program";
    }

    @Override
    public Image getIcon() {
        if (image == null || image.isDisposed()) {
            Display current = Display.getCurrent();
            image = new Image(current, program.getImageData());
        }
        return image;
    }

    public String getDeployMessage() {
        return "Packaged WGZ archive was passed to external application. Follow application prompts to deploy WGZ file.";
    }
}
