package org.symbian.tools.tmw.debug.internal.model;

import java.util.Collection;

import org.chromium.debug.core.model.Value;
import org.chromium.sdk.JsValue;
import org.chromium.sdk.JsVariable;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
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
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.internal.ui.javaeditor.JarEntryEditorInput;
import org.eclipse.wst.jsdt.ui.JavaScriptUI;

@SuppressWarnings("restriction")
public class SymbianDebugModelPresentation extends LabelProvider implements
		IDebugModelPresentation {
    private static final int DETAILS_DEPTH = 2;

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
            final JsValue jsValue = ((Value) value).getJsValue();
            detail = printJSON(jsValue, 0);
        }

        listener.detailComputed(value, detail);
    }

    private String printJSON(final JsValue value, int depth) {
        if (depth < DETAILS_DEPTH) {
            switch (value.getType()) {
            case TYPE_OBJECT:
                return printObject(value, depth);
            case TYPE_ARRAY:
                return printArray(value, depth);
            }
        }
        if (depth > 0) {
            if (value.getType() == JsValue.Type.TYPE_STRING) {
                return "\"" + value.getValueString() + "\"";
            }
        }
        return value.getValueString();
    }

    private String printArray(JsValue value, int depth) {
        final StringBuilder builder = new StringBuilder("{ ");
        String sep = "";

        Collection<? extends JsVariable> properties = value.asObject().asArray().toSparseArray().values();

        for (JsVariable jsVariable : properties) {
            builder.append(sep).append(printJSON(jsVariable.getValue(), depth + 1));
            sep = ", ";
        }

        builder.append(" }");
        return builder.toString();
    }

    private String printObject(final JsValue value, final int depth) {
        final StringBuilder builder = new StringBuilder("{ ");

        String sep = "";

        Collection<? extends JsVariable> properties = value.asObject().getProperties();
        for (JsVariable jsVariable : properties) {
            builder.append(sep).append(jsVariable.getName()).append(" : ");
            builder.append(printJSON(jsVariable.getValue(), depth + 1));
            sep = ", ";
        }

        builder.append(" }");
        return builder.toString();
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
        if (element instanceof IStorage) {
            return new JarEntryEditorInput((IStorage) element);
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
            if (element instanceof IStorage) {
                IStorage store = (IStorage) element;
                if (JavaScriptCore.isJavaScriptLikeFileName(store.getName())) {
                    return JavaScriptUI.ID_CU_EDITOR;
				} else {
					return "org.eclipse.wst.html.core.htmlsource.source";
				}
			}
		}
		return null;
	}
}
