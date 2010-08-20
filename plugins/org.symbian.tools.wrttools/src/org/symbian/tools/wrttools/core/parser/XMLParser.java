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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;
import java.util.logging.Logger;

import org.symbian.tools.wrttools.core.parser.XmlElement.Attribute;
import org.symbian.tools.wrttools.core.report.IMessageListener;
import org.symbian.tools.wrttools.core.report.Message;
import org.symbian.tools.wrttools.core.status.IWRTConstants;
import org.symbian.tools.wrttools.core.validator.ValidatorPropMessages;

public class XMLParser {
	private Logger log = Logger.getLogger(getClass().getName());
	protected static Stack<XmlElement> xmlStackElements = new Stack<XmlElement>();

	protected int lineNumber = 1;

	protected final int typeElmBegin = 3;
	protected final int typeElmValue = 7;
	protected final int typeElmEnd = 8;
	protected final int typeEmptyElm = 9;
	protected final int typeErrorElement = 10;

	protected final char beginElement = '<';
	protected final char endElement = '>';
	protected final String endEmptyElement = "/>";
	protected final String beginEndElement = "</";
	protected final String beginCommentElement = "<!--";
	protected final String endCommentElement = "-->";
	protected final String javaScriptCommentElement = "//";

	// <!--View Selector Tab-->

	protected int beginElementCount;
	protected int endElementCount;
	protected int endEmptyElementCount;
	protected int beginEndElementCount;

	protected int beginCommentElementCount;
	protected int endCommentElementCount;
	protected int javaScriptCommentElementCount;

	protected int posBeginElement = 0;
	protected int posEndElement = 0;
	protected int posEndEmptyElement = 0;
	protected int posBeginEndElement = 0;

	protected int posBeginCommentElement = 0;
	protected int posEndCommentElement = 0;
	protected int posJavaScriptCommentElement = 0;

	boolean docTypeSet = false;
	boolean xmlDeclarationSet = false;
	boolean rootElmStartSet = false;
	protected BufferedReader inputStream = null;
	protected String line;
	protected HashMap countMap;
	protected boolean error;
	protected int elmType = 0;
	protected int lastElmType = 0;
	protected XmlElement rootXml;
	protected String targetFileName;
	protected boolean enableValidation = false;
	protected boolean endDoc = false;

	public XmlElement parseXML(File fileName) throws IOException {
		log.finest("parseXML  >>--->>");
		lineNumber = 0;
		targetFileName = fileName.getName();
		showData("Starting parse#########------- fileName :  " + fileName);
		try {
			// rootXml=null;
			inputStream = new BufferedReader(new FileReader(fileName));
			line = "";
			
			while ((line = readNextLine()) != null) {
				showData("lineNumber  "+lineNumber+"  line :  " + line);
				countMap = new HashMap();
				line = line.trim();
				parseLine();

			}
			showData("all lins read " );
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}

		String msgkey = null;
		msgkey = "xml.element.not.closed";
		if (!xmlStackElements.isEmpty()) {
			// showData("-stack is not empty ");
			while (!xmlStackElements.isEmpty()) {

				String elmName = xmlStackElements.lastElement().getName().trim();
				Message msg = new Message();
				msg = new Message();
				msg.setMessageSource(IWRTConstants.VALIDATOR);
				msg.setLineNumber(xmlStackElements.lastElement().getLineNo());
				msg.setTargetObject(targetFileName);
				msg.setMessageKey(msgkey);
				msg.setMessage(ValidatorPropMessages.getString(msgkey) + "   "+ elmName);
				msg.setSeverity(IWRTConstants.ERROR);
				msg.setRecommendAction(ValidatorPropMessages.getString("todo."+ msgkey));
				xmlStackElements.lastElement().addError(msg);
				xmlStackElements.pop();

			}
		}

		log.finest("parseXML <<---<<");
		return rootXml;
	}

	protected void parseLine() throws IOException {
		log.finest("parseLine >>--->>");
//		 showData("parseLine>---->>:"+line+":");
		int x = 0;
		while (line != null && line.trim().length() > 0 && !endDoc) {
			//line = line.trim();
			getcount(line);
			String block = getNextElement();
			if (block != null)
				processBlock(block);
		}// end while.

		log.finest("parseLine <<---<<");
	}

