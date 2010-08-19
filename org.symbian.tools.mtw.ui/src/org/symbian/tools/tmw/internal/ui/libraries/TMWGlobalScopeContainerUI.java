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
package org.symbian.tools.tmw.internal.ui.libraries;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.common.project.facet.ui.IDecorationsProvider;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.internal.ui.IJsGlobalScopeContainerInitializerExtension;
import org.symbian.tools.tmw.core.TMWCore;
import org.symbian.tools.tmw.core.projects.ITMWProject;
import org.symbian.tools.tmw.core.runtimes.IMobileWebRuntime;

@SuppressWarnings("restriction")
public class TMWGlobalScopeContainerUI implements IJsGlobalScopeContainerInitializerExtension {

    public ImageDescriptor getImage(IPath containerPath, String element, IJavaScriptProject project) {
        final ITMWProject p = TMWCore.create(project.getProject());
        if (p != null) {
            final IMobileWebRuntime runtime = p.getTargetRuntime();
            if (runtime != null) {
                final IRuntime r = RuntimeManager.getRuntime(runtime.getId());
                final IDecorationsProvider decorationsProvider = (IDecorationsProvider) r
                        .getAdapter(IDecorationsProvider.class);
                if (decorationsProvider != null) {
                    return decorationsProvider.getIcon();
                }
            }
        }
        return null;
    }

}
