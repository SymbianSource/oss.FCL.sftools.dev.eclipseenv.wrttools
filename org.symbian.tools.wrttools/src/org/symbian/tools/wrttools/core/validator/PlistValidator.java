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
import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Logger;

import org.symbian.tools.wrttools.core.exception.ValidationException;
import org.symbian.tools.wrttools.core.parser.XmlElement;
import org.symbian.tools.wrttools.core.report.Message;
import org.symbian.tools.wrttools.core.report.MessageHandler;
import org.symbian.tools.wrttools.core.status.IWRTConstants;
import org.symbian.tools.wrttools.core.widgetmodel.WidgetModel;
import org.symbian.tools.wrttools.util.FileUtil;
import org.symbian.tools.wrttools.util.Util;

/**
 * PlistValidator Accepts the List <IMessage> messageList and will not process
 * it. Accept String fileName. Use HtmlParser to get all elements in a stack.
 * 
 * @author Sailaja duvvuri
 */
public class PlistValidator implements IValidator {
	
	public enum plistElements {
		plist, array, data, date, dict, real, integer, string, FALSE, TRUE, key, xml

	};

	public enum mandatoryPlistValues {
		DisplayName, Identifier, MainHTML
	};

	
//	private Hashtable<String, String> plistHashKey = null;
	private MessageHandler messageHandler;
	private FileUtil fileUtil = new FileUtil();
	private String plistFileName = null;
	private File plistFile = null;
	
	public List<String> filesToValidate;
	public String projDirParenPath;
	public String projDirName;
	
	private boolean filesSeleced = false;
	private boolean allRulesPassed;	
	
	private WidgetModel widgetModel;
	

	private Logger log = Logger.getLogger(getClass().getName());
	
	
	
	public WidgetModel getWidgetModel() {
		return widgetModel;
	}

	public void setWidgetModel(WidgetModel widgetModel) {
		this.widgetModel = widgetModel;
	}
	public MessageHandler getMessageHandler() {
		return messageHandler;
	}


	public void setMessageHandler(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}

	public List<String> getFilesToValidate() {
		return filesToValidate;
	}

	public void setFilesToValidate(List<String> filesToValidate) {
		this.filesToValidate = filesToValidate;
	}
/**
	 * @throws ValidationException 
 * @throws Exception 
	 * 
	 */
	public boolean validate( File widgetFile) throws ValidationException{
		log.info("validate>>--->>");
		List <File>dirList = fileUtil.getCurrDirFiles(widgetFile);
		projDirParenPath=widgetFile.getPath().substring(0,widgetFile.getPath().indexOf(widgetFile.getName()));
		projDirName=widgetFile.getName();
		FileUtil filehelper = new FileUtil();
	
		if((filesToValidate!=null&&filesToValidate.size()>1)){
			filesSeleced=true;
		}else{
			filesToValidate=filehelper.getCurrDirFileNames(widgetFile);			
		}

		plistFileName =widgetModel.getPlistFileName();
		projDirParenPath=widgetModel.widgetDirectory;
		projDirName=widgetModel.getWidgetName();
	
		if(plistFileName!=null&& plistFileName.length()>0)
		{
		plistFile=new File(projDirParenPath+projDirName+"/"+plistFileName);
		
		}else{		
			Message msg = new Message();
			msg.setMessageSource(IWRTConstants.VALIDATOR);
			msg.setMessageKey("plist.file.not.present");
			msg.setMessage(ValidatorPropMessages.getString("plist.file.not.present"));			
			msg.setRecommendAction(ValidatorPropMessages.getString("todo.include.plist"));
			msg.setFileTypeZip(getMessageHandler().isFileTypeZip());				
			msg.setSeverity(IWRTConstants.FATAL);
			getMessageHandler().publishMessage(msg);
			throw new ValidationException(ValidatorPropMessages.getString("plist.file.not.present"));
		}
		if(widgetModel.getPlistXmlFile()!=null){
			checkWellformedAndValidate();
			checkHtml(dirList);
			checkPlistValues();
			
		}else{
			Message msg = new Message();
			msg.setMessageSource(IWRTConstants.VALIDATOR);
			msg.setMessageKey("no.plist.element");
			msg.setMessage(ValidatorPropMessages.getString("no.plist.element"));
			msg.setFileTypeZip(getMessageHandler().isFileTypeZip());
			msg.setTargetObject(plistFileName);
			msg.setFullPath(plistFile.getAbsolutePath());
			msg.setSeverity(IWRTConstants.ERROR);
			msg.setRecommendAction(ValidatorPropMessages.getString("todo.no.plist.element"));
			getMessageHandler().publishMessage(msg);				
		allRulesPassed=false;	
			
			
			
		}		
	
		

		log.info("validate<<---<<");
		return allRulesPassed;
	}

	
	
	
	//---------------check plist wellformness----------------------//
	/**
	 * 
	 * @return
	 */
	private boolean checkWellformedAndValidate() {
		log.info("checkWellformedAndValidate>>---->>");
		boolean returnVal=true;
//		//commented out because  saxPlistParser is slow -------->>
//		PlistValidatorParser saxPlistParser= new PlistValidatorParser();
//		saxPlistParser.setMessageHandler(this.getMessageHandler());

//		if(!(saxPlistParser.parsePlist(projDirParenPath+projDirName+"/"+widgetModel.getPlistFileName())))
//		///<<-----------------------------<<
		{			
			
			parsePlistError(widgetModel.getPlistXmlFile() );
			validateElement(widgetModel.getPlistXmlFile());
//			if()
			returnVal= false;
		}
		log.info("checkWellformedAndValidate<<---<<");
		
		return returnVal;
	}
	
	
	private void parsePlistError(XmlElement rootXmlElement) {
		log.info("parsePlistError>>-->>");
		
		if (rootXmlElement.getErrorList() != null) {
			int errSize = rootXmlElement.getErrorList().size();
			for (int i = 0; i < errSize; i++) {
				Message msg = rootXmlElement.getErrorList().get(i);
				getMessageHandler().publishMessage(msg);

			}
		}

		if (rootXmlElement.getChildList() != null)
			for (XmlElement elm : rootXmlElement.getChildList()) {
				parsePlistError(elm);
			}
		log.info("parsePlistError<<---<<");
		
	}
	
