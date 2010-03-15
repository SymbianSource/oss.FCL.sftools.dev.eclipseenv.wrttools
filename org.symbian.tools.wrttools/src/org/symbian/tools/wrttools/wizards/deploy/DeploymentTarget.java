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

import org.eclipse.swt.graphics.Image;
import org.symbian.tools.wrttools.core.deployer.IWidgetDeployer;
import org.symbian.tools.wrttools.core.status.IWRTStatusListener;

public abstract class DeploymentTarget {
    private final String name;
    protected String addr;

    public DeploymentTarget(String name) {
        this.name = name;
    }

    public String getAddr() {
        return addr;
    }

    public String getName() {
        return name;
    }

    public abstract String getDescription();
    public abstract IWidgetDeployer createDeployer(IWRTStatusListener statusListener);
    public abstract String getType();
    public abstract Image getIcon();
    
    public boolean isResolved() {
        return true;
    }

    public abstract String getDeployMessage();
}
