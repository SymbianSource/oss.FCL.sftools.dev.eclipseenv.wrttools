/**
 * Copyright (c) 2009-2010 Symbian Foundation and/or its subsidiary(-ies).
 * All rights reserved.
 * This component and the accompanying materials are made available
 * under the terms of the License "Eclipse Public License v1.0"
 * which accompanies this distribution, and is available
 * at the URL "http://www.eclipse.org/legal/epl-v10.html".
 *
 * Initial Contributors:
 * Symbian Foundation - initial contribution.
 * Contributors:
 */
function AppManager() {};
/**
 * API to allow widgets to access and launch applications.
 */
AppManager.prototype.IAppManager = new IAppManager();
function DataSource() {};
DataSource.prototype.IDataSource = new IDataSource();
function Location() {};
Location.prototype.ILocation = new ILocation();
function Messaging() {};
Messaging.prototype.IMessaging = new IMessaging();
function Sensor() {};
Sensor.prototype.ISensor = new ISensor();
function SysInfo() {};
SysInfo.prototype.ISysInfo = new ISysInfo();

function Result() {}
Result.prototype.ReturnValue = new Object();
/**
 * This is a number that specifies a predefined error code.
 */
Result.prototype.ErrorCode = 0;
/**
 * This is a text string that describes the error.
 */
Result.prototype.ErrorMessage = "";
/**
 * This is a number used as an identification to match transactions 
 * started with the asynchronous call.
 */
Result.prototype.TransactionID = "";

/**
 * Use device object to access service. Do not use constructor.
 */
function IAppManager() {};
/**
 * Retrieves an iterable list of either user-installed applications
 * or all applications on the device, regardless of whether they
 * were preinstalled or installed by the user.
 * 
 * This is a synchronous method.
 * @param {criteria} criteria
 * @returns {Result}
 */
IAppManager.prototype.GetList = function(criteria) { return new Result(); };
/**
 * Launches an application based on a unique ID for the application (UID).
 * It also provides a way to open a specific document (by specifying a 
 * document path), even if it is not the default file type for the application
 * being launched.
 * 
 * This method can be invoked both synchronously and asynchronously.
 * 
 * @param {criteria} criteria This is an object that specifies which application to launch.
 * @param {callback} callback The callback argument is the name of the method that is executed when an asynchronous LaunchApp call has status information to return. This argument is used only with an asynchronous LaunchApp call.  
 * @returns {Result}
 */
IAppManager.prototype.LaunchApp = function(criteria) { return new Result(); };
/**
 * Launches an application based on a given document. This method automatically determines 
 * which application to launch for the specified document.
 * The application can be launched as chained (embedded) or standalone.
 * 
 * This method can be invoked both synchronously and asynchronously.
 * 
 * @param {criteria} criteria This is an object that specifies which document to launch.
 * @param {callback} callback The callback argument is the name of the method that is executed when an asynchronous call has status information to return. This argument is used only with an asynchronous call.  
 * @returns {Result}
 */
IAppManager.prototype.LaunchDoc = function(criteria) { return new Result(); };
/**
 * Cancels an outstanding asynchronous call.
 * If a cancel is sent, but the asynchronous call has already been completed,
 * then result.Errorcode is set to 0 (success).
 * 
 * This is a synchronous method.
 * @returns {Result}
 */
IAppManager.prototype.Cancel = function(criteria) { return new Result(); };

/**
 * Use device object to access service. Do not use constructor.
 */
function IDataSource() {};

/**
 * Retrieves a list of available entries.
 * 
 * @param {any} criteria
 * @returns {Result}
 */
IDataSource.prototype.GetList = function(criteria) { return new Result(); };

/**
 * Adds or replaces an entry.
 * 
 * @param {any} criteria
 * @returns {Result}
 */
IDataSource.prototype.Add = function(criteria) { return new Result(); };

/**
 * Deletes an entry from the list
 * 
 * This method can be called both synchronously and asynchronously
 * @param {criteria} criteria This is an object that specifies which entry to delete.
 * @param {callback} callback The callback argument is the name of the method that is executed when an asynchronous call has status information to return. This argument is used only with an asynchronous call.  
 * @returns {Result}
 */
IDataSource.prototype.Delete = function(criteria) { return new Result(); };

