package org.symbian.tools.wrttools.previewer.http;

import java.net.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.symbian.tools.wrttools.previewer.PreviewerPlugin;

public class BrowserConnectionJob extends Job {
	private final IPreviewStartupListener listener;
	private final URI uri;
	
	private boolean ready = false;
	private boolean success = false;

	public BrowserConnectionJob(IPreviewStartupListener listener, URI uri) {
		super("Connecting to WRT preview browser");
		this.listener = listener;
		this.uri = uri;
		setUser(false);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			listener.browserRunning(uri);
			synchronized (this) {
				success = true;
			}
		} catch (CoreException e) {
			synchronized (this) {
				success = false;
			}
			return e.getStatus();
		} finally {
			synchronized (this) {
				ready = true;
			}
		}
		return new Status(IStatus.OK, PreviewerPlugin.PLUGIN_ID, "");
	}

	public synchronized boolean isReady() {
		return ready;
	}
	
	public synchronized boolean isSuccess() {
		return success;
	}
}