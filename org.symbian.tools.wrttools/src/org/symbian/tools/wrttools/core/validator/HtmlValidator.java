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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.tidy.Node;
import org.w3c.tidy.Tidy;
import org.w3c.tidy.TidyMessage;
import org.w3c.tidy.TidyMessageListener;

import org.symbian.tools.wrttools.core.report.Message;
import org.symbian.tools.wrttools.core.report.MessageHandler;
import org.symbian.tools.wrttools.core.status.IWRTConstants;
import org.symbian.tools.wrttools.core.widgetmodel.WidgetModel;
import org.symbian.tools.wrttools.util.FileUtil;
import org.symbian.tools.wrttools.util.Util;

/**
* Html Validator
* Accepts the MessageManager .
* Accept File projDir.
* Use xmlParser to get all elements in a stack.
* Call rule methods() passing the MessageManager and the stack of elements.
*
* @author Sailaja duvvuri
*/
public class HtmlValidator implements IValidator {
    boolean htmlValidationStaus;
    private String htmlFileName=null;
    private String htmlPlistFileName=null;
    private File htmlFile=null;
    private Logger log = Logger.getLogger(getClass().getName());
    private MessageHandler messageHandler;
    private WidgetModel widgetModel;
    private boolean allRulesPassed;
    
    

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
    
	public boolean validate( File projDir) {
	
			Stack<String> htmlElements = null;
		FileUtil fileUtil = new FileUtil();
		List<File> dirList = fileUtil.getCurrDirFiles(projDir);
		htmlFileName=widgetModel.getMainHtml();

		 try {
			
				htmlFile = fileUtil.getFile(dirList, htmlFileName);
			
			if (htmlFile != null){
				htmlFileName = htmlFile.getName();
			if (widgetModel.getHtmlXmlFile()!=null) {
					checkWellformed(htmlFile,true);
					for(int i=0;i<widgetModel.getReferencedHtmlFiles().size();i++){
						File refHtmlFile=new File(widgetModel.widgetDirectory+widgetModel.widgetName+"/"+widgetModel.getReferencedHtmlFiles().get(i));
						if(refHtmlFile!=null)
						checkWellformed(refHtmlFile,true);
					}
					for(int i=0;i<widgetModel.getReferencedCssFiles().size();i++){
						File refHtmlFile=new File(widgetModel.widgetDirectory+widgetModel.widgetName+"/"+widgetModel.getReferencedCssFiles().get(i));
						if(refHtmlFile!=null)
						checkWellformed(refHtmlFile,false);
					}
					for(int i=0;i<widgetModel.getReferencedJavaScriptFiles().size();i++){
						File refHtmlFile=new File(widgetModel.widgetDirectory+widgetModel.widgetName+"/"+widgetModel.getReferencedJavaScriptFiles().get(i));
						if(refHtmlFile!=null)
						checkWellformed(refHtmlFile,false);
					}	
					for(int i=0;i<widgetModel.getReferencedImageFiles().size();i++){
						File refHtmlFile=new File(widgetModel.widgetDirectory+widgetModel.widgetName+"/"+widgetModel.getReferencedImageFiles().get(i));
						if(refHtmlFile!=null)
						checkWellformed(refHtmlFile,false);
					}	
					for(int i=0;i<widgetModel.getReferencedIFrameFiles().size();i++){
						File refHtmlFile=new File(widgetModel.widgetDirectory+widgetModel.widgetName+"/"+widgetModel.getReferencedIFrameFiles().get(i));
						if(refHtmlFile!=null)
						checkWellformed(refHtmlFile,false);
					}	
					for(int i=0;i<widgetModel.getReferencedEmbedFiles().size();i++){
						File refHtmlFile=new File(widgetModel.widgetDirectory+widgetModel.widgetName+"/"+widgetModel.getReferencedEmbedFiles().get(i));
						if(refHtmlFile!=null)
						checkWellformed(refHtmlFile,false);
					}	
				
			} else {
				Message msg = new Message();
				msg.setMessageSource(IWRTConstants.VALIDATOR);
				msg.setMessageKey("no.html.element");
				msg.setMessage(ValidatorPropMessages.getString("no.html.element"));
				msg.setFileTypeZip(messageHandler.isFileTypeZip());
				msg.setTargetObject(htmlFileName);
				msg.setFullPath(htmlFile.getAbsolutePath());
				msg.setSeverity(IWRTConstants.ERROR);
				msg.setRecommendAction(ValidatorPropMessages.getString("todo.no.html.element"));
				messageHandler.publishMessage(msg);
				allRulesPassed = false;

			}
		 }else{
			 Message msg = new Message();
				msg.setMessageSource(IWRTConstants.VALIDATOR);
				msg.setMessage(ValidatorPropMessages.getString("html.File.Not.Present"));
				msg.setFileTypeZip(messageHandler.isFileTypeZip());
				msg.setSeverity(IWRTConstants.FATAL);
				messageHandler.publishMessage(msg);
		 }

		} catch (Exception e) {

			Util.logEvent(log, Level.SEVERE, e);
			Message msg = new Message();
			msg.setMessageSource(IWRTConstants.VALIDATOR);
			msg.setMessageKey("html.File.Not.Present");
			
			msg.setMessage(ValidatorPropMessages.getString("html.File.Not.Present"));
			msg.setFileTypeZip(messageHandler.isFileTypeZip());
			msg.setSeverity(IWRTConstants.FATAL);
			messageHandler.publishMessage(msg);
		}

		log.info("validate<<---<<");

		return allRulesPassed;
	}

