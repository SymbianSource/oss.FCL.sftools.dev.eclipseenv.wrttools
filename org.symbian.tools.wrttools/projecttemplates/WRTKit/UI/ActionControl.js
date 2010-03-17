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
// The ActionControl class is an abstract base class for action controls like
// buttons. Don't use ActionControl directly.

// Constructor.
function ActionControl(id, caption) {
    if (id != UI_NO_INIT_ID) {
        this.init(id, caption);
    }
}

// ActionControl inherits from Control.
ActionControl.prototype = new Control(UI_NO_INIT_ID);

// Reference to the button element.
ActionControl.prototype.buttonElement = null;

// Reference to the link element.
ActionControl.prototype.linkElement = null;

// Enabled status.
ActionControl.prototype.enabled = false;

// Initializer - called from constructor.
ActionControl.prototype.init = function(id, caption) {
    uiLogger.debug("ActionControl.init(" + id + ", " + caption + ")");
    
    // call superclass initializer
    Control.prototype.init.call(this, id, caption);
    
    // the control defaults to enabled
    this.enabled = true;
};

// Common event listeners hookup function called from subclasses.
ActionControl.prototype.bindActionControlListeners = function() {
    var self = this;
    this.linkElement.addEventListener("focus", function() { self.focusStateChanged(true); }, false);
    this.linkElement.addEventListener("blur", function() { self.focusStateChanged(false); }, false);
    this.buttonElement.addEventListener("mouseover", function() { self.hoverStateChanged(true); }, false);
    this.buttonElement.addEventListener("mouseout", function() { self.hoverStateChanged(false); }, false);
    this.buttonElement.addEventListener("mousedown", function(event) {
                                                       self.controlClicked(event);
                                                       event.stopPropagation();
                                                       event.preventDefault();
                                                   }, true);
    this.buttonElement.addEventListener("keydown", function(event) {
                                                    // center and enter trigger the action
                                                    if (event.keyCode == 0 || event.keyCode == 13) {
                                                        self.controlClicked();
                                                        event.stopPropagation();
                                                        event.preventDefault();
                                                    }
                                                 }, true);
};

// Returns the enabled state.
ActionControl.prototype.isEnabled = function() {
    return this.enabled;
};

// Sets the enabled state.
ActionControl.prototype.setEnabled = function(enabled) {
    uiLogger.debug("ActionControl.setEnabled(" + enabled + ")");
    // switch the state
    this.enabled = enabled;
};

// Sets the focused state for the control.
// Note: This may not always succeed.
ActionControl.prototype.setFocused = function(focused) {
    uiLogger.debug("ActionControl.setFocused(" + focused + ")");
    if (this.enabled) {
        if (focused) {
            this.linkElement.focus();
        } else {
            this.linkElement.blur();
        }
    }
};

// Callback for clicks.
ActionControl.prototype.controlClicked = function(event) {
    uiLogger.debug("ActionControl.controlClicked()");
    
    // if we're enabled then a click results in an action performed event
    if (this.enabled) {
        // focus when clicked
        if (!this.focused) {
            this.linkElement.focus();
        }
        
        // notify event listeners
        this.actionPerformed(event);
    }
};

// Callback for action performed events.
ActionControl.prototype.actionPerformed = function(event) {
    uiLogger.debug("ActionControl.actionPerformed()");
    // notify event listeners
    this.fireEvent(this.createEvent("ActionPerformed", event));
};
