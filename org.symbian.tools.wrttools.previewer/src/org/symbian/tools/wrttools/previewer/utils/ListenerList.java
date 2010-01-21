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
package org.symbian.tools.wrttools.previewer.utils;

import java.util.*;

/**
 * Utility class for listeners. Implemented with the assumption that
 * iteration is much more common than adding or removing listeners.
 *
 * @param <E> a listener interface
 */
public class ListenerList<E>  implements Iterable<E> {

	private ArrayList<E> listeners;
	
	public void add(E listener) {
		if (listeners == null || !listeners.contains(listener)) {
			ArrayList<E> newList = new ArrayList();
			if (listeners != null)
				newList.addAll(listeners);
			newList.add(listener);
			listeners = newList;
		}
	}
	
	public void remove(E listener) {
		if (listeners == null)
			return;
		ArrayList<E> newList = new ArrayList(listeners);
		newList.remove(listener);
		listeners = newList;
	}

	public Iterator<E> iterator() {
		Iterator<E> result;
		if (listeners != null) {
			result = listeners.iterator();
		}
		else {
			// would be nice if this could be a static, but it can't
			result = new EmptyIterator<E>();
		}
		return result;
	}
	
	public int size() {
		return listeners != null? listeners.size() : 0;
	}
	
	static class EmptyIterator<E> implements Iterator<E> {

		public boolean hasNext() {
			return false;
		}

		public E next() {
			throw new NoSuchElementException();
		}

		public void remove() {
			throw new IllegalStateException();
		}
	}
}
