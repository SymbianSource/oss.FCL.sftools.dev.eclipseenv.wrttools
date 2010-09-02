/*******************************************************************************
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
 *******************************************************************************/
package org.symbian.tools.tmw.ui.deployment.bluetooth;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

public final class BluetoothRule implements ISchedulingRule {
    public static final ISchedulingRule INSTANCE = new BluetoothRule();

    private BluetoothRule() {
        // No instantiation
    }

    public boolean contains(ISchedulingRule rule) {
        return isConflicting(rule);
    }

    public boolean isConflicting(ISchedulingRule rule) {
        return rule instanceof BluetoothRule;
    }

}
