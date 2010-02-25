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

package org.symbian.tools.wrttools.core.packager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;


//import org.apache.commons.io.IOUtils;

import org.symbian.tools.wrttools.core.status.IWRTConstants;
import org.symbian.tools.wrttools.core.status.IWRTStatusListener;
import org.symbian.tools.wrttools.core.status.WRTStatus;
import org.symbian.tools.wrttools.util.Util;

/**
 * A class is used to Zip folders and files in the same way as WinZip does.
 * @author ranoliya
 *
 */
class PackagerZipFolder {

	//ZIP output stream
	private ZipOutputStream pkgZipOutputStream = null;
	
	//Logger to log info. errors and messages
	private Logger logger = Logger.getLogger(getClass().getName());
	
	//List of files to be zipped
	private List<String> fileList;
	
	//Status handler for status reporting to GUI
	private IWRTStatusListener statusListener;
	
	//Source path from where files are to be zipped
	private String srcPath;
	
	//Widget file to be zipped
	private File wFile;
	
	//package root folder path
	private String rootFolderPath="";

	 /**
     * The default buffer size to use.
     */
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	
	/**
	 * This method creates a zip file for the given input at the given destination path
	 * @param srcpath - Input for the zip
	 * @param destPath - The destination path 
	 * @param listOfFiles - list of files to be included in the zip. for null list it will zip all the files and folders
	 * @param statushandler - Status handler form GUI
	 * @throws PackageException - exception during Zipping 
	 */
	public void zipProjectFolder(String srcpath, String destPath, List<String> listOfFiles, IWRTStatusListener statushandler) throws PackageException{
		fileList = listOfFiles;
		this.statusListener = statushandler;
		this.srcPath=srcpath;
		try
		{
			wFile = new File (srcPath);
			//invlid file or directory
			if (!wFile.isFile() && !wFile.isDirectory()
					|| null == destPath || !(destPath.length()>0)) {
				logAndThrowErrors(Level.SEVERE, WRTPackagerConstants.LOG_SOURCE_NOT_FOUND,null);
			}
			
			//create zip outputstream
			FileOutputStream outzip=null;

			outzip=new FileOutputStream(destPath);
			
			pkgZipOutputStream = new ZipOutputStream(outzip);
				
			zipFilesInFolder( wFile, rootFolderPath);
			
			
			pkgZipOutputStream.finish();
			pkgZipOutputStream.close();
		}catch (IOException e){
			e.printStackTrace();
			logAndThrowErrors(Level.SEVERE, WRTPackagerConstants.LOG_ZIP_FAILED,e);
		}
		
	}

	public void zipProjectFolder(String srcpath, String destPath, List<String> listOfFiles) throws PackageException{
		fileList = listOfFiles;
//		this.statusListener = statushandler;
		this.srcPath=srcpath;
		try
		{
			wFile = new File (srcPath);
			//invlid file or directory
			if (!wFile.isFile() && !wFile.isDirectory()
					|| null == destPath || !(destPath.length()>0)) {
				logAndThrowErrors(Level.SEVERE, WRTPackagerConstants.LOG_SOURCE_NOT_FOUND,null);
			}
			
			//create zip outputstream
		
			
			pkgZipOutputStream = new ZipOutputStream(new FileOutputStream(destPath));				
			zipFilesInFolder( wFile, rootFolderPath);
			pkgZipOutputStream.finish();
			pkgZipOutputStream.close();
		}catch (IOException e){
			e.printStackTrace();
			logAndThrowErrors(Level.SEVERE, WRTPackagerConstants.LOG_ZIP_FAILED,e);
		}
		
	}


