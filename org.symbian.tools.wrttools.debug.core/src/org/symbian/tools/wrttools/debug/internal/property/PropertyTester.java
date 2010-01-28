/*******************************************************************************
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
 *******************************************************************************/
package org.symbian.tools.wrttools.debug.internal.property;

import org.eclipse.core.resources.IResource;
import org.symbian.tools.wrttools.debug.internal.ChromeDebugUtils;
import org.symbian.tools.wrttools.util.ProjectUtils;

public class PropertyTester extends org.eclipse.core.expressions.PropertyTester {

	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (property.equals("isWrtProject")) {
			return ProjectUtils.hasWrtNature(((IResource) receiver).getProject());
		}
		return false;
	}

}
