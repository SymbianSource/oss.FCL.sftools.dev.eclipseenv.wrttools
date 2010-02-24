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

package org.symbian.tools.wrttools.core.widgetmodel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import org.symbian.tools.wrttools.core.exception.BaseException;
import org.symbian.tools.wrttools.core.exception.ValidationException;
import org.symbian.tools.wrttools.core.parser.XMLParser;
import org.symbian.tools.wrttools.core.parser.XMLPlistParser;
import org.symbian.tools.wrttools.core.parser.XmlElement;
import org.symbian.tools.wrttools.core.report.Message;
import org.symbian.tools.wrttools.core.report.MessageHandler;
import org.symbian.tools.wrttools.core.status.IWRTConstants;
import org.symbian.tools.wrttools.core.status.WRTStatusHandler;
import org.symbian.tools.wrttools.core.validator.ValidatorPropMessages;
import org.symbian.tools.wrttools.util.FileUtil;
import org.symbian.tools.wrttools.util.Util;

public class WidgetModel {
	
	private Logger log = Logger.getLogger(getClass().getName());

	private List<String> filesSelected;
	private MessageHandler messageHandler;
	private WRTStatusHandler statusHandler;
	private File widgetModelFile ;
	
	private XmlElement plistXmlFile;
	
	private List<String> referencedHtmlFiles = new ArrayList<String>();
	private List<String> referencedCssFiles = new ArrayList<String>();
	private List<String> referencedJavaScriptFiles = new ArrayList<String>();
	private List<String> referencedImageFiles = new ArrayList<String>();
	private LinkedList<String> referencedHtmlQueue = new LinkedList<String>();
	private List<String> allReferencedFiles = new ArrayList<String>();
	private List<String> referencedIFrameFiles = new ArrayList<String>();
	private List<String> referencedEmbedFiles = new ArrayList<String>();

	public String widgetDirectory;
	public String widgetName;
	
	private String plistFileName;
	private String displayname;
	private String identifier;
	private String version;
	private String mainHtml;
	private boolean allowNetworkAccess;
	private boolean allowFileAccessOutsideOfWidget;
	
	private boolean htmlPresent = false;
	private boolean plistPresent = false;

	private String currentDir ;


	private XMLParser htmlParser;
	//private XmlElement htmlXmlFile;
	private File htmlXmlFile;
	FileUtil fileUtil = new FileUtil();
	private boolean homeScreenValue;


	public boolean validateProject(String projPath)throws BaseException{
		try{
		boolean plistPresent = false;
		boolean htmlPresent = false;
		File srcFile = new File(projPath);		
//		a validation not to allow user to add root as a widget project
		String parent = srcFile.getParent();
		
		if(parent == null){
			return false;
		}
		getWidgetModel(projPath);
		plistPresent=this.isPlistPresent();
		htmlPresent=this.isHtmlPresent();
		if(!plistPresent)
		{
//			System.out.println("plist is not present widget model.");
			Message msg = new Message();
			msg.setMessageSource(IWRTConstants.ADDPROJECT);
			msg.setMessageKey("widgetmodel.plist.notpresent");
			msg.setMessage(ValidatorPropMessages.getString("widgetmodel.plist.notpresent"));
			msg.setSeverity(IWRTConstants.FATAL);
			// need to set all msg fields
			getMessageHandler().publishMessage(msg);
			
		}
		if(!htmlPresent)
		{
//			System.out.println("html is not present widget model.");
			Message msg = new Message();
			msg.setMessageSource(IWRTConstants.ADDPROJECT);
			msg.setMessageKey("widgetmodel.html.notpresent");
			msg.setMessage(ValidatorPropMessages
					.getString("widgetmodel.html.notpresent"));
			msg.setSeverity(IWRTConstants.FATAL);
			// need to set all msg fields
			getMessageHandler().publishMessage(msg);
			
		}
		}catch (Exception ex){
			log.severe("Html file is not present.  ");
//			ex.printStackTrace();
		}
		return (plistPresent && htmlPresent);
	}
	

	
	public List<String> getWidgetModelFromZip(String widgetZipFile) throws ValidationException {
		log.finest("getWidgetModelFromZip >>--->>");
		FileUtil fileUtil = new FileUtil();
		File newFile = fileUtil.unZip(widgetZipFile);
		log.finest("getWidgetModelFromZip <<---<<");
		getMessageHandler().setFileTypeZip(true);
		return getWidgetModel(newFile);
	}
	
	public List<String> getWidgetModelFromZipForWizard(String widgetZipFile) throws ValidationException {
		log.finest("getWidgetModelFromZip >>--->>");
		FileUtil fileUtil = new FileUtil();
		File newFile = fileUtil.unZip(widgetZipFile);
		log.finest("getWidgetModelFromZip <<---<<");
		getMessageHandler().setFileTypeZip(true);
		return getWidgetModelForWizard(newFile);
	}
	
	public List<String> getWidgetModel(String widgetDirPath) throws ValidationException {
		
		File widgetModel = new File(widgetDirPath);
		return getWidgetModel(widgetModel);
		}
	/**
	 * Accepts a zip file and returns a file object.
	 * @throws ValidationException 
	 */
	
//--------------------------------------------------------------------------------------->>
	