	protected void processBlock(String block) {
		log.finest("processBlock >>-->>");

		getcount(block);

		// showData("-------------------------------------------------------------------");
		// showData( "type "+elmType+" processBlock----------block process :"+block);
		// showData(" beginElementCount----------block process :"+beginElementCount);
		// showData(" endElementCount----------block process :"+endElementCount);
		// showData(" beginEndElementCount----------block process :"+beginEndElementCount);
		// showData(" posBeginEndElement----------block process :"+posBeginEndElement);
		// showData(error +" posBeginElement----------block process :"+posBeginElement);
		//		

		if (error) {

			String newBlock;
			if (beginElementCount == 2 && endElementCount == 1) {
				newBlock = block.substring(posBeginElement, block.indexOf('<',	posBeginElement + 1));
				line = block.substring(block.indexOf('<', posBeginElement + 1) + 1)	+ line;
				getcount(newBlock);
			} else if (beginElementCount == 1 && endElementCount == 2) {
				newBlock = block.substring(0, posEndElement);
				line = block.substring(posEndElement + 1) + line;
				getcount(newBlock);
			} else if (beginElementCount == 1 && beginEndElementCount == 0&& endElementCount == 0) {
				elmType = typeElmBegin;
			} else if (beginElementCount == 1 && beginEndElementCount == 1&& endElementCount == 0) {
				elmType = typeElmEnd;
			}

		}
		if (beginElementCount == 1 && endElementCount == 1) {
			error = false;
		}
		if ((block.startsWith("<?") || block.contains("?xml") || block	.contains("?>"))&& !xmlDeclarationSet) {
			startDocument(block);
			return;
		}
		if (block.startsWith("<!DOCTYPE")|| (block.contains("!DOCTYPE") && !docTypeSet)) {
			processDocType(block);
			return;
		}
		if (elmType == typeElmValue) {
			processElementValue(block);
			lastElmType = elmType;
		}
		if (elmType == typeEmptyElm) {
			processEmptyElement(block);
			lastElmType = elmType;
			return;//					
		}
		if (elmType == typeElmBegin) {
			processStartElement(block);
			lastElmType = elmType;
			return;
		}
		if (elmType == this.typeElmEnd) {
			processCloseElement(block);
			lastElmType = elmType;
			return;
		}

	}

	// ---------------Start Parsing
	// Methods---------------------------------------//

	public void startDocument(String xmlDeclaration) {
		log.finest("startDocument>>-->>");
		// showData("startDocument>>-->> :"+xmlDeclaration);
		rootXml = new XmlElement();
		rootXml.setXmlDeclaration(xmlDeclaration);
		xmlDeclarationSet = true;
		if (error) {
			String msgkey = null;
			if (beginElementCount == 1 && endElementCount == 0) {
				msgkey = "xml.declaration.error.missing.close";
			}
			if (beginElementCount == 0 && endElementCount == 1) {
				msgkey = "xml.declaration.error.missing.open";
			}
			if (beginElementCount == 0 && endElementCount == 0) {
				msgkey = "xml.declaration.error.missing.open.and.close";
			}

			Message msg = new Message();
			msg.setMessageSource(IWRTConstants.VALIDATOR);
			msg.setLineNumber(lineNumber);
			msg.setMessageKey(msgkey);
			msg.setMessage(ValidatorPropMessages.getString(msgkey)); //$NON-NLS-1$
			msg.setSeverity(IWRTConstants.ERROR);
			msg.setRecommendAction(ValidatorPropMessages.getString("todo."+ msgkey));
			rootXml.addError(msg);

		}

		log.finest("startDocument <<--<< ");

	}

	public void processDocType(String docType) {
		log.finest("processDocType >>--->>    : " + docType);
		// showData("processDocType >>--->> : " +docType);
		if (rootXml == null) {
			xmlStackElements = new Stack();
			rootXml = new XmlElement();
		}
		rootXml.setDocType(docType);
		this.docTypeSet = true;

		if (error) {
			String msgkey = null;
			if (beginElementCount == 1 && endElementCount == 0) {
				msgkey = "xml.doctype.error.missing.close";
			}
			if (beginElementCount == 0 && endElementCount == 1) {
				msgkey = "xml.doctype.error.missing.open";
			}
			if (beginElementCount == 0 && endElementCount == 0) {
				msgkey = "xml.doctype.error.missing.open.and.close";
			}

			Message msg = new Message();
			msg.setMessageSource(IWRTConstants.VALIDATOR);
			msg.setLineNumber(lineNumber);
			msg.setMessageKey(msgkey);
			msg.setTargetObject(targetFileName);
			msg.setMessage(ValidatorPropMessages.getString(msgkey));
			msg.setSeverity(IWRTConstants.ERROR);
			msg.setRecommendAction(ValidatorPropMessages.getString("todo."+ msgkey));
			rootXml.addError(msg);

		}

		log.finest("processDocType<<---<<   : ");

	}

