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

/*
	@chinnapp
*/

if(typeof NOKIA == "undefined" || !NOKIA) 
{
	var NOKIA = {
		version : 'WRT 1.1',
		currentDevice : '240x320',
		mode : 'portrait',
		resolution : ['240x320', '320x240', '360x640', '800x352'],
		rotationSupport : false,
		scriptsLoaded : {
			loader : false,
			widget : false,
			systeminfo : false,
			menu   : false,
			menuItem : false,
			console : false
		}
	};
	NOKIA.namespace = function(name)
	{
		var parts = name.split('.');
		var current = NOKIA;
		for(var key in parts){
			if(!current[parts[key]]){
				current[parts[key]] = {};
			}
			current = current[parts[key]];
		}  
	};
	
	NOKIA.init = function()
	{
		this.rotationSupport = new RotationSupport(accelerationCallback);

		// Not-Supported Browser check
		NOKIA.emulator.is_browserReady = (/MSIE/i.test(navigator.userAgent));
		if(NOKIA.emulator.is_browserReady)
		{
			var notSupportedBrowser = NOKIA.helper.getPreference('__SYM_NOKIA_NOT_SUPPORTED_BROWSER');
			if (notSupportedBrowser != 1) {
				$("#NotificationDiv")[0].className = 'show';
				$("#NotificationDiv").dialog({
					width: 550,
					minWidth: 550,
					minHeight: 350,
					height: 150,
					autoOpen: true,
					position: top,
					title: 'Notification window',
					buttons: {
						Cancel: function(){
							$("#NotificationDiv").dialog('close');
						},
						Continue: function(){
							$("#NotificationDiv").dialog('close');
							NOKIA.helper.setPreference('__SYM_NOKIA_NOT_SUPPORTED_BROWSER', 1);
							NOKIA.init();
						}
					}
				});
				return false;
			}else{
				$("#BrowserNotificationBar").css({display:'block'});
				$("#BrowserNotificationBar > p > a").click(function(){ $("#BrowserNotificationBar").hide(); });
			}
		}
	
		var src = "wrt_preview_main.html";
		if (window.location.href.match(/debug\-frame\.html/gi)) {
			src = "__sym-debug/index.html" + window.location.search;
		}
			
		$('iframe')[0].src = src;

		NOKIA.data.load(deviceResolutionList);
		
		var url = window.location.toString();			
		url = url.split('/');
		
		var pointer = 3;
		if(url[0] == 'http:')
			pointer = 2;

		var t = ''; 
		for(var i=pointer; i<url.length-1; i++){ 	t = t + url[i] + '/'; 	}
		if(url[0] == 'file:')
			NOKIA.emulator.url = 'file:///' + t;
		else
			NOKIA.emulator.url = 'http://' + t;

		//	Common Error/Notification Dialog
		NOKIA.helper.errorDailog = $("#Dialog").dialog({
			bgiframe: true, minHeight: 150, width: 450, modal: true, autoOpen: false,
			buttons: {	
					Cancel: function(){ $(this).dialog('close');	},			
					Reload: function(){ 
						$(this).dialog('close');
						$("#loaderDiv").html("Widget is reloading. Please wait...");
						$("#loaderDiv")[0].className = 'green';
						$("#loaderDiv").show();
						window.setTimeout(function(){
							document.location = document.location;
						}, 3000);
					}			
				}
		});

		//	validating Info.plist
		this.helper.getInfo('Info.plist', NOKIA.helper.getInfoCallback);	

		//	For getting Icon.png
		this.helper.getInfo('Icon.png', NOKIA.helper.getIconCallback);
	};

	/*
	 * NOKIA.data
	 */
	NOKIA.namespace('data.load');

	NOKIA.data.load = function(data){
		NOKIA.deviceList = data;
	};

	/*
	 * NOKIA.emulator
	 */
	NOKIA.namespace('menu');
	NOKIA.menu = new EmulatorMenu();

	/*
	 * NOKIA.emulator
	 */
	NOKIA.namespace('emulator');
	NOKIA.emulator = new Emulator();

	/*
	 * NOKIA.helper functions
	 */
	NOKIA.namespace('helper.loadScript');
	NOKIA.helper = new EmulatorHelper();
	

	/*
	 * NOKIA.layout functions
	 */
	NOKIA.namespace('layout');
	NOKIA.layout = new EmulatorLayout();
}

$(document).ready(function () {
	NOKIA.init();	
});

window.onresize = NOKIA.layout.render;

var EmulatorPreferences = {
		SELECTED_TAB:"__SYM_SELECTED_CONTROLS_TAB"
};
