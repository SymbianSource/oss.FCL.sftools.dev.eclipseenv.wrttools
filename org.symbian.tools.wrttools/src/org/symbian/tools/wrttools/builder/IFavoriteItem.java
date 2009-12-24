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
package org.symbian.tools.wrttools.builder;

import org.eclipse.core.runtime.IAdaptable;

public interface IFavoriteItem extends IAdaptable {
	String getName();
	void setName(String newName);
	String getLocation();
	boolean isFavoriteFor(Object obj);
	FavoriteItemType getType();
	String getInfo();
	
	static IFavoriteItem[] NONE = new IFavoriteItem[] {};

}
