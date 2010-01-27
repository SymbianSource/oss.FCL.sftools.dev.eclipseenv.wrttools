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

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;

import org.symbian.tools.wrttools.Activator;
import org.symbian.tools.wrttools.core.report.Message;
import org.symbian.tools.wrttools.core.status.IWRTConstants;

/**
 * A class to handle CodeScanner commandline tool execution, error processing
 * and generating CodeScanner specific markers.
 * 
 */
public class MarkerUtil   {

	public MarkerUtil() {
	}

	/**
	 * Map an IMarkerGenerator severity to an IMarker severity
	 * @param severity - an IMarkerGenerator severity
	 * @return an IMarker severity
	 */
	protected static int mapWRTMarkerSeverity(String severity) {
		if (IWRTConstants.INFO.equals(severity))
			return IMarker.SEVERITY_INFO;
		if (IWRTConstants.WARN.equals(severity))
			return IMarker.SEVERITY_WARNING;
		if (IWRTConstants.ERROR.equals(severity))
			return IMarker.SEVERITY_ERROR;
		if (IWRTConstants.FATAL.equals(severity))
			return IMarker.SEVERITY_ERROR;
		return IMarker.PRIORITY_LOW;
	}
	

	public static void addMarker(Message msg, IProject project) {
		try {
			if (project != null) {

				IResource markerResource = project;
				IMarker marker = null;
				if (markerResource != null) {
					// IMarker[] cur =
					
					// markerResource.findMarkers(CSMarker.CS_PROBLEM_MARKER,
					// false, IResource.DEPTH_ONE);
					IMarker[] markerList = null;
					int depth = IResource.DEPTH_INFINITE;
//					IPath filePath = new Path(msg.getTargetObject());
//					IFile fileProblem = project.getFile(filePath);
					if(msg.getTargetObject()!=null)
					{
						IPath filePath = new Path(msg.getTargetObject());
						IFile fileProblem = project.getFile(filePath);
					
					///----------------if error is associated with file create marker with file or else part
					if (fileProblem.exists()) {
						markerList = fileProblem.findMarkers(null, true, depth);
						marker = fileProblem.createMarker(IMarker.PROBLEM);
					} else {
						// if error is not associated with any file create marker with the project.
						markerList = project.findMarkers(null, true, depth);
						marker = project.createMarker(IMarker.PROBLEM);
					}
					
				}else{
					// if error is not associated with any file create marker with the project.
					markerList = project.findMarkers(null, true, depth);
					marker = project.createMarker(IMarker.PROBLEM);
					
				}	
					// try to find matching markers and don't put in duplicates
					
					if ((markerList != null) && (markerList.length > 0)) {
						for (int i = 0; i < markerList.length; i++) {
							int line = markerList[i].getAttribute(IMarker.LINE_NUMBER,-1);
							// int sev = ((Integer)
							// cur[i].getAttribute(IMarker.SEVERITY)).intValue();
							int sev = new Integer(markerList[i].getAttribute(IMarker.SEVERITY).toString());
							String mesg = (String) markerList[i].getAttribute(IMarker.MESSAGE);
							String message = msg.getMessage();
							//If the marker is previously created then return.
							if (line == msg.getLineNumber()	&& sev == mapWRTMarkerSeverity(msg.getSeverity())&& mesg.equals(message)) {
								return;
							}
						}
					}
				} 

//				if (markerResource != null) {
					// create the marker and set its attributes
					// IMarker marker =
					// project.createMarker(WRTMarker.WRT_PROBLEM_MARKER);

					if (marker != null) {
						String message = msg.getMessage();
						marker.setAttribute(IMarker.MESSAGE, message);
						marker.setAttribute(IMarker.SEVERITY, mapWRTMarkerSeverity(msg.getSeverity()));
						marker.setAttribute(IMarker.LINE_NUMBER, msg.getLineNumber());
					} 
//				}

			}
		} 
		catch (CoreException e) {
			Activator.log(IStatus.ERROR,	"Add Marker Exception ", e);
		}
	}
	

	public  static void deleteMarker(IProject project) {
		try {
			if (project != null) {
					project.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
			}
			
		}
		catch (CoreException e) {
			Activator.log(IStatus.ERROR,	"Delete Marker Exception ", e);
		}
	}
	
	
	/**
	 * 
	 * @param iMarkerList
	 */
	
	public   void deleteMarker(List<IMarker> iMarkerList) {
		try {			
			for (IMarker mark : iMarkerList) {
			mark.delete();
			}			
		}
		catch (CoreException e) {
			Activator.log(IStatus.ERROR,	"Delete All Markers Exception ", e);
		}
	}

}
