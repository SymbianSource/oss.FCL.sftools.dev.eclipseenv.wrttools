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
package org.symbian.tools.wrttools.core.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;
import java.util.logging.Logger;
import org.symbian.tools.wrttools.core.exception.ValidationException;
import org.symbian.tools.wrttools.core.report.Message;
import org.symbian.tools.wrttools.core.status.IWRTConstants;
import org.symbian.tools.wrttools.core.validator.ValidatorPropMessages;
import org.symbian.tools.wrttools.core.widgetmodel.WidgetModel;

public class XMLPlistParser extends XMLParser {
	private Logger log = Logger.getLogger(getClass().getName());
	public boolean isHomeScreenEnabled = false;

	public enum plistElements {
		plist, array, data, date, dict, real, integer, string, FALSE, TRUE, key, xml

	};

	public enum parentElements {
		plist, dict

	};

	public enum emptyElements {
		FALSE, TRUE

	};
	
/**
 * 
 */
	public void processStartElement(String startElem) {
		log.finest("processStartElement >>--->>    : " + startElem);
		// showData("processStartElement >>--->> : " + startElem);

		String elmName = null;
		Message msg = null;

		if (error) {
			String msgkey = null;
			if (beginElementCount == 1 && endElementCount == 0) {
				msgkey = "xml.element.error.missing.close";
			}
			if (beginElementCount == 0 && endElementCount == 1) {
				msgkey = "xml.element.error.missing.open";
			}
			if (beginElementCount == 0 && endElementCount == 0) {
				msgkey = "xml.element.error.missing.open.and.close";
			}

			msg = new Message();
			msg.setMessageSource(IWRTConstants.VALIDATOR);
			msg.setLineNumber(lineNumber);
			msg.setTargetObject(targetFileName);
			msg.setMessageKey(msgkey);
			msg.setMessage(ValidatorPropMessages.getString(msgkey) +"      '"+ elmName+"'");
			msg.setSeverity(IWRTConstants.ERROR);

		}
		elmName = getElementName(startElem);
		if (rootXml == null) {
			rootXml = new XmlElement();
			xmlStackElements = new Stack();

		}
		if (rootElmStartSet) {

			// ---------------------------------
			if (xmlStackElements.lastElement().getName().trim()
					.equalsIgnoreCase(elmName)) {
				// identify error .
				// a close element name which not matches with the last element
				// in the stack.
				String msgkey = null;
				msgkey = "xml.element.not.closed";
				msg = new Message();
				msg.setMessageSource(IWRTConstants.VALIDATOR);
				msg.setLineNumber(xmlStackElements.lastElement().getLineNo());
				msg.setTargetObject(targetFileName);
				msg.setMessageKey(msgkey);
				msg.setMessage(ValidatorPropMessages.getString(msgkey) + "   '"	+ xmlStackElements.lastElement().getName()+"'");
				msg.setSeverity(IWRTConstants.ERROR);
				xmlStackElements.lastElement().addError(msg);
//				showData(" -------------------adding msg  processStartElement : "	+ elmName + "    " + msgkey);

				xmlStackElements.pop();
			} else {
				// -------------------------------
				if (isEmpty(elmName)) {

					String msgkey = null;
					msgkey = "xml.empty.element.not.closed";
					msg = new Message();
					msg.setMessageSource(IWRTConstants.VALIDATOR);
					msg.setLineNumber(lineNumber);
					msg.setTargetObject(targetFileName);
					msg.setMessageKey(msgkey);
					msg.setMessage(ValidatorPropMessages.getString(msgkey)	+ "   '" + elmName+"'");
					msg.setSeverity(IWRTConstants.ERROR);
					if (rootXml == null) {
						// this is an error first element must not be empty
						rootXml = new XmlElement();
						xmlStackElements = new Stack();
						xmlStackElements.add(rootXml);
					}
					XmlElement emptyNode = new XmlElement();
					emptyNode.setName(elmName);
					emptyNode.setLineNo(lineNumber);
					emptyNode.addError(msg);
					addChild(emptyNode);

				} else {

					XmlElement xmlNode = new XmlElement();
					xmlNode.setName(elmName);
					xmlNode.setLineNo(lineNumber);
					if (!xmlStackElements.isEmpty()) {
						addChild(xmlNode);
						// if(validateElement(elmName)) {
						xmlStackElements.push(xmlNode);
						// }

						parseAttributes(xmlNode, startElem, elmName);
						if (error) {
							xmlNode.addError(msg);

						}
					}

				}
			}// ------------------------

		} else {
			rootElmStartSet = true;
			rootXml.setName(elmName);
			rootXml.setLineNo(lineNumber);
			xmlStackElements.push(rootXml);
			parseAttributes(rootXml, startElem, elmName);
			if (error) {
				rootXml.addError(msg);

			}
		}
		log.finest("processStartElement <<--<<    : ");
	}
	// =======================================================================================//

/**
 * 
 */
	public void processCloseElement(String closeElem) {
		log.finest("processCloseElement >>--->>   : " + closeElem);
//		showData("processCloseElement >>--->> " + closeElem);
		String elmName = null;
		Message msg = null;
		if (error) {
			getcount(closeElem);
			String msgkey = null;
			if (beginElementCount == 1 && endElementCount == 0) {
				msgkey = "xml.element.end.error.missing.close";
			}
			if (beginElementCount == 0 && endElementCount == 1) {
				msgkey = "xml.element.end.error.missing.open";
			}
			if (beginElementCount == 0 && endElementCount == 0) {
				msgkey = "xml.element.end.error.missing.open.and.close";
			}

			msg = new Message();
			msg.setMessageSource(IWRTConstants.VALIDATOR);
			msg.setLineNumber(lineNumber);
			msg.setTargetObject(targetFileName);
			msg.setMessageKey(msgkey);
			msg.setMessage(ValidatorPropMessages.getString(msgkey) + "   '"+elmName+"'");
			msg.setSeverity(IWRTConstants.ERROR);
		}
		elmName = getElementName(closeElem);

		if (rootXml == null) {
			rootXml = new XmlElement();
			xmlStackElements = new Stack();
			// error
		}

		if (!xmlStackElements.isEmpty()) {
			if (xmlStackElements.lastElement().getName().trim()
					.equalsIgnoreCase(elmName)) {
				if (error) {
					xmlStackElements.lastElement().addError(msg);
				}
				xmlStackElements.pop();
			} else {
				// identify error .
				// a close element name which not matches with the last element
				// in the stack.
				String msgkey = null;
				

				if (isParent(getElementName(closeElem))) {
//					showData(" --------- isParent : " + closeElem);
					if (closeElementInStack(getElementName(closeElem))) {
						msgkey = "xml.element.not.closed";
						msg = new Message();
						msg.setMessageSource(IWRTConstants.VALIDATOR);
						msg.setLineNumber(lineNumber);
						msg.setTargetObject(targetFileName);
						msg.setMessageKey(msgkey);
						msg.setMessage(ValidatorPropMessages.getString(msgkey) + "   '"	+ xmlStackElements.lastElement().getName()+"'");
						msg.setSeverity(IWRTConstants.ERROR);
						xmlStackElements.lastElement().addError(msg);
						xmlStackElements.pop();
						processCloseElement(closeElem);

					} else {
						msgkey = "xml.close.element.not.opened";
						msg = new Message();
						msg.setMessageSource(IWRTConstants.VALIDATOR);
						msg.setLineNumber(lineNumber);
						msg.setTargetObject(targetFileName);
						msg.setMessageKey(msgkey);
						msg.setMessage(ValidatorPropMessages.getString(msgkey)	+ "    '" + closeElem + "'");
						msg.setSeverity(IWRTConstants.ERROR);
						xmlStackElements.lastElement().addError(msg);
					}

				} else {
					msgkey = "xml.close.element.not.opened";
					msg = new Message();
					msg.setMessageSource(IWRTConstants.VALIDATOR);
					msg.setLineNumber(lineNumber);
					msg.setTargetObject(targetFileName);
					msg.setMessageKey(msgkey);
					msg.setMessage(ValidatorPropMessages.getString(msgkey)
							+ "    '" + closeElem + "'");
					msg.setSeverity(IWRTConstants.ERROR);
					xmlStackElements.lastElement().addError(msg);
				}

			}
		}
//		showData("processCloseElement <<--<<  " + closeElem);
		log.finest("processCloseElement <<--<<   : ");
	}
	// =======================================================================================//


/**
 * 
 * @param elmName
 * @return
 */
	public boolean isParent(String elmName) {
		parentElements[] values = parentElements.values();

		boolean isValidParent = false;
		for (parentElements validElement : values) {
			if (validElement.toString().equalsIgnoreCase(elmName.trim())) {
				isValidParent = true;
				break;
			}
			continue;
		}
		return isValidParent;
	}
/**
 * 
 * @param elmName
 * @return
 */
	public boolean isEmpty(String elmName) {
		emptyElements[] values = emptyElements.values();

		boolean isValidEmpty = false;
		for (emptyElements validElement : values) {
			if (validElement.toString().equalsIgnoreCase(elmName.trim())) {
				isValidEmpty = true;
				break;
			}
			continue;
		}
		return isValidEmpty;
	}
/**
 * 
 * @param chldelement
 */
	public void addChild(XmlElement chldelement) {

		if (isParent(xmlStackElements.lastElement().getName())) {
			xmlStackElements.lastElement().addChild(chldelement);

		} else {
			// a close element name which not matches with the last element in
			// the stack.

			String msgkey = null;
			Message msg = null;
			msgkey = "xml.element.not.closed";
			msg = new Message();
			msg.setMessageSource(IWRTConstants.VALIDATOR);
			msg.setLineNumber(xmlStackElements.lastElement().getLineNo());
			msg.setTargetObject(targetFileName);
			msg.setMessageKey(msgkey);
			msg.setMessage(ValidatorPropMessages.getString(msgkey) + "     '"
					+ xmlStackElements.lastElement().getName()+"'");
			msg.setSeverity(IWRTConstants.ERROR);
			xmlStackElements.lastElement().addError(msg);
//			showData("------------------- adding msg  addChild : "	+ chldelement.getName() + "    " + msgkey);
			xmlStackElements.pop();
			addChild(chldelement);

		}

	}
/**
 * 
 * @param rootXmlElement
 * @return
 */
	private boolean validateElement(String rootXmlElement) {
		log.info("validateElement >>--->>");
		plistElements[] values = plistElements.values();
		boolean isValidElement = false;
		for (plistElements validElement : values) {
			if (validElement.toString().equalsIgnoreCase(rootXmlElement.trim())) {
				isValidElement = true;
				break;

			}
			continue;
		}
		log.info("validateElement <<---<<");
		return isValidElement;
	}
	
