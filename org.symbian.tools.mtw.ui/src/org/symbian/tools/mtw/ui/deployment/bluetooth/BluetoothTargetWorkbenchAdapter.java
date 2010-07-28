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
package org.symbian.tools.mtw.ui.deployment.bluetooth;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter2;
import org.symbian.tools.mtw.ui.MTWCoreUI;

public class BluetoothTargetWorkbenchAdapter implements IWorkbenchAdapter, IWorkbenchAdapter2 {
    public RGB getForeground(Object element) {
        final BluetoothTarget target = (BluetoothTarget) element;
        if (!target.isDiscovered()) {
            return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay()
                    .getSystemColor(SWT.COLOR_DARK_GRAY).getRGB();
        }
        return null;
    }

    public RGB getBackground(Object element) {
        return null;
    }

    public FontData getFont(Object element) {
        return null;
    }

    public Object[] getChildren(Object o) {
        return null;
    }

    public ImageDescriptor getImageDescriptor(Object object) {
        return MTWCoreUI.getImages().getBluetoothImageDescriptor();
    }

    public String getLabel(Object o) {
        final BluetoothTarget target = (BluetoothTarget) o;
        return target.getName();
    }

    public Object getParent(Object o) {
        return null;
    }

}
