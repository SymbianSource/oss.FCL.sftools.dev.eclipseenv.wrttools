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
// Logger utility class that uses the Firebug console class.

// Constructor (everything is static so this is empty).
function Logger() {
    // Set default logger level.
    this.level = this.LOG_LEVEL_OFF;
}

// Logger levels.
Logger.prototype.LOG_LEVEL_DEBUG = 0;
Logger.prototype.LOG_LEVEL_INFO = 1;
Logger.prototype.LOG_LEVEL_WARN = 2;
Logger.prototype.LOG_LEVEL_ERROR = 3;
Logger.prototype.LOG_LEVEL_OFF = 4;

Logger.prototype.level = null;
Logger.prototype.filter = null;

// Disable logging on other browsers except Firefox.
Logger.prototype.enabled = (navigator.userAgent.indexOf("Firefox") != -1);

// Dumps an objects properties and methods to the console.
Logger.prototype.dump = function(obj) {
    if (this.enabled) {
        console.dir(obj);
    }
};

// Dumps a stracktrace to the console.
Logger.prototype.trace = function() {
    if (this.enabled) {
        console.trace();
    }
};

// Prints a debug message to the console.
Logger.prototype.debug = function(str) {
    if (this.enabled && this.level <= this.LOG_LEVEL_DEBUG) {
        if (this.filter == null) {
            console.debug(str);
        } else {
            var show = false;
            for (i in this.filter) {
                if (str.indexOf(this.filter[i]) >= 0) {
                    show = true;
                    break;
                }
            }
            if (show) {
                console.debug(str);
            }
        }
    }
};

// Prints an info message to the console.
Logger.prototype.info = function(str) {
    if (this.enabled && this.level <= this.LOG_LEVEL_INFO) {
        console.info(str);
    }
};

// Prints a warning message to the console.
Logger.prototype.warn = function(str) {
    if (this.enabled && this.level <= this.LOG_LEVEL_WARN) {
        console.warn(str);
    }
};

// Prints an error message to the console.
Logger.prototype.error = function(str) {
    if (this.enabled && this.level <= this.LOG_LEVEL_ERROR) {
        console.error(str);
    }
};
