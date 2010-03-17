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

///////////////////////////////////////////////////////////////////////////////
// This script includes the WRTKit for use in a widget.

// WRTKit version (major.minor.revision, e.g. 1.0.0).
var WRTKIT_VERSION_MAJOR = 1;
var WRTKIT_VERSION_MINOR = 0;
var WRTKIT_VERSION_REVISION = 0;
var WRTKIT_RESOURCE_DIRECTORY = "WRTKit/Resources/";

// Include util script files.
includeScript("WRTKit/Utils/Logger.js");

// Include UI visual definition.
includeStyleSheet("WRTKit/Resources/UI.css");

// Include all UI toolkit script files.
var UI_NO_INIT_ID = "UI_NO_INIT_ID";

includeScript("WRTKit/UI/UIInit.js");
includeScript("WRTKit/UI/UIElement.js");
includeScript("WRTKit/UI/Scrollbar.js");
includeScript("WRTKit/UI/NotificationPopup.js");
includeScript("WRTKit/UI/UIManager.js");
includeScript("WRTKit/UI/View.js");
includeScript("WRTKit/UI/ListView.js");
includeScript("WRTKit/UI/Control.js");
includeScript("WRTKit/UI/Separator.js");
includeScript("WRTKit/UI/Label.js");
includeScript("WRTKit/UI/ContentPanel.js");
includeScript("WRTKit/UI/TextEntryControl.js");
includeScript("WRTKit/UI/TextField.js");
includeScript("WRTKit/UI/TextArea.js");
includeScript("WRTKit/UI/SelectionControl.js");
includeScript("WRTKit/UI/SelectionMenu.js");
includeScript("WRTKit/UI/SelectionList.js");
includeScript("WRTKit/UI/ActionControl.js");
includeScript("WRTKit/UI/FormButton.js");
includeScript("WRTKit/UI/NavigationButton.js");
includeScript("WRTKit/UI/Ajax.js");

// Includes a script file by writing a script tag.
function includeScript(src) {
    document.write("<script type=\"text/javascript\" src=\"" + src + "\"></script>");
}

// Includes a style sheet by writing a style tag.
function includeStyleSheet(src) {
    document.write("<style type=\"text/css\"> @import url(\"" +  src + "\"); </style>");
}
