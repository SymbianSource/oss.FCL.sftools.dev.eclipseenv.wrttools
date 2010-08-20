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
import org.symbian.tools.wrttools.core.report.Message;
import org.symbian.tools.wrttools.core.report.MessageHandler;
import org.symbian.tools.wrttools.core.status.IWRTConstants;
import org.symbian.tools.wrttools.core.widgetmodel.WidgetModel;
import org.symbian.tools.wrttools.util.FileUtil;
import org.symbian.tools.wrttools.util.Util;

/**
 *Each project folder must contain mandatory files
 *a.        One  Html
 *b.        One  Plist 
 * This Class implements IValidator
 *Only one method validate(List <IMessage> messageList,String fileName)
 *The functionality of the mandatory files validation is to find all the files in the project 
 *directory and verify all the files are present the business logic will be implemented in the
 *rules class apply rules method Instantiate the MandatoryFilesRule and call the applyrules 
 *method ().
 *   - Calls the corresponding rules class and pass the filename
 * @author Sailaja Duvvuri
 *
 */
 
public class MandatoryFilesValidator implements IValidator {
	
	public enum FileTypeENUM {
		css, js, plist, png, jpeg, gif, jpg, htm, html, swf, strings

	};
	
	private Logger log = Logger.getLogger(getClass().getName());
	private boolean allRulesPassed;

	private boolean filesSeleced = false;
	public List<String> selectedFileList;
	public String widgetDirPath;
	public String widgetName;
	private MessageHandler messageHandler;
	private WidgetModel widgetModel;
	
	
    public List<String> getSelectedFileList() {
		return selectedFileList;
	}

	public void setSelectedFileList(List<String> filesToValidate) {
		this.selectedFileList = filesToValidate;
	}
	

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

	/*
     * Calls the corresponding rules class and pass the filename
     * List <IMessage> messageList may contain messages if the client has already
     * called called ant other validation and failed .
     */
    public boolean validate( File widgetFile) throws ValidationException {
		try{
    	log.info("validate( )>>--->>");
    	widgetDirPath=widgetModel.widgetDirectory;
		widgetName=widgetModel.getWidgetName();
		allRulesPassed = true;
		FileUtil filehelper = new FileUtil();
		List<String> fileNames=null;	
		
		if((selectedFileList!=null&&selectedFileList.size()>0)){
			filesSeleced=true;
			fileNames=selectedFileList;
		}else{
			selectedFileList=filehelper.getCurrDirFileNames(widgetFile);	
			fileNames=	  filehelper.getDirFileFullNames(widgetFile);
			
		}
	
		isPlistPresent();
		isHtmlPresent( fileNames);
		
		isPlistCopiesPresent( fileNames);
		
		log.info("validate<<---<<");
		}catch (Exception e) {	
//			e.printStackTrace();
			log.severe(e.getCause().toString());			
			throw new ValidationException(e);
		}
		return allRulesPassed;
	}

