package org.symbian.tools.wrttools.core;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.symbian.tools.wrttools.Activator;

public class WrtIdeCorePreferences extends AbstractPreferenceInitializer {
    public static final String WGZ_IMPORT_PATH = "wgz.import.path";

    @Override
    public void initializeDefaultPreferences() {
        Activator.getDefault().getPreferenceStore().setDefault(WGZ_IMPORT_PATH, System.getProperty("user.home"));
    }

}
