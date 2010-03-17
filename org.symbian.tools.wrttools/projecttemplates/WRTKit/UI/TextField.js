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
// The TextField class implements a single line text entry control.

// Constructor.
function TextField(id, caption, value, masked) {
    if (id != UI_NO_INIT_ID) {
        this.init(id, caption, value, masked);
    }
}

// TextField inherits from TextEntryControl.
TextField.prototype = new TextEntryControl(UI_NO_INIT_ID);

// Initializer - called from constructor.
TextField.prototype.init = function(id, caption, value, masked) {
    uiLogger.debug("TextField.init(" + id + ", " + caption + ", " + value + ", " + masked + ")");
    
    // call superclass initializer
    TextEntryControl.prototype.init.call(this, id, caption);
    
    // create the peer element
    this.peerElement = document.createElement("input");
    this.peerElement.type = masked ? "password" : "text";
    this.controlElement.appendChild(this.peerElement);
    
    // set the value
    this.peerElement.value = (value == null) ? "" : value;
    
    // bind event listeners
    this.bindTextEntryControlListeners();
    
    // update the style
    this.updateStyleFromState();
};

// Updates the style of the control to reflects the state of the control.
TextField.prototype.updateStyleFromState = function() {
    uiLogger.debug("TextField.updateStyleFromState()");
    
    // determine the state name
    var stateName = this.getStyleStateName();
    
    // set element class names
    this.setClassName(this.rootElement, "Control");
    this.setClassName(this.controlElement, "ControlElement");
    this.setClassName(this.assemblyElement, "ControlAssembly ControlAssembly" + stateName);
    this.setClassName(this.captionElement, "ControlCaption ControlCaption" + stateName);
    
    // set peer element class names
    var peerStateName = this.isEnabled() ? stateName : "Disabled";
    this.setClassName(this.peerElement, "TextField TextField" + peerStateName);
};
