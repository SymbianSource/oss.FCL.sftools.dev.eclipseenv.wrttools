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
package org.symbian.tools.tmw.internal.ui.deployment.targets;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchAdapter;

@SuppressWarnings("rawtypes")
public class AdapterFactory implements IAdapterFactory {
    private final IWorkbenchAdapter adapter = new WorkbenchAdapter() {
        @Override
        public ImageDescriptor getImageDescriptor(Object object) {
            if (object instanceof ExternalApplicationDeploymentType) {
                return ImageDescriptor.createFromImageData(((ExternalApplicationDeploymentType) object).getProgram()
                        .getImageData());
            }
            return super.getImageDescriptor(object);
        }

        public String getLabel(Object object) {
            if (object instanceof ExternalApplicationDeploymentType) {
                return ((ExternalApplicationDeploymentType) object).getName();
            }
            return super.getLabel(object);
        };
    };

    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adaptableObject instanceof ExternalApplicationDeploymentType
                && IWorkbenchAdapter.class.isAssignableFrom(adapterType)) {
            return adapter;
        }
        return null;
    }

    public Class[] getAdapterList() {
        return new Class[] { IWorkbenchAdapter.class };
    }

}
