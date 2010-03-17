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
// The SelectionMenu class implements a single or multi selection control
// that lets users select one or more options from a menu.

// Constructor.
function SelectionMenu(id, caption, options, multipleSelection, selected) {
    if (id != UI_NO_INIT_ID) {
        this.init(id, caption, options, multipleSelection, selected);
    }
}

// SelectionMenu inherits from SelectionControl.
SelectionMenu.prototype = new SelectionControl(UI_NO_INIT_ID);

// Reference to the peer HTML element.
SelectionControl.prototype.peerElement = null;

// Array for tracking option elements.
SelectionMenu.prototype.optionElements = null;

// Initializer - called from constructor.
SelectionMenu.prototype.init = function(id, caption, options, multipleSelection, selected) {
    uiLogger.debug("SelectionMenu.init(" + id + ", " + caption + ", " + options + ", " + multipleSelection + ", " + selected + ")");
    
    // call superclass initializer
    SelectionControl.prototype.init.call(this, id, caption, options, multipleSelection, selected);
    
    // create the control
    this.peerElement = document.createElement("select");
    this.peerElement.multiple = multipleSelection;
    this.controlElement.appendChild(this.peerElement);
    
    // init option elements array
    this.optionElements = [];
    
    // update the option elements to match the options in this control
    this.updateOptionElements();
    
    // bind event listeners
    var self = this;
    this.peerElement.addEventListener("focus", function() { self.focusStateChanged(true); }, false);
    this.peerElement.addEventListener("blur", function() { self.focusStateChanged(false); }, false);
    this.peerElement.addEventListener("mouseover", function() { self.hoverStateChanged(true); }, false);
    this.peerElement.addEventListener("mouseout", function() { self.hoverStateChanged(false); }, false);
    this.peerElement.addEventListener("change", function() { self.selectionChanged(); }, false);
};

// Returns the enabled state.
SelectionMenu.prototype.isEnabled = function() {
    return !this.peerElement.disabled;
};

// Sets the enabled state.
SelectionMenu.prototype.setEnabled = function(enabled) {
    uiLogger.debug("SelectionMenu.setEnabled(" + enabled + ")");
    this.peerElement.disabled = !enabled;
};

// Sets the focused state for the control.
// Note: This may not always succeed.
SelectionMenu.prototype.setFocused = function(focused) {
    uiLogger.debug("SelectionMenu.setFocused(" + focused + ")");
    if (focused) {
        this.peerElement.focus();
    } else {
        this.peerElement.blur();
    }
};

// Sets the currently selected options. Pass a single option in a single selection
// control or an array of selected controls in a multiple selection control. To
// deselect all options pass null in a single selection control and an empty array
// in a multiple selection control.
SelectionMenu.prototype.setSelected = function(selected) {
    // call superclass setSelected()
    SelectionControl.prototype.setSelected.call(this, selected);
    
    // iterate through the options and set the selected state
    // on the corresponding option element
    for (var i = 0; i < this.options.length; i++) {
        this.optionElements[i].selected = this.isSelected(this.options[i]);
    }
};

// Sets the options in the control.
SelectionMenu.prototype.setOptions = function(options) {
    // call superclass setOptions()
    SelectionControl.prototype.setOptions.call(this, options);
    this.updateOptionElements();
};

// Updates the option elements for the peer select element.
SelectionMenu.prototype.updateOptionElements = function() {
    // start by removing all current options from the select element
    while (this.peerElement.firstChild != null) {
        this.peerElement.removeChild(this.peerElement.firstChild);
    }
    
    // iterate through the options and add (and possibly create) a
    // properly configured option element for each option
    for (var i = 0; i < this.options.length; i++) {
        // do we need to create a new option element?
        if (i == this.optionElements.length) {
            this.optionElements.push(document.createElement("option"));
        }
        
        // get the option and option element we're working on
        var option = this.options[i];
        var optionElement = this.optionElements[i];
        
        // set the state for this option element and add it to the
        // peer select element
        optionElement.text = option.text;
        optionElement.selected = this.isSelected(option);
        this.peerElement.appendChild(optionElement);
    }
    
    // update the style
    this.updateStyleFromState();    
};

// Callback for selection change events.
SelectionMenu.prototype.selectionChanged = function() {
    uiLogger.debug("SelectionControl.selectionChanged()");
    
    // update the selected options array or reference
    this.selected = (this.multipleSelection) ? [] : null;
    for (var i = 0; i < this.options.length; i++) {
        if (this.optionElements[i].selected) {
            if (this.multipleSelection) {
                this.selected.push(this.options[i]);
            } else {
                this.selected = this.options[i];
                break;
            }
        }
    }
    
    // notify event listeners
    this.fireEvent(this.createEvent("SelectionChanged", this.getSelected()));
};

// Updates the style of the control to reflects the state of the control.
SelectionMenu.prototype.updateStyleFromState = function() {
    uiLogger.debug("SelectionMenu.updateStyleFromState()");
    
    // determine the state name
    var stateName = this.getStyleStateName();
    
    // set element class names
    this.setClassName(this.rootElement, "Control");
    this.setClassName(this.controlElement, "ControlElement");
    this.setClassName(this.assemblyElement, "ControlAssembly ControlAssembly" + stateName);
    this.setClassName(this.captionElement, "ControlCaption ControlCaption" + stateName);
    
    // set select and option element class names
    var peerStateName = this.isEnabled() ? stateName : "Disabled";
    this.setClassName(this.peerElement, "SelectionMenu SelectionMenu" + peerStateName);
    for (var i = 0; i < this.options.length; i++) {
        var option = this.optionElements[i];
        this.setClassName(option, "SelectionMenuOption SelectionMenuOption" + peerStateName);
    }
};
