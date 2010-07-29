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
package org.symbian.tools.wrttools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.symbian.tools.mtw.core.MTWCore;
import org.symbian.tools.mtw.core.projects.IMTWProject;
import org.symbian.tools.mtw.core.runtimes.IMobileWebRuntime;

public class WRTProject implements IMTWProject {
    private static final String PROP_PREFERED_SCREEN = "preferred.screen.size";
    private static final IPath PROPERTIES_FILE = new Path(".settings").append(Activator.PLUGIN_ID + ".properties");
    public static final String WRT11_RUNTIME = "org.symbian.wrt11";
    private final IProject project;

    public WRTProject(IProject project) {
        this.project = project;
    }

    public String getName() {
        return getProject().getName();
    }

    public String getPreferredScreenSize() {
        return getProps().getProperty(PROP_PREFERED_SCREEN);
    }

    //    public DeploymentTarget getDeploymentTarget() {
    //        Properties props = getProps();
    //        String property = props.getProperty(PROP_DEPLOYMENT_TARGET_NAME);
    //        if (property != null) {
    //            return DeploymentTargetRegistry.getRegistry().findTarget(property,
    //                    props.getProperty(PROP_DEPLOYMENT_TARGET_TYPE));
    //        } else {
    //            IPreferenceStore store = Activator.getDefault().getPreferenceStore();
    //            String name = store.getString(PROP_DEPLOYMENT_TARGET_NAME);
    //            if (name != null) {
    //                return DeploymentTargetRegistry.getRegistry().findTarget(name,
    //                        store.getString(PROP_DEPLOYMENT_TARGET_TYPE));
    //            }
    //
    //        }
    //        return null;
    //    }

    public IProject getProject() {
        return project;
    }

    private Properties getProps() {
        Properties props = new Properties();
        IFile file = project.getFile(PROPERTIES_FILE);
        if (file.exists()) {
            try {
                InputStream contents = file.getContents();
                try {
                    props.loadFromXML(contents);
                } finally {
                    contents.close();
                }
            } catch (InvalidPropertiesFormatException e) {
                Activator.log(e);
            } catch (IOException e) {
                Activator.log(e);
            } catch (CoreException e) {
                Activator.log(e);
            }
        }
        return props;
    }

    public IMobileWebRuntime getTargetRuntime() {
        return MTWCore.getDefault().getRuntimesManager().getRuntime(WRT11_RUNTIME);
    }

    private void saveProperties(Properties props) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            props.storeToXML(stream, null);
            IFile file = project.getFile(PROPERTIES_FILE);
            if (!file.exists()) {
                file.create(new ByteArrayInputStream(stream.toByteArray()), false, new NullProgressMonitor());
            } else {
                file.setContents(new ByteArrayInputStream(stream.toByteArray()), false, true, new NullProgressMonitor());
            }
        } catch (IOException e) {
            Activator.log(e);
        } catch (CoreException e) {
            Activator.log(e);
        }
    }

    public void setPreferredScreenSize(String screenSize) {
        Properties props = getProps();
        if (screenSize != null) {
            props.put(PROP_PREFERED_SCREEN, screenSize);
        } else {
            props.remove(PROP_PREFERED_SCREEN);
        }
        saveProperties(props);
    }

    public boolean validate(IProgressMonitor monitor) {
        try {
            project.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
            IMarker[] markers = project.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);

            boolean hasErrors = false;
            for (IMarker marker : markers) {
                if (marker.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO) == IMarker.SEVERITY_ERROR) {
                    hasErrors = true;
                    break;
                }
            }
            return !hasErrors;
        } catch (CoreException e1) {
            // Proofing from coding exceptions in JSDT
            Activator.log(e1);
        }
        return false;
    }
}
