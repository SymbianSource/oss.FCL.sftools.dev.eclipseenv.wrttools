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
// The NotificationPopup class handles the display of notifications such as
// warnings, information messages and progress indication.

// Constructor.
function NotificationPopup() {
    // create notification popup
    this.containerElement = document.createElement("div");
    this.containerElement.className = "NotificationPopupContainer";
    this.popupElement = document.createElement("div");
    this.popupElement.className = "NotificationPopup";
    this.typeIndicatorElement = document.createElement("div");
    this.typeIndicatorElement.className = "NotificationPopupTypeIndicator";
    this.textElement = document.createElement("div");
    this.textElement.className = "NotificationPopupText";
    this.progressBarElement = document.createElement("div");
    this.progressBarElement.className = "NotificationPopupProgressBar";
    
    // assemble popup
    this.popupElement.appendChild(this.typeIndicatorElement);
    this.popupElement.appendChild(this.textElement);
    this.popupElement.appendChild(this.progressBarElement);
    this.containerElement.appendChild(this.popupElement);
    
    // create progress bar image element and initialize it
    this.progressBarImageElement = document.createElement("img");
    var self = this;
    this.progressBarImageElement.addEventListener("load", function() { self.progressBarImageLoadingCompleted(); }, false);
    this.progressBarImageElement.setAttribute("alt", "");
    this.progressBarImageURL = this.getProgressBarImage(0);
    this.progressBarImageElement.src = this.progressBarImageURL;
    this.progressBarElement.appendChild(this.progressBarImageElement);
    
    // init the popup to be fully down
    this.popupElement.style.top = "100%";
    
    // set default popup contents
    this.setPopupContents(null, null, null);
}

// Notification container element.
NotificationPopup.prototype.containerElement = null;

// Notification popup element.
NotificationPopup.prototype.popupElement = null;

// Type indicator element.
NotificationPopup.prototype.typeIndicatorElement = null;

// Notification text element.
NotificationPopup.prototype.textElement = null;

// Progress bar element.
NotificationPopup.prototype.progressBarElement = null;

// Progress bar image element.
NotificationPopup.prototype.progressBarImageElement = null;

// Progress bar image URL.
NotificationPopup.prototype.progressBarImageURL = null;

// Has the progress bar image been loaded?
NotificationPopup.prototype.progressBarImageLoaded = false;

// Flag that tracks whether we're in the middle of starting to
// show a notification popup.
NotificationPopup.prototype.processingShowNotification = false;

// Notification popup position (0 = hidden, 1 = showing).
NotificationPopup.prototype.popupPosition = 0;

// Interval for timer ticks (in milliseconds)
NotificationPopup.prototype.ANIM_TIMER_INTERVAL = 25;

// Animation timer identifier or null if no active timer.
NotificationPopup.prototype.animTimerId = null;

// Time in milliseconds for the popup animation to complete
NotificationPopup.prototype.ANIM_TIME = 300;

// Flag that determines the behavior of showNotification(). If set to true
// the popup will snap open when showNotification() is called instead of
// animation.
NotificationPopup.prototype.SHOW_SNAPS_OPEN = true;

// Animation direction (0 = no movement, -1 hiding, +1 = showing).
NotificationPopup.prototype.animDir = 0;

// Auto-hide timer identifier or null if no active timer.
NotificationPopup.prototype.autoHideTimerId = null;

// The commanded display time.
NotificationPopup.prototype.displayTime = -1;

// Displays a notification.
NotificationPopup.prototype.showNotification = function(displayTime, type, text, progress) {
    uiLogger.debug("NotificationPopup.showNotification(" + displayTime + ", " + type + ", " + text + ", " + progress + ")");
    
    // mark that showNotification() has been called and that we are in
    // the middle of starting to show the notification popup
    this.processingShowNotification = true;
    
    // remember the display time
    this.displayTime = displayTime;
    
    // attach the popup to the document if not attached
    if (this.containerElement.parentNode == null) {
        document.body.appendChild(this.containerElement);
        uiLogger.debug("Notification popup attached to document");
    }
    
    // set popup contents and update style
    this.setPopupContents(type, text, progress);
    
    // if the progress image is loaded then we can complete the showing
    // of the notification popup immediately - otherwise the image loaded
    // allback will complete the process.
    if (this.progressBarImageLoaded) {
        this.completeShowNotification();
    }
};

// Completes displaying of a notification.
// Note: Used internally - don't call this directly.
NotificationPopup.prototype.completeShowNotification = function() {
    uiLogger.debug("NotificationPopup.completeShowNotification()");
    
    // animation direction is +1 for showing the popup
    if (this.popupPosition != 1) {
        if (this.SHOW_SNAPS_OPEN) {
            if (this.popupPosition == 0) {
                this.popupPosition = 1;
            }
        }
        this.animatePopup(1);
    }
    
    // setup auto hiding if a display time is specified
    if (this.displayTime > 0) {
        // stop any existing timer
        if (this.autoHideTimerId != null) {
            clearTimeout(this.autoHideTimerId);
            uiLogger.debug("Auto hide timer stopped");
        }
        // set timer to hide notification
        var self = this;
        this.autoHideTimerId = setTimeout(function() {
                                              if (self.displayTime > 0) {
                                                  self.hideNotification();
                                              }
                                          }, this.ANIM_TIME + this.displayTime);
        uiLogger.debug("Auto hide timer started");
    }
    
    // mark us as no longer processing a show notification call
    this.processingShowNotification = false;
};

