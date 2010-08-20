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
package org.symbian.tools.tmw.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

public final class Images {
    private final String DISCOVER_ICON = "icons/full/obj16/discover.gif";
    private final String EXCLUDED_ICON = "icons/full/obj16/excluded.gif";
    private final String BLUETOOTH_ICON = "icons/full/obj16/bluetooth.gif";

    private final ImageRegistry registry;

    Images(final ImageRegistry registry) {
        this.registry = registry;
        add(registry, DISCOVER_ICON);
        add(registry, EXCLUDED_ICON);
        add(registry, BLUETOOTH_ICON);
    }

    private void add(final ImageRegistry registry, String icon) {
        ImageDescriptor descriptor = TMWCoreUI.imageDescriptorFromPlugin(TMWCoreUI.PLUGIN_ID, icon);
        registry.put(icon, descriptor);
    }

    public Image getDiscoverButtonIcon() {
        return registry.get(DISCOVER_ICON);
    }

    public ImageDescriptor getExcludedIconDescriptor() {
        return registry.getDescriptor(EXCLUDED_ICON);
    }

    public ImageDescriptor getBluetoothImageDescriptor() {
        return registry.getDescriptor(BLUETOOTH_ICON);
    }
}