	/**
	 * This method scans one by one files inside the srcPath and 
	 * add it to the zip folder
	 * Method is called recursively to add each file of the folder to the Zip entry
	 * @param pkgFile -- File or folder to add to zip 
	 * @param baseName -- name under which it will add the pkgFile
	 * @throws PackageException - exception from Zip
	 */
	private void zipFilesInFolder(File pkgFile, String baseName) throws PackageException {
		
		if (pkgFile.isDirectory()) {
			File [] fList = pkgFile.listFiles() ;
			if(fList.length>0){
				//package selected files
				if(null != fileList && fileList.size()>0){
					for (int i=0; i< fList.length; i++){
//					System.out.println(i+"      fileList name -----------------     :"+fileList.get(i));
						rootFolderPath="";
						retrieveFileName(fList[i]);
						//if directory then call function to add all files in it
						if(fList[i].isDirectory())
						{
							zipFilesInFolder(fList[i], baseName);
						}
						for(int j=0; j<fileList.size(); j++){
							//if file is in list then zip it
							String compareStr = rootFolderPath.concat(fList[i].getName().toString().trim());
							if(compareStr.equals(fileList.get(j).toString())){
								if(fList[i].exists()){
										zipFilesInFolder( fList[i],fileList.get(j).toString()) ;
								}
							}
						}
					}
				}
				// package all the files if given fileList is null
				else
				{
					for (int i=0; i< fList.length; i++){
						if(fList[i].exists())
							zipFilesInFolder( fList[i],baseName) ;
					}
				}
			}
		} 
		// Add a file to the zip entry
		else {
			//Discard .wgz files from packaging
			String name = pkgFile.getName();
			String compare ="";
			if(name.length()>3 && null!= name){
				String tempStr = name.substring(name.length()-4, name.length());
				compare = tempStr;
			}
			
			//Discard .wgz file
			if(compare.length()>0 && compare.equalsIgnoreCase(".wgz")){
				logger.log(Level.INFO, ".wgz file discarded for Package: "+name);
				reportStatus(MessageFormat.format(PackagerMessages.getString("package.discard.wgz")
						,new Object[] {name}));
			}
			else{
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(pkgFile);
					
					ZipEntry zipEntry = new ZipEntry(baseName);
					pkgZipOutputStream.putNextEntry(zipEntry);
					
					
//					System.out.println("Copy >>>-->>    ");
					try{
						copy(fis, pkgZipOutputStream);
						
//						IOUtils.copy(fis, pkgZipOutputStream);
//						IOUtils.copyLarge(fis, pkgZipOutputStream);
						
					
					} catch (Exception e) {
							
						e.printStackTrace();
						
					}
//					System.out.println("Copy <<----<<    ");
					
					pkgZipOutputStream.closeEntry();
					fis.close();
				} catch (IOException e) {
					logAndThrowErrors(Level.SEVERE, WRTPackagerConstants.LOG_ZIP_FAILED,e);
				}
				finally{
					try {
						pkgZipOutputStream.closeEntry();
						fis.close();
					} catch (IOException e) {
						logAndThrowErrors(Level.SEVERE, e.getMessage(), e);
					}
				}
			}
		}
	}
	
	/**
	 * retrieve file name from source folder to current path
	 * @param file
	 */
	private void retrieveFileName(File file) {
		if(null !=file.getParent()){
			rootFolderPath=file.getParentFile().getName().toString().trim().concat("/") //$NON-NLS-1$
								.concat(rootFolderPath);
			//if it reaches to package source filder then return
//			if(file.getParentFile().getName().toString().trim().equals(wFile.getName())){
			if(file.getParentFile().getAbsolutePath().toString().trim()
					.equalsIgnoreCase(wFile.getAbsolutePath())){
				return;
			}
			else{
				retrieveFileName(file.getParentFile());
			}
		}
		
	}


	/**
	 * Reporting status to the GUI
	 * @param statusMessage - message to be delivered
	 */
	public void reportStatus(String statusMessage) {
		WRTStatus status = new WRTStatus();
		status.setStatusSource(IWRTConstants.StatusSourceType.PACKAGER.name());
		status.setStatusDescription(statusMessage);
		statusListener.emitStatus(status);
		
		
	}
	/**
	 * This method is used for logging and exception handling
	 * @param lev -- severity Level
	 * @param logMessage -- message to log
	 * @param e -- exception to throw, if its not an exception then it should be null
	 * @throws PackagerException
	 */
	private void logAndThrowErrors(Level lev , String logMessage, Exception e) throws PackageException {
		logger.log(lev, logMessage);
		reportStatus(WRTPackagerConstants.STA_PKG_FAILED);
		//for exceptions log error message and stack trace
		if(null!=e){
			Util.logEvent(logger, Level.SEVERE, e);
			throw new PackageException(e);
		}
	}
/**
 * 
 * @param input
 * @param output
 * @return
 * @throws IOException
 */	
	
    public static int copy(InputStream input, OutputStream output) throws IOException {
    	
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        
        return (int) count;
    }

    /**
     * Copy bytes from a large (over 2GB) <code>InputStream</code> to an
     * <code>OutputStream</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * 
     * @param input  the <code>InputStream</code> to read from
     * @param output  the <code>OutputStream</code> to write to
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException if an I/O error occurs
     * 
     *      */
    public static long copyLarge(InputStream input, OutputStream output)
            throws IOException {
    	
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        
        return count;
    }
	


}
