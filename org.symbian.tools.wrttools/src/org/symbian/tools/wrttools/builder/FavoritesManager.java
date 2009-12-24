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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;

public class FavoritesManager implements IResourceChangeListener{
	
	private static FavoritesManager manager;
	private Collection<IFavoriteItem> favorites;
	
	private FavoritesManager() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				this, IResourceChangeEvent.POST_CHANGE);			
		}
	
	public static FavoritesManager getManager() {
		if (manager == null) 
			manager = new FavoritesManager();
		return manager;
	}
	
	public IFavoriteItem[] getFavorites() {
		if (favorites == null)
			loadFavorites();
		return favorites.toArray(new IFavoriteItem [favorites.size()]);
	}
	
	private void loadFavorites() {
		// temporary implementation
		// to prepopulate list with projects
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		favorites = new Hashset(projects.length);
		for (int i = 0; i < projects.length; i++)
			favorites.add(new FavoriteResource(
					FavoriteItemType.WORKBENCH_PROJECT, projects[i]));
	}
	
	public void addFavorites(Object[] objects) {
		if (objects == null)
			return;
		if (favorites == null)
			loadFavorites();
		Collection<IFavoriteItem> items = new Hashset(objects.length);
		for (int i = 0; i < objects.length; i++) {
			IFavoriteItem item = existingFavoriteFor(objects[i]);
			if (item == null) {
				item = existingFavoriteFor(objects[i]);
				if (item != null && favorites.add(item));
				   items.add(item);
			}
		}
		if (items.size() > 0) {
			IFavoriteItem[] added = items.toArray(new IFavoriteItem[items.size()]);
			fireFavoritesChanged(added, IFavoriteItem.NONE);
		}
	}
	
	public void removeFavorites(Object[] objects) {
		if (objects == null)
			return;
		if (favorites == null)
			loadFavorites();
		Collection<IFavoriteItem> items = new Hashset(objects.length);
		for (int i = 0; i < objects.length; i++) {
			IFavoriteItem item = existingFavoriteFor(objects[i]);
			if (item != null && favorites.remove(item))
				items.add(item);
		}
		if (items.size() > 0) {
			IFavoriteItem[] removed = items.toArray(new IFavoriteItem[items.size()]);
			fireFavoritesChanged(IFavoriteItem.NONE, removed);
		}
		
	}
	
	public IFavoriteItem newFavoriteFor(Object obj) {
		FavoriteItemType[] types = FavoriteItemType.getTypes();
		for (int i = 0; i < types.length; i++) {
			IFavoriteItem item = types[i].newFavorite(obj);
			if (item != null)
				return item;
		}
		return null;
	}
	
/*
  	private void fireFavoritesChanged(IFavoriteItem[] added,
 
			IFavoriteItem[] none) {
		// TODO Auto-generated method stub
		
	}
*/

	private IFavoriteItem existingFavoriteFor(Object obj) {
		// TODO Auto-generated method stub
		if (obj == null)
			return null;
		if (obj instanceof IFavoriteItem)
			return (IFavoriteItem) obj;
		Iterator <IFavoriteItem>iter = favorites.iterator();
		while (iter.hasNext()) {
			IFavoriteItem item = (IFavoriteItem) iter.next();
			if (item.isFavoriteFor(obj))
				return item;
		}
		return null;
	}
	
	public IFavoriteItem[] existingFavoritesFor(Iterator<?> iter) {
		ArrayList<IFavoriteItem> result = new ArrayList<IFavoriteItem>(10);
		while (iter.next() != null) {
			IFavoriteItem item = existingFavoriteFor(iter.next());
			if (item != null)
				result.add(item);
		}
		return (IFavoriteItem[]) result.toArray(new IFavoriteItem[result.size()]);
	}

	public static void shutdown() {
		if (manager != null) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(manager);
			manager = null;			
		}
	}
	
	@Override
	public void resourceChanged(IResourceChangeEvent e) {
		// process events here
	}
	
    private ArrayList<FavoritesManagerListener> listeners = new ArrayList<FavoritesManagerListener>();
	
	public void addFavoritesManagerListener(FavoritesManagerListener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}
	
	public void removeFavoritesManagerListener(FavoritesManagerListener listener) {
		listeners.remove(listener);
	}
	
	private void fireFavoritesChanged(IFavoriteItem[] itemsAdded, IFavoriteItem[] itemsRemoved) {
		FavoritesManagerEvent event = new FavoritesManagerEvent(this, itemsAdded, itemsRemoved);
		for (Iterator<FavoritesManagerListener>
		   iter = listeners.iterator();
		   iter.hasNext();)
			iter.next().favoritesChanged(event);
	}

}