/**
 * Imports entries into data source
 * 
 * This method can be called both synchronously and asynchronously
 * @param {criteria} criteria object that specifies the entries to import and optionally the target
 * @param {callback} callback The callback argument is the name of the method that is executed when an asynchronous call has status information to return. This argument is used only with an asynchronous call.  
 * @returns {Result}
 */
IDataSource.prototype.Import = function(criteria) { return new Result(); };

/**
 * Exports entries from data source
 * 
 * This method can be called both synchronously and asynchronously
 * @param {criteria} criteria object that specifies the entries to export and optionally the source
 * @param {callback} callback The callback argument is the name of the method that is executed when an asynchronous call has status information to return. This argument is used only with an asynchronous call.  
 * @returns {Result}
 */
IDataSource.prototype.Export = function(criteria) { return new Result(); };

/**
 * Notifies the client when entries are created, updated, or deleted
 * 
 * This method can be called both synchronously and asynchronously
 * @param {criteria} criteria object that specifies the entries to monitor for changes and when
 * @param {callback} callback the name of the method that is executed when RequestNotification has results or status information to return.  
 * @returns {Result}
 */
IDataSource.prototype.RequestNotification = function(criteria, callback) { return new Result(); };

/**
 * Cancels an outstanding asynchronous call.
 * 
 * This is a synchronous method.
 * @param {criteria} criteria an object with the TransactionID property (number)
 * @returns {Result}
 */
IDataSource.prototype.Cancel = function(criteria) { return new Result(); };

/**
 * Use device object to access service. Do not use constructor.
 */
function ILocation() {};

/**
 * Retrieves the current location of the device
 * 
 * This method can be called both synchronously and asynchronously
 * @param {criteria} criteria specifies what type of device location information is returned and how
 * @param {callback} callback The callback argument is the name of the method that is executed when an asynchronous call has status information to return. This argument is used only with an asynchronous call.  
 * @returns {Result}
 */
ILocation.prototype.GetLocation = function(criteria) { return new Result(); };

/**
 * Retrieves periodic updates about the current location of the device based on a predefined update interval.
 * 
 * @param {criteria} criteria specifies what type of device location information is returned and how
 * @param {callback} callback the name of the method that is executed when Trace has results or status information to return.  
 * @returns {Result}
 */
ILocation.prototype.Trace = function(criteria, callback) { return new Result(); };

/**
 * Performs mathematical calculations based on a source location and a target location.
 * 
 * @param {criteria} criteria specifies the mathematical operation to perform and the input values to use in the operation.
 * @returns {Result}
 */
ILocation.prototype.Calculate = function(criteria) { return new Result(); };

/**
 * Cancels an outstanding asynchronous call.
 * 
 * @param {criteria} criteria specifies whether to cancel a GetLocation call or a Trace call. The object must contain the CancelRequestType property (string) that is used to specify the type of call to cancel. The possible values for criteria.CancelRequestType are:
 * "GetLocCancel" cancels an asynchronous GetLocation call.
 * "TraceCancel" cancels a Trace call.
 * @returns {Result}
 */
ILocation.prototype.CancelNotification = function(criteria) { return new Result(); };

/**
 * Use device object to access service. Do not use constructor.
 */
function IMessaging();
/**
 * Retrieves a list of messaging objects from the Message Store of the S60 
 * device. Each object contains messaging information, that is, header and 
 * content data for a single message.
 * 
 * @param {criteria} criteria specifies what messaging information is returned and how the returned information is sorted.
 * This is a synchronous method.
 */
IMessaging.prototype.GetList = function(criteria) { return new Result(); };

/**
 * Sends an SMS or MMS message.
 * 
 * This method can be called both synchronously and asynchronously.
 * @param {criteria} criteria specifies the type and details of the message to send.
 * @param {callback} callback the name of the method that is executed when an asynchronous call has status information to return. This argument is used only with an asynchronous call.  
 * @returns {Result}
 */
IMessaging.prototype.Send = function(criteria) { return new Result(); };
/**
 * Registers the widget to receive notifications of new incoming messages.
 * For each new message, the method returns the header information of 
 * that message.
 * 
 * This is an asynchronous method.
 * @param {criteria} criteria specifies the request for notification of new messages. The object must contain the Type property (string), and this property must contain the value "NewMessage"
 * @param {callback} callback the name of the method that is executed when RegisterNotification has results or status information to return  
 * @returns {Result}
 */
