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

package org.symbian.tools.wrttools.core.marker;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;

import org.symbian.tools.wrttools.Activator;


/**
 * A class to handle markers.
 */
public class WRTMarker {

	/**
	 * WRTValidator problem marker type. This can be used to recognize those markers
	 * in the workspace that flag problems detected by the CodeScanner plugin.
	 */
	public static final String WRT_PROBLEM_MARKER = Activator.PLUGIN_ID + ".org.symbian.tools.wrttools.core.validator.marker"; //$NON-NLS-1$
	
//	public static final String WRT_PROBLEM_MARKER = Activator.PLUGIN_ID + ".WRTMarker"; //$NON-NLS-1$
	
	/**
	 * WRTValidator extension to the marker problem markers which may hold a hint on
	 * the variable name that caused the error. Used by the ui to highlight the variable
	 * itself if it can be found.
	 */
	public static final String WRT_MARKER_VARIABLE = "problem.variable"; //$NON-NLS-1$
	
	public static final String WRT_MARKER_FILE_NAME = "problem.file.name"; //$NON-NLS-1$
	
	public static final String WRT_MARKER_FILE_FULL_PATH = "problem.file.full.path"; //$NON-NLS-1$
	
	/**
	 * WRTValidator extension to the marker problem markers which may hold 
	 * the path to the workspace external location of the file containing the problem 
	 */
	public static final String WRT_MARKER_EXTERNAL_LOCATION = "problem.externalLocation"; //$NON-NLS-1$

	/**
	 * WRTValidator extension to the marker problem markers which may hold 
	 * the name of the WRTValidator rule applicable to the problem 
	 */
	public static final String WRT_MARKER_RULE_NAME = "problem.ruleName"; //$NON-NLS-1$

	/**
	 * Remove all validate markers from a project.
	 * @param currProject - Project containing WRTValidator markers.
	 */
	 public static void removeAllMarkers(IProject currProject) {
		try {
			IWorkspace workspace = currProject.getWorkspace();

			// remove all WRTValidator markers
			IMarker[] markers = currProject.findMarkers(WRTMarker.WRT_PROBLEM_MARKER, true, IResource.DEPTH_INFINITE);
			if ((markers != null) && (markers.length > 0)) {
				workspace.deleteMarkers(markers);
			}		
		} catch (CoreException e){
			Activator.log(IStatus.ERROR,	"Marker Exception ", e);
        }
	 }

}


