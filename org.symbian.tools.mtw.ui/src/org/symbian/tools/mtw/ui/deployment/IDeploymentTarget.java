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
package org.symbian.tools.mtw.ui.deployment;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.IMemento;
import org.symbian.tools.mtw.core.projects.IMTWProject;
import org.symbian.tools.mtw.core.runtimes.IMobileWebRuntime;

/**
 * <p>This is particular deployment target instance. It can be a Bluetooth phone
 * connection, installed device emulator, FTP server, etc.</p>
 * 
 * <p>Targets can be cached and {@link #setProject(WRTProject)} will be called 
 * if active project was changed. Targets are single-threaded. This class
 * methods can be called from non-SWT thread.</p>
 * 
 * <p>Following adapters can be provided by either overriding getAdapter method 
 * or by contributing to org.eclipse.core.runtime.adapters extension point:</p>
 * <ul><li>{@link org.eclipse.ui.IPersistable} - to persist advanced per-
 * project target configuration</li><li>
 * {@link org.eclipse.ui.model.IWorkbenchAdapter} or 
 * {@link org.eclipse.ui.model.IWorkbenchAdapter2} to customize target 
 * presentation in IDE user interface.</li></ul>
 * 
 * <p>Overwriting <code>equals</code> and <code>hashCode</code> might be desirable 
 * if new objects are created each time user does discovery process.</p>
 * @author Eugene Ostroukhov (eugeneo@symbian.org)
 */
public interface IDeploymentTarget extends IAdaptable {
    /**
     * Returns unique ID. There is no restriction on ID string format. Cannot 
     * be <code>null</code>
     */
    String getId();

    /**
     * <p>Returns user-readable name. Can be null if 
     * {@link org.eclipse.ui.model.IWorkbenchAdapter} is provided.<p>
     * <p>Name is not used for purposes other then presentation and may change
     * between invocations</p>
     */
    String getName();

    /**
     * Provides more details about target. This information is only displayed 
     * to the user and is not important for the application.
     */
    String getDescription();

    /**
     * Deploy application to this target.
     * 
     * @param project project to deploy to the target
     * @param runtime runtime that will be used to run packaged application
     * @param monitor progress monitor to report deployment progress
     */
    IStatus deploy(IMTWProject project, IMobileWebRuntime runtime, IProgressMonitor monitor) throws CoreException;

    /**
     * Save project-specific settings to the memento. Workspace-wide settings 
     * should be managed separately.
     */
    void save(IMemento memento);

    /**
     * Restore project-specific deployment settings. Target should be reset to 
     * default state if its state cannot be restored.
     */
    void load(IMemento memento);
}
