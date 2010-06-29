/**
 * Copyright (c) 2009-2010 Symbian Foundation and/or its subsidiary(-ies). All
 * rights reserved. This component and the accompanying materials are made
 * available under the terms of the License "Eclipse Public License v1.0" which
 * accompanies this distribution, and is available at the URL
 * "http://www.eclipse.org/legal/epl-v10.html".
 * 
 * Initial Contributors: Nokia Corporation - initial contribution.
 * 
 * Contributors:
 * 
 * Description:
 * 
 */
function Emulator() {
	this.child = false;
	this.accelerationCallback = false;
	this.iconFile = 'preview/images/default-Icon.png';
	this.loaded = false;
	this.FORCE = 62;
	this.state = new EmulatorState(0, this.FORCE, 0, true);
	this.orientation = "DisplayUp";

	this.plist = {
		DisplayName : '',
		Identifier : '',
		MainHTML : '',
		AllowNetworkAccess : "false",
		Version : '',
		MiniViewEnabled : "false",
		is_browserReady : false
	};
};

Emulator.prototype.setAccelerationCallback = function(acceleration) {
	this.accelerationCallback = acceleration;
	this.accelerationCallback(this.state.XAxis, this.state.YAxis, this.state.ZAxis, this.orientation);
};

function orientationFromAcceleration(x, y, z) {
	var orientation = NOKIA.emulator.orientation;

	var xangle = Math.asin(x) * 180 / Math.PI;
	if (xangle > 55) {
		orientation = "DisplayRightUp";
	} else if (xangle < -55) {
		orientation = "DisplayLeftUp";
	}

	var yangle = Math.asin(y) * 180 / Math.PI;
	if (yangle > 55) {
		orientation = "DisplayUp";
	} else if (yangle < -55) {
		orientation = "DisplayDown";
	}

	var zangle = Math.asin(z) * 180 / Math.PI;
	if (zangle > 75) {
		orientation = "DisplayUpwards";
	} else if (zangle < -75) {
		orientation = "DisplayDownwards";
	}

	return orientation;
}


Emulator.prototype.accelerationChanged = function(x, y, z) {
	this.state.XAxis = x * this.FORCE;
	this.state.YAxis = y * this.FORCE;
	this.state.ZAxis = z * this.FORCE;
	
	var orientation = orientationFromAcceleration(x, y, z);
	
	if (orientation != this.orientation) {
		this.orientation = orientation;
		NOKIA.layout.render();
	}
	
	if (this.accelerationCallback) {
		this.accelerationCallback(this.state.XAxis, this.state.YAxis, this.state.ZAxis, this.orientation);
	}
};

Emulator.prototype.load = function() {
	if (this.loaded)
		return false;
	NOKIA.layout._console_minimized = (NOKIA.helper
			.getPreference('__SYM_NOKIA_CONSOLE_OPEN') != "true");
	// load the saved device Info
	var device = NOKIA.helper.getPreference('__SYM_NOKIA_EMULATOR_DEVICE');
	NOKIA.currentDevice = device || NOKIA.currentDevice;

	// load the saved device mode
	var mode = NOKIA.helper.getPreference('__SYM_NOKIA_EMULATOR_DEVICE_MODE');
	if (mode != null)
		NOKIA.mode = mode;

	var orientation = Number(NOKIA.helper
			.getPreference('__SYM_NOKIA_EMULATOR_ORIENTATION'));
	if (typeof orientation == "number" && orientation >= -90
			&& orientation <= 180)
		NOKIA.orientation = orientation;

	// SAVE the device DATA
	NOKIA.helper.setPreference('__SYM_NOKIA_EMULATOR_DEVICE',
			NOKIA.currentDevice);
	NOKIA.helper.setPreference('__SYM_NOKIA_EMULATOR_DEVICE_MODE', NOKIA.mode);
	NOKIA.helper.setPreference('__SYM_NOKIA_EMULATOR_ORIENTATION',
			NOKIA.orientation);

	this.loaded = true;

};

Emulator.prototype.render = function() {
	this.load();
	// Selecting Resoltion
	var resOptions = $("#resOptions")[0];
	for ( var i = 0; i < NOKIA.resolution.length; i++) {
		if (NOKIA.resolution[i] == NOKIA.currentDevice) {
			resOptions.options[i].selected = true;
			break;
		}
	}

	if (!NOKIA.emulator.orientationSupports())
		NOKIA.mode = NOKIA.deviceList[NOKIA.currentDevice]['default'];

	if (typeof NOKIA.deviceList == 'undefined'
			|| typeof NOKIA.deviceList[NOKIA.currentDevice] == 'undefined'
			|| typeof NOKIA.deviceList[NOKIA.currentDevice][NOKIA.mode] == 'undefined') {
		alert('Deive resolution: ' + NOKIA.currentDevice + ' or the mode: '
				+ NOKIA.mode + ' not found');
		return false;
	}

	this.setStyle();
	var or = (NOKIA.mode == 'portrait') ? NOKIA.orientation
			: (NOKIA.orientation + 90);
	var val = "rotate(" + or + "deg)";
	$("#DeviceDisplayLayout").css("-moz-transform", val);
	$("#DeviceDisplayLayout").css("-webkit-transform", val);
};