	public List<String> getWidgetModel(File widget) throws ValidationException {
		log.finest("getWidgetModel >>--->>");
//		showData("getWidgetModel >>--->>  ");	
		
		List<File> dirList;
		File plist;
		
		try{
		 widgetModelFile = widget;
		 
		
			 
		if (widgetModelFile.isDirectory() && widgetModelFile != null) {
			// reset all variables
			reset();
		
			// get the widget name and directory.
	
		// some times the parent is the dirve itself like c:\	
			if (widgetModelFile.getParentFile().toString().endsWith("/") 
					||widgetModelFile.getParentFile().toString().endsWith("\\")) {
					widgetDirectory = widgetModelFile.getParentFile().getAbsolutePath();
				} else {
					widgetDirectory = widgetModelFile.getParentFile().getAbsolutePath()+ "/";
				}
			
			
//			System.out.println("----widgetDirectory-------1"+widgetDirectory);		
			widgetDirectory = Util.replaceChar(widgetDirectory, '\\', '/');
			widgetName = widgetModelFile.getName();
			
			// get the dir list of widget
			dirList = fileUtil.getCurrDirFiles(widgetModelFile);
			
			// find the plist file.
			plist = getPlistfile(dirList);
			
			
			htmlParser = new XMLParser();
			XMLPlistParser plistXMLParser= new  XMLPlistParser();
			htmlXmlFile=null;
			try {
				if (plist != null) {
					plistXmlFile = plistXMLParser.parseXML(plist);
					setPlistElements(plistXmlFile);
					setPlistFileName(plist.getName());
					
					File mailHtmlfile = new File(widgetDirectory + widgetName	+ "/" + mainHtml);
					if (mailHtmlfile != null && mailHtmlfile.length() > 0) {
						//htmlParser = new XMLParser();
						//htmlXmlFile = htmlParser.parseXML(mailHtmlfile);
												
						htmlXmlFile = mailHtmlfile;
						if(htmlXmlFile!=null) {
						currentDir="";						
						callAllreference(htmlXmlFile);
						while (referencedHtmlQueue != null	&& !referencedHtmlQueue.isEmpty()) {
							
							String htmlRef = referencedHtmlQueue.poll();
							if (htmlRef != null && htmlRef.length() > 0) {
								File refHtmlfile = new File(widgetDirectory+ widgetName + "/" + htmlRef.trim());
								if(refHtmlfile.exists()){
								currentDir=refHtmlfile.getAbsolutePath().substring(0,refHtmlfile.getAbsolutePath().indexOf(refHtmlfile.getName())) ;								htmlParser = new XMLParser();
								currentDir=Util.replaceChar(currentDir, '\\', '/');
								currentDir=currentDir.substring(currentDir.indexOf(widgetDirectory + widgetName)+(widgetDirectory + widgetName).length()) ;
								if(currentDir.trim().equals("/"))currentDir="";
								//XmlElement refHtmlXmlFile = null;
								//refHtmlXmlFile = htmlParser.parseXML(refHtmlfile);
								
								callAllreference(refHtmlfile);
								}else{
									showData("htmlXmlFile is not present");
									Message msg = new Message();
									msg.setMessageSource(IWRTConstants.VALIDATOR);
									msg.setMessageKey("WidgetModel.refrenced.html.missing");
									msg.setMessage(ValidatorPropMessages
											.getString("WidgetModel.refrenced.html.missing"));
									msg.setFileTypeZip(getMessageHandler().isFileTypeZip());
									msg.setTargetObject(htmlRef);
									msg.setFullPath(null);
									msg.setSeverity(IWRTConstants.WARN);
									// need to set all msg fields
									getMessageHandler().publishMessage(msg);
									log.severe("The input file " + widgetModelFile
											+ " is not a directory or does not exist.  ");
									
								}
								
							}
						}					
						}else {
							showData(" htmlXmlFile null");
						}
					} else {
						showData("Main Html  File is not present ");

					}// ----end html null
				} else {
					
					showData("Plist   File is not present ");

				}// ---end if plist null
				displayModel();

			} catch (IOException e) {
				log.severe("The input file " + widgetModelFile
						+ " is not a directory or does not exist.  ");
//				e.printStackTrace();
			}

			log.finest("getWidgetModel <<---<<");
			
			allReferencedFiles.addAll(referencedHtmlFiles);
			allReferencedFiles.addAll(referencedJavaScriptFiles);
			allReferencedFiles.addAll(referencedCssFiles);
			allReferencedFiles.addAll(referencedImageFiles);
			allReferencedFiles.addAll(referencedIFrameFiles);
			allReferencedFiles.addAll(referencedEmbedFiles);
			
			
			return allReferencedFiles;
		} else {

			Message msg = new Message();
			msg.setMessageSource(IWRTConstants.VALIDATOR);
			msg.setMessageKey("WidgetModel.File.NotDirectory");
			msg.setMessage(ValidatorPropMessages
					.getString("WidgetModel.File.NotDirectory"));
			msg.setRecommendAction(ValidatorPropMessages
					.getString("todo.widgetmodel"));
			msg.setFileTypeZip(getMessageHandler().isFileTypeZip());
			msg.setTargetObject(widget.getName());
			msg.setFullPath(null);
			msg.setSeverity(IWRTConstants.FATAL);
			// need to set all msg fields
			getMessageHandler().publishMessage(msg);
			log.severe("The input file " + widgetModelFile
					+ " is not a directory or does not exist.  ");
		
			throw new ValidationException(
					"No directory found for the given input file  "
							+ widget);
		}
		}finally{

			 dirList=null;
			 plist=null;
			log.finest("getWidgetModel <<---<<");
			
		}

	}


