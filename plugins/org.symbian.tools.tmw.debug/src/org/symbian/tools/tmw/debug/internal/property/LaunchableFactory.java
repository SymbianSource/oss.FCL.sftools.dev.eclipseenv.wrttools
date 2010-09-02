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
package org.symbian.tools.tmw.debug.internal.property;

import org.eclipse.core.runtime.IAdapterFactory;

public class LaunchableFactory implements IAdapterFactory {

    @SuppressWarnings("rawtypes")
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        // It is only needed to exist
        return null;
    }

    @SuppressWarnings("rawtypes")
    public Class[] getAdapterList() {
        // It is only needed to exist
        return null;
    }

}
