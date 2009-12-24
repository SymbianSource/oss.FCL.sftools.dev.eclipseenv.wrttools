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

//The implementation for POST_CHANGE uses another class that can be used 
// to visit the changes in the resource delta. 

   import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

   public class MyResourceChangeReporter implements IResourceChangeListener {

	  @Override
	  // TODO Auto-generated method stub
      public void resourceChanged(IResourceChangeEvent event) {
         IResource res = event.getResource();
         switch (event.getType()) {
            case IResourceChangeEvent.PRE_CLOSE:
               System.out.print("Project ");
               System.out.print(res.getFullPath());
               System.out.println(" is about to close.");
               break;
            case IResourceChangeEvent.PRE_DELETE:
               System.out.print("Project ");
               System.out.print(res.getFullPath());
               System.out.println(" is about to be deleted.");
               break;
            case IResourceChangeEvent.POST_CHANGE:
               System.out.println("Resources have changed.");
               try {
				event.getDelta().accept(new DeltaPrinter());
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
               break;
            case IResourceChangeEvent.PRE_BUILD:
               System.out.println("Build about to run.");
               try {
				event.getDelta().accept(new DeltaPrinter());
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
               break;
            case IResourceChangeEvent.POST_BUILD:
               System.out.println("Build complete.");
               try {
				event.getDelta().accept(new DeltaPrinter());
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
               break;
         }
      }
   }
   
   //TODO The DeltaPrinter class implements the IResourceDeltaVisitor interface 
   //     to interrogate the resource delta. The visit() method is called for 
   //     each resource change in the resource delta. The visitor uses a return 
   //     value to indicate whether deltas for child resources should be visited. 

   class DeltaPrinter implements IResourceDeltaVisitor {

	   @Override
	      public boolean visit(IResourceDelta delta) throws CoreException{
	         IResource res = delta.getResource();
	         switch (delta.getKind()) {
	            case IResourceDelta.ADDED:
	               System.out.print("Resource ");
	               System.out.print(res.getFullPath());
	               System.out.println(" was added.");
	               break;
	            case IResourceDelta.REMOVED:
	               System.out.print("Resource ");
	               System.out.print(res.getFullPath());
	               System.out.println(" was removed.");
	               break;
	            case IResourceDelta.CHANGED:
	               System.out.print("Resource ");
	               System.out.print(res.getFullPath());
	               System.out.println(" has changed.");
	               int flags = delta.getFlags();
	               if ((flags & IResourceDelta.CONTENT) != 0) {
	                     System.out.println("--> Content Change");
	               }
	               if ((flags & IResourceDelta.REPLACED) != 0) {
	                     System.out.println("--> Content Replaced");
	               }
	               if ((flags & IResourceDelta.MARKERS) != 0) {
	                     System.out.println("--> Marker Change");
	                     IMarkerDelta[] markers = delta.getMarkerDeltas();
	                     // if interested in markers, check these deltas
	               }

	               break;
	         }
	         return true; // visit the children
	      }
	   }