	public List<String> getWidgetModelForWizard(File widget) throws ValidationException {
		List<File> dirList;
		File plist;
		try{
		 widgetModelFile = widget;
		 
		if (widgetModelFile.isDirectory() && widgetModelFile != null) {
			// reset all variables
			reset();
		
			// get the widget name and directory.
	
			// some times the parent is the dirve itself like c:\	
			if (widgetModelFile.getParentFile().toString().endsWith("/") 
					||widgetModelFile.getParentFile().toString().endsWith("\\")) {
					widgetDirectory = widgetModelFile.getParentFile().getAbsolutePath();
				} else {
					widgetDirectory = widgetModelFile.getParentFile().getAbsolutePath()+ "/";
				}
			
			
			widgetDirectory = Util.replaceChar(widgetDirectory, '\\', '/');
			widgetName = widgetModelFile.getName();
			
			// get the dir list of widget
			dirList = fileUtil.getCurrDirFiles(widgetModelFile);
			
			// find the plist file.
			plist = getPlistfile(dirList);
			
			
			htmlParser = new XMLParser();
			XMLPlistParser plistXMLParser= new  XMLPlistParser();
			try {
				if (plist != null) {
					plistXmlFile = plistXMLParser.parseXML(plist);
					setPlistElements(plistXmlFile);
					setPlistFileName(plist.getName());

				} else {
					
					showData("Plist   File is not present ");

				}// ---end if plist null
			displayModel();

			} catch (IOException e) {
				log.severe("The input file " + widgetModelFile
						+ " is not a directory or does not exist.  ");
			}
			return allReferencedFiles;
		} else {

			Message msg = new Message();
			msg.setMessageSource(IWRTConstants.VALIDATOR);
			msg.setMessageKey("WidgetModel.File.NotDirectory");
			msg.setMessage(ValidatorPropMessages
					.getString("WidgetModel.File.NotDirectory"));
			msg.setRecommendAction(ValidatorPropMessages
					.getString("todo.widgetmodel"));
			msg.setFileTypeZip(getMessageHandler().isFileTypeZip());
			msg.setTargetObject(widget.getName());
			msg.setFullPath(null);
			msg.setSeverity(IWRTConstants.FATAL);
			// need to set all msg fields
			getMessageHandler().publishMessage(msg);
			log.severe("The input file " + widgetModelFile
					+ " is not a directory or does not exist.  ");
		
			throw new ValidationException(
					"No directory found for the given input file  "
							+ widget);
		}
		}finally{

			 dirList=null;
			 plist=null;
			log.finest("getWidgetModel <<---<<");
			
		}

	}


	
	private File getPlistfile(List <File>dirList){
		String  selectedFileName=null;	
		log.finest("getPlistfile >>-->>  ");
//		showData("getPlistfile >>--->>  ");
//		showData("projDirParenPath  :" + projDirParenPath);
//		showData("projDirName  :" + projDirName);
//		filesSelected = fileUtil.getCurrDirFileFullNames(widgetModelFile);
		List<String> files;

		if(filesSelected!=null&&filesSelected.size()>0){
			 files=filesSelected;
			
		}else{
			files=fileUtil.getCurrDirFileFullNames(widgetModelFile);	
		}		
			for (String fileName : files) {	
				
				if (fileName.trim().startsWith(widgetDirectory )) {
					fileName = fileName.substring((fileName.indexOf( widgetDirectory+widgetName	+ "/")+widgetDirectory.length()+widgetName.length()	+ 1));
				}
				if (fileName.trim().startsWith(widgetName+"/")) {
					fileName = fileName.substring((fileName.indexOf(widgetName	+ "/")+ widgetName.length() + 1));
				}				
				if (fileName.contains("\\")||fileName.contains("/")) {
					continue;
				}
				if (fileName.trim().equalsIgnoreCase("info.plist")) {
					selectedFileName=fileName;
					return new File (widgetDirectory+widgetName+"/"+fileName);
				}

			}	
//		return  fileUtil.getFile(dirList, selectedFileName);
			return null;
		
		}
	
	
	private void setPlistElements(XmlElement rootXml) throws ValidationException {
			
		if (rootXml!=null&&rootXml.getChildList()!=null&&rootXml.getChildList().size() == 1) {
			XmlElement dict = rootXml.getChildList().get(0);
			XmlElement key = null;
			boolean miniViewEnable = false;
			if (dict.getChildList().size() > 1) {
				for (XmlElement elm : dict.getChildList()) {	
						
					if (elm.getName().trim().equalsIgnoreCase("String")) {
						if (key != null && key.getValue() != null) {												        
							if (key.getValue().trim().equalsIgnoreCase(	"DisplayName")) {
								displayname = elm.getValue();
							}
							if (key.getValue().trim().equalsIgnoreCase(	"Identifier")) {
								identifier = elm.getValue();
							}
							if (key.getValue().trim().equalsIgnoreCase("Version")) {
									version = elm.getValue();
							}
							if (key.getValue().trim().equalsIgnoreCase(	"MainHTML")) {
								mainHtml = elm.getValue();
							}
							if (key.getValue().trim().equalsIgnoreCase(	"AllowNetworkAccess")) {
									allowNetworkAccess = new Boolean(elm.getValue());
							}
							if (key.getValue().trim().equalsIgnoreCase(	"AllowFileAccessOutsideOfWidget")) {
								allowFileAccessOutsideOfWidget = new Boolean(elm.getValue());
							}							
							if (key.getValue().trim().equalsIgnoreCase(	"MiniViewEnabled")) {
								homeScreenValue = new Boolean(elm.getValue());
							}
							key = null;
						}
					} 
					else if (elm.getName().trim().equalsIgnoreCase("key")) {
						key = elm;
						if(key.getName().contains("CFBundle")){
							key.setName(key.getName().substring(key.getName().indexOf("CFBundle")+8));
						}
						if (key.getValue().trim().equalsIgnoreCase(	"MiniViewEnabled")) {
							miniViewEnable = true;							
						}
					}
					else{
						if(miniViewEnable){
							homeScreenValue = new Boolean(elm.getName());
						}
					}
				}
			}
			if((mainHtml!=null&&mainHtml.trim().length()==0)||mainHtml==null) {
                
                Message msg = new Message();
                msg.setMessageSource(IWRTConstants.ADDPROJECT);
                msg.setMessageKey("plist.parsing.error.mainHtmlkey.missing");
                msg.setMessage(ValidatorPropMessages.getString("plist.parsing.error.mainHtmlkey.missing"));
                msg.setFileTypeZip(getMessageHandler().isFileTypeZip());                        
                msg.setSeverity(IWRTConstants.FATAL);
                // need to set all msg fields
                getMessageHandler().publishMessage(msg);
                }
		}
	}	
	
	
	private void getHtmlJavaScriptReference(Document doc) {
		String previousKey=null;
		String previousValue=null;
		//Get all elements:
		//NodeList list = doc.getChildNodes();
		NodeList list = doc.getElementsByTagName("script");
		
		//Get the number of elements:
		int attrSize = list.getLength();
		
		//Loop through all the elements:
		for (int j = 0; j < attrSize; j++) {
		    org.w3c.dom.Node attr = list.item(j);
		    NamedNodeMap arrList = attr.getAttributes();
		    int len = arrList.getLength();
		    for(int i  = 0; i <len; i++ ){
		    	org.w3c.dom.Node curAttr = arrList.item(i);
				if (curAttr.getNodeName().trim().equalsIgnoreCase("src")){
					previousKey=curAttr.getNodeName().trim();
					previousValue=curAttr.getNodeValue().trim();
				}
				if (curAttr.getNodeName().trim().equalsIgnoreCase("type")&& (curAttr.getNodeValue().trim().equalsIgnoreCase("text/javascript"))){
					if(i<len-1 && previousKey!="src"){
						i++;
						curAttr = arrList.item(i);
						if (curAttr.getNodeName().trim().equalsIgnoreCase("src")) {
							if(!referencedJavaScriptFiles.contains(currentDir+curAttr.getNodeValue().trim())){
							referencedJavaScriptFiles.add(currentDir+curAttr.getNodeValue().trim());						
							}
							previousKey=null;
							previousValue=null;
						}
						}else if(previousKey!=null&&previousKey.equalsIgnoreCase("src")){
							if(!referencedJavaScriptFiles.contains(currentDir+previousValue.trim())){							
							referencedJavaScriptFiles.add(currentDir+previousValue.trim());}
							previousKey=null;
							previousValue=null;
						}
					}
				}
			}		
	}
	
