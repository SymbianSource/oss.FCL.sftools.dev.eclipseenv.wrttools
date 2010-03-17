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
// The View class is an abstract base class for views in the UI toolkit.
// Don't use the View directly - instead use a concrete subclass like ListView.

// Constructor.
function View(id) {
    if (id != UI_NO_INIT_ID) {
        this.init(id);
    }
}

// View inherits from UIElement.
View.prototype = new UIElement(UI_NO_INIT_ID);

// Currently focused control.
View.prototype.focusedControl = null;

// Initializer - called from constructor.
View.prototype.init = function(id) {
    uiLogger.debug("View.init(" + id + ")");
    
    // call superclass initializer
    UIElement.prototype.init.call(this, id);
};

// Returns the currently focused control; null if none.
View.prototype.getFocusedControl = function() {
    return this.focusedControl;
};

// Used to notify the view that the focused control has changed.
View.prototype.focusedControlChanged = function(control) {
    uiLogger.debug("View.focusedControlChanged(" + control + ")");
    this.focusedControl = control;
    // notify event listeners
    this.fireEvent(this.createEvent("FocusedControlChanged", this.focusedControl));
};

// Attempts to focus the first focusable control.
// Override in subclasses as required.
View.prototype.focusFirstControl = function() {
    uiLogger.debug("View.focusFirstControl()");
};

// Attempts to reset all control focus states.
// Override in subclasses as required.
View.prototype.resetControlFocusStates = function() {
    uiLogger.debug("View.resetControlFocusStates()");
};
