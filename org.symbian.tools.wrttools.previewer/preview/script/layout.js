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
function EmulatorLayout() {
	this._console_minimized = true;
	this._console_enabled = false;
	this._consoleWindowHeight = 290;
	this._consoleHeaderHeight = 31;
	this._tabHeight = 27;
	this.currentTab = 0;
}

EmulatorLayout.prototype.init = function() {
	// Toggle console window
	$('#preview-ui-bottom-header')
			.click(
					function() {
						NOKIA.layout._console_minimized = (NOKIA.layout._console_minimized) ? false
								: true;
						if (NOKIA.layout.currentTab == 0) {
							$("#Console-Notification").hide();
						}
						NOKIA.layout.render();
						NOKIA.helper.setPreference("__SYM_NOKIA_CONSOLE_OPEN",
								!NOKIA.layout._console_minimized);
					});

	// clear Log
	$("#Console-Clear-Button").click(function() {
		$("#preview-ui-bottom-body > *").not("p#hint").remove();
		$("p#hint").show();
	});

	$('#preview-ui-bottom').show();
	NOKIA.layout.render();
};

EmulatorLayout.prototype.log = function(type, msg) {
	if (this.currentTab != 0 || this._console_minimized) {
		$('#Console-Notification').show();
	}
	$("p#hint").hide();
	var p = document.createElement('p');
	p.className = type;
	p.innerHTML = msg;
	var divBody = $('#preview-ui-bottom-body');
	divBody.append(p);
	divBody[0].scrollTop = divBody[0].scrollHeight;
};

EmulatorLayout.prototype.render = function() {
	var _width = parseInt(window.innerWidth);
	var _height = parseInt(window.innerHeight);

	if (!NOKIA.layout._console_enabled) {
		$('#preview-ui-bottom').css( {
			display : 'none'
		});

		$('#preview-ui-top').css( {
			height : _height + 'px'
		});

		return false;
	}

	if (!NOKIA.layout._console_minimized) {
		$('#Console-Toggle-Button')[0].className = 'open';

		// set STYLE details for TOP window
		$('#preview-ui-top').css(
				{
					height : parseInt(_height
							- NOKIA.layout._consoleWindowHeight)
							+ 'px'
				});

		// set STYLE details for Bottom window
		$('#preview-ui-bottom').css( {
			height : NOKIA.layout._consoleWindowHeight + 'px',
			display : 'block'
		});

		$('#preview-ui-bottom-header').css( {
			height : NOKIA.layout._consoleHeaderHeight + 'px'
		});

		$('#tabs').css(
				{
					height : parseInt(NOKIA.layout._consoleWindowHeight
							- NOKIA.layout._consoleHeaderHeight)
							+ 'px',
					display : 'block'
				});
		$('#console').css(
				{
					height : parseInt(NOKIA.layout._consoleWindowHeight
							- NOKIA.layout._consoleHeaderHeight
							- NOKIA.layout._tabHeight * 2)
							+ 'px',
					display : 'block'
				});

		// Auto scroll when console window opened from MINIMIZED =>
		// MAXIMIZED state
		window
				.setTimeout(
						function() {
							$('#preview-ui-bottom-body')[0].scrollTop = $('#preview-ui-bottom-body')[0].scrollHeight;
						}, 100);

	} else {
		$('#Console-Toggle-Button')[0].className = 'close';

		// set STYLE details for TOP window
		$('#preview-ui-top').css(
				{
					height : parseInt(_height
							- NOKIA.layout._consoleHeaderHeight)
							+ 'px'
				});

		// set STYLE details for Bottom window
		$('#preview-ui-bottom').css( {
			height : NOKIA.layout._consoleHeaderHeight + 'px',
			display : 'block'
		});

		$('#preview-ui-bottom-header').css( {
			height : NOKIA.layout._consoleHeaderHeight + 'px',
			display : 'block'
		});
	}
};