	private void getHtmlCssReference(Document doc) {
		
			String previousKey=null;
			String previousValue=null;
			boolean cssTypeSet=false;
			boolean cssRefSet=false;
			String typeKey=null;
			String typeValue=null;
			
			//Get all elements:
			NodeList list = doc.getElementsByTagName("style");
			//Get the number of elements:
			int attrSize = list.getLength();				
			
			//Loop through all the elements:
			for (int i = 0; i < attrSize; i++) {
			    org.w3c.dom.Node attr = list.item(i);
			    NamedNodeMap arrList = attr.getAttributes();
			    int len = arrList.getLength();
			    for(int j  = len-1; j >= 0; j--){
			    	org.w3c.dom.Node curAttr = arrList.item(j);
			    	if (curAttr.getNodeName().trim().equalsIgnoreCase("href")) {					
						previousKey=attr.getAttributes().getNamedItem("href").getNodeName().trim();
						previousValue=attr.getAttributes().getNamedItem("href").getNodeValue().trim();
						cssRefSet=true;
					}
				    if (curAttr.getNodeName().trim().equalsIgnoreCase("type")
							&& curAttr.getNodeValue().trim().equalsIgnoreCase("text/css")) {
						typeKey=curAttr.getNodeName().trim();
						typeValue=curAttr.getNodeValue().trim();
						cssTypeSet=true;
//							showData("cssTypeSet=true;" + cssTypeSet);
						if(j<len-1 && previousKey!="href"){
							j++;
							curAttr = arrList.item(j);
							if (curAttr != null && curAttr.getNodeName().trim().equalsIgnoreCase("href")) {
								if(!curAttr.getNodeValue().startsWith("http://") && !referencedCssFiles.contains(currentDir+curAttr.getNodeValue().trim())){											
									referencedCssFiles.add(currentDir+curAttr.getNodeValue().trim());
								}
								previousKey=null;
								previousValue=null;
								cssRefSet=true;
							}
							j--;
						}else if(previousKey!=null&&previousKey.equalsIgnoreCase("src")){
								
							if(!curAttr.getNodeValue().startsWith("http://") && !referencedCssFiles.contains(currentDir+previousValue.trim())){							
							referencedCssFiles.add(currentDir+previousValue.trim());
							}
							
							previousKey=null;
							previousValue=null;
							cssRefSet=true;
						}
							}
					}
			    if(cssTypeSet && !cssRefSet && attr.getNodeValue()!=null&& attr.getFirstChild()!=null && attr.getFirstChild().getNodeValue().contains("import")){
//						showData("cssTypeSet   :"+ cssTypeSet+ "cssRefSet"+cssRefSet);
					parseImportString(attr.getFirstChild().getNodeValue());
				}
			    getHtmlCssReferenceLinkNode(doc);
   }

	}
	
