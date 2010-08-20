function EmulatorMenu() {
	this.is_menu_visible = false; // true/false
	this.is_softkeys_visible = false; // true : only when MenuItem's are
										// displayed
	this.softkeys_visibility = true; // true/false : for hide/show SFK's
	this.is_dimmed = false;
	this.is_rsk_overridden = false;
	this.log_counter = 1;
	this.enable_log = false;
	this.rsk_label = '';
	this.rsk_event = false;
	this.highlighted_item = null;
	
	this.lskCallback = null;
	this.innerLskCallback = null;
	this.rskCallback = null;
}

EmulatorMenu.prototype.hide = function() {
	$("#MenuItemsArea").fadeIn("slow");

	// Hide the SFK's If user hidden them from his code
	if (NOKIA.menu.softkeys_visibility)
		$("#SoftKeysArea").fadeIn("slow");

	NOKIA.menu.is_softkeys_visible = false;
};

EmulatorMenu.prototype.log = function(str) {
	if (!this.enable_log)
		return false;
	NOKIA.layout.log("log", NOKIA.menu.log_counter + ' ' + str);
	NOKIA.layout.log("log", 'is_rsk_overridden: '
			+ NOKIA.menu.is_rsk_overridden);
	NOKIA.layout.log("log", 'rsk_label: ' + NOKIA.menu.rsk_label);
	NOKIA.layout.log("log", 'rsk_event: ' + NOKIA.menu.rsk_event);

	NOKIA.menu.log_counter++;
};

EmulatorMenu.prototype.show = function() {
	if (NOKIA.menu.is_dimmed)
		return false;

	NOKIA.menu.showSoftKeys();

	NOKIA.menu.is_menu_visible = true;
	$("#MenuItemsArea").show();

	NOKIA.menu.highlighted_item = $("#MenuItemsArea > ul > li")[0];
	NOKIA.menu.highlighted_item.className = 'active';

	$("#MenuItemsArea > ul > li").mouseover(function() {
		if (NOKIA.menu.highlighted_item != null) {
			NOKIA.menu.highlighted_item.className = '';
			NOKIA.menu.highlighted_item = null;
		}

		NOKIA.menu.highlighted_item = this;
		NOKIA.menu.highlighted_item.className = 'active';
	});

	$("#SoftKeysArea").mouseout(function() {
		if (!NOKIA.menu.is_menu_visible) {
			return false;
		}

		if (NOKIA.helper.intervalId) {
			clearInterval(NOKIA.helper.intervalId);
		}
		NOKIA.helper.intervalId = setTimeout(function() {
			NOKIA.menu.cancel();
		}, 500);
	});

	// Change the label "Options" to "Select" to LSK
	$("#LskLabel > a")[0].innerHTML = "Select";
	NOKIA.menu.setInnerLsk(NOKIA.menu.selectMenu);

	// Change the label "Exit" to "Cancel" to RSK
	$("#RskLabel > a")[0].innerHTML = 'Cancel';
	NOKIA.menu.setInnerRsk(NOKIA.menu.cancel);

	NOKIA.emulator.setMenuItemsStyle();

};

EmulatorMenu.prototype.selectMenu = function() {
	try {
		if (typeof NOKIA.menu.highlighted_item.onclick != 'undefined') {
			eval(NOKIA.menu.highlighted_item.onclick)();
		}
	} catch (e) {

	}
	// NOKIA.menu.cancel();
};

EmulatorMenu.prototype.cancel = function() {
	if (NOKIA.menu.is_dimmed)
		return false;

	NOKIA.menu.hideSoftKeys();

	NOKIA.menu.is_menu_visible = false;
	$("#MenuItemsArea").hide();

	// Reset the "OPTION" label to LSK
	$("#LskLabel > a")[0].innerHTML = 'Options';
	NOKIA.menu.setInnerLsk(NOKIA.emulator.child.menu.show);

	// Change the label "CANCEL" to "EXIT" to RSK
	if (!NOKIA.menu.is_rsk_overridden) {
		$("#RskLabel > a")[0].innerHTML = 'Exit';
		NOKIA.menu.setInnerRsk(NOKIA.menu.exit);
	} else {
		$("#RskLabel > a")[0].innerHTML = NOKIA.menu.rsk_label;
		NOKIA.menu.setInnerRsk(NOKIA.menu.rsk_event);
	}

};

