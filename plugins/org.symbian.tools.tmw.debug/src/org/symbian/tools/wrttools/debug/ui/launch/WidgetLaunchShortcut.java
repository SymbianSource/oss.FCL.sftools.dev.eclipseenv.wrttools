/*******************************************************************************
 * Copyright (c) 2009 Symbian Foundation and/or its subsidiary(-ies).
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
 *******************************************************************************/
package org.symbian.tools.wrttools.debug.ui.launch;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut2;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.symbian.tools.wrttools.debug.internal.Activator;
import org.symbian.tools.wrttools.debug.internal.IConstants;
import org.symbian.tools.wrttools.debug.internal.launch.WidgetLaunchDelegate;

public class WidgetLaunchShortcut implements ILaunchShortcut2 {

    public IResource getLaunchableResource(IEditorPart editorpart) {
        IEditorInput input = editorpart.getEditorInput();
        return (IResource) input.getAdapter(IResource.class);
    }

    public IResource getLaunchableResource(ISelection selection) {
        if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
            Object object = ((IStructuredSelection) selection).getFirstElement();
            IResource resource = null;
            if (object instanceof IResource) {
                resource = (IResource) object;
            } else if (object instanceof IAdaptable) {
                resource = (IResource) ((IAdaptable) object).getAdapter(IResource.class);
            }
            return resource;
        }
        return null;
    }

    public ILaunchConfiguration[] getLaunchConfigurations(IEditorPart editorpart) {
        return getLaunchConfigurations(getLaunchableResource(editorpart));
    }

    private ILaunchConfiguration getLaunchConfigurations(IProject project) throws CoreException {
        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
        ILaunchConfigurationType type = launchManager.getLaunchConfigurationType(WidgetLaunchDelegate.ID);
        ILaunchConfiguration configuration = null;
        ILaunchConfiguration[] configurations = launchManager.getLaunchConfigurations(type);
        for (ILaunchConfiguration c : configurations) {
            if (project.getName().equals(c.getAttribute(IConstants.PROP_PROJECT_NAME, (String) null))) {
                configuration = c;
                break;
            }
        }
        return configuration;
    }

    private ILaunchConfiguration[] getLaunchConfigurations(IResource resource) {
        if (resource != null) {
            try {
                ILaunchConfiguration launchConfigurations = getLaunchConfigurations(resource.getProject());
                if (launchConfigurations != null) {
                    return new ILaunchConfiguration[] { launchConfigurations };
                }
            } catch (CoreException e) {
                Activator.log(e);
            }
        }
        return null;
    }

    public ILaunchConfiguration[] getLaunchConfigurations(ISelection selection) {
        return getLaunchConfigurations(getLaunchableResource(selection));
    }

    public void launch(IEditorPart editor, String mode) {
        launch(getLaunchableResource(editor), mode);
    }

    private void launch(IResource launchableResource, String mode) {
        try {
            IProject project = launchableResource.getProject();
            ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
            ILaunchConfigurationType type = launchManager.getLaunchConfigurationType(WidgetLaunchDelegate.ID);
            ILaunchConfiguration configuration = getLaunchConfigurations(project);
            if (configuration == null) {
                ILaunchConfigurationWorkingCopy copy = type.newInstance(null, launchManager
                        .generateLaunchConfigurationName(project.getName()));
                copy.setAttribute(IConstants.PROP_PROJECT_NAME, project.getName());
                copy.setMappedResources(new IResource[] { project });
                configuration = copy.doSave();
            }
            DebugUITools.launch(configuration, mode);
        } catch (CoreException e) {
            Activator.log(e);
        }
    }

    public void launch(ISelection selection, String mode) {
        launch(getLaunchableResource(selection), mode);
    }
}
