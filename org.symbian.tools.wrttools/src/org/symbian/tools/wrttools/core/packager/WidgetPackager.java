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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.symbian.tools.wrttools.core.status.IWRTConstants;
import org.symbian.tools.wrttools.core.status.IWRTStatusListener;
import org.symbian.tools.wrttools.core.status.WRTStatus;
import org.symbian.tools.wrttools.core.status.WRTStatusHandler;
import org.symbian.tools.wrttools.util.Util;

/**
 * This is the main class for core component Packager.
 * This class reads from the given source file and creates widget package at 
 * the given destination path
 * @author ranoliya
 *
 */
public class WidgetPackager implements IWRTPackager {
	
	// source path for packager
	private String srcPath = "";
	
	//destination path for packager
	private String destPath = ""; 
	
	//name of the package
	private String packageName = "";
	
	//list of files to package
	private List<String> listOfFiles ;

	//status handler for creating status listener
	private WRTStatusHandler statusHandler;
	
	private IWRTStatusListener statusListener;
	
	//Logger to log errors and info
	private Logger logger = Logger.getLogger(getClass().getName());
	
	private String errorMessage = "";
			
	/**
	 * Class Constructor the packaging process
	 */
	public WidgetPackager(IWRTStatusListener  wrtStatusListener) {
		statusHandler = new WRTStatusHandler();
		statusHandler.addListener(wrtStatusListener);
		statusListener = wrtStatusListener;
	}

	/**
	 * Method packages the input folder, Initial step for Packaging
	 * @param sPath -- Project Source path
	 * @param dPath -- Package destination path
	 * @param fileList -- List of files to be included in the package
	 * @throws PackageException
	 */
	public void packageWidget(String sPath, String dPath,List<String> fileList) throws PackageException
	{
		if(null != sPath || null != dPath){
		
			listOfFiles = new ArrayList<String>();
			this.listOfFiles = fileList;	
		
			sPath = Util.replaceChar(sPath, File.separatorChar, '/');
			this.srcPath = sPath;	
			reportStatus(WRTPackagerConstants.STA_PKG_START);
			if(!validateFilesToPackage()){
				
				reportStatus(WRTPackagerConstants.STA_PKG_FAILED);
				errorMessage = PackagerMessages.getString("package.missing.mandatory.files");
				throw new PackageException(PackagerMessages.getString("package.missing.mandatory.files"));

			}				
			// if destination path is not specified
			if(dPath.equals("")){ //$NON-NLS-1$
				dPath=sPath;
			}			
			this.packageName=new File(dPath).getName();			
			File destDir = new File(dPath);
			dPath = destDir.getParentFile().getAbsolutePath().concat("/");
			this.destPath = Util.replaceChar(dPath, File.separatorChar, '/');	
			

			initPackage();
		}
		else
		{
			reportStatus(WRTPackagerConstants.LOG_NULL_INPUT);
			logAndThrowErrors(Level.SEVERE, WRTPackagerConstants.LOG_NULL_INPUT, null);
		}
	}
	
	/**
	 * This is the packager invocation  method that calls packager main API
	 * @throws PackagerException
	 */
	private void initPackage() throws PackageException {
		File destFile = new File(destPath);
		if(!destFile.canWrite() || !destFile.exists()|| (destFile.exists() && destFile.isFile())){ // to avoid considering file as existing dir
			boolean success = destFile.mkdir();
			if(!success){
				reportStatus(WRTPackagerConstants.ERR_DIR_CREATE_FAIL);
				reportStatus(WRTPackagerConstants.STA_PKG_FAILED);
				throw new PackageException();
//				return;
			}
		}
		
		
		int fileType = checkInputType();
		
		String srcStatus = MessageFormat.format(PackagerMessages.getString("WidgetPackager.WidgetPackager.filesFrom")
				,new Object[]{srcPath}); 
		
		
		
		reportStatus(srcStatus);
		
		boolean pass = createWidgetPackage(fileType);
		if(pass){
			String destStatus =  MessageFormat.format(PackagerMessages.getString("WidgetPackager.WidgetPackager.fileDest")
					,new Object[]{destPath.concat(packageName)});
			reportStatus(destStatus);
			reportStatus(WRTPackagerConstants.STA_PKG_PASSED);
		}
	}

	
	/**
	 * This method checks for the user input for Packager and returns File Type
	 * @return int -- File Type
	 * @throws PackagerException 
	 */
	public int checkInputType() throws PackageException {
		int fileType = 2;
		File checkInput ;
		
		checkInput = new File(srcPath);
		if(checkInput.exists()){
		
			// user input for packaging is project directory
			if (checkInput.isDirectory()) {
				fileType = 1; 
			}
			
			// user input is unknown to packager - report to status
			else {
				fileType = 2;
				
				logAndThrowErrors(Level.SEVERE, WRTPackagerConstants.LOG_UNSUPPORTED_INPUT,null);
			}
		}
		return fileType;
	}