// Hides the currently displayed notification.
NotificationPopup.prototype.hideNotification = function() {
    uiLogger.debug("NotificationPopup.hideNotification()");
    // mark us as no longer processing a show notification call
    this.processingShowNotification = false;
    
    // animation direction is -1 for hiding the popup
    if (this.popupPosition != 0) {
        this.animatePopup(-1);
    }
    
    // stop auto hide timer if one is set
    if (this.autoHideTimerId != null) {
        clearTimeout(this.autoHideTimerId);
        this.autoHideTimerId = null;
        uiLogger.debug("Auto hide timer stopped");
    }
};

// Starts animation of the popup (1 to show, -1 to hide).
NotificationPopup.prototype.animatePopup = function(direction) {
    uiLogger.debug("NotificationPopup.animatePopup(" + direction + ")");
    // set the direction and star the animation timer
    this.animDir = direction;
    if (this.animTimerId == null) {
        var self = this;
        this.animTimerId = setInterval(function() { self.animate(); }, this.ANIM_TIMER_INTERVAL);
        uiLogger.debug("Notification popup animation started");
    }
};

// Callback for animation timer.
NotificationPopup.prototype.animate = function() {
    // calculate new popup position and clamp
    var animStep = (this.ANIM_TIMER_INTERVAL / this.ANIM_TIME) * this.animDir;
    var newPos = this.popupPosition + animStep;
    if (newPos < 0) {
        newPos = 0;
    } else if (newPos > 1) {
        newPos = 1;
    }
    
    // set the new position to the popup element
    this.popupPosition = newPos;
    this.popupElement.style.top = (100 - Math.round(this.popupPosition * 100)) + "%";
    
    // have we reached the end of the animation?
    if (newPos == 0 || newPos == 1) {
        // reset animation direction
        this.animDir = 0;
        
        // remove the popup from the body if its hidden
        if (newPos == 0) {
            document.body.removeChild(this.containerElement);
            uiLogger.debug("Notification popup detached from document");
        }
        
        // stop timer
        clearTimeout(this.animTimerId);
        this.animTimerId = null;
        uiLogger.debug("Notification popup animation stopped");
    }
};

// Returns a URL for the progress bar image to use for the specified progress.
NotificationPopup.prototype.getProgressBarImage = function(progress) {
    // path for progress bar images
    var progressBarImagePath = "WRTKit/Resources/";
    
    if (progress < 0) {
        // unknown progress
        return progressBarImagePath + "ProgressBarUnknown.gif";
    } else {
        // known progress (should be between 0 and 1)
        var progPct = Math.round(progress * 10) * 10;
        if (progPct < 0) {
            progPct = 0;
        } else if (progPct > 100) {
            progPct = 100;
        }
        return progressBarImagePath + "ProgressBar" + progPct + ".png";
    }
};

// Sets the contents of the popup.
NotificationPopup.prototype.setPopupContents = function(type, text, progress) {
    uiLogger.debug("NotificationPopup.setPopupContents(" + type + ", " + text + ", " + progress + ")");
    
    // figure out notification type style name
    var typeName = (type == null) ? "none" : type.toLowerCase();
    typeName = typeName.charAt(0).toUpperCase() + typeName.substring(1);
    
    // set type element class names
    this.typeIndicatorElement.className = "NotificationPopupTypeIndicator NotificationPopupTypeIndicator" + typeName;
    
    // set notification text
    this.textElement.innerHTML = (text == null) ? "" : text;
    
    // set progress
    this.progressBarElement.style.display = (progress == null) ? "none" : "block";
    if (progress != null) {
        var imgURL = this.getProgressBarImage(progress);
        if (imgURL != this.progressBarImageURL) {
            // load new image
            this.progressBarImageLoaded = false;
            this.progressBarImageURL = imgURL;
            this.progressBarImageElement.src = imgURL;
        } else {
            // the correct image is already loaded
            this.progressBarImageLoaded = true;
        }
    } else {
        // there is no progress bar so there is no need
        // to load any progress bar image
        this.progressBarImageLoaded = true;
    }
};

// Callback for notifying the object that its progress bar image completed loading.
NotificationPopup.prototype.progressBarImageLoadingCompleted = function() {
    uiLogger.debug("NotificationPopup.progressBarImageLoadingCompleted()");
    
    // mark the progress bar image as loaded
    this.progressBarImageLoaded = true;
    
    // complete the process of displaying the notification popup
    // if it has been commanded but not yet completed
    if (this.processingShowNotification) {
        this.completeShowNotification();
    }
};
