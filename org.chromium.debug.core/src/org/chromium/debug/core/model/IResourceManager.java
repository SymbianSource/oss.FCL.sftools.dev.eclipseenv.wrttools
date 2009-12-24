package org.chromium.debug.core.model;

import org.chromium.sdk.Script;
import org.eclipse.core.resources.IFile;

public interface IResourceManager {
	IFile getResource(Script script);
	void addScript(Script script);
	boolean isAddingFile(IFile file);
	Script getScript(IFile file);
	void clear();
}
