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
// The SelectionControl class is an abstract base class for controls that lets
// the user select one or more options from a list of options. Don't use
// SelectionControl directly.

// Constructor.
function SelectionControl(id, caption, options, multipleSelection, selected) {
    if (id != UI_NO_INIT_ID) {
        this.init(id, caption, options, multipleSelection, selected);
    }
}

// SelectionControl inherits from Control.
SelectionControl.prototype = new Control(UI_NO_INIT_ID);

// List of options.
SelectionControl.prototype.options = null;

// The single selected option in single selection controls
// or list of options in multi selection controls.
SelectionControl.prototype.selected = null;

// Single or multiple selection.
SelectionControl.prototype.multipleSelection = false;

// Initializer - called from constructor.
SelectionControl.prototype.init = function(id, caption, options, multipleSelection, selected) {
    uiLogger.debug("SelectionControl.init(" + id + ", " + caption + ", " + options + ", " + multipleSelection + ", " + selected + ")");
    
    // call superclass initializer
    Control.prototype.init.call(this, id, caption);
    
    // set the multiple selection property
    this.multipleSelection = multipleSelection;
    
    // init options and selected (makes copies of the original arrays)
    this.options = (options != null) ? options.slice(0) : [];
    if (multipleSelection) {
        this.selected = (selected == null) ? [] : selected.slice(0);
    } else {
        this.selected = selected;
    }
    this.validateSelected();
};

// Returns true if the control is a multiple selection control; false if single.
SelectionControl.prototype.isMultipleSelection = function() {
    return this.multipleSelection;
};

// Returns true if the specified option is selected; false if not.
SelectionControl.prototype.isSelected = function(option) {
    if (this.multipleSelection) {
        // multiple selection
        // iterate through all selected options and look for the specified option
        for (var i = 0; i < this.selected.length; i++) {
            if (this.selected[i] == option) {
                return true;
            }
        }
        return false;
    } else {
        // single selection
        return (this.selected == option);
    }
};

// Returns the currently selected option in a single selection control or
// an array of selected options in a multiple selection control. If there are
// no selected options a single selection control returns null and a multiple
// selection control returns an empty array.
SelectionControl.prototype.getSelected = function() {
    return this.multipleSelection ? this.selected.slice(0) : this.selected;
};

// Sets the currently selected options. Pass a single option in a single selection
// control or an array of selected controls in a multiple selection control. To
// deselect all options pass null in a single selection control and an empty array
// in a multiple selection control.
// Override in sublcasses to provide full implementation.
SelectionControl.prototype.setSelected = function(selected) {
    this.selected = this.multipleSelection ? selected.slice(0) : selected;
    // make sure the selected option or options are legal
    this.validateSelected();
};

// Ensures that the selected option or options exist among the options in this control.
SelectionControl.prototype.validateSelected = function() {
    if (this.multipleSelection) {
        // multiple selection
        // iterate through all selected options and ensure they exist among the options
        for (var i = 0; i < this.selected.length; i++) {
            // check that the selected option exists among the options
            var found = false;
            for (var j = 0; j < this.options.length; j++) {
                if (this.options[j] == this.selected[i]) {
                    // found - stop looking for this option
                    found = true;
                    break;
                }
            }
            // not found - remove this selected element
            if (!found) {
                this.selected.splice(i, 1);
                // since we removed an entry we must re-check this position
                i--;
            }
        }
    } else {
        // single selection
        if (this.selected != null) {
            // check that the selected option exists among the options
            for (var i = 0; i < this.options.length; i++) {
                if (this.options[i] == this.selected) {
                    // found - we're done
                    return;
                }
            }
            // not found - remove the selection
            this.selected = null;
        }
    }
};

// Returns the options in the control as an array of option objects with
// a value and text property.
SelectionControl.prototype.getOptions = function() {
    return this.options;
};

// Sets the options in the control.
// Override in sublcasses to provide full implementation.
SelectionControl.prototype.setOptions = function(options) {
    this.options = options.slice(0);
    // make sure the selected option or options are legal
    this.validateSelected();
};

// Returns the option that has the specified value; null if none.
SelectionControl.prototype.getOptionForValue = function(value) {
    // iterate through all options and look for a match
    for (var i = 0; i < this.options.length; i++) {
        if (this.options[i].value == value) {
            return this.options[i];
        }
    }
    return null;
};
