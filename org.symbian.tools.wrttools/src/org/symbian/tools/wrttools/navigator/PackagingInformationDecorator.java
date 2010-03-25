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
package org.symbian.tools.wrttools.navigator;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.symbian.tools.wrttools.core.WRTImages;
import org.symbian.tools.wrttools.util.ProjectUtils;

public class PackagingInformationDecorator implements ILightweightLabelDecorator {

    public void decorate(Object element, IDecoration decoration) {
        IResource resource = null;
        if (element instanceof IResource) {
            resource = (IResource) element;
        } else if (element instanceof IAdaptable) {
            resource = (IResource) ((IAdaptable) element).getAdapter(IResource.class);
        }
        if (resource != null && ProjectUtils.isExcluded(resource)) {
            decoration.addOverlay(WRTImages.getExcludedImageDescriptor(), IDecoration.TOP_RIGHT);
        }
    }

    public void addListener(ILabelProviderListener listener) {
        // Do nothing

    }

    public void dispose() {
        // Do nothing

    }

    public boolean isLabelProperty(Object element, String property) {
        // Do nothing
        return false;
    }

    public void removeListener(ILabelProviderListener listener) {
        // Do nohing

    }

}
