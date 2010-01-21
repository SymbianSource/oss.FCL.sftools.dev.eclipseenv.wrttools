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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * Helper class to track changes to a resource handle. To the extent
 * that Eclipse provides the necessary notification, moves and
 * renames will be tracked and getResource() will return a
 * reference to the resource.
 * A listener interface is provided to give notifications of
 * moves, content changes, and deletions.
 * Upon deletion, since this object can no longer provide any
 * useful servce, the dispose method is automatically called
 * @author dpodwall
 *
 */
public class TrackedResource implements IDisposable {
	
	private IResource resource;
	private long modificationStamp;
	private IPath resourcePath;
	private Class resourceClass;
	private ListenerList<IListener> listeners = new ListenerList<IListener>();
	private ResourceHandler handler;
	
	public interface IListener {
		void resourceChanged(TrackedResource resource);
		/**
		 * Called after a resource has been moved and the
		 * TrackedResource has been updated.
		 * @param oldPath the previous location of the resource
		 */
		void resourceMoved(TrackedResource resource, IPath oldPath);
		void resourceDeleted(TrackedResource resource);
	}
		
	/**
	 * Initialize with the desired resource and being
	 * listening for changes. Once initialized the initial
	 * IResource shouldn't be kept, call getResource as
	 * needed to get the valid resource handle.
	 */
	public TrackedResource(IResource resource) {
		Check.checkArg(resource);
		this.resource = resource;
		this.resourceClass = resource.getClass();
		this.resourcePath = resource.getFullPath();
		Check.checkState(resourcePath != null);
		enableTracking(true);
	}

	/**
	 * Dispose must be called in order to unregister the workspace listener.
	 * It is automatically called if the resource is deleted.
	 */
	public void dispose() {
		if (handler != null) {
			enableTracking(false);
			resourcePath = null;
		}
	}
	
	/**
	 * Set the tracking status to the desired status and return the
	 * previous state.
	 */
	public synchronized boolean enableTracking(boolean enable) {
		boolean wasEnabled = handler != null;
		if (enable == wasEnabled) {
			return wasEnabled;
		}
		
		if (enable) {
			if (handler == null) {
				handler = new ResourceHandler();
				ResourcesPlugin.getWorkspace().addResourceChangeListener(handler);
				modificationStamp = resource.getModificationStamp();
			}
			
		} else if (handler != null){
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(handler);
			handler = null;
		}
		return wasEnabled;
	}

	/**
	 * Get the current resource handle. It will differ from 
	 * the constructor argument if the resource moved.
	 * Can return null if the resource was deleted
	 * 
	 * If non-null, the value will be compatible with
	 * the original resource. In other words if an IFile
	 * was originally used, this will return an IFile (or subclass)
	 * or null.
	 */
	public IResource getResource() {
		return resource;
	}
	
	/**
	 * Returns the last seen modification stamp.
	 */
	public long getLastModificationStamp() {
		return modificationStamp;
	}
	
	/**
	 * Force an update of the cached modification stamp to
	 * the lastest value. This will inhibit notifications for
	 * the same modification stamp value.
	 */
	public void updateModificationStamp() {
		try {
			resource.refreshLocal(0, null);
		} catch (CoreException x) {
			UtilsPlugin.log(x);
		}
		modificationStamp = resource.getModificationStamp();
	}
	
	/**
	 * Convenience routine to return as IFile
	 */
	public IFile getFile() {
		IFile result = null;
		if (resource instanceof IFile) {
			result = (IFile) resource;
		}
		return result;
	}
	
	/**
	 * Convenience routine to return resource's project.
	 * If the resource is a project it will return that resource,
	 * otherwise the containing project.
	 */
	public IProject getProject() {
		IProject result = null;
		if (resource != null) {
			result = resource.getProject();
		}
		return result;
	}
	
	/**
	 * Listener for change events on the tracked resource.
	 * Note that these events may be delivered on
	 * threads other than the UI thread.
	 */
	public void addListener(IListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(IListener listener) {
		listeners.remove(listener);
	}
	
	private void fireResourceChanged() {
		for (IListener l : listeners) {
			l.resourceChanged(this);
		}
	}
	
	private void fireResourceMoved(IPath oldPath) {
		for (IListener l : listeners) {
			l.resourceMoved(this, oldPath);
		}
	}
	
	private void fireResourceDeleted() {
		for (IListener l : listeners) {
			l.resourceDeleted(this);
		}
	}
	
	private void handleDeletion() {
		fireResourceDeleted();
		dispose();
	}
	
	private void handleMove(IResourceDelta delta) {
		IPath movedToPath = delta.getMovedToPath();
		if (movedToPath != null) {
			IResource newResource = ResourcesPlugin.getWorkspace().getRoot().findMember(movedToPath);
			if (newResource != null) {
				// the new resource should be compatible with the previous, e.g.
				// if we started with an IFile the new one should be an IFile
				Check.checkState(resourceClass.isInstance(newResource));
				resource = newResource;
				modificationStamp = resource.getModificationStamp();
				IPath oldPath = resourcePath;
				resourcePath = movedToPath;
				fireResourceMoved(oldPath);
			}
		}
	}
	
	private void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();
		if (delta != null && resourcePath != null) {
			delta = delta.findMember(resourcePath);
			if (delta != null) {
				int kind = delta.getKind();
				switch (kind) {
				case IResourceDelta.CHANGED:
					if ((IResourceDelta.CONTENT & delta.getFlags()) != 0) {
						long currStamp = delta.getResource().getModificationStamp();
						if (currStamp != modificationStamp) {
							modificationStamp = currStamp;
							fireResourceChanged();
						}
					}
					break;
					
				case IResourceDelta.REMOVED:
					if ((IResourceDelta.MOVED_TO & delta.getFlags()) != 0) {
						handleMove(delta);
					}
					else {
						handleDeletion();
					}
					break;
				}
			}
		}
	}
	
	private class ResourceHandler implements IResourceChangeListener {

		public void resourceChanged(IResourceChangeEvent event) {
			TrackedResource.this.resourceChanged(event);			
		}
	}
}
