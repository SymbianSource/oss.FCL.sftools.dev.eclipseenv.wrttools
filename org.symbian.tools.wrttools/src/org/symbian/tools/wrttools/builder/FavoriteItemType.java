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
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public abstract class FavoriteItemType implements Comparable {
	private static final ISharedImages PLATFORM_IMAGES = PlatformUI.getWorkbench().getSharedImages();
	private final String id;
	private final String printName;
	private final int ordinal;
	
	private FavoriteItemType (String id, String name, int position) {
		this.id = id;
		this.ordinal = position;
		this.printName = name;
	}

	public String getId() {
		return id;
	}
	
	public String getName() {
		return printName;
	}
	
    public static final FavoriteItemType UNKNOWN = 
    	new FavoriteItemType("Unknown", "Unknown", 0) {
    	public Image getImage() {
    		return null;
    	}
    	public IFavoriteItem newFavorite(Object obj) {
    		return null;
    	}
    	public IFavoriteItem loadFavorite(String info) {
    		return null;
    	}
		public int compareTo(Object o) {
			// TODO Auto-generated method stub
			return 0;
		}	
	};
	
	public static final FavoriteItemType WORKBENCH_FILE =
		new FavoriteItemType("WBFile", "Workbench File", 1) {
		public Image getImage() {
			return PLATFORM_IMAGES.getImage(org.eclipse.ui.ISharedImages.IMG_OBJ_FILE);
		}
		public IFavoriteItem newFavorite(Object obj) {
			if (!(obj instanceof IFile))
			  return null;
			  return new FavoriteResource(this, (IFile) obj);
		}
		public IFavoriteItem loadFavorite(String info) {
			return FavoriteResource.loadFavorite(this, info);
		}
		public int compareTo(Object o) {
			// TODO Auto-generated method stub
			return 0;
		}	    
	};
	
	public static final FavoriteItemType WORKBENCH_FOLDER =
		new FavoriteItemType("WBFolder", "Workbench Folder", 2) {
		public Image getImage() {
			return PLATFORM_IMAGES.getImage(org.eclipse.ui.ISharedImages.IMG_OBJ_FOLDER);
		}
		public IFavoriteItem newFavorite(Object obj) {
			if (!(obj instanceof IFolder))
				return null;
			return new FavoriteResource(this, (IFolder) obj);
		}
		public IFavoriteItem loadFavorite(String info) {
			return FavoriteResource.loadFavorite(this, info);
		}
		public int compareTo(Object o) {
			// TODO Auto-generated method stub
			return 0;
		}
	};
	static final FavoriteItemType WORKBENCH_PROJECT = null;
	static final FavoriteItemType JAVA_PACKAGE_ROOT = null;
	static final FavoriteItemType JAVA_PROJECT = null;
	static final FavoriteItemType JAVA_PACKAGE = null;
	static final FavoriteItemType JAVA_COMP_UNIT = null;
	static final FavoriteItemType JAVA_INTERFACE = null;
	static final FavoriteItemType JAVA_CLASS = null;
	static final FavoriteItemType JAVA_CLASS_FILE = null;
	
	private static final FavoriteItemType[] TYPES = {
		UNKNOWN, WORKBENCH_FILE, WORKBENCH_FOLDER, WORKBENCH_PROJECT,
		JAVA_PROJECT, JAVA_PACKAGE_ROOT, JAVA_PACKAGE,
		JAVA_CLASS_FILE, JAVA_COMP_UNIT, JAVA_INTERFACE, JAVA_CLASS};
	
	public static FavoriteItemType[] getTypes() {
		return TYPES;
	}
	
	public abstract Image getImage();
	public abstract IFavoriteItem newFavorite(Object obj);
	public abstract IFavoriteItem loadFavorite(String info);
	
	public int compareTo(FavoriteItemType other) {
		return this.ordinal - other.ordinal;
	}
}