Emulator.prototype.setMode = function(mode) {
	NOKIA.mode = mode;

	// SAVE the device DATA
	NOKIA.helper.setPreference('__SYM_NOKIA_EMULATOR_DEVICE_MODE', NOKIA.mode);

	NOKIA.emulator.render();
};

Emulator.prototype.toggle = function(o) {
	NOKIA.orientation = o;
	NOKIA.helper.setPreference('__SYM_NOKIA_EMULATOR_ORIENTATION',
			NOKIA.orientation);
	NOKIA.emulator.child.device.implementation.setOrientation(o, 0);
	if (NOKIA.emulator.orientationSupports() && (o == 0 || o == -90)) {
		NOKIA.emulator.setMode(o == 0 ? 'portrait' : 'landscape');
	} else {
		NOKIA.emulator.render();
	}
};

Emulator.prototype.orientationSupports = function() {
	return NOKIA.deviceList[NOKIA.currentDevice]['orientation'];
};

Emulator.prototype.setStyle = function() {
	if (!NOKIA.helper.checkDependencies()) {
		setTimeout(NOKIA.emulator.setStyle, 1000);
	}

	var deviceProperties = NOKIA.deviceList[NOKIA.currentDevice][NOKIA.mode];
	var style = deviceProperties['style'];

	// Apply Style and propertis to Device layers
	$("#DeviceDisplayLayout").css(style['layout']);
	$('#DisplayArea').css(style['display']);

	NOKIA.emulator.setWidgetStyle();

	$('#SoftKeysArea').css( {
		'width' : style['menu']['width'] + 'px',
		'height' : style['menu']['height'] + 'px',
		'float' : style['menu']['float']
	});

	$('#SoftKeysArea > ul > li').css('width',
			parseInt(style['menu']['width'] / 2) - 10);

	NOKIA.emulator.setMenuItemsStyle();

	$('#SoftKeys').css(style['softkeys']);
	// $('#SoftKeys > a > img').css(style['softkeysImg']);

	NOKIA.menu.createSFKArea();

	$("#DeviceDisplayLayout").show();
	$("#InspectorTab").show();

	if (/chrome/.test(navigator.userAgent.toLowerCase())) {
		$("#InspectorBtn").show();
	}
	if (/mac/.test(navigator.userAgent.toLowerCase())) {
		$("#MacShortcut").show();
	} else {
		$("#UniversalShortcut").show();
	}
},

Emulator.prototype.setWidgetStyle = function() {
	var style = NOKIA.deviceList[NOKIA.currentDevice][NOKIA.mode]['style'];
	var height;
	if (NOKIA.menu.softkeys_visibility || NOKIA.menu.is_softkeys_visible)
		height = parseInt(style['widget']['height'] - style['menu']['height']);
	else
		height = style['widget']['height'];

	$('#WidgetArea').css( {
		'width' : style['widget']['width'] + 'px',
		'height' : height + 'px',
		'float' : style['widget']['float']
	});
};

Emulator.prototype.setMenuItemsStyle = function() {
	var style = NOKIA.deviceList[NOKIA.currentDevice][NOKIA.mode]['style'];
	var count = 1;
	try {
		if (NOKIA.emulator.child.menu) {
			count = parseInt(NOKIA.helper
					.getElementsLengthInObject(NOKIA.emulator.child.menu.items)) + 1;
		}
	} catch (e) {
		count = 1;
	}
	var height = parseInt(count * style['menu']['optionKeysheight']) + 10;
	var top = parseInt(style['widget']['height'] - height);

	$('#MenuItemsArea').css(
			{
				'width' : style['widget']['width'] + 'px',
				'height' : height + 'px',
				'marginTop' : '-2px',
				'top' : (style['widget']['height'] - height
						- style['menu']['height'] + 2)
						+ 'px',
				'position' : 'relative'
			});
};

function EmulatorState(x, y, z, orientation) {
	this.XAxis = x;
	this.YAxis = y;
	this.ZAxis = z;
	this.portrait = orientation;
}