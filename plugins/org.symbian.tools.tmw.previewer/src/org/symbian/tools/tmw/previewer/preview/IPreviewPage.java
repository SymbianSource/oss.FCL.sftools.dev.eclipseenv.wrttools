package org.symbian.tools.tmw.previewer.preview;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.part.IPageBookViewPage;

public interface IPreviewPage extends IPageBookViewPage {
    void process(Collection<IFile> files);
    boolean isDisposed();
    void projectRenamed(IPath newPath);
    IProject getProject();
}
