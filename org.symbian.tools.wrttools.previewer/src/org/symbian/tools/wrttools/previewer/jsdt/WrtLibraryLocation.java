package org.symbian.tools.wrttools.previewer.jsdt;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.jsdt.core.compiler.libraries.LibraryLocation;
import org.symbian.tools.wrttools.previewer.PreviewerPlugin;

public class WrtLibraryLocation implements LibraryLocation {
	private static final Path LIBRARIES_PATH = new Path("/preview/script/lib");
	private static final char[][] FILE_NAMES = { 
		"console.js".toCharArray(),
		"device.js".toCharArray(),
		"loader.js".toCharArray(),
		"menu.js".toCharArray(),
		"menuItem.js".toCharArray(),
		"systeminfo.js".toCharArray(),
		"widget.js".toCharArray(),
	};
	
	@Override
	public char[][] getLibraryFileNames() {
		return FILE_NAMES;
	}

	@Override
	public String getLibraryPath(String name) {
		System.out.println(name);
		return null;
	}

	@Override
	public String getLibraryPath(char[] name) {
		URL entry = FileLocator.find(PreviewerPlugin.getDefault().getBundle(), LIBRARIES_PATH.append(new String(name)), null);
		if (entry != null) {
			try {
				URL fileURL = FileLocator.toFileURL(entry);
				final IPath path = new Path(fileURL.getPath());
				return path.toString();
			} catch (IOException e) {
				PreviewerPlugin.log(e);
			}
		}
		return null;
	}

	@Override
	public IPath getLibraryPathInPlugin() {
		return LIBRARIES_PATH;
	}

	@Override
	public IPath getWorkingLibPath() {
		return new Path(getLibraryPath(""));
	}

}
