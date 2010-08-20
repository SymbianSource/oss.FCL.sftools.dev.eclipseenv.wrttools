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
package org.symbian.tools.wrttools.util;

	import java.io.File;
	import java.io.FileOutputStream;
	import java.io.IOException;
	import java.io.InputStream;
	import java.util.Enumeration;
	import java.util.SortedSet;
	import java.util.TreeSet;
	import java.util.logging.Level;
	import java.util.logging.Logger;
	import java.util.zip.ZipEntry;
	import java.util.zip.ZipFile;

	import org.eclipse.core.runtime.Platform;

	public class Unzip {
		private Logger log = Logger.getLogger(getClass().getName());
		
		/** Mode listing */
		public static final int LIST = 0;
		
		/** Mode extracting */
		public static final int EXTRACT = 1;
		  
		/** Operating System Property Name */
		public static final String OSname = System.getProperty("os.name"); //$NON-NLS-1$
			
		/** MAC OS */
		public static final String MAC_OS ="Mac OS X"; //$NON-NLS-1$
			
		/** Type of mode: Extracting or Printing TOC */
		private int mode = LIST;
		
		/** Zip file for reading the archive */
		private ZipFile zipfile;
		
		/** Buffer for reading/writing the zip file data */
		private byte[] b;
		
		private String filePath = System.getProperty("java.io.tmpdir");
		private String inputfileName = null;
		private boolean warnedMkDir = false;
		private boolean isAppendNeeded;
		private String baseFolder;
		private boolean baseFolderFound = false;
		private String zipDirName=null;
		private boolean  zipDirNameCheckSet=false;
		private boolean webapp=false;
		public  static boolean canZip = false;
		
		public Unzip(boolean isAppendNeeded) {
		    b = new byte[8092];
		    this.isAppendNeeded=isAppendNeeded;
		}		  
		  
	   /** For a given Zip file, process each entry. */
	   private void unZip(String fileName) {
			log.finest("unzip  <<--->>");
			//System.out.println("File nam in unzip is   "+fileName);
		    dirsMade = new TreeSet();
		    try {
		      zipfile = new ZipFile(fileName);
		      //System.out.println("  zipfile name  : "+zipfile.getName());
		      canZip = true;
		      zipDirName=zipfile.getName();
		      
		      zipDirName= Helper.replaceChar(zipDirName, File.separatorChar, '/');
		      zipDirName= zipDirName.substring(zipDirName.lastIndexOf('/')+1);

		      if(zipDirName.endsWith(".zip")||(zipDirName.endsWith(".Zip"))){
		    	  zipDirName= zipDirName.substring(0,zipDirName.lastIndexOf("."));
		    	  zipDirNameCheckSet=false;
		    	  webapp=true;
		      }
		      else if(((new File(zipfile.getName()).isFile())&&(zipDirName.substring(zipDirName.lastIndexOf("."))).equalsIgnoreCase(".wgz"))){
		    	  zipDirName= zipDirName.substring(0,zipDirName.lastIndexOf("."));
		    	  zipDirNameCheckSet=false;
		    	  webapp=true;
		      }
		      Enumeration all = zipfile.entries();
		      if(zipfile.size()>1){
		    	  //System.out.println("In if condition...........zipfile size is  :"+zipfile.size());
		        while (all.hasMoreElements()) {
		          getFile((ZipEntry) all.nextElement());
		        }
	          }
		      zipfile.close();
		    } catch (IOException e) {
		    	 Helper.logEvent(log, Level.SEVERE, e);
		    	 canZip = false;
		      return;
		    }
			log.finest("unZip  <<---<<");
		  }
		  

		  /**
		   * Process each file from the zip, given its name. 
		   * Display the name, or create the file on disk.
		   */
		  private void getFile(ZipEntry e) throws IOException {
				log.finest("getFile  >>--->>");
			  String zipName = e.getName();
			  
			  if(webapp){
				  
				 String zipNameStartDir = e.getName();
				 zipNameStartDir= Helper.replaceChar(zipNameStartDir, File.separatorChar, '/');
				 zipNameStartDir= zipNameStartDir.substring(0,zipNameStartDir.indexOf('/')+1);				 
				
				if(zipNameStartDir!=null&&!zipNameStartDir.equalsIgnoreCase(zipDirName+'/')&&!zipDirNameCheckSet) {				
					  filePath=	  filePath+zipDirName+'/';
					  zipDirNameCheckSet=true;				  
				  }
		  }
			  if(zipDirNameCheckSet){
				  int indexOfName1 = zipName.indexOf('/');
					int indexOfName2 = zipName.indexOf('\\');
					int actIndex = indexOfName2;
					if (indexOfName1 > indexOfName2) {
						actIndex = indexOfName1;
					}
				  zipName = zipName.substring(actIndex+1);
			  }
			  if(isAppendNeeded && !(Platform.getOS().equals(Platform.OS_MACOSX) || Platform.getOS().equals(Platform.OS_LINUX))){			  
				  zipName= filePath+zipName;
			  } else {
				  zipName= filePath + File.separator +zipName;
			  }
			  //System.out.println(" unZip  -------------->> zipEntyName : "+zipName);
				
		    switch (mode) {
		    case EXTRACT:
		      if (zipName.startsWith("/")  && !(MAC_OS.equals(OSname)||Platform.getOS().equals(Platform.OS_LINUX))) {
		        if (!warnedMkDir)
		        warnedMkDir = true;
		        zipName = zipName.substring(1);
		      }
		      // if a directory, just return. We mkdir for every file,
		      // since some widely-used Zip creators don't put out
		      // any directory entries, or put them in the wrong place.
		      if (zipName.endsWith("/")) {
		        return;
		      }
		      // Else must be a file; open the file for output
		      // Get the directory part.
		      int ix = zipName.lastIndexOf('/');
		      if (ix > 0) {
		        String dirName = zipName.substring(0, ix);
		       if(!baseFolderFound){
		        	baseFolder = dirName.substring(dirName.lastIndexOf(File.separator)+1);
		        	baseFolderFound=true;
		        }
		        if (!dirsMade.contains(dirName)) {
		          File d = new File(dirName);
		          // If it already exists as a dir, don't do anything
		          if (!(d.exists() && d.isDirectory())) {
		            // Try to create the directory, warn if it fails
		            if (!d.mkdirs()) {
		            	log.severe("Warning: unable to mkdir "  + dirName);
		            }
		            dirsMade.add(dirName);
		          }
		        }
		      }
		      FileOutputStream os = new FileOutputStream(zipName);
		      InputStream is = (InputStream) zipfile.getInputStream(e);
		      int n = 0;
		      while ((n = is.read(b)) > 0)
		        os.write(b, 0, n);
		      is.close();
		      os.close();
		      break;
		    case LIST:
		      // Not extracting, just list
		      if (e.isDirectory()) {
		      } else {
		      }
		      break;
		    default:
		      throw new IllegalStateException("mode value (" + mode + ") bad");
		    }
			log.finest("getFile  <<---<<");
		  }
		  
		  public void extract(String fileName,String tempDir){
			log.finest("extract  >>--->>");
	 		
		    filePath=tempDir;
		    setMode(EXTRACT);
		    unZip(fileName);
			log.finest("extract  <<---<<");
		  }
		  
		  public void listfilesInZipDir(String fileName){
			log.finest("listfilesInZipDir  >>--->>");
	
		    setMode(LIST);
		    unZip(fileName);
			log.finest("listfilesInZipDir  <<---<<");  
		  }  
		  
		  /**
		   * Simple main program
		   * Unzip the archive
		   */
		  public static void main(String[] argv) {
			Unzip unzip = new Unzip(true);
			String zipTest = "C:\\Test\\FlickrApplication.zip";
			unzip.extract(zipTest, unzip.filePath);
		  }
			
		  /** Set the Mode (list, extract). */
		  private void setMode(int m) {
		    if (m == LIST || m == EXTRACT)
		      mode = m;
		  }

		  /** Cache of paths we've mkdir()ed. */
		  private SortedSet dirsMade;
		  
		  	  
		/**
		 * Returns the base directory of the zip file.
		 * @return the name of the base directory
		 */
		public String getBaseFolder() {
			return baseFolder;
		}

		  /** Construct an UnZip object. Just allocate the buffer */

}
