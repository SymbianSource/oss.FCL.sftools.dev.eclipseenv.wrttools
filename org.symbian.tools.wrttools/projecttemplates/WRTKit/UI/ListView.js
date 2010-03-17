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
// The ListView class implements a vertical list view that hosts controls
// as child components.

// Constructor.
function ListView(id, caption) {
    if (id != UI_NO_INIT_ID) {
        this.init(id, caption);
    }
}

// ListView inherits from View.
ListView.prototype = new View(UI_NO_INIT_ID);

// The caption of this view; null if none.
ListView.prototype.caption = null;

// The caption element of this view.
ListView.prototype.captionElement = null;

// The caption text element of this view.
ListView.prototype.captionTextElement = null;

// Root HTML element for controls.
ListView.prototype.listElement = null;

// List of controls in the view.
ListView.prototype.controls = null;

// Initializer for ListView.
ListView.prototype.init = function(id, caption) {
    uiLogger.debug("ListView.init(" + id + ", " + caption + ")");
    
    // call superclass initializer
    View.prototype.init.call(this, id);
    
    // init control array
    this.controls = [];
    
    // set style class name for root element
    this.rootElement.className = "ListView";
    
    // create caption and caption text elements
    this.captionElement = document.createElement("div");
    this.captionElement.className = "ListViewCaption";
    this.captionTextElement = document.createElement("div");
    this.captionTextElement.className = "ListViewCaptionText";
    this.captionElement.appendChild(this.captionTextElement);
    this.rootElement.appendChild(this.captionElement);
    
    // create root element for controls and add to the view root element
    this.listElement = document.createElement("div");
    this.listElement.className = "ListViewControlList";
    this.rootElement.appendChild(this.listElement);
    
    // set the caption
    this.setCaption(caption);
};

// Returns the caption; null if none.
ListView.prototype.getCaption = function() {
    return this.caption;
};

// Sets the caption; null if none.
ListView.prototype.setCaption = function(caption) {
    uiLogger.debug("ListView.setCaption(" + caption + ")");
    
    // set the display style
    this.captionElement.style.display = (caption == null) ? "none" : "block";
    
    // set the caption
    this.caption = caption;
    this.captionTextElement.innerHTML = (caption == null) ? "" : caption;
};

// Returns an array of controls in the view.
ListView.prototype.getControls = function() {
    return this.controls;
};

// Adds a control to the view.
ListView.prototype.addControl = function(control) {
    uiLogger.debug("ListView.addControl(" + control + ")");
    
    // add the control to the controls array and attach it to the list element
    this.controls.push(control);
    this.listElement.appendChild(control.rootElement);
    control.view = this;
};

// Inserts a control to the view before the specified control.
ListView.prototype.insertControl = function(control, beforeControl) {
    uiLogger.debug("ListView.insertControl(" + control + ", " + beforeControl + ")");
    
    // iterate through current controls
    for (var i = 0; i < this.controls.length; i++) {
        // is this the control we should insert before?
        if (this.controls[i] == beforeControl) {
            // we found the control to insert before - insert here and connect to list element
            this.controls.splice(i, 0, control);
            this.listElement.insertBefore(control.rootElement, beforeControl.rootElement);
            control.view = this;
            return;
        }
    }
    
    // the control wasn't found so we'll add it last
    this.addControl(control);
};

// Removes a control from the view.
ListView.prototype.removeControl = function(control) {
    uiLogger.debug("ListView.removeControl(" + control + ")");
    
    // iterate through current controls
    for (var i = 0; i < this.controls.length; i++) {
        // is this the control we should remove?
        if (this.controls[i] == control) {
            // we found the control to remove - remove it from the list element
            this.controls.splice(i, 1);
            this.listElement.removeChild(control.rootElement);
            control.view = null;
        }
    }
};

// Attempts to focus the first focusable control.
ListView.prototype.focusFirstControl = function() {
    uiLogger.debug("ListView.focusFirstControl()");
    for (var i = 0; i < this.controls.length; i++) {
        // is this control focusable?
        var control = this.controls[i];
        if (control.isFocusable()) {
            control.setFocused(true);
            break;
        }
    }
};

// Attempts to reset all control focus states.
// Override in subclasses as required.
ListView.prototype.resetControlFocusStates = function() {
    uiLogger.debug("ListView.resetControlFocusStates()");
    for (var i = 0; i < this.controls.length; i++) {
        this.controls[i].resetFocusState();
    }
};
