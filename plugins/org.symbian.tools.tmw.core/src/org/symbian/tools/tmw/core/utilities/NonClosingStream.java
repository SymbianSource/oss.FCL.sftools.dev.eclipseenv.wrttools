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
package org.symbian.tools.tmw.core.utilities;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class NonClosingStream extends FilterInputStream {
    public NonClosingStream(InputStream in) {
        super(in);
    }

    @Override
    public void close() throws IOException {
        // Avoid closing ZIP file
    }
}
