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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUtil {
	private Logger log = Logger.getLogger(getClass().getName());
	private List fileList = new ArrayList();
	private List dirList = new ArrayList();
	private List<File> widgetFileList = new ArrayList<File>();
/**
 * 
 * @param fileName
 * @return
 */
	public File unZip(String fileName) {
		log.finest("unZip  >>--->>");
		String unzipFilePath = System.getProperty("java.io.tmpdir");
		Unzip unZip = new Unzip(true);
		unZip.extract(fileName, unzipFilePath);
		int indexOfName1 = fileName.lastIndexOf('/');
		int indexOfName2 = fileName.lastIndexOf('\\');
		int actIndex = indexOfName2;
		if (indexOfName1 > indexOfName2) {
			actIndex = indexOfName1;
		}
		String zipfilename = fileName.substring(actIndex + 1, fileName
				.lastIndexOf('.'));

		File widgetModelFile = new File(unzipFilePath + "/" + zipfilename);
		log.finest("unZip  <<---<<");
		return widgetModelFile;
	}
	
	public File unZip(String fileName,String unzipFilePath) {
		log.finest("unZip  >>--->>");
		System.out.println("in unzip class fileName ***:"+fileName);
		System.out.println("in unzip class unzipFilePath ***:"+unzipFilePath);
//		String unzipFilePath = System.getProperty("java.io.tmpdir");
		Unzip unZip = new Unzip(true);
		unZip.extract(fileName, unzipFilePath);
		int indexOfName1 = fileName.lastIndexOf('/');
		int indexOfName2 = fileName.lastIndexOf('\\');
		int actIndex = indexOfName2;
		if (indexOfName1 > indexOfName2) {
			actIndex = indexOfName1;
		}
		String zipfilename = fileName.substring(actIndex + 1, fileName
				.lastIndexOf('.'));

		File widgetModelFile = new File(unzipFilePath + "/" + zipfilename);
		log.finest("unZip  <<---<<");
		return widgetModelFile;
	}
	
	public File deleteParentEmptydir(String fileName,String unzipFilePath) {
		
		log.finest("unZip  >>--->>");
//		String unzipFilePath = System.getProperty("java.io.tmpdir");
		
		File unzipFile = new File( fileName);
		List curList=	getCurrDirAllFiles(unzipFile);
		String name=null;
//		for (int i = 0; i < curList.size(); i++) {
////			Util.showData("---- unZipParentEmptydir file name ---------------:"+((File)curList.get(i)).getAbsolutePath());
//			
//	
//	}
		if(curList.size()==1){
		File newfile=	((File)curList.get(0))	;
		try {
			name=newfile.getName();
			copyDirectory( newfile , new File (unzipFilePath+"/"+name));
			this.deleteDirFile(unzipFile);
			return new File(unzipFilePath+"/"+name);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}else{
			return unzipFile;
		}
		
		log.finest("unZip  <<---<<");
		return null;
	}
	
/**
 * 
 * @param file
 * @return
 */
	private String getFileName(File file) {
		return file.getName();

	}

	/**
	 * 
	 * @param dir
	 * @return
	 */
	public List getDirFileNames(File dir) {
		log.finest("getDirFileNames  >>--->>");
			fileList.add(getFileName(dir));
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				getDirFileNames(new File(dir, children[i]));
			}
		}
		log.finest("getDirFileNames  <<---<<");
		return fileList;
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 */
	
	private String getFileFullName(File file) {
		return Util.replaceChar(file.getAbsolutePath(), '\\', '/');
		

	}

	/**
	 * 
	 * @param dir
	 * @return
	 */
	public List getDirFileFullNames(File dir) {
		log.finest("getDirFileNames  >>--->>");
//			fileList.add(getFileFullName(dir));
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				getDirFileFullNames(new File(dir, children[i]));
			}
		}
		else {
			fileList.add(getFileFullName(dir));
		}
		log.finest("getDirFileNames  <<---<<");

		return fileList;
	}

	
	
	/**
	 * 
	 * @param dir
	 * @return
	 */
	
	//	 Return the files which are in the directory exclude the recursive process.
	public List<String> getCurrDirFileNames(File dir) {
		log.finest("getCurrDirFileNames  >>--->>");
		List<String> fileNameList = new ArrayList();
		try 
		{
			File[] widgetFileArray = dir.listFiles();
			if (widgetFileArray.length > 0 && widgetFileArray != null) {
				int len = widgetFileArray.length;
				for (int i = 0; i < len; i++) {
					String path =Util.replaceChar(widgetFileArray[i].getPath(), '\\', '/');
					fileNameList.add(path);
				}

			}
		} 
		catch (Exception e) {
			 Util.logEvent(log, Level.INFO, e);				
				
		}
		log.finest("getCurrDirFileNames  <<---<<");
		return fileNameList;
	}

	/**
	 * 
	 * @param dir
	 * @return
	 */
	//	 Return the files which are in the directory exclude the recursive process.
	public List<String> getCurrDirFileFullNames(File dir) {
		
		log.finest("getCurrDirFileNames  >>--->>");
		List<String> fileNameList = new ArrayList();
		try 
		{
			File[] widgetFileArray = dir.listFiles();
			if (widgetFileArray.length > 0 && widgetFileArray != null) {
				int len = widgetFileArray.length;
				for (int i = 0; i < len; i++) {
					String path =Util.replaceChar(widgetFileArray[i].getPath(), '\\', '/');
					fileNameList.add(path);
				}

			}
		} 
		catch (Exception e) {
			 Util.logEvent(log, Level.INFO, e);				
				
		}
		log.finest("getCurrDirFileNames  <<---<<");
		return fileNameList;
	}
	/**
	 * 
	 * @param dir
	 * @return
	 */
	//	 Return the files which are in the directory exclude the recursive process and return a File.
	public List<File> getCurrDirFiles(File dir) {
		log.finest("getCurrDirFiles  >>--->>");
		List<File> fileList = new ArrayList<File>();
		try {
			File[] widgetFileArray = dir.listFiles();
			if (widgetFileArray.length > 0 && widgetFileArray != null) {
				int len = widgetFileArray.length;
				for (int i = 0; i < len; i++) {
					
					if (!widgetFileArray[i].isDirectory()) {
						fileList.add(widgetFileArray[i]);
					}
				}

			}
		} catch (Exception e) {
			 Util.logEvent(log, Level.INFO, e);				
				}
		log.finest("getCurrDirFiles  <<---<<");
		return fileList;
	}
	
	public List<File> getCurrDirAllFiles(File dir) {
		log.finest("getCurrDirAllFiles  >>--->>");
		List<File> fileList = new ArrayList<File>();
		try {
			File[] widgetFileArray = dir.listFiles();
			if (widgetFileArray.length > 0 && widgetFileArray != null) {
				int len = widgetFileArray.length;
				for (int i = 0; i < len; i++) {
					
						fileList.add(widgetFileArray[i]);
				
				}

			}
		} catch (Exception e) {
			 Util.logEvent(log, Level.INFO, e);				
				}
		log.finest("getCurrDirAllFiles  <<---<<");
		return fileList;
	}
	
