package org.symbian.tools.wrttools.core.libraries;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IWrtIdeContainer {
	void populateProject(IProject project, IProgressMonitor monitor) throws IOException, CoreException;
}
