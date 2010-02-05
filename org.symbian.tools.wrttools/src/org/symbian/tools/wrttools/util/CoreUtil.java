package org.symbian.tools.wrttools.util;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
}
