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
package org.symbian.tools.tmw.ui.deployment;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Objects of this class provide UI for the users to visualize or configure
 * their deployment targets. This object will not be reused between wizard 
 * deployment wizard restarts.
 * There will be one instance for the target type. This object will not be 
 * instantiated for each target.
 * 
 * @author Eugene Ostroukhov (eugeneo@symbian.org)
 */
public interface ITargetDetailsPane {
    interface Context {
        void revalidate();
    }
    void init(Context page);
    void setTarget(IDeploymentTarget target);
    void createControl(Composite parent);
    Control getControl();
    IStatus validate();
}
