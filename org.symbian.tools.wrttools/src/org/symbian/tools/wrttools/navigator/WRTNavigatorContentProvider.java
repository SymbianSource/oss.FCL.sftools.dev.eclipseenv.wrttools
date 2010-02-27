package org.symbian.tools.wrttools.navigator;

import java.util.Collection;
import java.util.Comparator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.core.IJavaScriptModel;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.core.JavaScriptModelException;
import org.eclipse.wst.jsdt.internal.ui.navigator.JavaNavigatorContentProvider;
import org.symbian.tools.wrttools.Activator;

@SuppressWarnings({"restriction", "unchecked"})
public class WRTNavigatorContentProvider extends JavaNavigatorContentProvider
		implements ITreeContentProvider {
	private static final class RootResourceFinder implements
			IResourceDeltaVisitor {
		public IResource resource = null;

		private IResource getCommonAncestor(IResource r1, IResource r2) {
			if (r1 == null) {
				return r2;
			}
			if (r2 == null) {
				return null;
			}
			if (r1.equals(r2)) {
				return r1;
			}
			IPath p1 = r1.getFullPath();
			IPath p2 = r2.getFullPath();
			int l1 = p1.segmentCount();
			int l2 = p2.segmentCount();

			if (l1 == 0 || l2 == 0) {
				return null;
			}
			int l = Math.min(l1, l2);
			do {
				p1 = p1.uptoSegment(l);
				p2 = p2.uptoSegment(l);
				l = l - 1;
			} while (l > 0 && (p1 != p2));
			IWorkspaceRoot r = r1.getWorkspace().getRoot();
			return l == 0 ? null : l == 1 ? r.getProject(p1.lastSegment()) : r
					.getFolder(p1);
		}

		public boolean visit(IResourceDelta delta) throws CoreException {
			if ((delta.getKind() & (IResourceDelta.ADDED | IResourceDelta.REMOVED)) != 0) {
				resource = getCommonAncestor(resource, delta.getResource()
						.getParent());
				return false;
			}
			return true;
		}
	}

	private static final class TreeElementsComparator implements
			Comparator<Object> {
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

		private IResource getResource(Object o1) {
			final IResource result;
			if (o1 instanceof IResource) {
				result = (IResource) o1;
			} else if (o1 instanceof IAdaptable) {
				result = (IResource) ((IAdaptable) o1)
						.getAdapter(IResource.class);
			} else {
				result = null;
			}
			return result;
		}

		private boolean isFolder(IResource res1) {
			return res1.getType() == IResource.FOLDER
					|| res1.getType() == IResource.PROJECT;
		}
	}

	private final IResourceChangeListener listener = new IResourceChangeListener() {
		public void resourceChanged(IResourceChangeEvent event) {
			IResource refresh = null;
			if (event.getDelta() != null) {
				RootResourceFinder visitor = new RootResourceFinder();
				try {
					event.getDelta().accept(visitor);
				} catch (CoreException e) {
					Activator.log(e);
					refreshViewer(null);
				}
				refresh = visitor.resource;
			}
			refreshViewer(refresh);
		}
	};
	private Viewer viewer;

	@Override
	public void dispose() {
		if (viewer != null) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(
					listener);
		}
		super.dispose();
	}

	@Override
	protected Object[] getFolderContent(IFolder folder) throws CoreException {
        return folder.members();
	}

	@Override
	protected Object[] getPackageFragmentRoots(IJavaScriptProject project)
			throws JavaScriptModelException {
		try {
            return project.getProject().members();
		} catch (CoreException e) {
			Activator.log(e);
		}
		return new Object[0];
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (this.viewer == null) {
			ResourcesPlugin.getWorkspace().addResourceChangeListener(listener);
		}
		this.viewer = viewer;
		super.inputChanged(viewer, oldInput, newInput);
	}
	//	
	// @SuppressWarnings("unchecked")
	// @Override
	// protected void postAdd(final Object parent, final Object element,
	// Collection runnables) {
	// final Object p;
	// if (element instanceof IProject) {
	// p = null;
	// } else {
	// p = parent;
	// }
	// runnables.add(new Runnable() {
	// @Override
	// public void run() {
	// if (p != null) {
	// ((TreeViewer) viewer).refresh(p);
	// } else {
	// ((TreeViewer) viewer).refresh();
	// }
	// }
	// });
	// }

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

	protected void refreshViewer(final IResource resource) {
		viewer.getControl().getDisplay().asyncExec(new Runnable() {
			public void run() {
				((TreeViewer) viewer).refresh(resource);
			}
		});
	}

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof IFile) {
            IFile file = (IFile) parentElement;
            IJavaScriptElement element = JavaScriptCore.create(file);
            if (element != null) {
                return super.getChildren(element);
            }
        }
        return super.getChildren(parentElement);
    }
}
