/**
 * Copyright (c) 2009-2010 Symbian Foundation and/or its subsidiary(-ies).
 * All rights reserved.
 * This component and the accompanying materials are made available
 * under the terms of the License "Eclipse Public License v1.0"
 * which accompanies this distribution, and is available
 * at the URL "http://www.eclipse.org/legal/epl-v10.html".
 *
 * Initial Contributors:
 * Symbian Foundation - initial contribution.
 * Contributors:
 */

function Menu() {};
Menu.prototype = new Object();
Menu.prototype.menu = new Menu();

/**
 * Adds a menu item to the top level of the options menu list.
 * @param {MenuItem}
 * @return {Void}
 */
Menu.prototype.append = function(MenuItem)	{};
	
	
/**
 * Removes menu item from options menu list. If the 
 * removed menu item has a cascading submenu in it, 
 * the submenu will also be removed.
 * @param {MenuItem}
 * @return {Void}
 */
Menu.prototype.remove = function(MenuItem) {};

/**
 * Retrieves handle of the menu item instance by its ID 
 * @param {Integer}
 * @return {MenuItem}
 */
Menu.prototype.getMenuItemById = function(id) {
	return new MenuItem();
};

/**
 * Retrieves handle of the menu item instance by its ID 
 * @param {String}
 * @return {MenuItem}
 */
Menu.prototype.getMenuItemByName = function(name) {
	return new MenuItem();
};

/**
 * Customizes label and operation associated with right softkey.
 */
Menu.prototype.setRightSoftkeyLabel = function(label, callback) {};

/**
 * Customizes label and operation associated with right softkey.
 */
Menu.prototype.setLeftSoftkeyLabel = function(label, callback) {};
	
/**
 * Displays the softkeys.
 */
Menu.prototype.showSoftkeys = function() {};

/**
 * Displays the softkeys.
 */
Menu.prototype.hideSoftkeys = function() {};

/**
 * Removes all items from the options menu pane. This operation 
 * will also clear all submenus if such exist.
 */
Menu.prototype.clear = function() {};

/**
 * The onShow property of the menu object is an event handler 
 * for the event of when the options menu is open.
 */
Menu.prototype.onShow = new Function();


/**
 * Creates and instantiates an instance of the MenuItem object.
 */
function MenuItem(name, id) {};
MenuItem.prototype = new Object();


/**
 * Adds a child menu item to the parent menu item in the options 
 * menu list. This results in the creation of a submenu list in 
 * the menu tree. 
 */
MenuItem.prototype.append = function(childMenuItem) {}


/**
 * Removes a child menu item and its children (if any) from the parent menu item.
 */
MenuItem.prototype.remove = function(childMenuItem) {}

/**
 * Call the setDimmed method to show or hide an existing menu item.
 */
MenuItem.prototype.setDimmed = function(flag) {}

/**
 * Event handler for the event when the menu item is selected.
 */
MenuItem.prototype.onSelect = new Function();