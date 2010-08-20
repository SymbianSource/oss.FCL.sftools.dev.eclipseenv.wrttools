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
package org.symbian.tools.wrttools.core.validator;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.symbian.tools.wrttools.core.exception.ReportException;
import org.symbian.tools.wrttools.core.exception.ValidationException;
import org.symbian.tools.wrttools.core.report.IMessageListener;
import org.symbian.tools.wrttools.core.report.MessageHandler;
import org.symbian.tools.wrttools.core.status.IWRTConstants;
import org.symbian.tools.wrttools.core.status.IWRTStatusListener;
import org.symbian.tools.wrttools.core.status.WRTStatus;
import org.symbian.tools.wrttools.core.status.WRTStatusHandler;
import org.symbian.tools.wrttools.core.widgetmodel.WidgetModel;
import org.symbian.tools.wrttools.util.Util;

/**
 * The primary class for client to invoke various validation functionalities.  This is the only
 * class user to interact.  It has public methods to validate individual files html, plist, 
 * The methods of WidgetValidator class returns boolean to the calling class.   
 *  
 * The primary mechanism (class )for user/client  to invoke  various validation functionalities.
 * This is the only class user should interact with. It has Public methods for validating individual 
 * files (Plist, html ). This class provides method for validating a widget project and 
 * also a widget projectzipfile.
 * 
 * Instantiate widget model class here.  Iterate through the directory to get the file names, (.html, .plist, .css, .javascript)
 * parse through these files to validate.  
 * Validate to check if 1. .html, .plist are present or not.  
 *  
 * .html - will look for valid Document Type Definition (DTD) declaration. 
 *        (<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">)
 *  
 * .plist -  This file will be parsed to verify for the Nokia specific DTD 
 *        
 *            1. <key >DisplayName</key> - display name should exist
 *            2. <key>Identifier</key>  - value should be of org.symbian.tools.wrttools. **
 *            3. <key>MainHTML</key>    - value should be same as the .html file name
 * 
 *  
 * @author      sduvvuri
 * @version     1.0
 */
public class WidgetValidator {
//	private boolean widgetProjetValidation;
	private boolean manFileVal;
	private boolean plistVal;
	private boolean htmlVal;
	private MandatoryFilesValidator manFilevalidator;
	private PlistValidator plistValidator;
	
	private MessageHandler messageHandler;
	private IMessageListener msgListener;
	private WRTStatusHandler statusHandler;
	private IWRTStatusListener  wrtStatusListener;
	
	public List<String> filesSelected;
	WidgetModel widgetModel;
	private Logger log = Logger.getLogger(getClass().getName());
//	  
	
			
	/**
	 * Class Constructor the packaging process
	 */
	public WidgetValidator(IWRTStatusListener  wrtStatusListener) {
		statusHandler = new WRTStatusHandler();
		statusHandler.addListener(wrtStatusListener);
		this.wrtStatusListener = wrtStatusListener;
	}
//	
	
	/**
 * Method will validate a widget project.
 * Accept Variable widgetProjectDirName (Widget Project directory) and enumerate the files, call Individual validation procedures.
 * Even one validation is failed method will complete all validation and
 * add message to the message List.
 * A dummy List<IMessage> is passed as parameter to which the Rule methods will add
 * messages when a validation rules is failed.
 * Boolean variable will be returned to indicate the overall status of validation
 * even if one procedure is failed boolean false will be returned.
 * If returned value is false then List<IMessage> messageList size will be greater than zero
 * if returned value is true the List<IMessage> messageList will be empty
 * creating a dummy list to pass a parameter to the calling method
 * this list will be used by the rules method to add the validation messages
 * if any rules validation is failed
 * 
 * Passing List <IMessage> messageList will be removed from here.
 *  
 * @param fileName
 * @return
 * @throws ReportException 
 * @throws ValidationException 
 * 
 */
	
	WRTStatus status ;
	
	public WidgetValidator(IMessageListener msgListener,
			IWRTStatusListener wrtStatusListener)

