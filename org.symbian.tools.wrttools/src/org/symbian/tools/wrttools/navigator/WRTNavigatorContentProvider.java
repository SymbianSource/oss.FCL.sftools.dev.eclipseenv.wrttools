package org.symbian.tools.wrttools.navigator;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.core.IJavaScriptModel;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.core.JavaScriptModelException;
import org.eclipse.wst.jsdt.internal.ui.navigator.JavaNavigatorContentProvider;
import org.symbian.tools.wrttools.Activator;

public class WRTNavigatorContentProvider extends JavaNavigatorContentProvider
		implements ITreeContentProvider {

	private static final class TreeElementsComparator implements Comparator<Object> {
		@Override
		public int compare(Object o1, Object o2) {
			IResource res1 = getResource(o1);
			IResource res2 = getResource(o2);
			if (o1 == o2) {
				return 0;
			} else if (o1 == null) {
				return -1;
			} else if (o2 == null) {
				return 1;
			}
			
			boolean isFolder1 = isFolder(res1);
			boolean isFolder2 = isFolder(res2);
			
			if (isFolder1 == isFolder2) {
				return res1.getName().compareTo(res2.getName());
			} else if (isFolder1) {
				return 1;
			} else {
				return -1;
			}
		}

		private boolean isFolder(IResource res1) {
			return res1.getType() == IResource.FOLDER || res1.getType() == IResource.PROJECT;
		}

		private IResource getResource(Object o1) {
			final IResource result;
			if (o1 instanceof IResource) {
				result = (IResource) o1;
			} else if (o1 instanceof IAdaptable) {
				result = (IResource) ((IAdaptable) o1).getAdapter(IResource.class);
			} else {
				result = null;
			}
			return result;
		}
	}
	
	
	@Override
	protected Object[] getPackageFragmentRoots(IJavaScriptProject project)
			throws JavaScriptModelException {
		try {
			return filter(project.getProject().members());
		} catch (CoreException e) {
			Activator.log(e);
		}
		return new Object[0];
	}
	
	private Object[] filter(IResource[] members) {
		TreeSet<Object> output = new TreeSet<Object>(new TreeElementsComparator());
		for (int i = 0; i < members.length; i++) {
			IResource resource = members[i];
			Object res = resource;
			if (resource.getType() == IResource.FILE) {
				IJavaScriptElement element = JavaScriptCore.create(resource);
				if (element != null) {
					res = element;
				}
			}
			output.add(res);
		}
		return output.toArray();
	}

	@Override
	protected Object[] getFolderContent(IFolder folder) throws CoreException {
		return filter(folder.members());
	}
	
	@Override
	protected void postAdd(Object parent, Object element, Collection runnables) {
		if (parent instanceof IWorkspace) {
			super.postAdd(((IWorkspace) parent).getRoot(), element, runnables);
		} else if (parent instanceof IJavaScriptModel) {
			super.postAdd(((IJavaScriptModel) parent).getWorkspace().getRoot(),
					element, runnables);
		} else {
			super.postAdd(parent, element, runnables);
		}
	}
	
//	@Override
//	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
//		this.viewer = viewer;
//		super.inputChanged(viewer, oldInput, newInput);
//	}
//	
//	@SuppressWarnings("unchecked")
//	@Override
//	protected void postAdd(final Object parent, final Object element, Collection runnables) {
//		final Object p;
//		if (element instanceof IProject) {
//			p = null;
//		} else {
//			p = parent;
//		}
//		runnables.add(new Runnable() {
//			@Override
//			public void run() {
//				if (p != null) {
//					((TreeViewer) viewer).refresh(p);
//				} else {
//					((TreeViewer) viewer).refresh();
//				}
//			}
//		});
//	}
}
