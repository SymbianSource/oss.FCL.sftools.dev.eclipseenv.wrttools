package org.symbian.tools.wrttools.core.libraries;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.JsGlobalScopeContainerInitializer;
import org.eclipse.wst.jsdt.core.compiler.libraries.LibraryLocation;
import org.osgi.framework.Bundle;
import org.symbian.tools.wrttools.Activator;

public class WrtLibInitializer extends JsGlobalScopeContainerInitializer {

    public static class WrtLocation implements LibraryLocation {
        private static final WrtLocation LOCATION = new WrtLocation();
		
		public char[][] getLibraryFileNames() {
			return convert(getFiles());
		}

		private char[][] convert(String[] files) {
			final Set<char[]> set = new HashSet<char[]>();
			for (String string : files) {
				if (string.endsWith(".js")) {
					set.add(string.toCharArray());
				}
			}
			return set.toArray(new char[set.size()][]);
		}

		public IPath getLibraryPathInPlugin() {
            return new Path("/libraries/core");
		}
		
        public static WrtLocation getInstance() {
			return LOCATION;
		}
		
		public String[] getFiles() {
			Bundle bundle = Activator.getDefault().getBundle();
			String path = getLibraryPathInPlugin().toString();

			final Set<String> set = getEntries(bundle, path);
			return set.toArray(new String[set.size()]);
		}

		@SuppressWarnings("unchecked")
		private Set<String> getEntries(Bundle bundle, String p) {
			final Set<String> set = new TreeSet<String>();
			Enumeration entries = bundle.getEntryPaths(p);
			while (entries.hasMoreElements()) {
				String path = (String) entries.nextElement();
				if (path.endsWith("/")) {
					set.addAll(getEntries(bundle, path));
				} else {
					set.add(path.substring(getLibraryPathInPlugin().toString().length()));
				}
			}
			return set;
		}

		public String getLibraryPath(String name) {
			System.out.println(name);
			return null;
		}

		public String getLibraryPath(char[] name) {
			Bundle bundle = Activator.getDefault().getBundle();
			URL url = FileLocator.find(bundle, getLibraryPathInPlugin().append(new String(name)), null);
			try {
				URL fileURL = FileLocator.toFileURL(url);
				return fileURL.getPath();
			} catch (IOException e) {
				Activator.log(e);
			}
			return null;
		}

		public IPath getWorkingLibPath() {
			System.out.println();
			return null;
		}
	}

	public LibraryLocation getLibraryLocation() {
        return WrtLocation.getInstance();
	}

	@Override
	public String getDescription() {
        return "WebRuntime Toolkit Support Library";
	}
	
	@Override
	public String getDescription(IPath containerPath, IJavaScriptProject project) {
		return containerPath.lastSegment();
	}

    @Override
    public String[] containerSuperTypes() {
        return new String[] { "Window" };
    }
}
