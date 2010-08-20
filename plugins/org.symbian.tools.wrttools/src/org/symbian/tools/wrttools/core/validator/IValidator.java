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

import org.symbian.tools.wrttools.core.report.MessageHandler;

/**
* IValidator is an interface that, when implemented, validates a model. A Model
* can be any file for widget project plist,html, javaScript, css are in the set of model.
* A validator loads its model through an FileUtil, traverses the model
* itself, and communicates with the user through the IReporter. Because each
* validator instance performs on the same type of model input, and performs the
* same rule checks against that input, no more than one instance of a validator
* is needed.
*
* When a validator adds a validation message, it identifies itself through a
* unique id; thus, when a particular's file's validation messages need to be
* removed , all messages associated with that file, by that validator, are
* removed. Every message shown to the user, whether it's the message in a
* ValidationException, or a validation error, will be displayed to the user.
* Validators do not display messages to the user other than validation messages
* and subtask messages. This is necessary for Locale-neutrality and
* environment-neutrality. If a catastrophic error occurs, from which the
* validator cannot recover, the validator should throw a ValidationException
*
* A dummy List<IMessage> is passed as parameter to which the Rule methods will
* add messages when a validation rules is failed. Boolean variable will be
* returned to indicate the overall status of validation even if one procedure
* is failed boolean false will be returned. If returned value is false then
* List<IMessage> messageList size will be greater than zero if returned value
* is true the List<IMessage> messageList will be empty creating a dummy list
* to pass a parameter to the calling method this list will be used by the rules
* method to add the validation messages if any rules validation is failed
*
* @author Sailaja Duvvuri
* 
*/
public interface IValidator {

	
	
		/**
		* Every validator class must implement this method. Validation logic for each
		* type will be different A dummy List<IMessage> is passed as parameter to
		* which the Rule methods will add messages when a validation rules is failed.
		*
		* Boolean variable will be returned to indicate the overall status of
		* validation even if one procedure is failed boolean false will be returned. If
		* returned value is false then List<IMessage> messageList size will be greater
		* than zero if returned value is true the List<IMessage> messageList will be
		* empty creating a dummy list to pass a parameter to the calling method this
		* list will be used by the rules method to add the validation messages if any
		* rules validation is failed
		*
		* filename is assumed as absolute path.
		* need to set the option a relative path will be implemented 
		*  
		* @param filenme
		* @return
		*/	
		public boolean validate(File filename)throws Exception ;


		public MessageHandler getMessageHandler() ;


		public void setMessageHandler(MessageHandler messageHandler) ;
}