	private void getHtmlCssReferenceLinkNode(Document doc) {
		
		String previousKey=null;
		String previousValue=null;
		boolean cssTypeSet=false;
		boolean cssRefSet=false;
		String typeKey=null;
		String typeValue=null;
		
		//Get all elements:
		NodeList list = doc.getElementsByTagName("link");
		//Get the number of elements:
		int attrSize = list.getLength();				
		
		//Loop through all the elements:
		for (int i = 0; i < attrSize; i++) {
		    org.w3c.dom.Node attr = list.item(i);
		    NamedNodeMap arrList = attr.getAttributes();
		    int len = arrList.getLength();
		    for(int j  = len-1; j >= 0; j-- ){
		    	org.w3c.dom.Node curAttr = arrList.item(j);
		    	if (curAttr.getNodeName().trim().equalsIgnoreCase("href")) {					
					previousKey=attr.getAttributes().getNamedItem("href").getNodeName().trim();
					previousValue=attr.getAttributes().getNamedItem("href").getNodeValue().trim();
					cssRefSet=true;
				}
			    if (curAttr.getNodeName().trim().equalsIgnoreCase("type")
						&& curAttr.getNodeValue().trim().equalsIgnoreCase("text/css")) {
					typeKey=curAttr.getNodeName().trim();
					typeValue=curAttr.getNodeValue().trim();
					cssTypeSet=true;
//						showData("cssTypeSet=true;" + cssTypeSet);
					if(j<len-1 && previousKey!="href"){
						j++;
						curAttr = arrList.item(j);
						if (curAttr.getNodeName().trim().equalsIgnoreCase("href")) {
							if(!curAttr.getNodeValue().startsWith("http://") && !referencedCssFiles.contains(currentDir+curAttr.getNodeValue().trim())){										
								referencedCssFiles.add(currentDir+curAttr.getNodeValue().trim());
							}
							previousKey=null;
							previousValue=null;
							cssRefSet=true;
						}
						j--;
						}else if(previousKey!=null&&previousKey.equalsIgnoreCase("src")){
							
							if(!curAttr.getNodeValue().startsWith("http://") && !referencedCssFiles.contains(currentDir+previousValue.trim())){							
							referencedCssFiles.add(currentDir+previousValue.trim());
							}
							
							previousKey=null;
							previousValue=null;
							cssRefSet=true;
						}
						}
				}
		    if(cssTypeSet && !cssRefSet && attr.getNodeValue()!=null&& attr.getFirstChild()!=null && attr.getFirstChild().getNodeValue().contains("import")){
//					showData("cssTypeSet   :"+ cssTypeSet+ "cssRefSet"+cssRefSet);
				parseImportString(attr.getFirstChild().getNodeValue());
			}
   }

}

	
	private void getHtmlImageReference(Document doc) {
		//Get all elements:
		NodeList list = doc.getElementsByTagName("img");
		
		//Get the number of elements:
		int attrSize = list.getLength();
		
		//Loop through all the elements:
		for (int i = 0; i < attrSize; i++) {
		    org.w3c.dom.Node attr = list.item(i);
		    NamedNodeMap arrList = attr.getAttributes();
		    for(int j  = 0; j < arrList.getLength(); j++ ){
		    	if (attr.getNodeName().trim().equalsIgnoreCase("img") && arrList.item(j).getNodeName().equalsIgnoreCase("src")) {					
		    		if (!referencedImageFiles.contains(currentDir+arrList.item(j).getNodeValue().trim())) {
						referencedImageFiles.add(currentDir+arrList.item(j).getNodeValue().trim());
					}
		    		else if (arrList.item(j).getNodeName().trim().equalsIgnoreCase("background")) {
						if (!referencedImageFiles.contains(currentDir+arrList.item(j).getNodeValue().trim())) {
							this.referencedImageFiles.add(currentDir+arrList.item(j).getNodeValue().trim());
						}
		    		}
		    	}
		    }
		}
		    NodeList list1 = doc.getElementsByTagName("style");
			
			//Get the number of elements:
			int attrSize1 = list1.getLength();
			
			//Loop through all the elements:
			for (int i = 0; i < attrSize1; i++) {
				if(!list1.item(i).getNodeValue().contains(IWRTConstants.IMAGE_REF)){
					continue;
				}
			    org.w3c.dom.Node attr = list1.item(i);
			    NamedNodeMap arrList = attr.getAttributes();
			    for(int j  = 0; j < arrList.getLength(); j++ ){
					String refblock = attr.getNodeValue().trim();
					refblock = refblock.replace(IWRTConstants.IMAGE_REF, "");
					while (refblock != null && refblock.trim().length() > 0) {
						if (refblock.contains("|")) {
							String block = refblock.substring(0, refblock.indexOf("|"));
//								showData("Java Image Reference 2     :" + block + ":");
							if (!referencedImageFiles.contains(currentDir+block.trim())) {
								referencedImageFiles.add(currentDir+block.trim());
							}
							
							refblock = refblock.substring(refblock.indexOf("|") + 1);

						} else {
							refblock = null;

						}
					}// end while.
		    }
		}
	}
	private void getHtmlIFrameReference(Document doc) {
		//Get all elements:
		NodeList list = doc.getElementsByTagName("iframe");
		
		//Get the number of elements:
		int attrSize = list.getLength();
		
		//Loop through all the elements:
		for (int i = 0; i < attrSize; i++) {
		    org.w3c.dom.Node attr = list.item(i);
		    NamedNodeMap arrList = attr.getAttributes();
		    for(int j  = 0; j < arrList.getLength(); j++ ){
		    	if (attr.getNodeName().trim().equalsIgnoreCase("iframe") && arrList.item(j).getNodeName().equalsIgnoreCase("src")) {					
		    		if (!referencedIFrameFiles.contains(currentDir+arrList.item(j).getNodeValue().trim())) {
		    			referencedIFrameFiles.add(currentDir+arrList.item(j).getNodeValue().trim());
					}
		    		else if (arrList.item(j).getNodeName().trim().equalsIgnoreCase("background")) {
						if (!referencedIFrameFiles.contains(currentDir+arrList.item(j).getNodeValue().trim())) {
							this.referencedIFrameFiles.add(currentDir+arrList.item(j).getNodeValue().trim());
						}
		    		}
		    	}
		    }
		}
	}
	
