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
package org.symbian.tools.wrttools.core.project;

import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IProject;
import org.symbian.tools.tmw.core.projects.IMTWProject;
import org.symbian.tools.tmw.core.projects.IMTWProjectProvider;
import org.symbian.tools.wrttools.WRTProject;
import org.symbian.tools.wrttools.util.ProjectUtils;

public class WRTIDEProjectProvider implements IMTWProjectProvider {
    private final Map<IProject, IMTWProject> projects = new WeakHashMap<IProject, IMTWProject>();

    public IMTWProject create(IProject project) {
        IMTWProject p = projects.get(project);
        if (p == null) {
            p = new WRTProject(project);
            projects.put(project, p);
        }
        return p;
    }

    public boolean isSupportedProject(IProject project) {
        return ProjectUtils.hasWrtNature(project);
    }
}