	public void processStartElement(String startElem) {
		log.finest("processStartElement >>--->>    : " + startElem);
		// showData("processStartElement >>--->> : "+startElem);

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
			msg.setMessage(ValidatorPropMessages.getString(msgkey) + elmName);
			msg.setSeverity(IWRTConstants.ERROR);
			msg.setRecommendAction(ValidatorPropMessages.getString("todo."
					+ msgkey));

		}
		elmName = getElementName(startElem);
		if (rootXml == null) {
			rootXml = new XmlElement();
			xmlStackElements = new Stack();

		}
		if (rootElmStartSet) {

			XmlElement xmlNode = new XmlElement();
			xmlNode.setName(elmName);
			xmlNode.setLineNo(lineNumber);
			if (!xmlStackElements.isEmpty()) {
				xmlStackElements.lastElement().addChild(xmlNode);
				xmlStackElements.push(xmlNode);
				parseAttributes(xmlNode, startElem, elmName);
				if (error) {
					xmlNode.addError(msg);

				}
			}

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

	public void processCloseElement(String closeElem) {
		log.finest("processCloseElement >>--->>   : " + closeElem);
//		 showData("processCloseElement >>--->> "+closeElem);
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
			msg.setMessage(ValidatorPropMessages.getString(msgkey) + elmName);
			msg.setSeverity(IWRTConstants.ERROR);
			msg.setRecommendAction(ValidatorPropMessages.getString("todo."+ msgkey));

		}
		elmName = getElementName(closeElem);

		if (rootXml == null) {
			rootXml = new XmlElement();
			xmlStackElements = new Stack();
			// error
		}

		if (!xmlStackElements.isEmpty()) {
			if (xmlStackElements.lastElement().getName().trim()	.equalsIgnoreCase(elmName)) {
				if (error) {
					xmlStackElements.lastElement().addError(msg);
				}
				xmlStackElements.pop();
			} else {
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
				msg.setMessage(ValidatorPropMessages.getString(msgkey) + "   "+ xmlStackElements.lastElement().getName());
				msg.setSeverity(IWRTConstants.ERROR);
				msg.setRecommendAction(ValidatorPropMessages.getString("todo."+ msgkey));
				xmlStackElements.lastElement().addError(msg);
				xmlStackElements.pop();

			}
		}
		log.finest("processCloseElement <<--<<   : ");
	}

	public void processEmptyElement(String emptyElem) {
		log.finest("processEmptyElement >>--->>    : " + emptyElem);
		// showData("processEmptyElement >>--->> : "+emptyElem);
		Message msg = null;
		String elmName = null;
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
			msg.setMessage(ValidatorPropMessages.getString(msgkey) + elmName);
			msg.setSeverity(IWRTConstants.ERROR);
			msg.setRecommendAction(ValidatorPropMessages.getString("todo."+ msgkey));

		}
		elmName = getElementName(emptyElem);