	private void getHtmlEmbedReference(Document doc) {
		//Get all elements:
		NodeList list = doc.getElementsByTagName("embed");
		
		//Get the number of elements:
		int attrSize = list.getLength();
		
		//Loop through all the elements:
		for (int i = 0; i < attrSize; i++) {
		    org.w3c.dom.Node attr = list.item(i);
		    NamedNodeMap arrList = attr.getAttributes();
		    for(int j  = 0; j < arrList.getLength(); j++ ){
		    	if (attr.getNodeName().trim().equalsIgnoreCase("embed") && arrList.item(j).getNodeName().equalsIgnoreCase("src")) {					
		    		if (!referencedEmbedFiles.contains(currentDir+arrList.item(j).getNodeValue().trim())) {
		    			referencedEmbedFiles.add(currentDir+arrList.item(j).getNodeValue().trim());
					}
		    		else if (arrList.item(j).getNodeName().trim().equalsIgnoreCase("background")) {
						if (!referencedEmbedFiles.contains(currentDir+arrList.item(j).getNodeValue().trim())) {
							this.referencedEmbedFiles.add(currentDir+arrList.item(j).getNodeValue().trim());
						}
		    		}
		    	}
		    }
		}
	}
	
	/**
	 * 
	 * @param rootXmlElement
	 */
	

	private void getHtmlReference(Document doc) {
		
		
		//Get all "a" elements:
		NodeList list = doc.getElementsByTagName("a");
		
		//Get the number of elements:
		int attrSize = list.getLength();
		
		//Loop through all the "a" elements:
		for (int i = 0; i < attrSize; i++) {
			if(list.item(i).getParentNode().getNodeName().equalsIgnoreCase("script") && list.item(i).getParentNode().getNodeValue() == null){
				continue;
			}
			String href = null;
			org.w3c.dom.Node attr = list.item(i);
		    NamedNodeMap arrList = attr.getAttributes();
		    for(int j  = 0; j < arrList.getLength(); j++ ){
		    	if (arrList.item(j).getNodeName().equalsIgnoreCase("href")) {
				//Get the "href" attribute from the current "a" element:
		    		href = arrList.item(j).getNodeValue();
					if ((href.toLowerCase().contains(".htm")|| href.toLowerCase().contains(".html")) &&
						!(href.toLowerCase().startsWith("http://")|| href.toLowerCase().startsWith("https://"))) {
						String attrValue = arrList.item(j).getNodeValue().trim();
						if (!referencedHtmlFiles.contains(currentDir+attrValue.toLowerCase())) {
							referencedHtmlFiles.add(currentDir+attrValue.toLowerCase());
							referencedHtmlQueue.add(attrValue);
						}
					}
		    	}
			}
		}
	}
		
		/**
		 * call the methods for parsing the references.
		 * @param refXmlParsedElementFile
		 */
	
