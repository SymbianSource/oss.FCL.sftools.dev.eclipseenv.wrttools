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
package org.symbian.tools.wrttools.builder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.symbian.tools.wrttools.Activator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;


public class PropertiesFileAuditor extends IncrementalProjectBuilder {

	private static final String MARKER_ID = Activator.PLUGIN_ID + ".auditmarker";
	
	public PropertiesFileAuditor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		if (shouldAudit(kind)) {
			auditPluginManifest(monitor);
		}
		if (kind == IncrementalProjectBuilder.FULL_BUILD) { 
			System.out.println("PropertiesFileAuditor: Full build");
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				System.out.println("PropertiesFileAuditor: Full build - no changes");
				fullBuild(monitor);
			} else {
				System.out.println("PropertiesFileAuditor: Incremental build on " + delta);
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}
	
	 protected void clean(IProgressMonitor monitor) throws CoreException {
	      getProject().deleteMarkers(XML_MARKER_TYPE, 
	         true, IResource.DEPTH_INFINITE);
	      getProject().deleteMarkers(JS_MARKER_TYPE, 
	 	         true, IResource.DEPTH_INFINITE);
	   }
	
	protected void fullBuild(final IProgressMonitor monitor) throws CoreException {
	      try {
	         getProject().accept(new MyBuildVisitor());
	      } catch (CoreException e) { }
	}
	
	class MyBuildVisitor implements IResourceVisitor {
	      public boolean visit(IResource res) {
	         //build the specified resource.
	    	 checkXML(res);
	    	 checkJS(res);
	         //return true to continue visiting children.
	         return true;
	      }
	}
	
	protected void incrementalBuild(IResourceDelta delta, 
		         IProgressMonitor monitor) throws CoreException {
		      // the visitor does the work.
		      // delta.accept(new MyBuildDeltaVisitor());
		      
		      System.out.println("incremental build on "+delta);
		         try {
		            delta.accept(new IResourceDeltaVisitor() {
		               public boolean visit(IResourceDelta delta) {
		                  System.out.println("changed: "+
		                     delta.getResource().getRawLocation());
		                  return true; // visit children too
		               }
		            });
		         } catch (CoreException e) {
		            e.printStackTrace();
		         }
	}
	class MyBuildDeltaVisitor implements IResourceDeltaVisitor {
		
	     public boolean visit(IResourceDelta delta) {
	    	 IResource resource = delta.getResource();
	         switch (delta.getKind()) {
	         case IResourceDelta.ADDED :
	             // handle added resource
	        	 checkXML(resource);
	        	 checkJS(resource);
	             break;
	         case IResourceDelta.REMOVED :
	             // handle removed resource
	             break;
	         case IResourceDelta.CHANGED :
	             // handle changed resource
	        	 checkXML(resource);
	        	 checkJS(resource);
	             break;
	         }
	     // return true to continue visiting children
	     return true;
	     }
	 }
	
	class XMLErrorHandler extends DefaultHandler {
		
		private IFile file;

		public XMLErrorHandler(IFile file) {
			this.file = file;
		}

		private void addXMLMarker(SAXParseException e, int severity) {
			PropertiesFileAuditor.this.addXMLMarker(file, e.getMessage(), e
					.getLineNumber(), severity);
		}

		public void error(SAXParseException exception) throws SAXException {
			addXMLMarker(exception, IMarker.SEVERITY_ERROR);
		}

		public void fatalError(SAXParseException exception) throws SAXException {
			addXMLMarker(exception, IMarker.SEVERITY_ERROR);
		}

		public void warning(SAXParseException exception) throws SAXException {
			addXMLMarker(exception, IMarker.SEVERITY_WARNING);
		}
	}
	
class JSErrorHandler extends DefaultHandler {
		
		private IFile file;

		public JSErrorHandler(IFile file) {
			this.file = file;
		}

		private void addJSMarker(SAXParseException e, int severity) {
			PropertiesFileAuditor.this.addJSMarker(file, e.getMessage(), e
					.getLineNumber(), severity);
		}

		public void error(SAXParseException exception) throws SAXException {
			addJSMarker(exception, IMarker.SEVERITY_ERROR);
		}

		public void fatalError(SAXParseException exception) throws SAXException {
			addJSMarker(exception, IMarker.SEVERITY_ERROR);
		}

		public void warning(SAXParseException exception) throws SAXException {
			addJSMarker(exception, IMarker.SEVERITY_WARNING);
		}
	}
	
	private static final String XML_MARKER_TYPE = "org.symbian.tools.wrt.xmlProblem";
	private static final String JS_MARKER_TYPE = "org.symbian.tools.wrt.jsProblem";

	private SAXParserFactory parserFactory;
	
