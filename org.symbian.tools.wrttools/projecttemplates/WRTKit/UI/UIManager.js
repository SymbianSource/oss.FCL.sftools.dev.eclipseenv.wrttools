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
// The UI manager manages a set of views and other user interface elements.

// Constructor.
function UIManager(viewParentElement, scrollbarParentElement, enableScrollBar, delayInit) {    
    uiLogger.debug("UIManager(" + viewParentElement + ", " + scrollbarParentElement + ")");
    if (delayInit == null) {
        this.init(viewParentElement, enableScrollBar, scrollbarParentElement);
    }
}

// Parent element for views.
UIManager.prototype.viewParentElement = null;

// Parent element for scrollbar.
UIManager.prototype.scrollbarParentElement = null;

// The currently displayed view.
UIManager.prototype.currentView = null;

// Reference to the scrollbar.
UIManager.prototype.scrollbar = null;

// Current scroll Y position.
UIManager.prototype.scrollY = -1;

// Current viewport height.
UIManager.prototype.viewportHeight = -1;

// Current document height.
UIManager.prototype.documentHeight = -1;

// Timer identifier or null if no active timer.
UIManager.prototype.timerId = null;

// Interval for timer ticks for the UI manager timer (in milliseconds)
UIManager.prototype.TIMER_INTERVAL = 250;

// Reference to the notification popup used to displays notifications.
UIManager.prototype.notificationPopup = null;

// is scrollbar enabled
UIManager.prototype.enableScrollBar = null;

// init function
UIManager.prototype.init = function(viewParentElement, enableScrollBar, scrollbarParentElement) {
    this.enableScrollBar = enableScrollBar;
    
    // parent element for views
    if (viewParentElement == null) {
        // create a parent for views
        this.viewParentElement = document.createElement("div");
        this.viewParentElement.className = "ViewContainer";
        document.body.appendChild(this.viewParentElement);
    }
    else {
        this.viewParentElement = viewParentElement;
    }
    
    // parent element for scrollbar
    if (enableScrollBar) {
        if (scrollbarParentElement == null) {
            // create a parent for the scrollbar
            this.scrollbarParentElement = document.createElement("div");
            this.scrollbarParentElement.className = "DocumentScrollbarContainer";
            document.body.appendChild(this.scrollbarParentElement);
        }
        else {
            this.scrollbarParentElement = scrollbarParentElement;
        }
    }
    
    // currently selected view
    this.currentView = null;
    
    // create the notification popup
    // the notification popup adds itself as a child element to the document body
    this.notificationPopup = new NotificationPopup();
    
    // create scrollbar
    if (enableScrollBar) {
        this.scrollbar = new Scrollbar(this.scrollbarParentElement);
    }
    
    // setup scrollbar tracking
    var self = this;
    this.startTimer();
    if (enableScrollBar) {
        window.addEventListener("resize", function(){
            self.updateScrollbar();
        }, false);
        window.addEventListener("scroll", function(){
            self.updateScrollbar();
        }, false);
    }
};

// Returns the current view.
UIManager.prototype.getView = function() {
    return this.currentView;
};

// Switches to the specified view.
UIManager.prototype.setView = function(view) {
    uiLogger.debug("View set to " + view.id);
    
    // remove the current view from the parent element
    if (this.currentView != null) {
        this.viewParentElement.removeChild(this.currentView.rootElement);
    }
    
    // reset scroll
    window.scrollTo(0, 0);
    
    // add the new view to the parent element
    if (view != null) {
        this.currentView = view;
        this.currentView.resetControlFocusStates();
        this.viewParentElement.appendChild(this.currentView.rootElement);
    }
    
    // update scrollbar
    if (this.enableScrollBar) {
        this.updateScrollbar();
    }
    
    // focus the first focusable control
    // a timer is used to prevent unwanted focus shift
    setTimeout(function() { view.focusFirstControl(); }, 1);
};

// Updates the scrollbar.
UIManager.prototype.updateScrollbar = function() {
    if (this.enableScrollBar) {
        // get current viewport and document position and dimensions
        var scrollY = window.scrollY;
        var viewportHeight = window.innerHeight;
        var documentHeight = Math.max(document.documentElement.scrollHeight, document.height);
        
        // check if the scroll position or view has changed
        if (this.scrollY != scrollY ||
                this.viewportHeight != viewportHeight ||
                this.documentHeight != documentHeight) {
            // scroll position or view has changed
            this.scrollY = scrollY;
            this.viewportHeight = viewportHeight;
            this.documentHeight = documentHeight;
            
            // update the scrollbar
            this.scrollbar.update(scrollY, viewportHeight, documentHeight);
            uiLogger.debug("Scrollbar updated");
        }
    }
};

// Starts the view manager timer.
UIManager.prototype.startTimer = function() {
    if (this.timerId == null) {
        uiLogger.debug("UIManager timer started");
        var self = this;
        // setup the timer
        this.timerId = setInterval(function() { self.onTimer(); }, this.TIMER_INTERVAL);
    } else {
        uiLogger.warn("UIManager timer already running");
    }
};

// Stops the view manager timer.
UIManager.prototype.stopTimer = function() {
    if (this.timerId != null) {
        // stop the timer
        clearTimeout(this.timerId);
        this.timerId = null;
    } else {
        uiLogger.warn("UIManager timer already stopped");
    }
};

// Timer callback function.
UIManager.prototype.onTimer = function() {
    if (this.enableScrollBar) {
        // make sure the scrollbar is up to date
        this.updateScrollbar();
    }
};

// Displays a notification.
UIManager.prototype.showNotification = function(displayTime, type, text, progress) {
    uiLogger.debug("UIManager.showNotification(" + displayTime + ", " + type + ", " + text + ", " + progress + ")");
    // use the notification popup to show the notification
    this.notificationPopup.showNotification(displayTime, type, text, progress);
};

// Hides the currently displayed notification.
UIManager.prototype.hideNotification = function() {
    uiLogger.debug("UIManager.hideNotification()");
    // hide the notification popup
    this.notificationPopup.hideNotification();
};
