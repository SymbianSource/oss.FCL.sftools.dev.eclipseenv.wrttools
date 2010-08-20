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
package org.symbian.tools.tmw.core.internal.runtimes;

import java.net.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.jsdt.core.IIncludePathEntry;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.IJsGlobalScopeContainer;
import org.eclipse.wst.jsdt.core.IJsGlobalScopeContainerInitializer;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.core.JsGlobalScopeContainerInitializer;
import org.eclipse.wst.jsdt.core.compiler.libraries.LibraryLocation;
import org.symbian.tools.tmw.core.TMWCore;

public class MobileRuntimeLibraryContainerInitializer extends JsGlobalScopeContainerInitializer implements
        IJsGlobalScopeContainerInitializer {

    public boolean allowAttachJsDoc() {
        return false;
    }

    public boolean canUpdateJsGlobalScopeContainer(IPath containerPath, IJavaScriptProject project) {
        return true;
    }

    public String[] containerSuperTypes() {
        return new String[0];
    }

    public Object getComparisonID(IPath containerPath, IJavaScriptProject project) {
        return containerPath.append(project.getElementName());
    }

    @Override
    public String getDescription() {
        return "Project library with mobile web runtime APIs";
    }

    public String getDescription(IPath containerPath, IJavaScriptProject project) {
        return getDescription();
    }

    public IJsGlobalScopeContainer getFailureContainer(final IPath containerPath, IJavaScriptProject project) {
        return new IJsGlobalScopeContainer() {

            public String getDescription() {
                final String description = MobileRuntimeLibraryContainerInitializer.this.getDescription();
                return description;
            }

            public IIncludePathEntry[] getIncludepathEntries() {
                return new IIncludePathEntry[0];
            }

            public int getKind() {
                return K_APPLICATION;
            }

            public IPath getPath() {
                return containerPath;
            }

            public String[] resolvedLibraryImport(String a) {
                return new String[0];
            }
        };
    }

    public URI getHostPath(IPath path, IJavaScriptProject project) {
        return null;
    }

    public String getInferenceID() {
        return null;
    }

    public LibraryLocation getLibraryLocation() {
        return null;
    }

    public void initialize(IPath containerPath, IJavaScriptProject project) throws CoreException {
        if (!project.isOpen() || TMWCore.create(project.getProject()) != null) {
            JavaScriptCore.setJsGlobalScopeContainer(containerPath, new IJavaScriptProject[] { project },
                    new IJsGlobalScopeContainer[] { new MobileRuntimeLibraryContainer(project, containerPath) }, null);
        }
    }

    public void removeFromProject(IJavaScriptProject project) {
        // Do nothing
    }

    public void requestJsGlobalScopeContainerUpdate(IPath containerPath, IJavaScriptProject project,
            IJsGlobalScopeContainer containerSuggestion) throws CoreException {
        // TODO
        System.out.println("abc");
    }
}
