package org.symbian.tools.wrttools.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.symbian.tools.wrttools.Activator;

public class CoreUtil {
	public static final String METADATA_FILE = "Info.plist";
	public static final String PROPERTY_PATTERN = "<key>\\s*{0}\\s*</key>\\s*<string>\\s*(.*)\\s*</string>";

	public static boolean isWindows() {
		return "windows".equals(Platform.getOS());
	}

	public static boolean isMac() {
		return "macosx".equals(Platform.getOS());
	}

	public static boolean isLinux() {
		return "linux".equals(Platform.getOS());
	}

	public static String getIndexFileName(String buffer) {
		if (buffer != null) {
			Matcher matcher = getPropertyLookupPattern("MainHTML").matcher(buffer);
			if (matcher.find()) {
				return matcher.group(1);
			}
		}
		return null;
	}

    public static String getApplicationName(String buffer) {
        if (buffer != null) {
            Matcher matcher = getPropertyLookupPattern("DisplayName").matcher(buffer);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }

	public static Pattern getPropertyLookupPattern(String propertyName) {
		return Pattern.compile(MessageFormat.format(PROPERTY_PATTERN, propertyName), Pattern.CASE_INSENSITIVE);
	}

	public static IRegion getIndexFileNameRegion(String string) {
		Matcher matcher = getPropertyLookupPattern("MainHTML").matcher(string);
		if (matcher.find()) {
			int start = matcher.start(1);
			return new Region(start, matcher.end(1) - start);
		}
		return null;
	}

	public static IFile getFile(IProject project, String fileName) throws CoreException {
		String n = fileName.toLowerCase();
		IResource[] members = project.members();
		for (IResource iResource : members) {
			if (iResource.getType() == IResource.FILE
					&& n.equals(iResource.getName().toLowerCase())
					&& iResource.isAccessible()) {
				return (IFile) iResource;
			}
		}
		return null;
	}

	public static String readFile(IProject project, IFile file)
			throws CoreException {
		try {
			if (file != null && file.isAccessible()) {
				final BufferedReader reader = new BufferedReader(
						new InputStreamReader(file.getContents(), file.getCharset()));
				return read(reader);
			}
			return null;
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, MessageFormat.format("Failed to read file {0} in project {1}", file.getName(), project.getName())));
		}
	}

    public static String read(final Reader reader) throws IOException {
        StringBuffer buffer = new StringBuffer();
        try {
        	int c = 0;
        	char[] buf = new char[4096];
        	while ((c = reader.read(buf)) > 0) {
        		buffer.append(buf, 0, c);
        	}
        	return buffer.toString();
        } finally {
        	reader.close();
        }
    }

	private static final Map<IProject, IndexFileRecord> INDEX_FILES = new HashMap<IProject, IndexFileRecord>();
	
	public static synchronized String getIndexFile(IProject project) throws CoreException {
		// There will really be a lot of calls to this method. We need to cache values.
		IFile file = getFile(project, METADATA_FILE);
		if (file == null) {
			return null;
		}
		if (INDEX_FILES.containsKey(project)) {
			IndexFileRecord record = INDEX_FILES.get(project);
			if (file == null || !file.isAccessible()) {
				throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, MessageFormat.format("No Info.plist for project {0}", project.getName())));
			}
			if (record.timeStamp == file.getModificationStamp()) {
				return record.fileName;
			}
		}
		String fileName = getIndexFileName(readFile(project, file));
		INDEX_FILES.put(project, new IndexFileRecord(fileName, file.getModificationStamp()));
		return fileName;
	}
	
	private static class IndexFileRecord {
		public final String fileName;
		public final long timeStamp;
		
		public IndexFileRecord(String fileName, long timeStamp) {
			this.fileName = fileName;
			this.timeStamp = timeStamp;
		}
	}
}
