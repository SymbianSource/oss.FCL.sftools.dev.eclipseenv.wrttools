/**
 * Copyright (c) 2009 Symbian Foundation and/or its subsidiary(-ies).
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
package org.symbian.tools.wrttools.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This allows blocking the IP clients from closing the stream. This
 * class is needed when creating workbench files from the Zip archives.
 */
public final class NonClosingStream extends FilterInputStream {
	public NonClosingStream(InputStream in) {
		super(in);
	}

	@Override
	public void close() throws IOException {
		// We do not need Eclipse closing the Zip stream
	}
}