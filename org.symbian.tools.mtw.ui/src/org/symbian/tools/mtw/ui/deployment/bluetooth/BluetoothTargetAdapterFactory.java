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
package org.symbian.tools.mtw.ui.deployment.bluetooth;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter2;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class BluetoothTargetAdapterFactory implements IAdapterFactory {
    private final BluetoothTargetWorkbenchAdapter workbenchAdapter = new BluetoothTargetWorkbenchAdapter();

    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if ((adapterType.isAssignableFrom(IWorkbenchAdapter.class) || adapterType
                .isAssignableFrom(IWorkbenchAdapter2.class)) && adaptableObject instanceof BluetoothTarget) {
            return workbenchAdapter;
        }
        return null;
    }

    public Class[] getAdapterList() {
        return new Class[] { IWorkbenchAdapter2.class, IWorkbenchAdapter.class };
    }

}