	private void addJSMarker(IFile file, String message, int lineNumber,
			int severity) {
		try {
			IMarker marker = file.createMarker(JS_MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e) {
		}
	}

	private void addXMLMarker(IFile file, String message, int lineNumber,
			int severity) {
		try {
			IMarker marker = file.createMarker(XML_MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e) {
		}
	}
	void checkXML(IResource resource) {
		if (resource instanceof IFile && resource.getName().endsWith(".xml")) {
			IFile file = (IFile) resource;
			deleteXMLMarkers(file);
			XMLErrorHandler reporter = new XMLErrorHandler(file);
			try {
				getParser().parse(file.getContents(), reporter);
			} catch (Exception e1) {
			}
		}
	}
	
	void checkJS(IResource resource) {
		if (resource instanceof IFile && resource.getName().endsWith(".js")) {
			IFile file = (IFile) resource;
			deleteJSMarkers(file);
			JSErrorHandler reporter = new JSErrorHandler(file);
			try {
				getParser().parse(file.getContents(), reporter);
			} catch (Exception e1) {
			}
		}
	}

	private void deleteXMLMarkers(IFile file) {
		try {
			file.deleteMarkers(XML_MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}
	
	private void deleteJSMarkers(IFile file) {
		try {
			file.deleteMarkers(JS_MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}
	
	private SAXParser getParser() throws ParserConfigurationException,
	SAXException {
		if (parserFactory == null) {
			parserFactory = SAXParserFactory.newInstance();
		}
		return parserFactory.newSAXParser();
	}
	
/*
	//TODO: Implementing resource change listeners
	//The listener checks for each event type and reports information 
	//about the resource that was changed and the kinds of changes that occurred. 
	IResourceChangeListener listener = new MyResourceChangeReporter();
	   ResourcesPlugin.getWorkspace().addResourceChangeListener(listener,
	      IResourceChangeEvent.PRE_CLOSE
	      | IResourceChangeEvent.PRE_DELETE
	      | IResourceChangeEvent.PRE_BUILD
	      | IResourceChangeEvent.POST_BUILD
	      | IResourceChangeEvent.POST_CHANGE);
	   
*/
	
	private boolean shouldAudit(int kind) {
		if (kind == FULL_BUILD)
			return true;
			
		IResourceDelta delta = getDelta(getProject());
		if (delta == null)
			return false;
		IResourceDelta[] children = delta.getAffectedChildren();
		for (int i = 0; i < children.length; i++) {
			IResourceDelta child = children[i];
			String fileName = child.getProjectRelativePath().lastSegment();
			if (fileName.equals("plugin.xml") ||
					fileName.equals("plugin.properties"))
				return true;
		}
		return false;
	}
	
	public static final int MISSING_KEY_VIOLATION = 1;
	public static final int UNUSED_KEY_VIOLATION = 2;
	
	private void auditPluginManifest(IProgressMonitor monitor) {
		monitor.beginTask("Audit plugin manifest", 4);
		
		// remove any old markers
		if (!deleteAuditMarkers(getProject())) {
			return;
		}
		if (checkCancel(monitor))
			return;
		
		IProject proj = getProject();
		
		if (checkCancel (monitor)) 
			return;
		Map<String, Location> pluginKeys = scanPlugin(getProject().getFile("plugin.xml"));
		monitor.worked(1);
		
		if (checkCancel(monitor)) 
			return;
		
		Map<String, Location> propertyKeys = scanProperties(getProject().getFile("plugin.properties"));
		monitor.worked(1);
		
		if (checkCancel(monitor)) 
			return;
		
		Iterator<Map.Entry<String, Location>> iter = pluginKeys.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Location> entry = iter.next();
			if (!propertyKeys.containsKey(entry.getKey()))
				reportProblem("Missing property key",
						((Location) entry.getValue()),
						MISSING_KEY_VIOLATION,
						true);
		}
		monitor.worked(1);
		
		if (checkCancel (monitor))
			return;
		
		iter = propertyKeys.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Location> entry = iter.next();
			if (!pluginKeys.containsKey(entry.getKey()))
				reportProblem("Unused property key",
				((Location) entry.getValue()),
				UNUSED_KEY_VIOLATION,
				false);
		}
		monitor.done();		
	}
	
	private boolean checkCancel(IProgressMonitor monitor) {
		if (monitor.isCanceled()) {
			// Discard build state if necessary
			throw new OperationCanceledException();
		}
		return false;
	}
	
	private Map<String, Location> scanPlugin(IFile file) {
		Map<String,Location> keys = new HashMap<String, Location>();
		String content = readFile(file);
		int start = 0;
		
		while (true) {
			start = content.indexOf("\"%", start);
			if (start < 0)
				break;
			int end = content.indexOf('"', start + 2);
			if (end < 0)
				break;
			Location loc = new Location();
			loc.file = file;
			loc.key = content.substring(start + 2, end);
			loc.charStart = start + 1;
			loc.charEnd = end;
			keys.put(loc.key, loc);
			start = end + 1;
		}
		return keys;
	}
	
	private Map<String, Location> scanProperties(IFile file) {
		Map<String,Location> keys = new HashMap<String, Location>();
		String content = readFile(file);
		int end = 0;
		while (true) {
			end = content.indexOf('=', end);
			if (end < 0)
				break;
			int start = end - 1;
			while (start >= 0) {
				char ch = content.charAt(start);
				if (ch == '\r' || ch == '\n')
					break;
				start --;
			}
			start++;
			String found = content.substring(start, end).trim();
			if (found.length() == 0 || 
				found.charAt(0) == '#' || 
				found.indexOf('=') != -1)
				continue;
			Location loc = new Location();
			loc.file = file;
			loc.key = found;
			loc.charStart = start;
			loc.charEnd = end;
			keys.put(loc.key, loc);
			end++;
		}
		return keys;
	}
	
	private String readFile(IFile file) {
		if (!file.exists())
			return "";
		InputStream stream = null;
		try {
			stream = file.getContents();
			Reader reader = new BufferedReader(
					new InputStreamReader(stream));
			StringBuffer result = new StringBuffer(2048);
			char[] buf = new char[2048];
			while (true) {
				int count = reader.read(buf);
				if (count < 0)
					break;
				result.append(buf, 0, count);
			}
			return result.toString();
		}
		catch (Exception e) {
			// log the logError
			return "";
		}
		finally {
			try {
				if (stream != null)
					stream.close();
			}
			catch (IOException e) {
				// log the logError
				return "";
			}
		}
	}
	private class Location {
		int charStart;
		int charEnd;
		IFile file;
		String key;
	}
	private void reportProblem(String msg, Location loc, int violation, boolean isError) {
		System.out.println((isError ? "ERROR: " : "WARNING: ")
				+ msg + " \""
				+ loc.key + "\" in "
				+ loc.file.getFullPath());
	}
	
	public static boolean projectHasBuilder(IProject project) {
		// Cannot modify closed projects
		if (!project.isOpen())
			return false
			;
		// get the description
		IProjectDescription description;
		
		try {
			description = project.getDescription();
		} catch (CoreException e) {
			// Log the error
			return false;
		}
		// Look for the builder
		int index = -1;
		ICommand[] cmds = description.getBuildSpec();
		for (int j = 0; j < cmds.length; j++) {
			if (cmds[j].getBuilderName().equals(BUILDER_ID)) {
				index = j;
				break;
			}
		}
		if (index == -1)
			return false;
		return true;  // project has builder
	}
	
	public static boolean hasBuilder(IProject project) {
		// TODO Auto-generated method stub
		try {
			return project.isOpen() && projectHasBuilder(project);
		}
		catch (Exception e) {
			// log the error
			return false;
		}
	}
	
	public static final String BUILDER_ID = Activator.PLUGIN_ID + Activator.PLUGIN_ID + ".propertiesFileAuditor";
	
	public static void addBuilderToProject(IProject project) {
		// Cannot modify closed projects
		if (!project.isOpen())
			return;
		
		// Get the description
		IProjectDescription description;
		try {
			description = project.getDescription();
		}
		catch (CoreException e) {
			// log the logError
			return;
		}
		
		// Look for builder already associated
		ICommand[] cmds = description.getBuildSpec();
		for (int j = 0; j < cmds.length; j++)
			if (cmds[j].getBuilderName().equals(BUILDER_ID))
				return;
		
		// Associate builder with project
		ICommand newCmd = description.newCommand();
		newCmd.setBuilderName(BUILDER_ID);
		ArrayList<ICommand> newCmds = new ArrayList<ICommand>();
		newCmds.addAll (Arrays.asList(cmds));
		newCmds.add(newCmd);	
				description.setBuildSpec(
						(ICommand[]) newCmds.toArray(
								new ICommand[newCmds.size()]));		
		try {
			project.setDescription(description, null);
		}
		catch (CoreException e) {
			// log the logError
		}
		
	}
	
	public static void removeBuilderFromProject(IProject project) {
		// Cannot modify closed projects
		if (!project.isOpen())
			return;
		// get the description
		IProjectDescription description;
		
		try {
			description = project.getDescription();
		} catch (CoreException e) {
			// Log the error
			return;
		}
		
		// Look for the builder
		int index = -1;
		ICommand[] cmds = description.getBuildSpec();
		for (int j = 0; j < cmds.length; j++) {
			if (cmds[j].getBuilderName().equals(BUILDER_ID)) {
				index = j;
				break;
			}
		}
		if (index == -1)
			return;
		
		// Remove builder from project
		ArrayList<ICommand> newCmds = new ArrayList<ICommand>();
		newCmds.addAll(Arrays.asList(cmds));
		newCmds.remove(index);
		description.setBuildSpec(
				(ICommand[]) newCmds.toArray(
						new ICommand[newCmds.size()]));
		
		try {
			project.setDescription(description, null);
		}
		catch (CoreException e) {
			// log the error
		}
	}

	public static boolean deleteAuditMarkers(IProject project) {
		// TODO Auto-generated method stub
		try {
			project.deleteMarkers(MARKER_ID, false, 0 /* IRESOURCE.DEPTH_INFINITIVE */);
			return true;
		}
		catch (CoreException e) {
			// log the error
			return false;
		}
		
	}
}