    /**
	 * Accept msg list and List of filenames Loop throught the filelist and see
	 * if Plist is present. if Plist is present return to the apply rule method
	 * if Plist is not present after the loop boolean var is set to false and an
	 * appropriate message is added to the msg list
	 * 
	 * @param messageList
	 * @param fileNames
	 */
    private void isPlistPresent() {   	

		log.info("isPlistPresent >>--->>");
    	
		if(widgetModel.isPlistPresent()){
			boolean plistSelected=false;
			
			if(filesSeleced){
				for(String fileName :selectedFileList){
					fileName = fileName.substring((fileName.indexOf(widgetName	+ "/")+ widgetName.length() + 1));
					
					if(fileName.equalsIgnoreCase(widgetModel.getPlistFileName())){
						plistSelected=true;
						break;
					}
						
				}
				
				
				if(!plistSelected){					
					Message msg = new Message();
					msg.setMessageSource(IWRTConstants.VALIDATOR);
					msg.setMessageSource(IWRTConstants.VALIDATOR);
					String key="plist.File.Not.selected";
					msg.setMessageKey(key);
					msg.setMessage(ValidatorPropMessages.getString(key)); //$NON-NLS-1$
					msg.setSeverity(IWRTConstants.FATAL);
					msg.setRecommendAction(ValidatorPropMessages.getString("todo."+key));
					// need to set all msg fields
					getMessageHandler().publishMessage(msg);
					allRulesPassed = false;						
				}
		    	}
		}else{
		Message msg = new Message();
		msg.setMessageSource(IWRTConstants.VALIDATOR);
		msg.setMessageKey("plist.File.Not.Present");
		msg.setMessage(ValidatorPropMessages.getString("plist.File.Not.Present")); //$NON-NLS-1$
		msg.setSeverity(IWRTConstants.FATAL);
		msg.setRecommendAction(ValidatorPropMessages.getString("todo.include.plist"));
		// need to set all msg fields
		getMessageHandler().publishMessage(msg);
		log.info("isPlistPresent <<---<<");
		allRulesPassed = false;
		}
	}
   
    /**
	 * 
	 * @param messageList
	 * @param fileNames
	 */
    private void isHtmlPresent(List<String>  fileNames){
        log.info("isHtmlPresent>>--->> new ");
        boolean htmlSelected = false;
    
        if(fileNames!=null&&fileNames.size()>0){
      
        for (String fileName : fileNames) {
            if ( filesSeleced && fileName.trim().startsWith(widgetName + "/")) {
                fileName = fileName.substring((fileName.indexOf(widgetName    + "/")+ widgetName.length() + 1));
                int extLen = fileName.lastIndexOf('.');
                String extention = "";
                if(extLen > 0){
                	extention = fileName.substring(extLen ,fileName.length());
                }
                    if (fileName.equalsIgnoreCase(widgetModel.getMainHtml())&& 
    						(extention.equalsIgnoreCase(".htm")|| extention.equalsIgnoreCase(".html"))) {
                        htmlSelected = true;
                    }

            }
           
            if(!filesSeleced){
                if (fileName.trim().startsWith(widgetDirPath+widgetName + "/")) {
                    fileName = fileName.substring((fileName.indexOf(widgetDirPath+widgetName+ "/")+ (widgetDirPath+widgetName+"/").length()));
               
                }
            }
            String fileType=fileName.substring(fileName.lastIndexOf('.')+1);
            if (fileType.trim().equalsIgnoreCase("htm")
                    || fileType.trim().equalsIgnoreCase("html")) {
                if(fileName!=null&&widgetModel.getMainHtml()!=null&&!fileName.trim().equalsIgnoreCase(widgetModel.getMainHtml().trim())){
                     if(!widgetModel.getReferencedHtmlFiles().contains(fileName.toLowerCase()))
                     {
                            Message msg= new Message();
                            msg.setMessageSource(IWRTConstants.VALIDATOR);
                            String key=("non.referenced.html.File.Present");
                             msg.setMessage(ValidatorPropMessages.getString(key));
                            msg.setTargetObject(fileName);
                               msg.setRecommendAction(ValidatorPropMessages.getString("todo."+key)+"   --: "+fileName);           
                            msg.setSeverity(IWRTConstants.WARN);
                            getMessageHandler().publishMessage(msg);
                            log.finest("isHtmlPresent(MessageManager messageManager,List<String>  fileNames)<<---<<");
                            allRulesPassed=false;     
                         
                           
                 }
                   
                }
            }
        }
        //<<---end for loop<<---<<
       
        if(!htmlSelected &&filesSeleced&&widgetModel.isPlistPresent()){
           
            Message msg = new Message();
            msg.setMessageSource(IWRTConstants.VALIDATOR);
            msg.setMessageSource(IWRTConstants.VALIDATOR);
            String key="html.File.Not.selected";
            msg.setMessageKey(key);
            msg.setMessage(ValidatorPropMessages.getString(key)); //$NON-NLS-1$
            msg.setSeverity(IWRTConstants.FATAL);
            msg.setRecommendAction(ValidatorPropMessages.getString("todo."+key));
            // need to set all msg fields
            getMessageHandler().publishMessage(msg);
            allRulesPassed = false;   
        }

        }// end if filenames
     if( widgetModel.isPlistPresent()) {
        if(!widgetModel.isHtmlPresent()){
        Message msg= new Message();
        msg.setMessageSource(IWRTConstants.VALIDATOR);
        msg.setMessageKey("html.File.Not.Present");
        msg.setMessage(ValidatorPropMessages.getString("html.File.Not.Present"));
        msg.setSeverity(IWRTConstants.FATAL);
        msg.setRecommendAction(ValidatorPropMessages.getString("todo.include.html"));
        getMessageHandler().publishMessage(msg);
        log.info("isHtmlPresent(MessageManager messageManager,List<String>  fileNames)<<---<<");
        allRulesPassed=false;
        }
     }
        log.info("isHtmlPresent<<---<<");
       

    }

    
    
