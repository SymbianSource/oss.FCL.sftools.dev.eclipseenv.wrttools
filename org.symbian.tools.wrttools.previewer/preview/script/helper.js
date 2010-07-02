function EmulatorHelper() {
	this.path = document.location.pathname;
	this.errorDailog = null;
	this.prefDailog = null;
	this.inspectorDailog = null;
	this.intervalId = null;
	this.infoPlistCounter = false;
	this.IconFileCounter = false;
}

EmulatorHelper.prototype.loadScript = function(path) {
	var head = document.getElementsByTagName("head")[0]
			|| document.documentElement;
	var script = document.createElement("script");

	script.type = "text/javascript";
	script.src = src;
	head.appendChild(script);
};

EmulatorHelper.prototype.loadPreferences = function() {
	if (/dreamweaver/i.test(navigator.userAgent)) {
		$("#dwDeviceHelp")[0].className = '';
		// $("#resSupportLink")[0].className = 'hide';
	}
	// Selecting Resoltion
	var resOptions = $("#resOptions")[0];
	for ( var i = 0; i < NOKIA.resolution.length; i++) {
		if (NOKIA.resolution[i] == NOKIA.currentDevice) {
			resOptions.options[i].selected = true;
			break;
		}
	}

	NOKIA.version = "1.1";
};

EmulatorHelper.prototype.getInfo = function(url, callback) {
	try {
		var xhr = this.ajax();

		if ((/AppleWebKit/i.test(navigator.userAgent)))
			xhr.open("GET", url, false);
		else
			xhr.open("GET", url, true);

		xhr.onreadystatechange = function() {
			// readyState = 4 ; "complete"
			if (xhr.readyState == 4) {
				// status = 200 ; "ok"
				if ((xhr.status == 200) || (!xhr.status)) {
					callback(true, xhr);
				} else {
					callback(false, xhr);
				}
			}

		};
		xhr.send(null);
	} catch (e) {
		if (e.name == 'NS_ERROR_FILE_NOT_FOUND') {
			callback(false, xhr);
		}
	}
};

EmulatorHelper.prototype.getInfoCallback = function(flag, xhr) {
	// If Info.plis NOT FOUND / FAILED LOAD
	// an ERROR!, unable to proceed further
	// STOP there
	if (!flag) {
		if (!NOKIA.helper.infoPlistCounter) {
			NOKIA.helper.infoPlistCounter = true;
			NOKIA.helper.getInfo('info.plist', NOKIA.helper.getInfoCallback);
			return false;
		}

		NOKIA.helper
				.error('WRT Previewer: Initialization failed. Could not open Info.plist file.<br/>Please ensure <strong style="color:#efe352;">Info.plist</strong> file exists in the project root folder and that it is named properly.');
		return false;
	} else {

		var xmlString = xhr.responseText;

		// do some cheating here
		xmlString = xmlString.replace(/<!--(.|[\n\r])*?-->/gm, "");
		xmlString = xmlString.replace(/<\s*true\s*\/>/gi,
				"<string>true</string>");
		xmlString = xmlString.replace(/<\s*false\s*\/>/gi,
				"<string>false</string>");
		xmlString = xmlString.replace(/[\n\r]/gi, "");
		// return the JSON Object
		NOKIA.helper.validate(xmlString);
	}
};

EmulatorHelper.prototype.getIconCallback = function(flag, xhr) {

	if (!flag) {
		if (!NOKIA.helper.IconFileCounter) {
			NOKIA.helper.IconFileCounter = true;
			NOKIA.helper.getInfo('icon.png', NOKIA.helper.getIconCallback);
			return false;
		}
	} else
		NOKIA.emulator.iconFile = (NOKIA.helper.IconFileCounter) ? "icon.png"
				: "Icon.png";
};

EmulatorHelper.prototype.validate = function(xmlObject) {
	var values = xmlObject
			.match(/.*<plist.*?(<dict.*?>\s*(<key[^>]*?>[^<]*?<\/key>\s*<string[^>]*?>[^<]*?<\/string>)*\s*<\/dict>)\s*<\/plist>/)[1];
	if (values == null || values == undefined) {
		NOKIA.helper.error('Corrupted Info.plist file');
		return false;
	}
	values = values.replace(/<dict.*?(<key.*?>\s*.*\s*<\/string>)\s*<\/dict>/,
			"{ $1 }");
	values = values
			.replace(
					/\s*<key.*?>\s*(.*?)\s*<\/key>\s*<string.*?>\s*(.*?)\s*<\/string>\s*/g,
					"\"$1\" : \"$2\", ");
	values = values.replace(/"\s*?,\s*?}/g, "\" }");
	try {
		NOKIA.emulator.plist = JSON.parse(values);
	} catch (exception) {
		NOKIA.helper.error('Corrupted Info.plist file');
		return false;
	}

	try {
		if (typeof NOKIA.emulator.plist.DisplayName != 'undefined') {
			document.title = NOKIA.emulator.plist.DisplayName + ' - '
					+ document.title;
		}
	} catch (e) {
	}

	// Add UI-Event listeners
	NOKIA.helper.addListeners();
	NOKIA.layout.init();
	NOKIA.emulator.render();
};

