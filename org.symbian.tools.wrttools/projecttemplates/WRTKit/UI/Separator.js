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
// The Separator class is used to provide a visual separator in a list.

// Constructor.
function Separator(id) {
    if (id != UI_NO_INIT_ID) {
        this.init(id);
    }
}

// Separator inherits from Control.
Separator.prototype = new Control(UI_NO_INIT_ID);

// Reference to the separator element.
Separator.prototype.separatorElement = null;

// Separator row element.
Separator.prototype.tableRowElement = null;

// Left cell element.
Separator.prototype.tableLeftCellElement = null;

// Center cell element.
Separator.prototype.tableCenterCellElement = null;

// Right cell element.
Separator.prototype.tableRightCellElement = null;

// Initializer - called from constructor.
Separator.prototype.init = function(id) {
    uiLogger.debug("Separator.init(" + id + ")");
    
    // call superclass initializer
    Control.prototype.init.call(this, id, null);
    
    // remove caption and control elements
    this.assemblyElement.removeChild(this.captionElement);
    this.assemblyElement.removeChild(this.controlElement);
    
    // create separator
    this.separatorElement = document.createElement("table");
    this.tableRowElement = document.createElement("tr");
    this.tableLeftCellElement = document.createElement("td");
    this.tableCenterCellElement = document.createElement("td");
    this.tableRightCellElement = document.createElement("td");
    this.tableRowElement.appendChild(this.tableLeftCellElement);
    this.tableRowElement.appendChild(this.tableCenterCellElement);
    this.tableRowElement.appendChild(this.tableRightCellElement);
    this.separatorElement.appendChild(this.tableRowElement);
    this.assemblyElement.appendChild(this.separatorElement);
    
    // update style
    this.updateStyleFromState();
};

// Returns the enabled state for the control.
Separator.prototype.isEnabled = function() {
    return true;
};

// Returns the focusable state for the control.
Separator.prototype.isFocusable = function() {
    return false;
};

// Updates the style of the control to reflects the state of the control.
Separator.prototype.updateStyleFromState = function() {
    uiLogger.debug("Separator.updateStyleFromState()");
    
    // set element class names
    this.setClassName(this.rootElement, "Control");
    this.setClassName(this.assemblyElement, "ControlAssembly ControlAssemblyNormal");
    this.setClassName(this.separatorElement, "Separator");
    this.setClassName(this.tableRowElement, "SeparatorRow");
    this.setClassName(this.tableLeftCellElement, "SeparatorLeftCell");
    this.setClassName(this.tableCenterCellElement, "SeparatorCenterCell");
    this.setClassName(this.tableRightCellElement, "SeparatorRightCell");
};
