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

package org.symbian.tools.wrttools.core.report;

public class Message {

	/**
	 * The name of the resource bundle category which contains the string. (A
	 * resource bundle category identifies the base name of the .properties
	 * file, e.g. "mymessages.properties", "mymessages_en_US.properties" both
	 * have the same resource bundle category: "mymessages"
	 * 
	 */
	private String bundle;
	/**
	 * The id of the message in the resource bundle; used to extract the message
	 * from the bundle.
	 */
	private String messageKey;

	/**
	 * If the message applies to a particular object (e.g. a plist,html
	 * css,javascript), this parameter identifies the object. Otherwise, this
	 * value is null.
	 */
	private String targetObject;

	// Identifies the severity of the message. This flag's value is one of the
	// widget constants.

	private String severity;

	private String message;

	/**
	 * when a message is created
	 */
	private int messageSource;

	private int lineNumber;

	private String fullPath;
	
	private String fileName;

	private boolean fileTypeZip = false;
	private String recommendAction;

	public String getBundle() {
		return bundle;
	}

	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	public String getTargetObject() {
		return targetObject;
	}

	public void setTargetObject(String targetObject) {
		this.targetObject = targetObject;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String _severity) {
		severity = _severity;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String _message) {
		message = _message;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String _messageKey) {

		messageKey = _messageKey;
	}

	public int getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(int _module) {
		messageSource = _module;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	public boolean isFileTypeZip() {
		return fileTypeZip;
	}

	public void setFileTypeZip(boolean fileTypeZip) {
		this.fileTypeZip = fileTypeZip;
	}

	public String getRecommendAction() {
		return recommendAction;
	}

	public void setRecommendAction(String recommendAction) {
		this.recommendAction = recommendAction;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}