/**
 * 
 * @param path
 * @return
 */
	//	 Returns the list of directories and files for the specified path. 
	public List getDirFileNames(String path) {
		log.finest("getDirFileNames  >>--->>");
		getDirFileNames(new File(path));
//		for (int i = 0; i < fileList.size(); i++) {
//		}
		log.finest("getDirFileNames  <<---<<");
		return fileList;
	}
/**
 * 
 * @param widgetDir
 * @return
 */
	//	  File getWidgetModel(String widgetDirPath){

	public List<File> getDirectoryFileList(File widgetDir) {
		log.finest("getDirectoryFileList  >>--->>");
		try {
			File[] widgetFileArray = widgetDir.listFiles();
			if (widgetFileArray.length > 0 && widgetFileArray != null) {
				int len = widgetFileArray.length;
				for (int i = 0; i < len; i++) {
					widgetFileList.add(widgetFileArray[i]);
					if (widgetFileArray[i].isDirectory()) {
						getDirectoryFileList(widgetFileArray[i]);
					}

				}

			}
		} catch (Exception e) {
			 Util.logEvent(log, Level.INFO, e);	;
		}

		//		      zippy = new ZipFile(fileName);
		log.finest("getDirectoryFileList  <<---<<");
		return widgetFileList;
	}
/**
 * 
 * @param widgetDir
 * @return
 */
	public List<File> getDirectoryFileList(String widgetDir) {

		return getDirectoryFileList(new File(widgetDir));
	}
