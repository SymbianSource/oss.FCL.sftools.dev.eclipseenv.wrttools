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
// The NavigationButton class implements a button control for use in
// navigational contexts in menu-style UIs.

// Constructor.
function NavigationButton(id, image, text) {
    if (id != UI_NO_INIT_ID) {
        this.init(id, image, text);
    }
}

// NavigationButton inherits from ActionControl.
NavigationButton.prototype = new ActionControl(UI_NO_INIT_ID);

// Button table element.
NavigationButton.prototype.tableElement = null;

// Button table row element.
NavigationButton.prototype.tableRowElement = null;

// Button table left cell element.
NavigationButton.prototype.tableLeftCellElement = null;

// Button table right cell element.
NavigationButton.prototype.tableRightCellElement = null;

// Button image element.
NavigationButton.prototype.imageElement = null;

// Button link element.
NavigationButton.prototype.linkElement = null;

// Button text element.
NavigationButton.prototype.textElement = null;

// Initializer - called from constructor.
NavigationButton.prototype.init = function(id, image, text) {
    uiLogger.debug("NavigationButton.init(" + id + ", " + image + ", " + text + ")");
    
    // call superclass initializer
    ActionControl.prototype.init.call(this, id, null);
    
    // remove caption element
    this.assemblyElement.removeChild(this.captionElement);
    
    // construct the button
    this.buttonElement = document.createElement("div");
    this.tableElement = document.createElement("table");
    this.tableRowElement = document.createElement("tr");
    this.tableLeftCellElement = document.createElement("td");
    this.tableRightCellElement = document.createElement("td");
    this.imageElement = null;
    this.linkElement = document.createElement("a");
    this.linkElement.href = "JavaScript:void(0)";
    this.textElement = document.createElement("span");
    this.tableElement.appendChild(this.tableRowElement);
    this.tableRowElement.appendChild(this.tableLeftCellElement);
    this.tableRowElement.appendChild(this.tableRightCellElement);
    this.tableRightCellElement.appendChild(this.linkElement);
    this.linkElement.appendChild(this.textElement);
    this.buttonElement.appendChild(this.tableElement);
    this.controlElement.appendChild(this.buttonElement);
    
    // set the image and text
    this.setImage(image);
    this.setText(text);
    
    // bind event listeners
    this.bindActionControlListeners();
    
    // update the style
    this.updateStyleFromState();
};

// Sets the enabled state.
NavigationButton.prototype.setEnabled = function(enabled) {
    uiLogger.debug("NavigationButton.setEnabled(" + enabled + ")");
    
    // bail out early if there is no change in state
    if (this.enabled == enabled) {
        return;
    }
    
    // set the enabled state
    this.enabled = enabled;
    
    if (this.enabled) {
        // diabled -> enabled
        this.tableRightCellElement.removeChild(this.textElement);
        this.tableRightCellElement.appendChild(this.linkElement);
        this.linkElement.appendChild(this.textElement);
    } else {
        // enabled -> diabled
        this.linkElement.removeChild(this.textElement);
        this.tableRightCellElement.removeChild(this.linkElement);
        this.tableRightCellElement.appendChild(this.textElement);
    }
    
    // update the style
    this.updateStyleFromState();
};

// Returns the button image (URL); null if none.
NavigationButton.prototype.getImage = function() {
    return (this.imageElement != null) ? this.imageElement.src : null;
};

// Sets the button image (URL); null if none.
NavigationButton.prototype.setImage = function(image) {
    uiLogger.debug("NavigationButton.setImage(" + image + ")");
    
    if (image == null) {
        // remove image - if any
        if (this.imageElement != null) {
            this.tableLeftCellElement.removeChild(this.imageElement);
        }
    } else {
        // default to not append image element
        var append = false;
        
        // create image element if one doesn't exist
        if (this.imageElement == null) {
            this.imageElement = document.createElement("img");
            this.imageElement.setAttribute("alt", "");
            append = true;
        }
        
        // set image source URL
        this.imageElement.src = image;
        
        // append the image element to the left cell?
        if (append) {
            this.tableLeftCellElement.appendChild(this.imageElement);
        }
    }
};

// Returns the button text.
NavigationButton.prototype.getText = function() {
    return this.textElement.innerHTML;
};

// Sets the button text.
NavigationButton.prototype.setText = function(text) {
    uiLogger.debug("NavigationButton.setText(" + text + ")");
    this.textElement.innerHTML = (text == null) ? "" : text;;
};

// Updates the style of the control to reflects the state of the control.
NavigationButton.prototype.updateStyleFromState = function() {
    uiLogger.debug("NavigationButton.updateStyleFromState()");
    
    // determine the state name
    var stateName = this.getStyleStateName();
    
    // set root element class name
    this.setClassName(this.rootElement, "Control");
    
    // set the control assembly class names
    this.setClassName(this.assemblyElement, "ControlAssembly ControlAssembly" + stateName);
    
    // control element
    this.setClassName(this.controlElement, "ControlElement NavigationButtonControlElement");
    
    // set the button table class names
    this.setClassName(this.buttonElement, "NavigationButton");
    this.setClassName(this.tableElement, "NavigationButtonTable");
    this.setClassName(this.tableRowElement, "NavigationButtonRow");
    this.setClassName(this.tableLeftCellElement, "NavigationButtonImageCell");
    this.setClassName(this.tableRightCellElement, "NavigationButtonTextCell");
    
    // set image class names
    if (this.imageElement) {
        this.setClassName(this.imageElement, "NavigationButtonImage");
    }
    
    // set the button text class name
    this.setClassName(this.textElement, "NavigationButtonText NavigationButtonText" + stateName);
};
