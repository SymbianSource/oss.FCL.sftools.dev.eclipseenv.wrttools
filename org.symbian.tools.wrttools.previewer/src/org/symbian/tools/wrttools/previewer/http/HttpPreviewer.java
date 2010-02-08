package org.symbian.tools.wrttools.previewer.http;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

public class HttpPreviewer {

	public URI previewProject(IProject project) {
		WebAppInterface.getInstance(); // Ensure server is up
		try {
			return new URI(WorkspaceResourcesServlet.getPreviewerStartingPage(project.getName()));
		} catch (URISyntaxException e) {
			return null;
		}
	}

	public IFile getFileFromUrl(String name) {
		return WorkspaceResourcesServlet.getFileFromUrl(name);
	}

	public String getHttpUrl(IResource resource) {
		return WorkspaceResourcesServlet.getHttpUrl(resource);
	}

	public URI previewProject(IProject project, IPreviewStartupListener listener) {
		if (listener == null) {
			return previewProject(project);
		} else {
			return WebAppInterface.getInstance().prepareDebugger(project, listener);
		}
	}

	public File getLocalFile(String name) {
		return WorkspaceResourcesServlet.getPreviewerResource(name);
		
	}

}