/**
 * 
 * @param files
 * @param fileName1
 * @param fileName2
 * @return
 */
	public File getFile(List<File> files, String fileName1, String fileName2) {
		log.finest("getFile  >>--->>");

		for (File file : files) {
			if (file.getName().endsWith(fileName1)
					|| file.getName().endsWith(fileName2)) {
				return file;
			}
		}
		log.finest("getFile  <<---<<");
		return null;
	}
	/**
	 * 
	 * @param files
	 * @param fileName
	 * @return
	 */

	  public File getFile(List<File>  files,String fileName){
		  log.finest("getFile  >>--->>");
		  if(fileName!=null &&files!=null &&files.size()>0){
	        for(File file:  files){
	        	if(file.getName().trim().equalsIgnoreCase(fileName)){            	
	            return file;            
	        }
	      }
		  }
			log.finest("getFile  <<---<<");
	      return null;  
	    }
	  
	  public void deleteDirFile( File deleteFile)
	  {
		  log.finest("deleteDirFile  >>--->>");
			if(deleteFile.isDirectory()){
			List<File> unzipDirList = getDirectoryFileList(deleteFile);
			for (File file : unzipDirList) {
				if (!file.isDirectory()) 
				{
					log.finest("delete Dir file  :"+file.getName());
					file.delete();
				}
			}
			List<File> unzipDirList2 = getDirectoryFileList(deleteFile);
			for (File file : unzipDirList2) {
							file.delete();
			}
			}
			log.finest("delete file :"+deleteFile.getName());
			deleteFile.delete();
			log.finest("deleteDirFile  <<---<<");
	  }
	  
	  public void clearDirectory( File deleteFile)
	  {		  
		  log.finest("clearDirectory  >>--->>");
		
			List<File> currentDirFileList = getCurrDirAllFiles(deleteFile);
			for (File file : currentDirFileList) {
					 deleteDirFile( file);			
			}
			log.finest("clearDirectory  <<---<<");
	  }

	  public static void copy(String fromFileName, String toFileName)
	      throws IOException {
	    File fromFile = new File(fromFileName);
	    File toFile = new File(toFileName);

	    if (!fromFile.exists())
	      throw new IOException("FileCopy: " + "no such source file: "
	          + fromFileName);
	    if (!fromFile.isFile())
	      throw new IOException("FileCopy: " + "can't copy directory: "
	          + fromFileName);
	    if (!fromFile.canRead())
	      throw new IOException("FileCopy: " + "source file is unreadable: "
	          + fromFileName);

	    if (toFile.isDirectory())
	      toFile = new File(toFile, fromFile.getName());

	    if (toFile.exists()) {
	      if (!toFile.canWrite())
	        throw new IOException("FileCopy: "
	            + "destination file is unwriteable: " + toFileName);
	      System.out.print("Overwrite existing file " + toFile.getName()
	          + "? (Y/N): ");
	      System.out.flush();
	      BufferedReader in = new BufferedReader(new InputStreamReader(
	          System.in));
	      String response = in.readLine();
	      if (!response.equals("Y") && !response.equals("y"))
	        throw new IOException("FileCopy: "
	            + "existing file was not overwritten.");
	    } else {
	      String parent = toFile.getParent();
	      if (parent == null)
	        parent = System.getProperty("user.dir");
	      File dir = new File(parent);
	      if (!dir.exists())
	        throw new IOException("FileCopy: "
	            + "destination directory doesn't exist: " + parent);
	      if (dir.isFile())
	        throw new IOException("FileCopy: "
	            + "destination is not a directory: " + parent);
	      if (!dir.canWrite())
	        throw new IOException("FileCopy: "
	            + "destination directory is unwriteable: " + parent);
	    }

	    FileInputStream from = null;
	    FileOutputStream to = null;
	    try {
	      from = new FileInputStream(fromFile);
	      to = new FileOutputStream(toFile);
	      byte[] buffer = new byte[4096];
	      int bytesRead;

	      while ((bytesRead = from.read(buffer)) != -1)
	        to.write(buffer, 0, bytesRead); // write
	    } finally {
	      if (from != null)
	        try {
	          from.close();
	        } catch (IOException e) {
	          ;
	        }
	      if (to != null)
	        try {
	          to.close();
	        } catch (IOException e) {
	          ;
	        }
	    }
	  }
	  public void copyDirectory(File sourceLocation , File targetLocation)
	    throws IOException {
	        
	        if (sourceLocation.isDirectory()) {
	            if (!targetLocation.exists()) {
	                targetLocation.mkdir();
	            }
	            
	            String[] children = sourceLocation.list();
	            for (int i=0; i<children.length; i++) {
	                copyDirectory(new File(sourceLocation, children[i]),
	                        new File(targetLocation, children[i]));
	            }
	        } else {
	            
	            InputStream in = new FileInputStream(sourceLocation);
	            OutputStream out = new FileOutputStream(targetLocation);
	            
	            // Copy the bits from instream to outstream
	            byte[] buf = new byte[1024];
	            int len;
	            while ((len = in.read(buf)) > 0) {
	                out.write(buf, 0, len);
	            }
	            in.close();
	            out.close();
	        }
	    } 
	  
	public static void main(String[] args) {
		FileUtil trs = new FileUtil();
		String candidate = "C:/Test/t1/HOROSCOPEnew.zip";
		 System.out.println("main starting   :");
		String filePath=System.getProperty("java.io.tmpdir");
		//	    	traverse(new File(dirName));
		//		   trs.getDirectoryFileList( dirName);
		trs.unZip(candidate,"C:/Test/t1/");
		
		 System.out.println("------------------------------------------------------");
	List curList=	trs.getCurrDirAllFiles(new File ("C:/Test/t1/HOROSCOPEnew"));
		for (int i = 0; i < curList.size(); i++) {
			Util.showData("----file name ---------------:"+((File)curList.get(i)).getAbsolutePath());
			
	
	}
		trs.deleteParentEmptydir("C:/Test/t1/HOROSCOPEnew","C:/Test/t1") ;
//		Util.showData(trs.getCurrDirFileNames(new File (candidate)), "File List");
//		trs.trimFilePath(new File(filePath+"Beep"));
	}

	public File getFile(String string) {
	return new File(string);

	}
	
	
	/**
	 * Returns the logger
	 * @return
	 */
	public Logger getLog() {
		return log;
	}

}
