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
package org.symbian.tools.wrttools.core.libraries.jsdt;

import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;

import org.eclipse.wst.jsdt.core.infer.IInferenceFile;
import org.eclipse.wst.jsdt.core.infer.InferEngine;
import org.eclipse.wst.jsdt.core.infer.InferrenceProvider;
import org.eclipse.wst.jsdt.core.infer.RefactoringSupport;
import org.eclipse.wst.jsdt.core.infer.ResolutionConfiguration;

public class PhoneGapInferrenceProvider implements InferrenceProvider {
    public static final String ID = "org.symbian.tools.wrttools.phonegap";
    private static final Collection<String> PHONEGAP_TYPES = new TreeSet<String>(Arrays.asList("Acceleration",
            "AccelerationOptions", "Accelerometer", "Camera", "DeviceError", "Contacts", "Contact", "Geolocation",
            "PositionOptions", "Coordinates", "Media", "Notification", "Orientation", "Position", "PositionError",
            "Sms", "Storage"));

    public int applysTo(IInferenceFile scriptFile) {
        //        String path = String.valueOf(scriptFile.getFileName());
        //
        //        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path));
        //        if (file != null && file.isAccessible()) {
        //            IJavaScriptUnit unit = (IJavaScriptUnit) JavaScriptCore.create(file);
        //            try {
        //                IType[] types = unit.getAllTypes();
        //                int typeCount = 0;
        //                for (IType type : types) {
        //                    if (PHONEGAP_TYPES.contains(type.getElementName())) {
        //                        typeCount += 1;
        //                    }
        //                }
        //                if (typeCount > 1) {
        //                    return ONLY_THIS;
        //                }
        //            } catch (JavaScriptModelException e) {
        //                Activator.log(e);
        //            }
        //        }
        return NOT_THIS;
    }

    public String getID() {
        return ID;
    }

    public InferEngine getInferEngine() {
        final InferEngine engine = new PhoneGapInferEngine();
        engine.inferenceProvider = this;
        return engine;
    }

    public RefactoringSupport getRefactoringSupport() {
        // TODO Auto-generated method stub
        return null;
    }

    public ResolutionConfiguration getResolutionConfiguration() {
        final ResolutionConfiguration configuration = new ResolutionConfiguration();
        return configuration;
    }

}
