package org.symbian.tools.wrttools.debug.internal.model;

import org.chromium.debug.core.model.Value;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.jsdt.ui.JavaScriptUI;

public class SymbianDebugModelPresentation extends LabelProvider implements
		IDebugModelPresentation {

	public void setAttribute(String attribute, Object value) {
	}

	@Override
	public Image getImage(Object element) {
		// use default image
		return null;
	}

	@Override
	public String getText(Object element) {
		// use default label text
		return null;
	}

	public void computeDetail(IValue value, IValueDetailListener listener) {
		String detail = ""; //$NON-NLS-1$
		if (value instanceof Value) {
			// Avoid quoting string JavaScript values by getting the value
			// string
			// from the underlying JsValue.
			detail = ((Value) value).getJsValue().getValueString();
		}

		listener.detailComputed(value, detail);
	}

	public IEditorInput getEditorInput(Object element) {
		return toEditorInput(element);
	}

	public static IEditorInput toEditorInput(Object element) {
		if (element instanceof IFile) {
			return new FileEditorInput((IFile) element);
		}
		if (element instanceof IFileStore) {
			return new FileStoreEditorInput((IFileStore) element);
		}

		if (element instanceof ILineBreakpoint) {
			return new FileEditorInput((IFile) ((ILineBreakpoint) element)
					.getMarker().getResource());
		}

		return null;
	}

	public String getEditorId(IEditorInput input, Object element) {
		if (input instanceof IFileEditorInput) {
			IFile file;
			if (element instanceof IFile) {
				file = (IFile) element;
			} else if (element instanceof IBreakpoint) {
				IBreakpoint breakpoint = (IBreakpoint) element;
				IResource resource = breakpoint.getMarker().getResource();
				// Can the breakpoint resource be folder or project? Better
				// check for it.
				if (resource instanceof IFile == false) {
					return null;
				}
				file = (IFile) resource;
			} else {
				return null;
			}

			// Pick the editor based on the file extension, taking user
			// preferences into account.
			try {
				return IDE.getEditorDescriptor(file).getId();
			} catch (PartInitException e) {
				// TODO(peter.rybin): should it really be the default case?
				// There might be no virtual project.
				return null;
			}
		} else {
			if (element instanceof IFileStore) {
				IFileStore store = (IFileStore) element;
				String ext = new Path(store.getName()).getFileExtension();
				if (ext.equals("js")) {
					return JavaScriptUI.ID_CU_EDITOR;
				} else {
					return "org.eclipse.wst.html.core.htmlsource.source";
				}
			}
		}
		return null;
	}
}
