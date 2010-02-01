package org.symbian.tools.wrttools.util;

import org.eclipse.core.runtime.Platform;

public class CoreUtil {

	public static boolean isWindows() {
		return "windows".equals(Platform.getOS());
	}

	public static boolean isMac() {
		return "macosx".equals(Platform.getOS());
	}

	public static boolean isLinux() {
		return "linux".equals(Platform.getOS());
	}
}