		if (rootXml == null) {
			// this is an error first element must not be empty
			rootXml = new XmlElement();
			xmlStackElements = new Stack();
			xmlStackElements.add(rootXml);
		}
		XmlElement emptyNode = new XmlElement();
		emptyNode.setName(elmName);
		emptyNode.setLineNo(lineNumber);
		parseAttributes(emptyNode, emptyElem, elmName);
		if (error) {
			emptyNode.addError(msg);
		}
		xmlStackElements.lastElement().addChild(emptyNode);
		log.finest("processEmptyElement <<--<<    : ");
	}

	public void processElementValue(String value) {
		// showData("processElementValue >>---<< : "+value);
		log.finest("processElementValue >>---<<   : " + value);
		boolean validchar = validateChar(value);
		if ((rootXml == null || xmlStackElements == null)
				&& (xmlStackElements.size() < 1)) {

			if (!validchar) {
				String msgkey = "char.present.before.start.line";
				Message msg = new Message();
				msg.setMessageSource(IWRTConstants.VALIDATOR);
				msg.setLineNumber(lineNumber);
				msg.setMessageKey(msgkey);
				msg.setMessage(value + ValidatorPropMessages.getString(msgkey));
				msg.setSeverity(IWRTConstants.ERROR);
				msg.setRecommendAction(ValidatorPropMessages.getString("todo."+ msgkey));
				// this is an error first element must not be empty
				rootXml = new XmlElement();
				xmlStackElements = new Stack();
				rootXml.addError(msg);

			}
			return;

		}
		if (!(xmlStackElements.lastElement().getName().equalsIgnoreCase("style") && value.length() > 100)) {
			xmlStackElements.lastElement().setValue(value);
		} else {
			xmlStackElements.lastElement().setValue(parseStyleBody(value));

		}

	}

	// ===========================================================
	// Helpers Methods
	// ===========================================================

	protected String getNextElement() throws IOException {
//		showData("getNextElement>>---->>");
		String element = "";
		String processLine = line;
		boolean processLineActive = false;

		// showData("-------------------------------------------------");
		// showData("beginElementCount :"+beginElementCount);
		// showData("endElementCount :"+endElementCount);
		// showData("beginEndElementCount :"+beginEndElementCount);
		//		 
		//
		// showData("beginCommentElementCount :"+beginCommentElementCount);
		// showData("endCommentElementCount :"+endCommentElementCount);
		//		 
		// showData("posBeginElement :"+posBeginElement);
		// showData("posEndElement :"+posEndElement);
		//		

		if (beginCommentElementCount > 0 && endCommentElementCount > 0) {

			if (rootXml != null && xmlStackElements != null
					&& !xmlStackElements.isEmpty()) {
				if (xmlStackElements.lastElement().getName().trim()	.equalsIgnoreCase("script")
						|| xmlStackElements.lastElement().getName().trim().equalsIgnoreCase("Style")

				) {

					if (line.trim().startsWith(beginCommentElement)) {
						String afterEndComment = line.substring(posEndCommentElement + 3);
						String beforeEndComment = line.substring(posBeginCommentElement + 4,posEndCommentElement);
						line = beforeEndComment + afterEndComment;

					} else {
						line = line.replaceFirst(beginCommentElement, " ");
						line = line.replaceFirst(endCommentElement, " ");
					}
					getcount(line);
				}

			}

			if (line.trim().startsWith(beginCommentElement)) {
				line = line.substring(posEndCommentElement + 3);
				return null;
			} else {
				String beforeComment = line.substring(0, posBeginCommentElement);
				line = beforeComment + line.substring(posEndCommentElement + 3);
			}

		}
		if (beginCommentElementCount == 1 && endCommentElementCount == 0) {
			// line = line + " " + inputStream.readLine();
			// lineNumber++;
			readNextLine();
			return null;
		}

		if (beginElementCount == 0 && endElementCount == 0) {
			// line = line + " " + inputStream.readLine();
			// lineNumber++;
			readNextLine();
			return null;
		}
		if (beginElementCount == 2 && endElementCount == 2
				&& beginEndElementCount == 1) {
			element = line.substring(0, posEndElement + 1);
			line = line.substring(posEndElement + 1);
			elmType = typeElmBegin;
			return element;
		}

		if (beginElementCount == 2 && beginEndElementCount == 1) {
			element = line.substring(0, posBeginEndElement);
			line = line.substring(posBeginEndElement);
			error = true;
			elmType = typeErrorElement;
			return element;
		}
		if (beginElementCount == 1 && endElementCount == 2) {
			element = line.substring(0, posEndElement + 1);
			line = line.substring(posEndElement + 1);
			error = true;
			elmType = typeErrorElement;
			return element;
		}

		if (beginElementCount > 0 && endElementCount == 0) {
			// line = line + " " + inputStream.readLine();
			// lineNumber++;
			readNextLine();
			return null;
		}

		if (beginElementCount == 0 && endElementCount > 0) {
			element = line.substring(0, posEndElement + 1);
			line = line.substring(posEndElement + 1);
			error = true;
			elmType = typeErrorElement;
			return element;
		}

		if (beginElementCount > 0 && endElementCount > 0) {
			if (posBeginElement < posEndElement) {
				if (beginElementCount > 1 && endElementCount > 1&& trimElementSpace(line).startsWith("<")) {
					element = line.substring(posBeginElement, posEndElement + 1);
					line = line.substring(posEndElement + 1);
					getcount(element);
					processLine = element;
					processLineActive = true;
				}
				if (beginElementCount == 1 && endElementCount == 1&& trimElementSpace(processLine).startsWith("<")) {
					if (beginEndElementCount == 1) {
						elmType = typeElmEnd;
					} else if (endEmptyElementCount == 1) {
						elmType = typeEmptyElm;
					} else {
						elmType = typeElmBegin;
					}
					if (processLineActive)
						return element;
					element = line.substring(posBeginElement, posEndElement + 1);
					line = line.substring(posEndElement + 1);
					error = false;
					return element;
				} else if (beginElementCount == 2 && endElementCount == 1
						&& trimElementSpace(processLine).startsWith("<")) {
					// clear case of error begin element is not close properly
					error = true;
					if (element.lastIndexOf('<') < posEndElement) {

						if (processLineActive) {
							element = element.substring(0, element.lastIndexOf('<'));
							line = processLine.substring((processLine.lastIndexOf('<')))+ line;
						} else {
							getcount(line);
							element = line.substring(posBeginElement, line.lastIndexOf('<'));
							line = line.substring(line.lastIndexOf('<'));

						}
					}
					getcount(element);
					if (beginElementCount == 1 && endElementCount == 1)
						error = false;
					if (beginEndElementCount == 1)
						elmType = typeElmEnd;
					if (endEmptyElementCount == 1)
						elmType = typeEmptyElm;
					if (lastElmType == typeElmEnd && beginElementCount == 1)
						elmType = typeElmBegin;
					if (lastElmType == typeElmEnd && endElementCount == 1&& endEmptyElementCount == 0)
						elmType = typeElmBegin;
					return element;
				} else if (!(trimElementSpace(processLine).startsWith("<"))) {
					if (!xmlDeclarationSet)
						error = true;
					if (!this.xmlDeclarationSet)
						error = true;

					element = line.substring(0, posBeginElement);
					line = line.substring(posBeginElement);
					elmType = typeElmValue;
				}

			} else {

				if (beginElementCount == 1 && endElementCount == 2
						&& !trimElementSpace(processLine).startsWith("<")
						&& lastElmType == typeElmEnd) {

					element = line.substring(0, posEndElement + 1);
					line = line.substring(posEndElement + 1);
					elmType = typeElmBegin;
					error = true;
				} else {
					element = line.substring(0, posEndElement - 1);
					line = line.substring(posEndElement);
					error = true;
					elmType = typeErrorElement;
				}
			}
		}
		if (line.trim().length() == 0)
			line = null;
//		showData("getNextElement  <<---<<");
		return element;

	}

	protected void getcount(String inputLine) {
		char[] array = inputLine.toLowerCase().toCharArray();
		beginElementCount = 0;
		endElementCount = 0;
		endEmptyElementCount = 0;
		beginEndElementCount = 0;

		posBeginElement = 0;
		posEndElement = 0;
		posEndEmptyElement = 0;
		posBeginEndElement = 0;

		beginCommentElementCount = 0;
		endCommentElementCount = 0;
		posBeginCommentElement = 0;
		posEndCommentElement = 0;

		posJavaScriptCommentElement = 0;
		javaScriptCommentElementCount = 0;

		countMap.clear();
		countMap = null;
		countMap = new HashMap();
		int count = 0;
		char previous = ' ';
		String comment = "";

		for (int i = 0; i < array.length; i++) {
			char charToCount = array[i];
			count = 1;
			if (charToCount == '>' && previous == '/') {
				if (countMap.containsKey(endEmptyElement)) {
					count = (Integer) countMap.get(endEmptyElement);
					count++;
				}
				countMap.put(endEmptyElement, count);
			}
			count = 1;

			if (charToCount == '/' && previous == '<') {
				if (countMap.containsKey(beginEndElement)) {
					count = (Integer) countMap.get(beginEndElement);
					count++;
				}
				countMap.put(beginEndElement, count);
			}
			count = 1;

			if (charToCount == '/' && previous == '/') {
				if (countMap.containsKey(javaScriptCommentElement)) {
					count = (Integer) countMap.get(javaScriptCommentElement);
					count++;
				}
				countMap.put(javaScriptCommentElement, count);
			}
			count = 1;
			if (charToCount == '-' && previous == '-') {

				if (i > 2 && array[i - 2] == '!' && array[i - 3] == '<') {
					if (countMap.containsKey(beginCommentElement)) {
						count = (Integer) countMap.get(beginCommentElement);
						count++;
					}
					countMap.put(beginCommentElement, count);
				}

			}
			count = 1;
			if (charToCount == '>' && previous == '-') {

				if (array[i - 2] == '-') {
					if (countMap.containsKey(endCommentElement)) {
						count = (Integer) countMap.get(endCommentElement);
						count++;
					}
					countMap.put(endCommentElement, count);
				}
			}

			count = 1;
			if (countMap.containsKey(charToCount)) {
				count = (Integer) countMap.get(charToCount);
				count++;
			}
			if (charToCount != ' ') {
				previous = charToCount;
			}
			countMap.put(charToCount, count);
		}

		if (countMap.containsKey(beginElement)) {
			beginElementCount = (Integer) countMap.get(beginElement);
			posBeginElement = inputLine.indexOf(beginElement);
		}
		if (countMap.containsKey(endElement)) {
			endElementCount = (Integer) countMap.get(endElement);
			posEndElement = inputLine.indexOf(endElement);
		}
		if (countMap.containsKey(endEmptyElement)) {
			endEmptyElementCount = (Integer) countMap.get(endEmptyElement);
			posEndEmptyElement = inputLine.indexOf(endEmptyElement);
		}
		if (countMap.containsKey(beginEndElement)) {
			beginEndElementCount = (Integer) countMap.get(beginEndElement);
			posBeginEndElement = inputLine.indexOf(beginEndElement);

		}
		if (countMap.containsKey(this.beginCommentElement)) {
			beginCommentElementCount = (Integer) countMap
					.get(beginCommentElement);
			posBeginCommentElement = inputLine.indexOf(beginCommentElement);

		}
		if (countMap.containsKey(this.endCommentElement)) {
			endCommentElementCount = (Integer) countMap.get(endCommentElement);
			posEndCommentElement = inputLine.indexOf(endCommentElement);

		}
		// showData("endEmptyElementCount :"+endEmptyElementCount);
		// showData("posEndEmptyElement :"+posEndEmptyElement);
		//
		// showData("beginElementCount :"+beginElementCount);
		// showData("posBeginElement :"+posBeginElement);
		// showData("beginEndElementCount :"+beginEndElementCount);
		// showData("posBeginEndElement :"+posBeginEndElement);
		// showData("endElementCount :"+endElementCount);
		// showData("posEndElement :"+posEndElement);

	}

	protected String getElementName(String element) {
		String elmName = null;
		int start = 0;
		int end = 0;
		int spaceIndex = element.indexOf(' ');
		if (beginEndElementCount == 1) {
			start = posBeginEndElement + 2;
		} else if (beginElementCount == 1) {
			start = posBeginElement + 1;
		} else if (beginElementCount == 0) {
			if (element.trim().startsWith("/"))
				start = element.indexOf("/") + 1;
		}

		if (this.endEmptyElementCount == 1) {
			end = posEndEmptyElement;
			if (spaceIndex < posEndEmptyElement && spaceIndex > start) {
				end = element.indexOf(' ');
			}

		} else if (endElementCount == 1) {

			end = posEndElement;
			if (spaceIndex < posEndElement && spaceIndex > start) {
				end = spaceIndex;
			}
		} else if (endElementCount == 0) {
			if (spaceIndex < element.length() && spaceIndex > start) {
				end = spaceIndex;
			}
			if (element.trim().endsWith("/")) {
				if (spaceIndex < element.indexOf("/") && spaceIndex > start) {
					end = spaceIndex;
				} else {
					end = element.indexOf("/");
				}
			}
			if (beginEndElementCount == 1 && spaceIndex < posBeginEndElement) {
				end = element.length();

			}

		}
		if (end <= start)
			end = element.length();
		elmName = element.substring(start, end);
		return elmName;
	}

	protected void parseAttributes(XmlElement elm, String elementLine,
			String name) {
		// log.finest("parseAttributes >>--->> ");
		// showData(" parseAttributes ------->>:" + elementLine+": name :" +
		// name);

		log.finest("parseAttributes >>--->>  name   : " + name);
		log.finest("parseAttributes >>--->>  startElement  : " + elementLine);

		elementLine = trimElementAttrSpace(elementLine);
		int nameEndIndex = elementLine.indexOf(name) + name.length();
		int spaceIndex = 0;
		int equalIndex = 0;
		int quotesIndex = 0;
		int endIndex = 0;
		elementLine = (elementLine.substring(nameEndIndex)).trim();

		char before = ' ';
		char x = ' ';
		StringBuffer otherString = new StringBuffer();
		String key;
		String value;

		Attribute attr = null;
		while (elementLine != null && elementLine.trim().length() > 0) {
			spaceIndex = elementLine.indexOf(' ');
			equalIndex = elementLine.indexOf('=');
			if (elementLine.contains("=")) {
				key = elementLine.substring(0, equalIndex);
				key.replaceAll("=", "");
				log.finest("Key   : " + key);
				// showData("Key :"+key);
				elementLine = elementLine.substring(equalIndex + 1).trim();
				if (elementLine.contains("\"")) {
					if (elementLine.indexOf('"') == 0) {
						elementLine = elementLine.substring(1);
					}

				}
				spaceIndex = elementLine.indexOf(' ');
				quotesIndex = elementLine.indexOf('"');
				if (quotesIndex < 0)
					quotesIndex = elementLine.indexOf('\'');
				equalIndex = elementLine.indexOf('=');
				endIndex = elementLine.indexOf('>');
				if (spaceIndex > quotesIndex) {

					value = elementLine.substring(0, spaceIndex - 1);
					elementLine = elementLine.substring(spaceIndex).trim();
				} else if (spaceIndex < quotesIndex && equalIndex < quotesIndex
						&& spaceIndex > 0) {

					value = elementLine.substring(0, spaceIndex);
					elementLine = elementLine.substring(spaceIndex).trim();
				} else if (spaceIndex < quotesIndex && spaceIndex > 0) {

					value = elementLine.substring(0, quotesIndex);
					elementLine = elementLine.substring(quotesIndex).trim();
				} else if (spaceIndex < quotesIndex && spaceIndex == -1) {

					value = elementLine.substring(0, quotesIndex);
					elementLine = elementLine.substring(quotesIndex + 1).trim();
				} else {

					value = elementLine.substring(0, endIndex);
					elementLine = "";
				}
				log.finest("Value  : " + value);
				// showData(key+"= '"+value+"'");
				attr = elm.new Attribute();
				attr.setKey(key);
				value = value.replaceAll("\"", "");
				value = value.replaceAll("'", "");
				value = value.trim();

				attr.setValue(value);
				elm.addAttribute(attr);
				//				
			} else {
				elementLine = "";
			}

		}
		log.finest("parseAttributes <<--<<");

	}

	// --------------End Parsing
	// Methods-------------------------------------------------------------------
	public String trimElementSpace(String element) {
		log.finest("trimElementSpace>>--->>");
		StringBuffer otherString = new StringBuffer();
		for (int i = 0; i < element.length(); i++) {
			char x = element.charAt(i);
			int ascii = (x > 127) ? '?' : (char) (x & 0x7F);

			if (x != ' ' && ascii != 9) {
				otherString.append(x);
			}
		}
		log.finest("trimElementSpace<<---<<");
		return otherString.toString();
	}

	public String trimElementAttrSpace(String element) {
		log.finest("trimElementAttrSpace>>--->>");
		StringBuffer otherString = new StringBuffer();
		char before = ' ';
		char x = ' ';
		for (int i = 0; i < element.length(); i++) {
			if (i != 0) {
				before = x;
			}

			x = element.charAt(i);
			int ascii = (x > 127) ? '?' : (char) (x & 0x7F);

			if (x != ' ' && ascii != 9) {
				if ((before == ' ' && x != '=') || (before == ' ' && x != '"')) {
					otherString.append(' ');
				}
				otherString.append(x);

			}
		}
		log.finest("trimElementAttrSpace<<---<<");
		return otherString.toString();
	}

	public String readNextLine() throws IOException {

		String nextLine = inputStream.readLine();
		lineNumber++;
		if (nextLine == null) {
			endDoc = true;
			return null;
		}
		String testLine = nextLine;
		testLine = testLine.trim();
		if (rootXml != null && xmlStackElements != null	&& !xmlStackElements.isEmpty()) {
			
			if (xmlStackElements.lastElement().getName().trim()	.equalsIgnoreCase("script")
					&& testLine.startsWith(this.javaScriptCommentElement)) {
			
				if (!(beginCommentElementCount == 1	&& endCommentElementCount == 0 && testLine
						.contains(endCommentElement))) {
					nextLine = "";
					readNextLine();
				}
			}
		}

		line = line + " " + nextLine;
		line = line.trim();

		return line;
	}

	protected static void showData(String s) {
//		if (s != null && s.trim().length() > 0)
//			System.out.println(s);
	}

	protected void getNameAndValue(XmlElement rootXml) {
		// showData("getName "+rootXml.getName());
		// showData("value "+rootXml.getValue());

		for (XmlElement elm : rootXml.getChildList()) {
			getNameAndValue(elm);
		}
		if (rootXml.getAttrList() != null)
			for (Attribute attr : rootXml.getAttrList()) {
				showData("Attr     " + attr.getKey() + "='" + attr.getValue()
						+ "'");
			}
	}

	protected boolean validateChar(String block) {
		char x = ' ';
		int ascii = 0;
		boolean invalid = false;
		String invalidChar = "";
		for (int i = 0; i < block.length(); i++) {
			x = block.charAt(i);
			ascii = (x > 127) ? new Integer(x) : (char) (x & 0x7F);
			if (x > 127) {
				invalidChar = invalidChar + x;
				invalid = true;
			}
			// showData(" ascii for " + x + " is : " + ascii);
		}

		// showData("invalidChar :" + invalidChar);

		if (invalid && enableValidation) {
			String msgkey = "unrecognized.char.present.at.line.no";
			Message msg = new Message();
			msg.setMessageSource(IWRTConstants.VALIDATOR);
			msg.setLineNumber(lineNumber);
			msg.setTargetObject(targetFileName);
			msg.setMessageKey(msgkey);
			msg.setMessage("  '" + invalidChar + "'   "
					+ ValidatorPropMessages.getString(msgkey));
			msg.setSeverity(IWRTConstants.WARN);
			msg.setRecommendAction(ValidatorPropMessages.getString("todo."
					+ msgkey));

		}

		return invalid;
	}

	protected String parseStyleBody(String styleBody) {
		String returnBlock = IWRTConstants.IMAGE_REF;
		// System.out.println("value------- importString "+importString+":");
		while (styleBody != null && styleBody.trim().length() > 0) {
			// showData("importString -- : "+importString);
			String block = null;
			if (styleBody.contains("background-image:")) {
				// showData("styleBody.indexOf(background-image:) -- :
				// "+styleBody.indexOf("background-image:"));
				// showData("styleBody.indexOf(';') -- :
				// "+styleBody.indexOf(';'));
				if (styleBody.indexOf("background-image:") > styleBody
						.indexOf(';')) {
					styleBody = styleBody.substring(styleBody
							.indexOf("background-image:"));
				}

				block = styleBody.substring(styleBody
						.indexOf("background-image:"), styleBody.indexOf(';'));
				styleBody = styleBody.substring(styleBody.indexOf(';') + 1);
				// showData("styleBody : "+styleBody);
			} else {
				styleBody = null;
			}

			if (block != null && block.contains("background-image:")) {
				block = block.replace("background-image:", "");
				block = block.replaceAll("url", "");
				block = block.replaceAll(";", "");
				block = block.trim();
				if (block.contains("(") && block.startsWith("("))
					block = block.substring(1);

				if (block.contains(")") && block.endsWith(")"))
					block = block.substring(0, block.length() - 1);
				if (block.trim().length() > 0)
					returnBlock = returnBlock + block + "|";
			}
		}// end while.
		// showData("styleBody : "+returnBlock+":");
		return returnBlock;
	}

	public class MessageListener implements IMessageListener {
		private int messageSource;

		public int getMessageSource() {
			return 1;
		}

		public void setMessageSource(int messageSource) {
			this.messageSource = messageSource;
		}

		public boolean isMessageHandled(Message msg) {

			if (msg.getMessageSource() == (messageSource)) {
				return true;
			}
			return false;
		}

		public void receiveMessage(Message msg) {
			// showData("------------------------msg "+msg.getMessage());
		}
	}

	public static void main(String[] args) {
		try {
			XMLParser xparser = new XMLParser();
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
