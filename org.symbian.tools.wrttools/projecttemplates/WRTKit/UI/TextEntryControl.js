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
// The TextEntryControl class is an abstract base class for the single and multi-
// line text entry controls TextField and TextArea. Don't use TextEntryControl
// directly.

// Constructor.
function TextEntryControl(id, caption) {
    if (id != UI_NO_INIT_ID) {
        this.init(id, caption);
    }
}

// TextEntryControl inherits from Control.
TextEntryControl.prototype = new Control(UI_NO_INIT_ID);

// Reference to the peer HTML element.
TextEntryControl.prototype.peerElement = null;

// Initializer - called from constructor.
TextEntryControl.prototype.init = function(id, caption) {
    uiLogger.debug("TextEntryControl.init(" + id + ", " + caption + ")");
    
    // call superclass initializer
    Control.prototype.init.call(this, id, caption);
};

// Common event listeners hookup function called from subclasses.
TextEntryControl.prototype.bindTextEntryControlListeners = function() {
    var self = this;
    this.peerElement.addEventListener("focus", function() { self.focusStateChanged(true); }, false);
    this.peerElement.addEventListener("blur", function() { self.focusStateChanged(false); }, false);
    this.peerElement.addEventListener("mouseover", function() { self.hoverStateChanged(true); }, false);
    this.peerElement.addEventListener("mouseout", function() { self.hoverStateChanged(false); }, false);
    this.peerElement.addEventListener("change", function() { self.valueChanged(); }, false);
};

// Returns the enabled state.
// Override this in subclasses as required to implement the state change.
TextEntryControl.prototype.isEnabled = function() {
    return !this.peerElement.readOnly;
};

// Sets the enabled state.
// Override this in subclasses as required to implement the state change.
TextEntryControl.prototype.setEnabled = function(enabled) {
    uiLogger.debug("TextEntryControl.setEnabled(" + enabled + ")");
    this.peerElement.readOnly = !enabled;
    // update the style
    this.updateStyleFromState();
};

// Returns the control text.
TextEntryControl.prototype.getText = function() {
    return this.peerElement.value;
};

// Sets the text for the control.
TextEntryControl.prototype.setText = function(text) {
    this.peerElement.value = text;
};

// Returns the focusable state for the control.
TextEntryControl.prototype.isFocusable = function() {
    // text entry controls are always focusable
    return true;
};

// Sets the focused state for the control.
// Note: This may not always succeed.
TextEntryControl.prototype.setFocused = function(focused) {
    uiLogger.debug("TextEntryControl.setFocused(" + focused + ")");
    if (focused) {
        this.peerElement.focus();
    } else {
        this.peerElement.blur();
    }
};

// Callback for value change events.
TextEntryControl.prototype.valueChanged = function() {
    uiLogger.debug("TextEntryControl.valueChanged()");
    // notify event listeners
    this.fireEvent(this.createEvent("ValueChanged", this.peerElement.value));
};
