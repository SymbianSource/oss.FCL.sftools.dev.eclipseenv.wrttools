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
package org.symbian.tools.tmw.internal.ui.deployment;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter2;

public class TargetWorkbenchAdapter implements IWorkbenchAdapter, IWorkbenchAdapter2 {
    /**
     * 
     */
    private final DeploymentTargetWrapper deploymentTargetWrapper;

    /**
     * @param deploymentTargetWrapper
     */
    TargetWorkbenchAdapter(DeploymentTargetWrapper deploymentTargetWrapper) {
        this.deploymentTargetWrapper = deploymentTargetWrapper;
    }

    public String getLabel(Object object) {
        return this.deploymentTargetWrapper.getName();
    }

    public ImageDescriptor getImageDescriptor(Object object) {
        return this.deploymentTargetWrapper.getType().getImageDescriptor();
    }

    public RGB getForeground(Object element) {
        // TODO Auto-generated method stub
        return null;
    }

    public RGB getBackground(Object element) {
        // TODO Auto-generated method stub
        return null;
    }

    public FontData getFont(Object element) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object[] getChildren(Object o) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getParent(Object o) {
        // TODO Auto-generated method stub
        return null;
    }
}