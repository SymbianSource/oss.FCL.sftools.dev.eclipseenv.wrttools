package org.symbian.tools.tmw.previewer.http;

import java.net.URI;

import org.eclipse.core.runtime.CoreException;

public interface IPreviewStartupListener {
    boolean browserRunning(URI uri, String sId) throws CoreException;
}