	//---------------------------check html------------------------------//
	/**
	 * 
	 * @param dirList
	 * @return
	 */
	
	private boolean checkHtml(List<File> dirList) {
			boolean checkHtml = false;
		log.info("checkHtml>>--->>");
			if (widgetModel.getMainHtml()!=null&&widgetModel.getMainHtml().trim().length()>0){
			String htmlName	=widgetModel.getMainHtml();
		File htmlFile = fileUtil.getFile(dirList, widgetModel.getMainHtml());
		if(htmlFile==null)
		{
			htmlFile = fileUtil.getFile(dirList, "html","htm");			
			}
			if (htmlFile != null) {
				
				if (htmlFile.getName().equalsIgnoreCase(htmlName)) {
				checkHtml=true;
			} else {
				
				Message msg = new Message();
				msg.setMessageSource(IWRTConstants.VALIDATOR);
				msg.setMessageKey("plist.html.element.mismatch");				
				Object[] arguments = {htmlFile.getName() , "<>" , htmlName}; 
				String message = MessageFormat.format(ValidatorPropMessages.getString("plist.html.element.mismatch")+"{0}"+"{1}"+"{2}",arguments); 
				msg.setMessage(message);				
				msg.setFileTypeZip(getMessageHandler().isFileTypeZip());
				
				msg.setTargetObject(plistFile.getName());
				msg.setFullPath(plistFile.getAbsolutePath());
				msg.setSeverity(IWRTConstants.ERROR);
				msg.setRecommendAction(ValidatorPropMessages.getString("todo.correct.plist.for.html"));				
				getMessageHandler().publishMessage(msg);
				allRulesPassed=false;
			}
		}
	}else{
		
		Message msg = new Message();
		msg.setMessageSource(IWRTConstants.VALIDATOR);
		msg.setMessageKey("plist.html.element.mailHtml.missing");				
		msg.setMessage(ValidatorPropMessages.getString("plist.html.element.mailHtml.missing"));
		
		msg.setFileTypeZip(getMessageHandler().isFileTypeZip());		
		msg.setTargetObject(plistFile.getName());
		msg.setFullPath(plistFile.getAbsolutePath());
		msg.setSeverity(IWRTConstants.ERROR);
		msg.setRecommendAction(ValidatorPropMessages.getString("todo.correct.plist"));
		
		getMessageHandler().publishMessage(msg);
		allRulesPassed=false;	
	}
		
			log.info("checkHtml<<---<<");
		
		return checkHtml;
	}	
	
	
	
	
	
	
	private boolean checkPlistValues() {
		boolean checkPlistValues = true;
		log.info("checkPlistValies >>--->>");		
				
			if(!(widgetModel.getDisplayname()!=null&&widgetModel.getDisplayname().trim().length()>0)){
				Message msg = new Message();
				msg.setMessageSource(IWRTConstants.VALIDATOR);
				msg.setMessageKey("plist.mankey.mising");
				
				Object[] arguments = {" 'Display Name'  ", ValidatorPropMessages.getString("not.in.plist.file")}; 
				String message = MessageFormat.format(ValidatorPropMessages.getString("plist.mankey.mising")+"{0}"+"{1}",arguments); 
				msg.setMessage(message);

				msg.setFileTypeZip(getMessageHandler().isFileTypeZip());

				msg.setTargetObject(plistFile.getName());
				msg.setFullPath(plistFile.getAbsolutePath());
				Object[] arguments2 = {" 'Display Name'  "  ,"to plistFile."}; 
				String message2 = MessageFormat.format(ValidatorPropMessages.getString("todo.add.mankey.plist.element")+"{0}"+"{1}",arguments2); 
				msg.setRecommendAction(message2);
				
				msg.setSeverity(IWRTConstants.ERROR);
				getMessageHandler().publishMessage(msg);
				
			} else /* validating Widget name (Display name) */
			{
				String strError = Util.validateWidgetName(widgetModel.getDisplayname());				
				if ( strError != null ){
					Message msg = new Message();
					msg.setMessageSource(IWRTConstants.VALIDATOR);
					msg.setMessageKey("plist.mankey.mising");
					
					Object[] arguments = {" 'Display Name'  ", ValidatorPropMessages.getString("contains.invalid.character")}; 
					String message = MessageFormat.format(ValidatorPropMessages.getString("plist.mankey.mising")+"{0}"+"{1}",arguments); 
					msg.setMessage(message);

					msg.setFileTypeZip(getMessageHandler().isFileTypeZip());

					msg.setTargetObject(plistFile.getName());
					msg.setFullPath(plistFile.getAbsolutePath());
					Object[] arguments2 = {" 'Display Name'  "  ,"to plistFile."}; 
					String message2 = MessageFormat.format(ValidatorPropMessages.getString("todo.valid.character")+"{0}"+"{1}",arguments2); 
					msg.setRecommendAction(message2);
					
					msg.setSeverity(IWRTConstants.ERROR);
					getMessageHandler().publishMessage(msg);					
				}
			}

			if(!(widgetModel.getMainHtml()!=null&&widgetModel.getMainHtml().trim().length()>0)){
				Message msg = new Message();
				msg.setMessageSource(IWRTConstants.VALIDATOR);
				msg.setMessageKey("plist.mankey.mising");

				Object[] arguments = {" 'MainHTML '  ", ValidatorPropMessages.getString("not.in.plist.file")}; 
				String message = MessageFormat.format(ValidatorPropMessages.getString("plist.mankey.mising")+"{0}"+"{1}",arguments); 
				msg.setMessage(message);

				msg.setFileTypeZip(getMessageHandler().isFileTypeZip());

				msg.setTargetObject(plistFile.getName());
				msg.setFullPath(plistFile.getAbsolutePath());
				Object[] arguments2 = {" 'MainHTML'  "  ,"to plistFile."}; 
				String message2 = MessageFormat.format(ValidatorPropMessages.getString("todo.add.mankey.plist.element")+"{0}"+"{1}",arguments2); 
				msg.setRecommendAction(message2);
				
				msg.setSeverity(IWRTConstants.ERROR);
				getMessageHandler().publishMessage(msg);
				
			}
			if(!(widgetModel.getIdentifier()!=null&&widgetModel.getIdentifier().trim().length()>0)){
				Message msg = new Message();
				msg.setMessageSource(IWRTConstants.VALIDATOR);
				msg.setMessageKey("plist.mankey.mising");
				Object[] arguments = {" 'Identifier '  ", ValidatorPropMessages.getString("not.in.plist.file")}; 
				String message = MessageFormat.format(ValidatorPropMessages.getString("plist.mankey.mising")+"{0}"+"{1}",arguments); 
				msg.setMessage(message);

				msg.setFileTypeZip(getMessageHandler().isFileTypeZip());

				msg.setTargetObject(plistFile.getName());
				msg.setFullPath(plistFile.getAbsolutePath());
				Object[] arguments2 = {" 'Identifier'  "  ,"to plistFile."}; 
				String message2 = MessageFormat.format(ValidatorPropMessages.getString("todo.add.mankey.plist.element")+"{0}"+"{1}",arguments2); 
				msg.setRecommendAction(message2);
				
				msg.setSeverity(IWRTConstants.ERROR);
				getMessageHandler().publishMessage(msg);
				
			} else /* validating Widget Idenfier (UID) */
			{
				String strError = Util.validateWidgetID(widgetModel.getIdentifier());				
				if ( strError != null ){
					Message msg = new Message();
					msg.setMessageSource(IWRTConstants.VALIDATOR);
					msg.setMessageKey("plist.mankey.mising");
					
					Object[] arguments = {" 'Identifier'  ", ValidatorPropMessages.getString("contains.invalid.character")}; 
					String message = MessageFormat.format(ValidatorPropMessages.getString("plist.mankey.mising")+"{0}"+"{1}",arguments); 
					msg.setMessage(message);

					msg.setFileTypeZip(getMessageHandler().isFileTypeZip());

					msg.setTargetObject(plistFile.getName());
					msg.setFullPath(plistFile.getAbsolutePath());
					Object[] arguments2 = {" 'Identifier'  "  ,"to plistFile."}; 
					String message2 = MessageFormat.format(ValidatorPropMessages.getString("todo.valid.character")+"{0}"+"{1}",arguments2); 
					msg.setRecommendAction(message2);
					
					msg.setSeverity(IWRTConstants.ERROR);
					getMessageHandler().publishMessage(msg);					
				}
			}
			log.info("checkPlistValues<<---<<");
		return checkPlistValues;
	}
	

