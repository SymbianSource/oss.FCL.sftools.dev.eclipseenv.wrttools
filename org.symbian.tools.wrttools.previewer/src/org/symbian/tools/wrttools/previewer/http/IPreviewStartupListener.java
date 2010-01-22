package org.symbian.tools.wrttools.previewer.http;

import java.net.URI;

import org.eclipse.core.runtime.CoreException;

public interface IPreviewStartupListener {
	boolean browserRunning(URI uri) throws CoreException;
}
