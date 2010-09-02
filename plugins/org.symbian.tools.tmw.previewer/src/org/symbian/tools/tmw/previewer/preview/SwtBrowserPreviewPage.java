package org.symbian.tools.tmw.previewer.preview;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;

public class SwtBrowserPreviewPage extends AbstractPreviewPage implements IPreviewPage {

    public SwtBrowserPreviewPage(IProject project, PreviewView previewView) {
        super(project, previewView);
    }

    @Override
    protected Browser createBrowser(Composite parent) {
        return new Browser(parent, SWT.NONE);
    }
}
