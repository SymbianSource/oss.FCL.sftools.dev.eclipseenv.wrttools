package org.symbian.tools.wrttools.previewer.jsdt;

import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.JsGlobalScopeContainerInitializer;
import org.eclipse.wst.jsdt.core.compiler.libraries.LibraryLocation;

public class WrtContainerInitializer extends JsGlobalScopeContainerInitializer {
	public static final String CONTAINER_ID = "org.symbian.wrt";
	
	@Override
	public LibraryLocation getLibraryLocation() {
		return new WrtLibraryLocation();
	}
	
	@Override
	public String getDescription() {
		return "WebRuntime Support Library";
	}
	
	@Override
	public String getDescription(IPath containerPath, IJavaScriptProject project) {
		return getDescription();
	}
}