	private void Replace(String fname, String oldPattern, String replPattern){
		String line;
		StringBuffer sb = new StringBuffer();
		try {
			FileInputStream fis = new FileInputStream(fname);
			BufferedReader reader=new BufferedReader ( new InputStreamReader(fis));
			while((line = reader.readLine()) != null) {
				line = line.replaceAll(oldPattern, replPattern);
				sb.append(line+"\n");
			}
			reader.close();
			BufferedWriter out=new BufferedWriter ( new FileWriter(fname));
			out.write(sb.toString());
			out.close();
		}
		catch (Throwable e) {
		            System.err.println("*** exception ***");
		}
	}
	private void ReplaceMiniViewEnabled(String fname, String oldPattern, String replPattern){
		try {
			String line;
			boolean miniViewEnableTurn = false;
			StringBuffer sb = new StringBuffer();
			FileInputStream fis = new FileInputStream(fname);
			BufferedReader reader=new BufferedReader ( new InputStreamReader(fis));
			while((line = reader.readLine()) != null) {
				if(!miniViewEnableTurn){
					sb.append(line+"\n");
				}
				else if(miniViewEnableTurn){
					line = line.replace(oldPattern, replPattern);
					sb.append(line+"\n");
					miniViewEnableTurn = false;
				}
				if(line.indexOf("MiniViewEnabled")> 0){
					miniViewEnableTurn = true;
				}
			}
			reader.close();
			BufferedWriter out=new BufferedWriter ( new FileWriter(fname.toString()));
			out.write(sb.toString());
			out.close();
		}
		catch (Throwable e) {
	            System.err.println("*** exception ***");
	    }	
	}

