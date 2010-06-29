/**
 * Copyright (c) 2009-2010 Symbian Foundation and/or its subsidiary(-ies).
 * All rights reserved.
 * This component and the accompanying materials are made available
 * under the terms of the License "Eclipse Public License v1.0"
 * which accompanies this distribution, and is available
 * at the URL "http://www.eclipse.org/legal/epl-v10.html".
 *
 * Initial Contributors:
 * Nokia Corporation - initial contribution.
 * 
 * Contributors:
 * 
 * Description:
 * 
 */

/**
 * Sensor.js
 * 
 * Web Runtime Service API emulation 
 * WRT v1.1
 * 
 */

(function(){
	var CHANNEL_ACCEL = 7;
	var CHANNEL_ACCELDT = 8;
	var CHANNEL_ORIENTATION = 10;
	var CHANNEL_ROTATION = 11;

	var transactionToCallback = new Object();
	var nextTransactionId = 2123148; // Random seed number! (Actual number doesn't matter)

	var AccelerometerAxis			= new Array();
	var AccelerometerDoubleTapping	= new Array();
	var Orientation					= new Array();
	var Rotation					= new Array();
	var orientation;
	var xaxis = 0, yaxis = 0, zaxis = 0;
	
	var provider = 'Service.Sensor',
		Interface = 'ISensor';

	/**
	 * Sensor service
	 */
	var SensorService = function(){
		this.FindSensorChannel 			= __FindSensorChannel;
		this.RegisterForNotification	= __RegisterForNotification;
		this.Cancel						= __Cancel;
		this.GetChannelProperty			= __GetChannelProperty;
	};

	device.implementation.extend(provider, Interface, new SensorService() );

	/******************************************************/	
	/******************************************************/	
	/******************************************************/	

	var	context = device.implementation.context,
		_t = context._t,
		method = '',
		result = false,
		DBase = null;

	function notifyAcceleration(x, y, z, o) {
		xaxis = Math.round(x);
		yaxis = Math.round(y);
		zaxis = Math.round(z);

		var res = createAccelerationResult();

		window.setTimeout(function() {
			for ( var i = 0; i < AccelerometerAxis.length; i++) {
				var callback = AccelerometerAxis[i];
				callback(getTransactionId(callback), 9, res);
			}
		}, 5);
		
		if (orientation != o) {
			orientation = o;
			var orRes = createOrientationResult();
			window.setTimeout(function() {
				for ( var i = 0; i < Orientation.length; i++) {
					var callback = Orientation[i];
					callback(getTransactionId(callback), 9, orRes);
				}
			}, 5);
		}
	}

	function createAccelerationResult() {
		return context.Result( {
			DataType : "AxisData",
			TimeStamp : new Date().getTime(),
			XAxisData : xaxis,
			YAxisData : yaxis,
			ZAxisData : zaxis
		});
	}

	function createOrientationResult() {
		return context.Result( {
			DataType : "AxisData",
			TimeStamp : new Date().getTime(),
			DeviceOrientation : orientation
		});
	}
		
	function getTransactionId(callback) {
		for ( var tId in transactionToCallback) {
			if (transactionToCallback[tId] == callback) {
				return tId;
			}
		}
	}
	
	/**
	 * Sensor: FindSensorChannel
	 * @param {Object} criteria
	 */
	function __FindSensorChannel(criteria){
		method = 'FindSensorChannel';
		if(!criteria)
			return error(device.implementation.ERR_MISSING_ARGUMENT, msg.msgCriteriaMissing);
			
		if(typeof criteria != 'object')
			return error(device.implementation.ERR_INVALID_SERVICE_ARGUMENT, msg.msgCriteriaMissing);
						
		if(typeof criteria.SearchCriterion == 'undefined')
			return error(device.implementation.ERR_MISSING_ARGUMENT, msg.msgDataMissing);

		if(typeof criteria.SearchCriterion != 'string')
			return error(device.implementation.ERR_BAD_ARGUMENT_TYPE, msg.msgidInvalid);

		if(!(criteria.SearchCriterion== "All" || criteria.SearchCriterion== "AccelerometerAxis" || criteria.SearchCriterion=="AccelerometerDoubleTapping" || criteria.SearchCriterion=="Orientation" || criteria.SearchCriterion=="Rotation"))
		 	return error(device.implementation.ERR_INVALID_SERVICE_ARGUMENT, msg.msgSearchParamInvalid);

		if(arguments.length > 1)
			return error(device.implementation.ERR_INVALID_SERVICE_ARGUMENT, msg.msgCriteriaMissing);			

		DBase = context.getData(provider);
		var returnValue;
		returnValue = DBase[criteria.SearchCriterion] || [] ;
		
		return context.Result(returnValue,0);			
	}



	/**
	 * Sensor: RegisterForNotification
	 * @param {Object} criteria, callback
	 */
	function __RegisterForNotification(criteria, callback, flag){
		flag = flag || false;
		method = 'RegisterForNotification';
		
		if(arguments.length >2 && (typeof flag != "undefined" && typeof flag != "boolean"))
			return error(device.implementation.ERR_INVALID_SERVICE_ARGUMENT, msg.msgCriteriaMissing);

		if(typeof callback != 'function')
			return error(device.implementation.ERR_MISSING_ARGUMENT, msg.msgInsufficentArgument);
		
		
		if(!criteria)
			return error(device.implementation.ERR_MISSING_ARGUMENT, msg.msgIncompleteInput);
			
		if(typeof criteria != 'object')
			return error(device.implementation.ERR_INVALID_SERVICE_ARGUMENT, msg.msgIncompleteInput);
						
		if(typeof criteria.ListeningType == 'undefined' || typeof criteria.ChannelInfoMap == 'undefined')
			return error(device.implementation.ERR_MISSING_ARGUMENT, msg.msgIncompleteInput);

		if(typeof criteria.ListeningType != 'string')
			return error(device.implementation.ERR_BAD_ARGUMENT_TYPE, msg.msgListenTypeInvalid);
			
		if(typeof criteria.ChannelInfoMap != 'object')
			return error(device.implementation.ERR_BAD_ARGUMENT_TYPE, msg.msgChannelInfoMapInvalid);
		
		if(!(criteria.ListeningType== "ChannelData" ))
		 	return error(device.implementation.ERR_INVALID_SERVICE_ARGUMENT, msg.msgOutofRange);

		if (typeof callback == 'function') {
			var channels = criteria.ChannelInfoMap;
			// for ( var channel in channels) {
			var result = false;
			switch (channels.ChannelId) {
			case CHANNEL_ACCEL:
				AccelerometerAxis.push(callback);
				result = createAccelerationResult();
				break;
			case CHANNEL_ACCELDT:
				AccelerometerDoubleTapping.push(callback);
				break;
			case CHANNEL_ORIENTATION:
				Orientation.push(callback);
				result = createOrientationResult();
				break;
			case CHANNEL_ROTATION:
				Rotation.push(callback);
				break;
			}
			// }
			var tID = nextTransactionId++;
			transactionToCallback[tID] = callback;
			if (result) {
				setTimeout(function() {callback(tID, 9, result);}, 20, callback);
			}
//			var result = context.callAsync(this, arguments.callee, criteria, callback);
			return context.AsyncResult(tID);
		}
				
		return context.ErrorResult();
	}
	
	/**
	 * Sensor: Cancel
	 * @param {Object} criteria
	 */
	function __Cancel(criteria){
		method = 'Cancel';

		if(arguments.length > 1 && typeof criteria != "object" && typeof criteria.TransactionID != "number" && arguments[1])
			return error(device.implementation.ERR_INVALID_SERVICE_ARGUMENT, msg.msgCriteriaMissing);

		if (!criteria || typeof criteria.TransactionID == 'undefined') 
			return error(device.implementation.ERR_MISSING_ARGUMENT, msg.msgTransIDMissing);

		if (criteria.TransactionID == Infinity || criteria.TransactionID == -Infinity) 
			return error(device.implementation.ERR_BAD_ARGUMENT_TYPE, msg.msgTransIDMissing);
		
		if (typeof criteria.TransactionID != 'number')
			return error(device.implementation.ERR_BAD_ARGUMENT_TYPE, msg.msgIncorrectTransID);

		var callback = transactionToCallback[criteria.TransactionID];
		if (typeof callback == 'function') {
			removeCallback(callback, AccelerometerAxis);
			removeCallback(callback, AccelerometerDoubleTapping);
			removeCallback(callback, Orientation);
			removeCallback(callback, Rotation);
			return context.ErrorResult();
		}
		return error(device.implementation.ERR_INVALID_SERVICE_ARGUMENT,
				msg.msgInvalidTransID);
	}

	
	function removeCallback(callback, array) {
		var i = 0;
		for (i = 0; i < array.length; i++) {
			var el = array[i];
			if (el == callback) {
				array.splice(i, 1);
				return;
			}
		}
	}

	/**
	 * Sensor: GetChannelProperty
	 * @param {Object} criteria
	 */
	function __GetChannelProperty(criteria){
		method = 'GetChannelProperty';

		if(!criteria)
			return error(device.implementation.ERR_MISSING_ARGUMENT, msg.msgIncompleteInput);
			
		if(typeof criteria != 'object')
			return error(device.implementation.ERR_INVALID_SERVICE_ARGUMENT, msg.msgIncompleteInput);

		if(typeof criteria.ChannelInfoMap == 'undefined' || typeof criteria.PropertyId == 'undefined')
			return error(device.implementation.ERR_MISSING_ARGUMENT, msg.msgIncompleteInput);
		
		if(typeof criteria.ChannelInfoMap != 'object')
			return error(device.implementation.ERR_BAD_ARGUMENT_TYPE, msg.msgChannelInfoMapInvalid);
		
		if(typeof criteria.PropertyId != 'string')
			return error(device.implementation.ERR_BAD_ARGUMENT_TYPE, msg.msgInvalidPropertyID);
		
		if(criteria.PropertyId != 'Availability' && criteria.PropertyId != "ChannelAccuracy" && criteria.PropertyId != "ChannelDataFormat" && criteria.PropertyId != "ChannelScale" && criteria.PropertyId != "ChannelUnit" && criteria.PropertyId != "ConnectionType" && criteria.PropertyId != "DataRate" && criteria.PropertyId != "Description" && criteria.PropertyId != "MeasureRange" && criteria.PropertyId != "ScaledRange" && criteria.PropertyId != "SensorModel")
			return error(device.implementation.ERR_INVALID_SERVICE_ARGUMENT, msg.msgInvalidPropertyID);

		if(arguments.length > 1)
			return error(device.implementation.ERR_INVALID_SERVICE_ARGUMENT, msg.msgCriteriaMissing);			

		DBase = context.getData(provider);
		var property = DBase['SensorProperty'];
		if(typeof criteria.ChannelInfoMap['ChannelId'] == 'undefined' || typeof criteria.ChannelInfoMap['ChannelId'] != 'number')
			return error(device.implementation.ERR_BAD_ARGUMENT_TYPE, msg.msgChannelInfoMapInvalid);

		var channel = null;
		if(criteria.ChannelInfoMap['ChannelId'] == 7)
		{
			channel = 'AccelerometerAxis';
		}
		else if(criteria.ChannelInfoMap['ChannelId'] == 8)
		{
			channel = 'AccelerometerDoubleTapping';
		}
		else if(criteria.ChannelInfoMap['ChannelId'] == 10)
		{
			channel = 'Orientation';
		}
		else if(criteria.ChannelInfoMap['ChannelId'] == 11)
		{
			channel = 'Rotation';
		}
	
		if(channel == null)
			return error(device.implementation.ERR_BAD_ARGUMENT_TYPE, msg.msgChannelInfoMapInvalid);
		
		var returnValue = property[channel][criteria.PropertyId];
		if(typeof returnValue == 'undefined')
			return context.ErrorResult(device.implementation.ERR_NOT_FOUND);
		return context.Result(returnValue,0);
	}

	/**
	 * Sensor: error
	 * @param {number,string} ErrorCode and ErrorString
	 * Replaces Error String with method name
	 */
	function error(code, msg /*, args...*/){

		var args = ['Sensors',method].concat([].slice.call(arguments,2));
		msg = msg ? _t().arg.apply(msg,args) : undefined;
		return context.ErrorResult(code, msg);
	}


	/** 
	 * error messages
	 * order of %s args: Service name, method name, parameter name 
	 */
	var msg = {	
		msgInterfaceNotSupported 	: '%s:Requested interface not supported by the provider',
		msgInterfaceMissing 		: '%s:Interface name missing',
		msgInsufficentArgument 		: '%s:%s:Insufficent argument for asynchronous request',
		msgListenTypeMissing 		: '%s:%s:Listening type missing',
		msgListenTypeInvalid		: '%s:%s:Listening type is invalid',
		msgChannelInfoMissing		: '%s:%s:ChannelInfoMap missing',
		msgIncompleteInput			: '%s:%s:Incomplete input param list',
		msgOutofRange				: '%s:%s:Listening type is out of allowed range',
		msgCallbackMissing			: '%s:%s:Callback missing',
		msgAlreadyRegistered		: '%s:%s:Notification is already registered on this channel',
		msgCriteriaMissing			: '%s:%s:Search criterion is missing',
		msgInvalidSearchCriteria	: '%s:%s:Invalid Search Criterion',
		msgChannelSearchInvalid		: '%s:%s:Channel search param type invalid',
		msgSearchParamInvalid		: '%s:%s:Invalid channel search param',
		msgTransIDMissing			: '%s:%s:Transaction id is missing',
		msgIncorrectTransID			: '%s:%s:Incorrect TransactionID',
		msgInvalidTransID			: '%s:%s:Invalid TransactionID',
		msgPropertyIDMissing		: '%s:%s:Property id missing',
		msgInvalidPropertyID		: '%s:%s:Property id is invalid',
		msgChannelNotSupported		: '%s:%s:Channel property not supported',
		msgChannelInfoMapInvalid	: '%s:%s:ChannelInfoMap Type Invalid'
	};
	
	_BRIDGE_REF.nokia.emulator.setAccelerationCallback(notifyAcceleration);
}) ();