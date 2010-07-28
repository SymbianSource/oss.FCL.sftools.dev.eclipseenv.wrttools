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
package org.symbian.tools.mtw.core.internal.runtimes;

import org.eclipse.core.runtime.IConfigurationElement;
import org.symbian.tools.mtw.core.runtimes.IMobileWebRuntime;

public final class MobileWebRuntime implements IMobileWebRuntime {
    private final IConfigurationElement element;

    public MobileWebRuntime(IConfigurationElement element) {
        this.element = element;
    }

    public String getId() {
        return element.getAttribute("id");
    }

    public String getName() {
        return element.getAttribute("name");
    }
}
