package org.symbian.tools.wrttools.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

public class CoreUtil {
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

	public static String readFile(IProject project, String fileName)
			throws CoreException, UnsupportedEncodingException, IOException {
		IFile file = getFile(project, fileName);
		if (file.isAccessible()) {
			InputStream contents = file.getContents();
			final BufferedReader reader = new BufferedReader(
					new InputStreamReader(contents, file.getCharset()));
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
		return null;
	}
}
