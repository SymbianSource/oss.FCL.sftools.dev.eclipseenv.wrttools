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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

public class FavoriteResource implements IFavoriteItem {

	public FavoriteResource(FavoriteItemType favoriteItemType, IFile obj) {
		// TODO Auto-generated constructor stub
	}

	public FavoriteResource(FavoriteItemType favoriteItemType, IFolder obj) {
		// TODO Auto-generated constructor stub
	}

	public FavoriteResource(FavoriteItemType workbenchProject, IProject iProject) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FavoriteItemType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isFavoriteFor(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setName(String newName) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	public static IFavoriteItem loadFavorite(FavoriteItemType favoriteItemType,
			String info) {
		// TODO Auto-generated method stub
		return null;
	}

}