	public void updateKeyValueInPlist(String newWidegtName,String newWidgetID,File widgetPath, File location, String homeScreenValue){
		boolean isHomeScreenWidget = false;
		WidgetModel widgetModel = new WidgetModel();
		try {
			widgetModel.getWidgetModelForWizard(widgetPath);			
		} catch (ValidationException e) {
			e.printStackTrace();
		}
	    File pList = new File(location.toString() + File.separator + widgetModel.getPlistFileName());
	    
		XmlElement rootXml;
		try {
			rootXml = parseXML(pList);
			if (rootXml!=null&&rootXml.getChildList()!=null&&rootXml.getChildList().size() == 1) {
				XmlElement dict = rootXml.getChildList().get(0);
				XmlElement key = null;
				if (dict.getChildList().size() > 1) {
					for (XmlElement elm : dict.getChildList()) {	
							
						if (elm.getName().trim().equalsIgnoreCase("String")) {
							if (key != null && key.getValue() != null) {												        
								if (key.getValue().trim().equalsIgnoreCase(	"DisplayName")&& !(widgetModel.getDisplayname().equalsIgnoreCase(newWidegtName))) {
									Replace(pList.toString(),">"+elm.getValue(), ">"+newWidegtName);
								}
								if (key.getValue().trim().equalsIgnoreCase(	"Identifier")&& !(widgetModel.getIdentifier().equalsIgnoreCase(newWidgetID))) {
									Replace(pList.toString(),">"+elm.getValue(), ">"+newWidgetID);
								}
								key = null;
							}
						} 
						else if (elm.getName().trim().equalsIgnoreCase("key")) {
							key = elm;
							if (key.getValue().trim().equalsIgnoreCase(	"MiniViewEnabled")) {
								isHomeScreenWidget = true;							
							}
						}
						else{
							if(isHomeScreenWidget){
								Boolean h1 = new Boolean(widgetModel.getHomeScreenValue());
								Boolean h2 = new Boolean(homeScreenValue);
								isHomeScreenEnabled = new Boolean(elm.getName());
								if(!(h1.equals(h2))){
								widgetModel.setHomeScreenValue(new Boolean(homeScreenValue));
								ReplaceMiniViewEnabled(pList.toString(),elm.getName(), homeScreenValue);
								isHomeScreenWidget = false;
								}
							}
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
/**
 * 
 * @param elmName
 * @return
 */
	private boolean closeElementInStack(String elmName) {
		log.info("closeElementInStack >>--->>");
		boolean elementPresent = false;
//		showData("------------------- xmlStackElements size: "		+ xmlStackElements.size());

		for (int i = 0; i < xmlStackElements.size(); i++) {
//			showData("------------------- xmlStackElements.get(i).getName() : "		+ xmlStackElements.get(i).getName());

			if (xmlStackElements.get(i).getName().equalsIgnoreCase(elmName)) {
				elementPresent = true;
//				break;
			}

		}
//		showData("------------------- elementPresent : " + elementPresent	+ "    " + elmName);
//		showData("------------------- xmlStackElements is empty : "	+ xmlStackElements.isEmpty());

		log.info("closeElementInStack <<---<<");
		return elementPresent;
	}
	
	
	
	/**
	 * 
	 * @param args
	 */
		public static void main(String[] args) {
			try {
				XMLPlistParser xparser = new XMLPlistParser();
				// xparser.messageHandler = new MessageHandler();
				// xparser.messageHandler.registerListener(xparser.new
				// MessageListener());
				// String fileName = "C:/Documents and
				// Settings/sduvvuri/Desktop/Validator/Info.plist";
				String fileName = "d:/test/abc/info.plist";
				XmlElement rootXml = xparser.parseXML(new File(fileName));
				showData("getName  " + rootXml.getName());
				showData("getXmlDeclaration  " + rootXml.getXmlDeclaration());
				showData("getDocType  " + rootXml.getDocType());
				showData("child size  " + rootXml.getChildList().size());
				if (rootXml != null) {
					for (XmlElement elm : rootXml.getChildList()) {

						xparser.getNameAndValue(elm);

					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

}
