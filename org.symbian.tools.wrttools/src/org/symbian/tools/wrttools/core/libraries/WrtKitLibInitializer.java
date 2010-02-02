package org.symbian.tools.wrttools.core.libraries;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.JsGlobalScopeContainerInitializer;
import org.eclipse.wst.jsdt.core.compiler.libraries.LibraryLocation;
import org.osgi.framework.Bundle;
import org.symbian.tools.wrttools.Activator;

public class WrtKitLibInitializer extends JsGlobalScopeContainerInitializer implements IWrtIdeContainer {

	public static class WrtKitLocation implements LibraryLocation {
		private static final WrtKitLocation LOCATION = new WrtKitLocation();
		
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
			return new Path("/projecttemplates/WRTKit");
		}
		
		public static WrtKitLocation getInstance() {
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
		return WrtKitLocation.getInstance();
	}

	@Override
	public String getDescription() {
		return "WRTKit Support Library";
	}
	
	@Override
	public String getDescription(IPath containerPath, IJavaScriptProject project) {
		return containerPath.lastSegment();
	}

	public void populateProject(IProject project,
			IProgressMonitor monitor) throws IOException, CoreException {
		WrtKitLocation location = WrtKitLocation.getInstance();
		String[] files = location.getFiles();
		Bundle bundle = Activator.getDefault().getBundle();
		monitor.beginTask("Copying library entries", files.length);
		for (String file : files) {
			Path path = new Path(file);
			InputStream stream = FileLocator.openStream(bundle, location.getLibraryPathInPlugin().append(path), false);
			try {
				IFile f = project.getFile(new Path("WRTKit").append(path));
				create(f, stream);
			} finally {
				stream.close();
			}
			monitor.worked(1);
		}
	}

	private void create(IFile f, InputStream stream) throws CoreException {
		IContainer container = f.getParent();
		createContainer(container);
		f.create(stream, false, new NullProgressMonitor());
	}

	private void createContainer(IContainer container) throws CoreException {
		if (!container.exists()) {
			createContainer(container.getParent());
			((IFolder) container).create(false, true, new NullProgressMonitor());
		}
	}
	
}
