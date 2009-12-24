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

import java.util.EventObject;

public class FavoritesManagerEvent extends EventObject {

	private static final long serialversionUID = 3697053173951102953L;
	
	private final IFavoriteItem[] added;
	private final IFavoriteItem[] removed;
	
	public FavoritesManagerEvent(FavoritesManager source, 
			                     IFavoriteItem[] itemsAdded,
			                     IFavoriteItem[] itemsRemoved) {
		super(source);
		added = itemsAdded;
		removed = itemsRemoved;
			                    	 
	}
	
	public IFavoriteItem[] getItemsAdded() {
		return added;
	}
	
	public IFavoriteItem[] getItemsRemoved() {
		return removed;
	}
}
