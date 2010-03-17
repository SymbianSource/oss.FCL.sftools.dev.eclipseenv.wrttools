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
// The FormButton class implements a button control for use in form-style UIs.

// Constructor.
function FormButton(id, text) {
    if (id != UI_NO_INIT_ID) {
        this.init(id, text);
    }
}

// FormButton inherits from ActionControl.
FormButton.prototype = new ActionControl(UI_NO_INIT_ID);

// Button table element.
FormButton.prototype.tableElement = null;

// Button table row element.
FormButton.prototype.tableRowElement = null;

// Button table left cell element.
FormButton.prototype.tableLeftCellElement = null;

// Button table center cell element.
FormButton.prototype.tableCenterCellElement = null;

// Button text element.
FormButton.prototype.textElement = null;

// Button table right cell element.
FormButton.prototype.tableRightCellElement = null;

// Initializer - called from constructor.
FormButton.prototype.init = function(id, text) {
    uiLogger.debug("FormButton.init(" + id + ", " + text + ")");
    
    // call superclass initializer
    ActionControl.prototype.init.call(this, id, null);
    
    // remove caption element
    this.assemblyElement.removeChild(this.captionElement);
    
    // construct the button
    this.buttonElement = document.createElement("div");
    this.tableElement = document.createElement("table");
    this.tableRowElement = document.createElement("tr");
    this.tableLeftCellElement = document.createElement("td");
    this.tableCenterCellElement = document.createElement("td");
    this.linkElement = document.createElement("a");
    this.linkElement.href = "JavaScript:void(0)";
    this.textElement = document.createElement("span");
    this.tableRightCellElement = document.createElement("td");
    this.tableElement.appendChild(this.tableRowElement);
    this.tableRowElement.appendChild(this.tableLeftCellElement);
    this.tableRowElement.appendChild(this.tableCenterCellElement);
    this.tableCenterCellElement.appendChild(this.linkElement);
    this.linkElement.appendChild(this.textElement);
    this.tableRowElement.appendChild(this.tableRightCellElement);
    this.buttonElement.appendChild(this.tableElement);
    this.controlElement.appendChild(this.buttonElement);
    
    // set the text
    this.setText(text);
    
    // bind event listeners
    this.bindActionControlListeners();
    
    // update the style
    this.updateStyleFromState();
};

// Sets the enabled state.
FormButton.prototype.setEnabled = function(enabled) {
    uiLogger.debug("FormButton.setEnabled(" + enabled + ")");
    
    // bail out early if there is no change in state
    if (this.enabled == enabled) {
        return;
    }
    
    // set the enabled state
    this.enabled = enabled;
    
    if (this.enabled) {
        // diabled -> enabled
        this.tableCenterCellElement.removeChild(this.textElement);
        this.tableCenterCellElement.appendChild(this.linkElement);
        this.linkElement.appendChild(this.textElement);
    } else {
        // enabled -> diabled
        this.linkElement.removeChild(this.textElement);
        this.tableCenterCellElement.removeChild(this.linkElement);
        this.tableCenterCellElement.appendChild(this.textElement);
    }
    
    // update the style
    this.updateStyleFromState();
};

// Returns the button text.
FormButton.prototype.getText = function() {
    return this.textElement.innerHTML;
};

// Sets the button text.
FormButton.prototype.setText = function(text) {
    uiLogger.debug("FormButton.setText(" + text + ")");
    this.textElement.innerHTML = (text == null) ? "" : text;;
};

// Updates the style of the control to reflects the state of the control.
FormButton.prototype.updateStyleFromState = function() {
    uiLogger.debug("FormButton.updateStyleFromState()");
    
    // determine the state name
    var stateName = this.getStyleStateName();
    
    // set root element class name
    this.setClassName(this.rootElement, "Control");
    
    // set the control assembly class names
    this.setClassName(this.assemblyElement, "ControlAssembly ControlAssemblyNormal");
    
    // control element
    this.setClassName(this.controlElement, "ControlElement FormButtonControlElement");
    
    // set the button table class names
    this.setClassName(this.buttonElement, "FormButton");
    this.setClassName(this.tableElement, "FormButtonTable");
    this.setClassName(this.tableRowElement, "FormButtonRow");
    this.setClassName(this.tableLeftCellElement, "FormButtonLeftCell FormButtonLeftCell" + stateName);
    this.setClassName(this.tableCenterCellElement, "FormButtonCenterCell FormButtonLeftCell" + stateName);
    this.setClassName(this.tableRightCellElement, "FormButtonRightCell FormButtonLeftCell" + stateName);
    
    // set the button text class name
    this.setClassName(this.textElement, "FormButtonText FormButtonText" + stateName);
};