	{
		log.finest(" WidgetValidator>>--->>");
		statusHandler = new WRTStatusHandler();
		statusHandler.addListener(wrtStatusListener);
		this.setWrtStatusListener(wrtStatusListener);
		messageHandler = new MessageHandler();
		messageHandler.registerListener(msgListener);
		status = new WRTStatus();
		status.setStatusSource(IWRTConstants.StatusSourceType.VALIDATOR.name());
		log.finest(" WidgetValidator<<---<<");
		
	}

	

	public boolean validateWidgetProject(File widget) throws ValidationException, ReportException  {
		
		// Here call the WidgetModel class to return the  file.
		log.info(" >>--->> validateWidgetProject");		
		
		 widgetModel= new WidgetModel();			
		File widgetProjFile;
//		MessageHandler	messageHandler.setFileTypeZip(false);
		
		try {
						
			widgetModel.setMessageHandler(getMessageHandler());
			widgetModel.setStatusHandler(statusHandler);
			widgetModel.setFilesSelected(getFilesSelected());
			
			 widgetModel.getWidgetModel(widget);
			 widgetProjFile=widgetModel.getWidgetModelFile();
			
		log.info("widgetProjFile   :"+widgetProjFile.getName());
			log.info(" calling ValidateMandatoryFiles ");
		
		ValidateMandatoryFiles(widgetProjFile);
		log.info("ValidateMandatoryFiles  is done");
		validatePlist( widgetProjFile);
		log.info("Validate plist is done");
		
		} catch (ReportException e) {
			 Util.logEvent(log, Level.INFO, e);	
			 status.setStatusDescription(ValidatorPropMessages.getString("initialize.messagemanager"));
			statusHandler.emitStatus(status);
			throw(e);
		}
		log.finest("validateWidgetProject <<---<<");
		
		if (manFileVal && plistVal && htmlVal) {
			return true;
		} else {
			return false;
		}
	}
	
	
	public boolean validateWidgetProject(String widgetNameFullPath) throws ValidationException, ReportException  {
		
		// Here call the WidgetModel class to return the  file.
		log.finest(" >>--->> validateWidgetProject");		
		 widgetModel= new WidgetModel();			
		File widgetProjFile;
//		messageHandler.setFileTypeZip(false);	
		try {
			Util.showData(getFilesSelected(), "selected Files");
						
			widgetModel.setMessageHandler(getMessageHandler());
			widgetModel.setStatusHandler(statusHandler);
			widgetModel.setFilesSelected(getFilesSelected());
			 widgetModel.getWidgetModel(widgetNameFullPath);
			 widgetProjFile=widgetModel.getWidgetModelFile();
			
		log.info("widgetProjFile   :"+widgetProjFile.getName());
		
		ValidateMandatoryFiles(widgetProjFile);
		validatePlist( widgetProjFile);
		log.info("Validate plist is done");
		
		} catch (ReportException e) {
			 Util.logEvent(log, Level.INFO, e);	
//			 status.setStatusDescription(ValidatorPropMessages.getString("initialize.messagemanager"));
//			statusHandler.emitStatus(status);
			emitStatus(ValidatorPropMessages.getString("initialize.messagemanager"));
			throw(e);
		}
		log.finest("validateWidgetProject <<---<<");
		
		if (manFileVal && plistVal && htmlVal) {
			return true;
		} else {
			return false;
		}
	}

/*
 * ReportHandler related function
 */
	
	public MessageHandler getMessageHandler() throws ReportException {
		log.finest(" >>---<<");
		if (messageHandler != null) {
			return messageHandler;
		} else {			
			throw new ReportException(
					"Message Manager  must be initialized  ");
		}

	}



