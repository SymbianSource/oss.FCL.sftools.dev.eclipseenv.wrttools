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
package org.symbian.tools.wrttools.wizards;

import org.eclipse.jface.wizard.IWizardPage;

import java.util.Map;

/**
 * An extended interface for {@link IWizardPage} that provides the data
 * collected in the page as a {@link Map} of values indexed by field identifier.
 */
public interface IWizardData extends IWizardPage {
	
	/**
	 * Returns a <code>java.util.Map<String, Object></code> of values indexed by field identifier.
	 * @return <code>java.util.Map<String, Object></code>
	 */
	Map<String, Object> getPageValues();
	
}

