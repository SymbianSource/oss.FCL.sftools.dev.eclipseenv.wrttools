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
package org.symbian.tools.wrttools.core.libraries;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.jsdt.core.infer.IInferenceFile;
import org.eclipse.wst.jsdt.core.infer.InferEngine;
import org.eclipse.wst.jsdt.core.infer.InferrenceProvider;
import org.eclipse.wst.jsdt.core.infer.RefactoringSupport;
import org.eclipse.wst.jsdt.core.infer.ResolutionConfiguration;
import org.symbian.tools.tmw.core.TMWCore;

public class PlatformServicesTypeProvider implements InferrenceProvider {
    public static final String ID = "org.symbian.tools.wrttools.platformservices";

    public int applysTo(IInferenceFile scriptFile) {
        char[] fileName = scriptFile.getFileName();
        if (fileName != null) {
            Path path = new Path(String.valueOf(fileName));
            if (path.segmentCount() > 1) {
                IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
                if (file.exists()) {
                    IProject project = file.getProject();
                    if (TMWCore.create(project) != null) {
                        return InferrenceProvider.MAYBE_THIS;
                    }
                }
            }
        }
        return InferrenceProvider.NOT_THIS;
    }

    public String getID() {
        return ID;
    }

    public InferEngine getInferEngine() {
        InferEngine engine = new WRTInferEngine();
        engine.inferenceProvider = this;
        return engine;
    }

    public RefactoringSupport getRefactoringSupport() {
        // TODO Auto-generated method stub
        return null;
    }

    public ResolutionConfiguration getResolutionConfiguration() {
        return new ResolutionConfiguration();
    }

}
