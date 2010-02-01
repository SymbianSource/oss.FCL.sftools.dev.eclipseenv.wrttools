package org.symbian.tools.wrttools.previewer.preview;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.part.IPageBookViewPage;

public interface IPreviewPage extends IPageBookViewPage {
	void process(Collection<IFile> files);
	boolean isDisposed();
}