	/**
	 * This method returns the Destination path for the packaged widget
	 * @return
	 */
	public String getDestPath() {
		return destPath;
	}

	/**
	 * Reporting status
	 * @param statusMessage
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
		
		//for exceptions log error message and stack trace
		if(null!=e){
			reportStatus(WRTPackagerConstants.STA_PKG_FAILED);
			Util.logEvent(logger, Level.SEVERE, e);
			throw new PackageException(e);
		}
	}

	/**
	 * This method creates Widget package-- overridden  
	 * @param - Input file type 
	 */
	private boolean createWidgetPackage(int fileType) throws PackageException {
		String widgetPath= ""; 
		String zipPath = ""; 
		boolean sucess = false;
		boolean packageDone = false;
		PackagerZipFolder zipFolder = new PackagerZipFolder();
		
		widgetPath = destPath.concat(packageName); //$NON-NLS-1$
		File reWrite = new File(widgetPath);
		if(reWrite.exists())
			reWrite.delete();
		//if source path and destination path are same
		if(srcPath.equals(destPath)){
			// Project folder as an input
			if (fileType==1){
				zipPath = destPath.concat("tmpwid.zip"); //$NON-NLS-1$
				zipFolder.zipProjectFolder(srcPath, zipPath,listOfFiles,statusListener);
//				zipFolder.zipProjectFolder(srcPath, zipPath,listOfFiles);
				
			}
			//unsupported file type
			else{
				logAndThrowErrors(Level.SEVERE, WRTPackagerConstants.LOG_UNSUPPORTED_INPUT,null);
			}
		}
		else {
			if (fileType==1){
				zipPath = destPath.concat("tmpwid.zip");//$NON-NLS-1$
//				zipFolder.zipProjectFolder(srcPath,zipPath,listOfFiles,statusListener );
				zipFolder.zipProjectFolder(srcPath, zipPath,listOfFiles);
			}
			else{
				logAndThrowErrors(Level.SEVERE, WRTPackagerConstants.LOG_UNSUPPORTED_INPUT, null);
			}
		}
		
		//rename file
		//created zip file
		File zFile = new File(zipPath);


		//new package name to use for rename  appended wgz.  This logic should be removed from package zip folder.
		File newFile = new File(widgetPath+".wgz");
		
		//delete .wgz if already exists
		if(newFile.exists()){
			newFile.delete();
		}
		//rename file
		if(zFile.exists()){
			sucess = zFile.renameTo(newFile);
		}
		//overwrite the existing file
		if(!sucess)
		{
				logAndThrowErrors(Level.WARNING, PackagerMessages.getString("WidgetPackager.fileOverwriteMsg"),null); 
		}
		
		File tempZip = new File(zipPath);
		//delete temp file if exists
		if(tempZip.exists())
		{
			tempZip.delete();
		}
		packageDone = true;
		return packageDone;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	
	/**
	 * This method validates basic files (.html and .plist) checked in packaging option tree
	 * @return -- mandatory files exists or not 
	 */
	private boolean validateFilesToPackage(){
		boolean isPlist = false;
		boolean isHtml = false;
		File srcDir = new File(srcPath);
		String plistFile = srcDir.getName().concat("/info.plist");
		
		if(listOfFiles.size()>0 && null != listOfFiles){
			for (String fileName : listOfFiles) {	
				try{
					if (fileName.equalsIgnoreCase(plistFile)) {
					isPlist = true;
				}				
				int len = fileName.lastIndexOf("/");
				String fName=fileName.substring(len+1, fileName.length());
//				 files with out extention  will have index out of bound exception
				int extLen = fName.lastIndexOf('.');
                String extention = "";
                if(extLen > 0){
                	extention = fName.substring(extLen, fName.length());
                }
				
				String htmlFile = srcDir.getName().concat("/").concat(fName);
				if(htmlFile.equalsIgnoreCase(fileName) && 
						(extention.equalsIgnoreCase(".htm")|| extention.equalsIgnoreCase(".html"))){
					isHtml = true;
				}
				
				if(isPlist && isHtml){
					return true;
				}
			}
			// files with out extention  will have index out of bound exception
//				try catch will catch that and will proceed to the next file in the 
//				for loop
			catch(Exception e){
//				e.printStackTrace();
				}
		}
			}
		String srcStatus = MessageFormat.format(PackagerMessages.getString("WidgetPackager.WidgetPackager.filesFrom")
				,new Object[]{srcPath});
		reportStatus( srcStatus);
		if(!isPlist ){
			 reportStatus( WRTPackagerConstants.ERR_PKG_MAN_PLIST_FILE_MISSING);
			
		}
		if(!isHtml){
			 reportStatus( WRTPackagerConstants.ERR_PKG_MAN_HTML_FILE_MISSING);
			
		}
		
		return (isPlist && isHtml);
	}
	
}
