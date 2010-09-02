/**
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
 */
package org.symbian.tools.wrttools.wizards.projectimport;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.internal.wizards.datatransfer.ILeveledImportStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;

/**
 * Class declared public only for test suite.
 */
@SuppressWarnings("restriction")
public interface ProjectRecord {
    boolean hasConflicts();
    String getProjectLabel();
    String getProjectName();

    //    URI getLocationURI();
    void setHasConflicts(boolean b);
    ImportOperation getImportOperation(IProject project, ILeveledImportStructureProvider structureProvider,
            IOverwriteQuery query);
}