    private void isPlistCopiesPresent(    List<String> fileNames) {
        log.info("isPlistCopiesPresent >>-->>");
        int len=widgetName.length()+1;
        if (fileNames != null && widgetModel.getPlistFileName()!=null&& fileNames.size() > 0) 
        {
                for (String fileName : fileNames) {
                log.info("isPlistCopiesPresent >>-->>2");
//                System.out.println("file names 1  ****: "+fileName);
           
                log.info("isPlistCopiesPresent fileName  :" + fileName);
                if (fileName.endsWith(".plist")) {                   
                    log.info("isPlistCopiesPresent fileName  :" + fileName);
//                    System.out.println("only plist file names ****  2: "+fileName);
                    if (!filesSeleced) {                       
                        log.info(" selected   :" + widgetDirPath+ widgetName + "/" + ":");
                        fileName = fileName.replace(widgetDirPath+ widgetName + "/", "");
                           
                    } else {
                        log.info(" selected   :" + widgetName + "/" + ":");
//                        System.out.println("plist file names ****  3: "+fileName);
                        fileName = fileName.substring(len);
//                        System.out.println("plist file names AFTER ****  4: "+fileName +" : "+widgetName);
                                                   
                    }
                    log.info("isPlistCopiesPresent fileName  :" + fileName);
                   
//                    if(fileName.trim().contains("/"))
//                    {
//                    }
                   
                if(!(   fileName.trim().equalsIgnoreCase(widgetModel.getPlistFileName().trim()))){
                   
                    if(fileName.trim().contains("/")){
                        Message msg = new Message();
                        msg.setMessageSource(IWRTConstants.VALIDATOR);
                        msg.setMessageKey("subfolder.duplicate.plist.file.present");
                        msg.setTargetObject(fileName);
                        msg.setMessage(ValidatorPropMessages.getString(msg.getMessageKey()));
                        msg.setSeverity(IWRTConstants.WARN);
//                        msg.setRecommendAction(ValidatorPropMessages.getString("todo."+msg.getMessageKey())+"  --:"+fileName);
                        getMessageHandler().publishMessage(msg);
                        allRulesPassed = false;
                       
                    }
                    else{

                        Message msg = new Message();
                        msg.setMessageSource(IWRTConstants.VALIDATOR);
                        msg.setMessageKey("duplicate.plist.file.present");
                        msg.setTargetObject(fileName);
                        msg.setMessage(ValidatorPropMessages.getString(msg.getMessageKey()));
                        msg.setSeverity(IWRTConstants.WARN);
                        msg.setRecommendAction(ValidatorPropMessages.getString("todo."+msg.getMessageKey())+"  --:"+fileName);
                        getMessageHandler().publishMessage(msg);
                        allRulesPassed = false;
                       
                    }
                }
               

               
                }

            }

        }
        log.info("isPlistCopiesPresent <<--<<");

        //        }
    }
}