IMessaging.prototype.RegisterNotification = function(criteria, callback) { return new Result(); };
/**
 * Cancels notification of new incoming messages.
 * 
 * This is a synchronous method.
 * @param {criteria} criteria specifies the request for cancelling notification of new messages. The object must contain the Type property (string), and this property must contain the value "NewMessage"
 * @returns {Result}
 */
IMessaging.prototype.CancelNotification = function(criteria) { return new Result(); };
/**
 * Changes the read status of a message. The status can be "Read", "Unread", "Replied", or "Forwarded".
 * 
 * This is a synchronous method.
 * @param {criteria} criteria specifies the message whose status to change and the new status
 * @returns {Result}
 */
IMessaging.prototype.ChangeStatus = function(criteria) { return new Result(); };
/**
 * Deletes a message.
 * 
 * This is a synchronous method.
 * @param {criteria} criteria specifies the message to delete. Should have a MessageId (number) field.
 * @returns {Result}
 */
IMessaging.prototype.Delete = function(criteria) { return new Result(); };
/**
 * Cancels an outstanding asynchronous Send or RegisterNotification call.
 * Note: To cancel a RegisterNotification call, use CancelNotification instead, as it provides a more convenient way of doing this.
 * 
 * This is a synchronous method.
 * @param {criteria} criteria an object with the TransactionID property (number). criteria.TransactionID specifies the transaction ID of the Send or RegisterNotification call to cancel. The transaction ID is the result.TransactionID value that was returned as part of the result of the initial call.
 * @returns {Result}
 */
IMessaging.prototype.Cancel = function(criteria) { return new Result(); };

/**
 * Use device object to access service. Do not use constructor.
 */
function ISensor() {};
/**
 * Searches for sensor channels available on the device.
 * 
 * This is a synchronous method.
 * @param {criteria} criteria specifies the search criteria
 * @returns {Result}
 */
ISensor.prototype.FindSensorChannel = function(criteria) { return new Result(); };
/**
 * Registers the client to receive data from one sensor channel.
 * 
 * This is an asynchronous method.
 * @param {criteria} criteria specifies the sensor channel to listen for data
 * @param {callback} callback  the name of the method that is executed when a RegisterForNotification call has results or status information to return  
 * @returns {Result}
 */
ISensor.prototype.RegisterForNotification = function(criteria, callback) { return new Result(); };
/**
 * Stops an ongoing RegisterForNotification call.
 * 
 * This is a synchronous method.
 * @param {criteria} an object with the TransactionID property (number). criteria.TransactionID specifies the transaction ID of the RegisterForNotification call to cancel. The transaction ID is the result.TransactionID value that was returned as part of the result of the initial call.
 * @returns {Result}
 */
ISensor.prototype.Cancel = function(criteria) { return new Result(); };

/**
 * Retrieves information about a sensor channel property.
 * 
 * This is a synchronous method.
 * @param {criteria} specifies which sensor channel property to retrieve information about. 
 * @returns {Result}
 */
ISensor.prototype.GetChannelProperty = function(criteria) { return new Result(); };

function ISysInfo() {};
/**
 * Retrieves information about a system attribute.
 * 
 * This method can be called both synchronously and asynchronously.
 * @param {criteria} criteria specifies the system attribute about which to retrieve information.
 * @param {callback} callback the name of the method that is executed when an asynchronous GetInfo call has results or status information to return.  
 * @returns {Result}
 */
ISysInfo.prototype.GetInfo = function(criteria) { return new Result(); };
/**
 * Modifies the value of a system attribute.
 * 
 * This is a synchronous method.
 * @param {criteria} specifies the new value for the system attribute. 
 * @returns {Result}
 */
ISysInfo.prototype.SetInfo = function(criteria) { return new Result(); };
/**
 * Notifies the client when the value of a system attribute is changed.
 * 
 * This is an asynchronous method.
 * @param {criteria} criteria specifies the system attribute to monitor for changes. 
 * @param {callback} callback the name of the method that is executed when a GetNotification call has results or status information to return  
 * @returns {Result}
 */
ISysInfo.prototype.GetNotification = function(criteria, callback) { return new Result(); };

/**
 * Cancels an ongoing asynchronous call made with a System Information API method.
 * 
 * This is a synchronous method.
 * @param {criteria} criteria an object with the TransactionID property (number). criteria.TransactionID specifies the transaction ID of the asynchronous call to cancel. The transaction ID is the result.TransactionID value that was returned as part of the result of the initial call. 
 * @returns {Result}
 */
ISysInfo.prototype.Cancel = function(criteria) { return new Result(); };