EmulatorMenu.prototype.exit = function() {
	if (NOKIA.menu.is_dimmed)
		return false;

	if (NOKIA.helper.setHomeScreen())
		return false;

	// clear the Menu Settings
	NOKIA.menu.cancel();
	NOKIA.emulator.child.menu.setRightSoftkeyLabel('', null);
	NOKIA.emulator.child.menu.items = [];
	NOKIA.menu.softkeys_visibility = false;

	// Hide Widget DIV
	NOKIA.menu.hideSoftKeys();
	NOKIA.menu.setInnerLsk(function() {
	});
	NOKIA.menu.setInnerRsk(function() {
	});

	$("#WidgetArea").hide();

	// Show Icon
	var style = NOKIA.deviceList[NOKIA.currentDevice][NOKIA.mode]['style'];
	$('#IconArea').css( {
		'width' : '100%',
		'height' : style['widget']['height'] + 'px',
		'float' : style['widget']['float']
	});

	$('#IconArea')[0].className = NOKIA.mode + NOKIA.currentDevice;

	var img = document.createElement('img');
	img.src = NOKIA.emulator.iconFile;
	img.border = 0;

	var div = document.createElement('div');
	var p = document.createElement('p');

	if (NOKIA.emulator.plist.DisplayName.length <= 12)
		p.innerHTML = NOKIA.emulator.plist.DisplayName;
	else
		p.innerHTML = NOKIA.emulator.plist.DisplayName.substr(0, 11) + '...';

	div.className = 'IconFile';
	div.style.marginTop = parseInt(parseInt(style['widget']['height'] / 2) - 80)
			+ 'px';
	div.appendChild(img);
	img.onclick = function() {

		// close the console DIV
		NOKIA.layout._console_enabled = false;
		NOKIA.layout.render();

		$("#loaderDiv").html("Application is loading. Please wait...");
		$("#loaderDiv")[0].className = 'green';
		$("#loaderDiv").show();
		window.setTimeout(function() {
			document.location = document.location;
		}, 3000);

	};

	div.appendChild(p);

	$("#loaderDiv").html("Click on Icon to Launch Application");
	$("#loaderDiv").show();
	$("#loaderDiv")[0].className = 'yellow';

	$('#IconArea').append(div);
	$('#IconArea').show();

	NOKIA.menu.is_dimmed = true;

};

EmulatorMenu.prototype.initLsk = function() {
	// for RSK
	$('#LskArea')[0].onclick = function() {
		NOKIA.menu.lsk();
	};
	
	$('#LskLabel > a')[0].onclick = function() {
		NOKIA.menu.lsk();
	};
	return true;
};

EmulatorMenu.prototype.initRsk = function() {
	// for RSK
	$('#RskArea')[0].onclick = function() {
		NOKIA.menu.rsk();
	};
	
	$('#RskLabel > a')[0].onclick = function() {
		NOKIA.menu.rsk();
	};
	return true;
};

EmulatorMenu.prototype.lsk = function() {
	if (!this.is_dimmed)
		if (this.lskCallback != null) {
			window.setTimeout(this.lskCallback, 10);
		} else {
			this.innerLskCallback();
		}
};

EmulatorMenu.prototype.rsk = function() {
	if (!this.is_dimmed)
		if (this.rskCallback != null) {
			window.setTimeout(this.rskCallback, 10);
		} else {
			this.innerRskCallback();
		}
};

EmulatorMenu.prototype.setInnerLsk = function(func) {
	this.innerLskCallback = func;
	this.initLsk();
	return true;
};

EmulatorMenu.prototype.setLsk = function(func) {
	this.lskCallback = func;
	return true;
};

EmulatorMenu.prototype.setInnerRsk = function(func) {
	this.innerRskCallback = func;
	this.initRsk();
	return true;
};

EmulatorMenu.prototype.setRsk = function(func) {
	this.rskCallback = func;
	return true;
};

EmulatorMenu.prototype.render = function() {
	if (!NOKIA.menu.softkeys_visibility)
		NOKIA.menu.hideSoftKeys();
	else
		NOKIA.menu.showSoftKeys();

	NOKIA.emulator.setWidgetStyle();
};

EmulatorMenu.prototype.init = function() {
	NOKIA.menu.render();
};

EmulatorMenu.prototype.createSFKArea = function() {
	var a = $('#SoftKeys > a');
	a.html('');

	var preferences = NOKIA.deviceList[NOKIA.currentDevice][NOKIA.mode];

	var lsk = document.createElement('img');
	lsk.border = "0";
	lsk.id = "LskArea";
	lsk.name = "LskArea";
	lsk.src = "preview/images/TransperantImage.png";
	lsk.style.width = preferences.style.softkeysImg.width;
	lsk.style.height = preferences.style.softkeysImg.height;

	var rsk = document.createElement('img');
	rsk.border = 0;
	rsk.id = "RskArea";
	rsk.name = "RskArea";
	rsk.src = "preview/images/TransperantImage.png";
	rsk.style.width = preferences.style.softkeysImg.width;
	rsk.style.height = preferences.style.softkeysImg.height;

	if (NOKIA.mode == 'portrait') {
		lsk.onclick = function() {
			$("#LskLabel > a").trigger('click');
		};
		a[0].appendChild(lsk);

		rsk.onclick = function() {
			$("#RskLabel > a").trigger('click');
		};
		a[1].appendChild(rsk);

	} else {

		rsk.onclick = function() {
			$("#RskLabel > a").trigger('click');
		};
		a[0].appendChild(rsk);

		lsk.onclick = function() {
			$("#LskLabel > a").trigger('click');
		};
		a[1].appendChild(lsk);
	}

};

EmulatorMenu.prototype.showSoftKeys = function() {
	NOKIA.menu.is_softkeys_visible = true;

	NOKIA.emulator.setWidgetStyle();
	$("#SoftKeysArea").show();
};

EmulatorMenu.prototype.hideSoftKeys = function() {
	// Hide the SFK's If user hidden them from his code
	if (!NOKIA.menu.softkeys_visibility)
		$("#SoftKeysArea").hide();

	NOKIA.menu.is_softkeys_visible = false;

	NOKIA.emulator.setWidgetStyle();
};
