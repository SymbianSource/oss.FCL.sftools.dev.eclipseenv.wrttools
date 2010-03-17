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
// The Label class implements a control that displays textual content.

// Constructor.
function Label(id, caption, text) {
    if (id != UI_NO_INIT_ID) {
        this.init(id, caption, text);
    }
}

// Label inherits from Control.
Label.prototype = new Control(UI_NO_INIT_ID);

// Content element for label text.
Label.prototype.contentElement = null;

// Initializer - called from constructor.
Label.prototype.init = function(id, caption, text) {
    uiLogger.debug("Label.init(" + id + ", " + caption + ", " + text + ")");
    
    // call superclass initializer
    Control.prototype.init.call(this, id, caption);
    
    // create content element
    this.contentElement = document.createElement("div");
    this.controlElement.appendChild(this.contentElement);
    
    // set the text
    this.setText(text);
};

// Returns the enabled state for the control.
Label.prototype.isEnabled = function() {
    return true;
};

// Returns the focusable state for the control.
Label.prototype.isFocusable = function() {
    return false;
};

// Returns the control text.
Label.prototype.getText = function() {
    return this.contentElement.innerHTML;
};

// Sets the text for the control.
Label.prototype.setText = function(text) {
    uiLogger.debug("Label.setText(" + text + ")");
    this.contentElement.innerHTML = (text == null) ? "" : text;
    this.updateStyleFromState();
};

// Updates the style of the control to reflects the state of the control.
Label.prototype.updateStyleFromState = function() {
    uiLogger.debug("Label.updateStyleFromState()");
    
    // set element class names
    this.setClassName(this.rootElement, "Control");
    this.setClassName(this.assemblyElement, "ControlAssembly ControlAssemblyNormal");
    this.setClassName(this.captionElement, "ControlCaption ControlCaptionNormal");
    this.setClassName(this.controlElement, "ControlElement");
    this.setClassName(this.contentElement, "LabelText");
};
