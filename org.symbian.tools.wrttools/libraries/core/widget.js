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

/**
 * Property widget
 * @type Widget
 * @memberOf Window
 */
Window.prototype.widget = new Widget();

/**
 * Property device
 * @type Device
 * @memberOf Window
 */
Window.prototype.device = new Device();

/**
 * Property menu
 * @type Menu
 * @memberOf Window
 */
Window.prototype.menu = new Menu();

function Widget() {};
Widget.prototype = new Object();
Widget.prototype.widget = new Widget();

/**
 * identifier is a read-only property of the widget object. 
 * It is the unique string identifier that identifies a widget 
 * after it has been installed into a device. The string returned 
 * is the value of the Identifier key defined in the widget 
 * property list file (info.plist).
 */
Widget.prototype.identifier = "";

/**
 * isrotationsupported is a read-only property that returns a 
 * Boolean value determining if the device supports landscape and 
 * portrait screen orientations. If the value is true, the 
 * device supports both landscape and portrait screen orientations.
 */
Widget.prototype.isrotationsupported = new Boolean();

/**
 * Allows the definition of a function to be called
 * when a Widget.is displayed
 */
Widget.prototype.onshow = new Function();

/**
 * Allows the definition of a function to be called
 * when a Widget.sent into the background (hidden)
 */
Widget.prototype.onhide = new Function();
	
/**
 * Launches the browser with the specified url
 * @param {String} url
 *     openURL()
 * @return {Void}
 */
Widget.prototype.openURL = function(url){};
	
/**
 * Returns previously stored preference associated with the specified key
 * @param {String} Key preference value to be fetch
 *     preferenceForKey()
 * @return {String} Value
 */
Widget.prototype.preferenceForKey = function(key) {return "";};
	
	
/**
 * Stores the key associated with the specified preference
 * @param {String} Preference value to be stored
 * @param {String} Key Preference value associated to
 *     setPreferenceForKey()
 * @return {Void}
 */
Widget.prototype.setPreferenceForKey = function(preference, key){};
	
/**
 * Toggle between Tabbed navigation mode or Cursor mode
 * @param {Boolean} Value
 *     setNavigationEnabled()
 * @return {Void}
 */
Widget.prototype.setNavigationEnabled = function(navigationMode) {};

/**
 * Toggle between Tabbed navigation mode or Cursor mode
 * @param {Boolean} Value
 *     setNavigationType()
 * @return {Void}
 */
Widget.prototype.setNavigationType = function(navigationMode) {};
	
/**
 * Open S0-Application identified by UID along with the specified params
 * @param {Integer} Uid hexadecimal value to a specified application
 * @param {String} Value
 *     openApplication()
 * @return {Void}
 */
Widget.prototype.openApplication = function(Uid, param) {};
	
/**
 * Prepares the Widget.to do transition to specified transitionState
 * @param {String} Value Transition state
 *     prepareForTransition()
 * @return {Void}
 */
Widget.prototype.prepareForTransition = function(transitionMode){};
	
/**
 * Does the animation to make the transition between the specified transitionState
 * @param {Void}
 *     performTransition()
 * @return {Void}
 */
Widget.prototype.performTransition = function(){};
	
/**
 * Set the preferred screen orientation to landscape.
 * The display will flip if the phone display orientation
 * is portrait and the phone supports landscape mode.
 * @param {Void}
 *     setDisplayLandscape()
 * @return {Void}
 */
Widget.prototype.setDisplayLandscape = function(){};
	
/**
 * Set the preferred screen orientation to portrait.
 * The display will flip if the phone display orientation
 * is landscape and the phone supports portrait mode.
 * @param {Void}
 *     setDisplayPortrait()
 * @return {Void}
 */
Widget.prototype.setDisplayPortrait = function(){};

/**
 * device object. entry point to device service API (SAPI)
 */
function Device() {}
Device.prototype = new Object();
Device.prototype.device = new Device();

/**
 * device API public method
 * 
 * @method
 * @param {string} provider Name of service provider, eg, "Service.Calendar"
 * @param {string} Interface Name of interface, eg, "IDataSource"
 * @return {Object} service object  
 */
Device.prototype.getServiceObject = function(provider, Interface) {
	return new Object();
};