EmulatorHelper.prototype.ajax = function() {
	// xmlHttpRequest object
	var request = null;

	// branch for native XMLHttpRequest object
	if (window.XMLHttpRequest && !(window.ActiveXObject)) {
		try {
			request = new XMLHttpRequest();
			try {
				// attach the Bypass code, if the browser is firefox
				if (netscape.security.PrivilegeManager.enablePrivilege) {
					// duplicate the function
					request._open = request.open;

					// redefine the function definition
					request.open = function(method, url, flag) {
						try {
							// Enable Universal Browser Read
							netscape.security.PrivilegeManager
									.enablePrivilege("UniversalBrowserRead");

							// call the native XmlHttpRequest.open method
							this._open(method, url, flag);
						} catch (e) {
							// call the native XmlHttpRequest.open method
							this._open(method, url, flag);
						}
					};
				}
			} catch (e) {
				// eatup all exceptions
			}
		} catch (e) {
			request = null;
		}
		// branch for IE/Windows ActiveX version
	} else if (window.ActiveXObject) {
		try {
			request = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (e) {
			try {
				request = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (e) {
				alert('Failed to create XmlHttprequest');
				return null;
			}
		}
	}

	return (request);
};

EmulatorHelper.prototype.error = function(msg) {
	if (NOKIA.menu.enable_log)
		NOKIA.layout.log("log", msg);

	$("#Dialog").html(msg);
	$("#Dialog").dialog('open');
};

EmulatorHelper.prototype.setPreference = function(name, value) {
	if (NOKIA.emulator.prefs[name] != value) {
		NOKIA.emulator.prefs[name] = value;
		$.post("preview/preferences.js", JSON.stringify( {
			"key" : name,
			"value" : value
		}), undefined, "json");
	}
};

EmulatorHelper.prototype.getPreference = function(name) {
	return NOKIA.emulator.prefs[name];
};

EmulatorHelper.prototype.addListeners = function() {
	NOKIA.helper.loadPreferences();
	/*
	 * Render Emulator for Interaction
	 */
	NOKIA.helper.inspectorDailog = $("#InspectorTab").dialog( {
		width : 370,
		minWidth : 300,
		minHeight : 200,
		height : 250,
		autoOpen : false,
		position : top,
		title : '&nbsp;',
		buttons : {
			"Close" : function() {
				$("#InspectorTab").dialog('close');
			},
			"Disconnect Debugger" : function() {
				$.ajax( {
					url : "__sym_command/terminateDebug"
				});
			}
		}
	});

	$('#InspectorBtn').click(function() {
		$('#InspectorTab').dialog('open');
		// Hack for Mac firefox
		if (/Mac/i.test(navigator.userAgent)) {
			$("#WidgetArea iframe").css( {
				overflow : 'hidden'
			});
		}
	});
	$('#resOptions').change(
			function(ele) {
				ele = ele.target || this;

				NOKIA.currentDevice = ele.options[ele.selectedIndex].text;

				// SAVE the device DATA
				NOKIA.helper.setPreference('__SYM_NOKIA_EMULATOR_DEVICE',
						NOKIA.currentDevice);

				NOKIA.emulator.render();
				NOKIA.helper.loadPreferences();
			});

	$("#iframeMask").click(function() {
		$("#orientationIcon").hide();
		$("#iframeMask").hide();
		$("#loaderDiv").hide();

		NOKIA.menu.is_dimmed = false;

		$("#WidgetArea")[0].className = '';

		NOKIA.menu.softkeys_visibility = true;
		NOKIA.menu.showSoftKeys();
	});

	// MenuItems DIV events
	$("#MenuItemsArea").mouseover(function() {
		if (NOKIA.helper.intervalId)
			clearInterval(NOKIA.helper.intervalId);

		$("#MenuItemsArea").show();
	});

	$("#MenuItemsArea").mouseout(function() {
		if (NOKIA.helper.intervalId)
			clearInterval(NOKIA.helper.intervalId);

		NOKIA.helper.intervalId = setTimeout(function() {
			NOKIA.menu.cancel();
		}, 10000);
	});

	NOKIA.layout.currentTab = NOKIA.helper
			.getPreference(EmulatorPreferences.SELECTED_TAB);
	if (NOKIA.layout.currentTab == undefined) {
		NOKIA.layout.currentTab = 0;
	}
	// Tabs
	$('#tabs').tabs(
			{
				select : function(event, ui) {
					var selectedTab = ui.index;
					NOKIA.helper.setPreference(
							EmulatorPreferences.SELECTED_TAB, selectedTab);
					NOKIA.layout.currentTab = selectedTab;
					if (selectedTab == 0) {
						$('#Console-Notification').hide();
					} 
				},
				selected : NOKIA.layout.currentTab
			});
	$(".tabs-bottom .ui-tabs-nav, .tabs-bottom .ui-tabs-nav > *").removeClass(
			"ui-corner-all ui-corner-top").addClass("ui-corner-bottom");

	$('#Console-Notification').click(function() {
		$('#tabs').tabs( {
			selected : 0
		});
		$(this).hide();
		$('Console-Toggle-Button').animate({scrollTop: $('#Console-Toggle-Button')[0].attr("scrollHeight")});
		return NOKIA.layout._console_minimized;
	});
	$("#clockwise").button( {
		icons : {
			primary : "button-clockwise"
		},
		text : null
	}).click(function() {
		NOKIA.emulator.turn(1);
	});
	$("#cclockwise").button( {
		icons : {
			primary : "button-cclockwise"
		},
		text : null
	}).click(function() {
		NOKIA.emulator.turn(-1);
	});
	$("#xleft").button( {
		icons : {
			primary : 'ui-icon-triangle-1-w'
		},
		text : false
	});
	$("#xright").button( {
		icons : {
			primary : 'ui-icon-triangle-1-e'
		},
		text : false
	});
	$("#yleft").button( {
		icons : {
			primary : 'ui-icon-triangle-1-w'
		},
		text : false
	});
	$("#yright").button( {
		icons : {
			primary : 'ui-icon-triangle-1-e'
		},
		text : false
	});
	$("#zleft").button( {
		icons : {
			primary : 'ui-icon-triangle-1-w'
		},
		text : false
	});
	$("#zright").button( {
		icons : {
			primary : 'ui-icon-triangle-1-e'
		},
		text : false
	});

	/*
	 * Event triggering
	 */

	// for battery
	$("#event-battery").click(function(event) {
		if (event.target.className == 'active') {
			$("#event-icons").hide();
			$("#event-battery-info").show();

			/*
			 * $('#slider').slider('option', 'value',
			 * NOKIA.emulator.child._BRIDGE_REF.helper.getBatteryStrength());
			 * NOKIA.emulator.child._BRIDGE_REF.helper.getBatteryStrength()
			 * $('#slider').slider('option', 'value', 10);
			 * $('#slider').slider();
			 */
		}
	});

	$("#event-battery-back").click(function(event) {
		$("#event-icons").show();
		$("#event-battery-info").hide();
	});

	// for messaging
	$("#event-messaging").click(function(event) {
		if (event.target.className == 'active') {
			$("#event-icons").hide();
			$("#event-messaging-info").show();
		}
	});

	$("#event-messaging-back").click(function(event) {
		$("#event-icons").show();
		$("#event-messaging-info").hide();
	});

	// for memory
	$("#event-memory").click(function(event) {
		if (event.target.className == 'active') {
			$("#event-icons").hide();
			$("#event-memory-info").show();
		}
	});

	$("#event-memory-back").click(function(event) {
		$("#event-icons").show();
		$("#event-memory-info").hide();
	});

	// Slider
	$('#slider').slider(
			{
				min : 0,
				max : 100,
				step : 1,
				value : 10,
				animate : true,
				slide : function(event, ui) {
					$("#slider-value-panel > span").html(
							ui.value.toString() + "%");
				},
				change : function(event, ui) {
					var chargeValue = ui.value;
					NOKIA.helper.trigger("power", "chargelevel", chargeValue);
					if (NOKIA.version == 'WRT 1.1')
						NOKIA.helper.triggerSapi("Service.SysInfo",
								"Battery.BatteryStrength", {
									Status : chargeValue
								});
				}
			});
	$("#slider-value-panel > span").html("10%");

	var cc = $("#close-camera");
	cc.click(NOKIA.helper.hideCamera);
	// Bind Buttons to trigger values to WRT 1.0 / 1.1 bindings

	$("#connect-charger").click(NOKIA.helper.triggerEvents);
	$("#disconnect-charger").click(NOKIA.helper.triggerEvents);

	$("#send-sms").click(NOKIA.helper.triggerEvents);
	$("#send-mms").click(NOKIA.helper.triggerEvents);

	$("#connect-memory-card").click(NOKIA.helper.triggerEvents);
	$("#disconnect-memory-card").click(NOKIA.helper.triggerEvents);

};

EmulatorHelper.prototype.setHomeScreen = function() {
	// HomeScreen Support
	if (NOKIA.deviceList[NOKIA.currentDevice].homeScreenSupport) {

		if (typeof NOKIA.emulator.plist.MiniViewEnabled != 'undefined') {
			if (NOKIA.emulator.plist.MiniViewEnabled != 'false') {
				$("#WidgetArea")[0].className = 'hs_' + NOKIA.mode;

				// menu handlining
				NOKIA.menu.softkeys_visibility = false;
				NOKIA.menu.cancel();
				NOKIA.menu.is_dimmed = true;

				$("#loaderDiv").html("Click on widget for Return to Full view");
				$("#loaderDiv")[0].className = 'green';
				$("#loaderDiv").show();

				$("#iframeMask").show();
				$("#orientationIcon").show();

				return true;
			}
		}
	}
	return false;
};

EmulatorHelper.prototype.getElementsLengthInObject = function(items) {
	var count = 0;
	for ( var i in items) {
		if (!items[i].isDimmed)
			count++;
	}

	return count;
};

EmulatorHelper.prototype.showCamera = function() {
	$("#camera").show();
	$("#WidgetArea").hide();
};

EmulatorHelper.prototype.hideCamera = function() {
	$("#camera").hide();
	$("#WidgetArea").show();
};

EmulatorHelper.prototype.triggerEvents = function(event) {
	if (typeof event.target.id == 'undefined')
		return false;

	switch (event.target.id) {
	// for battery
	case 'connect-charger':
		NOKIA.helper.trigger("power", "chargerconnected", 1);
		if (NOKIA.version == 'WRT 1.1')
			NOKIA.helper.triggerSapi("Service.SysInfo",
					"Battery.ChargingStatus", {
						Status : 1
					});
		break;

	case 'disconnect-charger':
		NOKIA.helper.trigger("power", "chargerconnected", 0);
		if (NOKIA.version == 'WRT 1.1')
			NOKIA.helper.triggerSapi("Service.SysInfo",
					"Battery.ChargingStatus", {
						Status : 0
					});
		break;

	// for messaging
	case 'send-sms':
		if (NOKIA.version == 'WRT 1.1')
			NOKIA.helper.triggerSapi("Service.Messaging", "NewMessage", {
				MessageType : 'SMS'
			});
		break;
	case 'send-mms':
		if (NOKIA.version == 'WRT 1.1')
			NOKIA.helper.triggerSapi("Service.Messaging", "NewMessage", {
				MessageType : 'MMS'
			});
		break;

	// for memory
	case 'connect-memory-card':
		if (NOKIA.version == 'WRT 1.1')
			NOKIA.helper.triggerSapi("Service.SysInfo", "Memory.MemoryCard", {
				Status : 1
			});
		break;
	case 'disconnect-memory-card':
		if (NOKIA.version == 'WRT 1.1')
			NOKIA.helper.triggerSapi("Service.SysInfo", "Memory.MemoryCard", {
				Status : 0
			});
		break;
	}
};

EmulatorHelper.prototype.triggerSapi = function(provider, eventType, data) {
	NOKIA.emulator.child.device.implementation.triggerListener(provider,
			eventType, data);
};

EmulatorHelper.prototype.trigger = function(provider, eventType, data) {
	NOKIA.emulator.child.widget.triggerListener(provider, eventType, data);
};

EmulatorHelper.prototype.checkDependencies = function() {

	for ( var key in NOKIA.scriptsLoaded) {
		if (!NOKIA.scriptsLoaded[key])
			return false;
	}

	// for LSK
	NOKIA.menu.setLsk(NOKIA.emulator.child.menu.show);

	// for RSK
	NOKIA.menu.setRsk(NOKIA.menu.exit);

	return true;
};

function accelerationCallback(x, y, z) {
	NOKIA.emulator.accelerationChanged(x, y, z);
}