	private void callAllreference(File refXmlParsedElementFile){
		//Stuff needed by Tidy:
		Tidy tidy = new Tidy();
		
		//Get the org.w3c.dom.Document from Tidy:
		Document doc = null;
		FileInputStream fis;
		try {
			fis = new FileInputStream(refXmlParsedElementFile);
			doc = tidy.parseDOM(fis, null);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(doc != null){
			//	showData("getHtmlReference ");
		
			getHtmlReference(doc);
			//	showData("getHtmlJavaScriptReference ");
		
			getHtmlJavaScriptReference(doc);
			//	showData("getHtmlCssReference ");
		
			getHtmlCssReference(doc);
			//	showData("getHtmlImageReference ");
		
			getHtmlImageReference(doc);
			//	showData("getHtmlReference ");
			getHtmlIFrameReference(doc);
			getHtmlEmbedReference(doc);
		}

	}
/**
 * 
 * @param importString
 */
private void parseImportString(String importString) {
	
		while (importString != null && importString.trim().length() > 0) {
			String block = null;
			if (importString.contains("@import")) {
				block = importString.substring(0, importString.indexOf(';'));
				importString = importString
						.substring(importString.indexOf(';') + 1);
			}

			if (block != null && block.contains("@import")) {
				block = block.replace("@import", "");
				block = block.replaceAll("\"", "");
				block = block.replaceAll(";", "");				
				if (block.trim().endsWith(".css")){
					if (!referencedCssFiles.contains(currentDir+block.trim())) {
						referencedCssFiles.add(currentDir+block.trim());
					}
				}
				else if(block.contains("(")){
					block = block.substring(block.lastIndexOf("(")+1, block.length()-1);
					if (block.trim().endsWith(".css")){
						if (!referencedCssFiles.contains(currentDir+block.trim())) {
							referencedCssFiles.add(currentDir+block.trim());
						}
					}
				}
			}
			else{
				break;
			}
			
			}// end while.
	}
		  
	/**
	 * 
	 * @param functionString
	 */	

private void parseScriptForhtmlRef(String functionString) {
	
		while (functionString != null && functionString.trim().length() > 0) {
			String block = null;
			if (functionString.toLowerCase().contains(".htm")) {
				if (functionString.toLowerCase().contains(".html")) {
					block = functionString.substring(0, functionString
							.indexOf(".html") + 5);
				} else {
					block = functionString.substring(0, functionString
							.indexOf(".htm") + 4);
				}
				functionString = functionString.substring(block.length());

				block = block.trim();
				if (block.contains("=")) {
					block = block.substring(block.indexOf('=') + 1);
					block = block.replaceAll("\"", "");
					block = block.replaceAll("'", "");
					// showData("referencedFilesHtml :"+block+":");
					if (!referencedHtmlFiles.contains(block.trim().toLowerCase())) {
						referencedHtmlFiles.add(currentDir+block.trim().toLowerCase());
						referencedHtmlQueue.add(block.trim());
					}
				}

			} else {
				functionString = null;
			}

		}// end while.

	}// end parceScriptForhtmlRef()
		


public MessageHandler getMessageHandler() throws ValidationException {
	if(messageHandler==null){
		messageHandler= new MessageHandler();
		//throw new ValidationException("Massagehandler is not Set");		
	}
		
		
	return messageHandler;
}



public void setMessageHandler(MessageHandler messageHandler) {
	this.messageHandler = messageHandler;
}



public WRTStatusHandler getStatusHandler() throws ValidationException {
	if(statusHandler==null){
//		statusHandler= new WRTStatusHandler();
	throw new ValidationException("StatusHandler is not Set");	
	}

	return statusHandler;
}



public void setStatusHandler(WRTStatusHandler statusHandler) {
	this.statusHandler = statusHandler;
}



public List<String> getFilesSelected() {
	return filesSelected;
}



public void setFilesSelected(List<String> filesSelected) {
	this.filesSelected = filesSelected;
}

		
		

public String getPlistFileName() {
	return plistFileName;
}



public void setPlistFileName(String plistFileName) {
	this.plistFileName = plistFileName;
}



public String getWidgetDirectory() {
	return widgetDirectory;
}



public void setWidgetDirectory(String widgetDirectory) {
	this.widgetDirectory = widgetDirectory;
}



public String getWidgetName() {
	return widgetName;
}



	public void setWidgetName(String widgetName) {
		this.widgetName = widgetName;
	}

	public boolean getHomeScreenValue() {
		return homeScreenValue;
	}

	public String getDisplayname() {
		return displayname;
	}

	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

	public void setHomeScreenValue(boolean homeScreenValue) {
		this.homeScreenValue = homeScreenValue;
	}
	
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getMainHtml() {
		return mainHtml;
	}

	public void setMainHtml(String mainHtml) {
		this.mainHtml = mainHtml;
	}

	public boolean isAllowNetworkAccess() {
		return allowNetworkAccess;
	}

	public void setAllowNetworkAccess(boolean allowNetworkAccess) {
		this.allowNetworkAccess = allowNetworkAccess;
	}

	public boolean isAllowFileAccessOutsideOfWidget() {
		return allowFileAccessOutsideOfWidget;
	}

	public void setAllowFileAccessOutsideOfWidget(
			boolean allowFileAccessOutsideOfWidget) {
		this.allowFileAccessOutsideOfWidget = allowFileAccessOutsideOfWidget;
	}
	
	
	public boolean isHtmlPresent() {
        htmlPresent= false;
        if(getMainHtml()!=null &&getMainHtml().length()>0){        
                
                File mailHtmlfile = new File(widgetDirectory + widgetName        + "/" + mainHtml);
                if (mailHtmlfile != null && mailHtmlfile.length() > 0) {
                        htmlPresent= true;
                }                        
                mailHtmlfile=null;
        }                
        return htmlPresent;
}


	public void setHtmlPresent(boolean htmlPresent) {
		this.htmlPresent = htmlPresent;
	}

	public boolean isPlistPresent() {
		if(getPlistFileName()!=null &&getPlistFileName().length()>0){
			plistPresent= true;
		}	else{			
			plistPresent= false;
		}	
		
		return plistPresent;
	}

	public void setPlistPresent(boolean plistPresent) {
		this.plistPresent = plistPresent;
	}
	

	public File getWidgetModelFile() {
		return widgetModelFile;
	}

	public void setWidgetModelFile(File widgetModelFile) {
		this.widgetModelFile = widgetModelFile;
	}

	public List<String> getReferencedHtmlFiles() {
		return referencedHtmlFiles;
	}

	public void setReferencedHtmlFiles(List<String> referencedHtmlFiles) {
		this.referencedHtmlFiles = referencedHtmlFiles;
	}

	public List<String> getReferencedCssFiles() {
		return referencedCssFiles;
	}

	public void setReferencedCssFiles(List<String> referencedCssFiles) {
		this.referencedCssFiles = referencedCssFiles;
	}

	public List<String> getReferencedJavaScriptFiles() {
		return referencedJavaScriptFiles;
	}

	public void setReferencedJavaScriptFiles(List<String> referencedJavaScriptFiles) {
		this.referencedJavaScriptFiles = referencedJavaScriptFiles;
	}

	public List<String> getReferencedImageFiles() {
		return referencedImageFiles;
	}

	public void setReferencedImageFiles(List<String> referencedImageFiles) {
		this.referencedImageFiles = referencedImageFiles;
	}

	public List<String> getReferencedIFrameFiles() {
		return referencedIFrameFiles;
	}

	public void setReferencedIFrameFiles(List<String> referencedIFrameFiles) {
		this.referencedIFrameFiles = referencedIFrameFiles;
	}
	
	public List<String> getReferencedEmbedFiles() {
		return referencedEmbedFiles;
	}

	public void setReferencedEmbedFiles(List<String> referencedEmbedFiles) {
		this.referencedEmbedFiles = referencedEmbedFiles;
	}
	
	public List<String> getAllReferencedFiles() {
		return allReferencedFiles;
	}

	public void setAllReferencedFiles(List<String> allReferencedFiles) {
		this.allReferencedFiles = allReferencedFiles;
	}

	public XmlElement getPlistXmlFile() {
		return plistXmlFile;
	}

	public void setPlistXmlFile(XmlElement plistXmlFile) {
		this.plistXmlFile = plistXmlFile;
	}

	private void reset() {
		referencedHtmlFiles = new ArrayList<String>();
		referencedCssFiles = new ArrayList<String>();
		referencedJavaScriptFiles = new ArrayList<String>();
		referencedImageFiles = new ArrayList<String>();
		referencedIFrameFiles = new ArrayList<String>();
		referencedEmbedFiles = new ArrayList<String>();
		referencedHtmlQueue = new LinkedList<String>();
		displayname = null;		
		identifier = null;
		version = null;
		mainHtml = null;
		allowNetworkAccess = false;
		allowFileAccessOutsideOfWidget = false;
		htmlPresent = false;
		plistPresent = false;

	}
	
	private void displayModel() {
		
		showData("widgetDirectory                 : "+widgetDirectory);	
		showData("widgetName                      : "+widgetName);	
	
		showData("Plist Filename                  : "+plistFileName);	
		showData("displayname                     : "+displayname);	
		showData("identifier                      : "+identifier);
		showData("version                         : "+version);
		showData("Main Html  File Name            : "+mainHtml);
		showData("allowNetworkAccess              : "+allowNetworkAccess);
		showData("Reference html File              ");
		for(String refname: referencedHtmlFiles)
		{	
			showData("	: "+refname);	
		}
		showData("Reference Java Script File ");
		showData("--------------------------");	
		for(String refname: referencedJavaScriptFiles)
		{	
			showData("	: "+refname);					
		}	
		showData("Reference CSS File");	
		showData("--------------------------");	
		
		for(String refname: referencedCssFiles)
		{	
			showData("	: "+refname);				
		}
		showData("Reference Image  File ");	
		showData("--------------------------");	
		for(String refname: this.referencedImageFiles)
		{	
		showData("	: "+refname);	
		}
			
		showData("getPlistXmlFile	: "+this.getPlistXmlFile());	
		//showData("htmlXmlFile	: "+this.getHtmlXmlFile());
		
		
	}
	
	
	  private static void showData(String s) {
//		System.out.println(s);
	}

	  public static void main(String[] args)  {
		  WidgetModel model= new WidgetModel();
		  try {
				showData(" ############################################################################################################################# ");
				model.getWidgetModel( "C:/ModelTest/Flickr");	

			  /**
				List<String> fileNames=null;	
				FileUtil filehelper = new FileUtil();
						fileNames=	  filehelper.getCurrDirFileFullNames(new File("C:/ModelTest"));
				
						for (String fileName : fileNames) {	
//							showData("----- Project name -----------"+fileName);
							
							if (new File(fileName).isDirectory()) {
								
								showData(" ############################################################################################################################# "+fileName);
								showData(" fileName : "+fileName);								
								 model.getWidgetModel( fileName);
									showData("------------------------------------------------------------------------------------------------------------------------------");
									
							}
							
						}
						*/
			  
			  
		} catch (ValidationException e) {
//			e.printStackTrace();
		}
		  
	  }

	  public File getHtmlXmlFile() {
			return htmlXmlFile;
		}

}
