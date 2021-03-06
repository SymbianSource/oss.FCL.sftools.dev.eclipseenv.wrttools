/**
 * Copyright (c) 2010 Symbian Foundation and/or its subsidiary(-ies).
 * All rights reserved.
 * This component and the accompanying materials are made available
 * under the terms of the License "Eclipse Public License v1.0"
 * which accompanies this distribution, and is available
 * at the URL "http://www.eclipse.org/legal/epl-v10.html".
 *
 * Initial Contributors:
 * Symbian Foundation - initial contribution.
 * Contributors:
 * Description:
 * Overview:
 * Details:
 * Platforms/Drives/Compatibility:
 * Assumptions/Requirement/Pre-requisites:
 * Failures and causes:
 */
package org.symbian.tools.wrttools.core;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.symbian.tools.wrttools.Activator;

public final class WRTImages {
    private static final String IMAGE_IMPORT_WIZARD_BANNER = "import_wizard_banner.png";
    private static final String IMAGE_IMPORT_WGZ_BANNER = "import_w_banner.png";
    private static final String IMAGE_NEW_WIZARD_BANNER = "WRT_wizard_banner.png";
    private static final String IMAGE_EMULATOR = "deploy_widget.gif";
    //    private static final String IMAGE_BLUETOOTH = "bluetooth.gif";
    private static final String IMAGE_EXCLUDED = "excluded.gif";
    private static final String IMAGE_WRTKIT = "main16.gif";

    public static void init(ImageRegistry reg) {
        add(reg, IMAGE_IMPORT_WIZARD_BANNER);
        add(reg, IMAGE_IMPORT_WGZ_BANNER);
        add(reg, IMAGE_NEW_WIZARD_BANNER);
        add(reg, IMAGE_EMULATOR);
        //        add(reg, IMAGE_BLUETOOTH);
        add(reg, IMAGE_EXCLUDED);
        add(reg, IMAGE_WRTKIT);
    }

    private static void add(ImageRegistry reg, String key) {
        reg.put(key, Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "/icons/" + key));
    }

    public static ImageDescriptor importWizardBanner() {
        return Activator.getDefault().getImageRegistry().getDescriptor(IMAGE_IMPORT_WIZARD_BANNER);
    }

    public static ImageDescriptor importWgzWizardBanner() {
        return Activator.getDefault().getImageRegistry().getDescriptor(IMAGE_IMPORT_WGZ_BANNER);
    }

    public static ImageDescriptor newWizardBanner() {
        return Activator.getDefault().getImageRegistry().getDescriptor(IMAGE_NEW_WIZARD_BANNER);
    }

    public static Image getEmulatorImage() {
        return Activator.getDefault().getImageRegistry().get(IMAGE_EMULATOR);
    }

    //    public static Image getBluetoothImage() {
    //        return Activator.getDefault().getImageRegistry().get(IMAGE_BLUETOOTH);
    //    }

    public static Image getExcludedImage() {
        return Activator.getDefault().getImageRegistry().get(IMAGE_EXCLUDED);
    }

    public static ImageDescriptor getExcludedImageDescriptor() {
        return Activator.getDefault().getImageRegistry().getDescriptor(IMAGE_EXCLUDED);
    }

    public static Image getWrtKitIcon() {
        return Activator.getDefault().getImageRegistry().get(IMAGE_WRTKIT);
    }

    private WRTImages() {
        // No instantiation
    }
}