	private void validateElement(XmlElement rootXmlElement) {
		log.info("validateElement >>--->>");		
		
		
//		showData("");
			plistElements[] values = plistElements.values();
			boolean isValidElement = false;
			for (plistElements validElement: values)
			{
				if (validElement.toString().equalsIgnoreCase(rootXmlElement.getName().trim()))
						{
					         isValidElement=true;
					         break;
					
						}
				continue;
			}
			if (! isValidElement)
			{
				Message msg = new Message();
				
				msg.setMessageSource(IWRTConstants.VALIDATOR);
				msg.setMessageKey("plist.element.not.supported");
				String msgtxt=ValidatorPropMessages.getString("plist.element.not.supported");
				Object[] arguments = {"   "  ,rootXmlElement.getName().trim()}; 
				String message = MessageFormat.format(ValidatorPropMessages.getString("plist.element.not.supported")+"{0}"+"{1}",arguments); 
				msg.setMessage(message);

				
				msg.setRecommendAction(ValidatorPropMessages.getString("todo.plist.element.not.Valid"));
				msg.setFileTypeZip(getMessageHandler().isFileTypeZip());
			
				msg.setTargetObject(plistFile.getName());
				msg.setFullPath(plistFile.getAbsolutePath());
				msg.setSeverity(IWRTConstants.ERROR);
				msg.setLineNumber(rootXmlElement.getLineNo());
				// need to set all msg fields
				getMessageHandler().publishMessage(msg);
				allRulesPassed=false;
			}		
		

		if (rootXmlElement.getChildList() != null)
			for (XmlElement elm : rootXmlElement.getChildList()) {
				validateElement(elm);
			}
		log.info("validateElement <<---<<");		
		
	}
	
	

	  private static void showData(String s) {
		}
	
	public static void main(String argv[]) throws Exception {
		PlistValidator validator = new PlistValidator();
//		validator.trimElementSpace("    s  ai   lu  ");
//		boolean isbegin=validator.isBeginAndCloseElement("<   key  >", "< \\    key1  >");

		final String XML_FILE_NAME = "C:\\Parser\\sax\\info.plist";
		try {
			validator.validate( new File (XML_FILE_NAME));
		} catch (ValidationException e) {			
		}
//		 bl.stackData("C:\\Parser\\sax\\info.plist");
	}

	

}
