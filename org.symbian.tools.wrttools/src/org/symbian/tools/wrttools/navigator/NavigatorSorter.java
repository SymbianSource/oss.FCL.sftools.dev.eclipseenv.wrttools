package org.symbian.tools.wrttools.navigator;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.ui.JavaScriptElementComparator;

public class NavigatorSorter extends ViewerSorter {
	private final JavaScriptElementComparator jsComparator = new JavaScriptElementComparator();
	
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 == e2) {
			return 0;
		} else if (e1 == null) { 
			return 1;
		} else if (e2 == null) {
			return -1;
		}
		
		if (e1 instanceof IJavaScriptElement && e2 instanceof IJavaScriptElement) {
			return compareJavaScriptElements(viewer, (IJavaScriptElement) e1, (IJavaScriptElement) e2);
		}
		IResource res1 = getResource(e1);
		IResource res2 = getResource(e2);
		
		if (res1 == res2) {
			return 0;
		} else if (res1 == null) {
			return 1;
		} else if (res2 == null) {
			return -1;
		} else {
			int res1type = res1.getType();
			int res2type = res2.getType();
			if (res1type == res2type) {
				// Note: Files cannot have same name in different case - enforced by Eclipse
				return res1.getName().toLowerCase().compareTo(res2.getName().toLowerCase());
			} else if (res1type == IResource.PROJECT) {
				return -1;
			} else if (res1type == IResource.PROJECT) {
				return 1;
			} else if (res1type == IResource.FOLDER) {
				return -1;
			} else {
				return 1;
			}
		}
	}

	private IResource getResource(Object e1) {
		if (e1 instanceof IResource) {
			return (IResource) e1;
		} else if (e1 instanceof IAdaptable) {
			return (IResource) ((IAdaptable) e1).getAdapter(IResource.class);
		} else {
			return null;
		}
	}

	private int compareJavaScriptElements(Viewer viewer, IJavaScriptElement e1, IJavaScriptElement e2) {
		return jsComparator.compare(viewer, e1, e2);
	}
	
}