	public String getHtmlPlistFileName() {
		return htmlPlistFileName;
	}



	public void setHtmlPlistFileName(String htmlPlistFileName) {
		this.htmlPlistFileName = htmlPlistFileName;
	}
	//---------------------------------code for Jtidy--------------------------------------->>
  private boolean checkWellformed(File htmlfile, boolean isParsingNeeded) {
	  String userDir = System.getProperty("user.dir");
	  String outputFile = userDir+ "/tidy.html";
	  //htmlfile will behave as directory when src field in html is blank (src =""), so skipping that.
	  if(htmlfile.isDirectory()){
		  return true;
	  }
      InputStream inStream;
      try {
		inStream = new FileInputStream(htmlfile);
		OutputStream outStream = new FileOutputStream(outputFile);
		//Parsing here is required only for html, coz js, css files cannot have reference files.
		//fixing bug 742
		if(isParsingNeeded){
		  Tidy jtidy = new Tidy();
		  TMessageListener msglis= new TMessageListener();
		  jtidy.setMessageListener(msglis);	  
		  Node xmlDocument = jtidy.parse(inStream, outStream);
		}
		  inStream.close();
      } catch (FileNotFoundException e) {
		Message msg = new Message();
		msg.setMessageSource(IWRTConstants.VALIDATOR);
		msg.setMessageKey("no.element");
		msg.setMessage(htmlfile.getName()+ " referred in main html" + " could not be found");
		msg.setFileTypeZip(messageHandler.isFileTypeZip());
		msg.setTargetObject(htmlfile.toString());
		msg.setFullPath(htmlfile.getAbsolutePath());
		msg.setSeverity(IWRTConstants.WARN);
		msg.setRecommendAction(ValidatorPropMessages.getString("todo.no.html.element"));
		messageHandler.publishMessage(msg);
		Util.logEvent(log, Level.SEVERE, e);			
		log.severe(e.getMessage());
	} catch (IOException e) {
		 Util.logEvent(log, Level.SEVERE, e);			
			log.severe(e.getMessage());
	}
	return false;
}
	
	public class TMessageListener implements TidyMessageListener {

		public void messageReceived(TidyMessage tdyMsg) {
			
			if (tdyMsg.getLevel().equals(TidyMessage.Level.ERROR)) {
				log.info("line     :" + tdyMsg.getLine());
				log.info("message  :" + tdyMsg.getMessage());
				Message msg = new Message();
				msg.setMessageSource(IWRTConstants.VALIDATOR);
				msg.setMessageKey("html.wellform.error");
				msg.setMessage(tdyMsg.getMessage());
				msg.setFileTypeZip(messageHandler.isFileTypeZip());
				msg.setTargetObject(htmlFileName);
				msg.setFullPath(htmlFile.getAbsolutePath());
				msg.setSeverity(IWRTConstants.WARN);
				msg.setLineNumber(tdyMsg.getLine());
				// msg.setRecommendAction(ValidatorPropMessages.getString("todo.correct.html.tag")+
				// tdyMsg.getLine()+" of "+htmlFileName);
				Object[] arguments = { tdyMsg.getLine(), " of , ", htmlFileName };
				String message = MessageFormat.format(ValidatorPropMessages
						.getString("todo.correct.html.tag")
						+ "{0}" + "{1}" + "{2}", arguments);
				msg.setRecommendAction(message);

				// set all msg fields
				// For generating TO DO list
				messageHandler.publishMessage(msg);
				allRulesPassed = false;

			}

		}
	}
//////<<<-----------------------------------code for Jtidy---------------------------------------//


}