package org.chromium.debug.core.model;

import org.chromium.sdk.DebugContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.model.IDebugTarget;

public interface IChromiumDebugTarget extends IDebugTarget {
	DebugContext getDebugContext();
	void fireResumeEvent(int detail);
	JavascriptVmEmbedder getJavascriptEmbedder();
	IResourceManager getResourceManager();
	IProject getDebugProject();
}
