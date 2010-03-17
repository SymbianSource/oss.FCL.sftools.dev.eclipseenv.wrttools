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
// The UIElement class is the base class for all user interface elements.

// Constructor.
function UIElement(id) {
    if (id != UI_NO_INIT_ID) {
        this.init(id);
    }
}

// UI element identifier.
UIElement.prototype.id = null;

// Root HTML element in the UI element.
UIElement.prototype.rootElement = null;

// Initializer for UIElement.
UIElement.prototype.init = function(id) {
    uiLogger.debug("UIElement.init(" + id + ")");
    
    // copy identifier
    this.id = id;
    
    // init event listener array
    this.eventListeners = [];
    
    // create the root element
    this.rootElement = document.createElement("div");
    if (id != null) {
        this.rootElement.id = id;
    }
};

// Returns an array containing the current event listeners.
UIElement.prototype.getEventListeners = function() {
    return this.eventListeners;
};

// Adds an event listener.
UIElement.prototype.addEventListener = function(eventType, listener) {
    var listenerDef = { type: eventType, listener: listener };
    this.eventListeners.push(listenerDef);
};

// Removes an event listener.
UIElement.prototype.removeEventListener = function(eventType, listener) {
    // iterate through current listeners and remove the specified
    // listener when its found
    for (var i = 0; i < this.eventListeners.length; i++) {
        var listenerDef = this.eventListeners[i];
        if ((listenerDef.type == eventType) &&
                (listenerDef.listener == listener)) {
            this.eventListeners.splice(i, 1);
            return;
        }
    }
};

// Factory method for an event object where this object is the source object.
UIElement.prototype.createEvent = function(type, value) {
    return { source: this, type: type, value: value };
};

// Fires an event to all listeners.
UIElement.prototype.fireEvent = function(event) {
    // iterate through all event listeners and notify them of the event
    for (var i = 0; i < this.eventListeners.length; i++) {
        var listenerDef = this.eventListeners[i];
        if (listenerDef.type == null || listenerDef.type == event.type) {
            listenerDef.listener.call(this, event);
        }
    }
};