	// Creating new Message List.
	private boolean ValidateMandatoryFiles(File widgetProjDir) throws ReportException, ValidationException
			{
		
		log.finest("ValidateMandatoryFiles-->>-->>");
		emitStatus(ValidatorPropMessages.getString("validate.man.file.started"));
//		statusHandler.emitStatus(status);
		manFilevalidator = new MandatoryFilesValidator();
		manFilevalidator.setWidgetModel(widgetModel);
		manFilevalidator.setSelectedFileList(filesSelected);
		manFilevalidator.setMessageHandler(getMessageHandler());
		manFileVal = manFilevalidator.validate(	widgetProjDir);

		
		emitStatus(ValidatorPropMessages.getString("validate.man.file.finished"));
//		statusHandler.emitStatus(status);
		log.finest("ValidateMandatoryFiles <<---<<");
			
		return manFileVal;
	}
	private boolean validatePlist(File fileName) throws ReportException, ValidationException{
		log.finest("validatePlist-->>-->>");
		emitStatus(ValidatorPropMessages.getString("validate.plist.started"));
		
//		statusHandler.emitStatus(status);
		

		plistValidator = new PlistValidator();
		plistValidator.setWidgetModel(widgetModel);
		plistValidator.setMessageHandler(getMessageHandler());
		if (widgetModel.isPlistPresent()) {
			plistValidator.setFilesToValidate(filesSelected);
			plistVal = plistValidator.validate( fileName);
		}
		emitStatus(ValidatorPropMessages.getString("validate.plist.finished"));
//		statusHandler.emitStatus(status);
		
		
		log.finest("validatePlist--<<--<<");
		return plistVal;
	}
	
	protected void emitStatus(String statusDescription) {
		WRTStatus status = new WRTStatus();
		status.setStatusSource(IWRTConstants.StatusSourceType.VALIDATOR.name());
		status.setStatusDescription(statusDescription);		
		getWrtStatusListener().emitStatus(status);
	}
	
	public IMessageListener getMsgListener() {
		return msgListener;
	}


	public void setMsgListener(IMessageListener msgListener) {
		this.msgListener = msgListener;
	}


	public IWRTStatusListener getWrtStatusListener() {
		return wrtStatusListener;
	}


	public void setWrtStatusListener(IWRTStatusListener wrtStatusListener) {
		this.wrtStatusListener = wrtStatusListener;
	}


	public List<String> getFilesSelected() {
		return filesSelected;
	}


	public void setFilesSelected(List<String> filesToValidate) {
		this.filesSelected = filesToValidate;
		
		 if (filesSelected != null && filesSelected.size() > 0) {
				for (String fileName : filesSelected) {
					fileName= Util.replaceChar(fileName, '\\', '/');
				}
			}
	}


	public WidgetModel getWidgetModel() {
		return widgetModel;
	}


	public void setWidgetModel(WidgetModel widgetModel) {
		this.widgetModel = widgetModel;
	}





	public void setMessageHandler(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}
	
	
	

	
	///**
	// * validateWidgetZip function takes zip file as a parameter.
	// * Enumerate the files, and parse through the files.
	// * 
	// * Input parameter List <IMessage> messageList will be removed from here.
	// * 
	// * @param zipfile
	// * @return
	// * @throws BaseException 
	// * @throws BaseException 
	// * @throws ValidatorException 
	// */	
//		private boolean validateWidgetZip(String fileName) throws BaseException, ValidatorException{
//			// HERE need to call the WidgetModel class to return the  file.
//			log.finest(" validateWidgetZip >>--->>");
//			WidgetModel widgetModel= new WidgetModel();			
//			File widgetProjFile;
////			messageManager.setMessageList(null);
//			messageManager.setFileTypeZip(true);
//			widgetProjFile = widgetModel.getWidgetModelFromZip(fileName, getMessageManager());
//			log.info("widgetProjFile   :"+widgetProjFile.getName());
//			ValidateMandatoryFiles(widgetProjFile);
//			validatePlist( widgetProjFile);
//			log.info("Validate plist is done");
//			validateHtml( widgetProjFile);	
//			FileUtil fileUtil = new FileUtil();		
//			fileUtil.deleteDirFile(  widgetProjFile);
//			log.finest("validateWidgetZip <<---<<");
//			
//			if (manFileVal && plistVal && htmlVal) {
//				return true;
//			} else {
//				return false;
//			}
//		}

}
