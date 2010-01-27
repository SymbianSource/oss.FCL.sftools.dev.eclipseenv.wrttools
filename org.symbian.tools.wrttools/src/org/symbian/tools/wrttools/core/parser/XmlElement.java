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

import java.util.ArrayList;
import java.util.List;

import org.symbian.tools.wrttools.core.report.Message;

public class XmlElement {

	/**
	 * @param args
	 */
	public enum nodeTypes{
		root, parent, child
	};
	private String xmlDeclaration  ;
	private String docType  ;

	private String nodeType;
	private String name;
	private boolean empty;
	private String value;
	private List<Attribute> attrList;
	private List<Message> errorList;
	private List<XmlElement> childList= new ArrayList<XmlElement>();
	private int lineNo  ;
	



//-------------only for root node--------------------//


	public int getLineNo() {
		return lineNo;
	}

	public void setLineNo(int lineNo) {
		this.lineNo = lineNo;
	}

	public String getXmlDeclaration() {
		return xmlDeclaration;
	}

	public void setXmlDeclaration(String xmlDeclaration) {
		this.xmlDeclaration = xmlDeclaration;
	}

	public String getDocType() {
		return docType;
	}


	public void setDocType(String docType) {
		this.docType = docType;
	}
	
	//-------------only for root node--------------------//

	
	


	public String getNodeType() {
		return nodeType;
	}


	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public boolean isEmpty() {
		return empty;
	}


	public void setEmpty(boolean empty) {
		this.empty = empty;
	}


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}


	public List<Attribute> getAttrList() {

		return attrList;
	}


	public void setAttrList(List attrList) {
		this.attrList = attrList;
	}
	
	public void addAttribute(Attribute attr) {
		if(attrList==null)
		{
			attrList = new ArrayList<Attribute>();
		}
		attrList.add(attr);
	}
	
	public void addError(Message msg) {
		if(errorList==null)
		{
			errorList = new ArrayList<Message>();
		}
		errorList.add(msg);
	}
	public List<Message> getErrorList() {
		return errorList;
	}

	public void setErrorList(List<Message> errorList) {
		this.errorList = errorList;
	}
		
	public List<XmlElement> getChildList() {
		return childList;
	}


	public void setChildList(List<XmlElement> childList) {
		this.childList = childList;
	}
	public void addChild(XmlElement child) {
		if(childList==null)
		{
			childList = new ArrayList<XmlElement>();
		}
		childList.add(child);
	}
public class Attribute  {
    
	private String key;
	private String value;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}

	

}
