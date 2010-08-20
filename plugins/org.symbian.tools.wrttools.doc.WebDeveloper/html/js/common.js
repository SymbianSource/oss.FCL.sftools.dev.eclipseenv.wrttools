/**
 * Check required support for TOC.
 */
function support() {
    if (document.getElementsByTagName && document.styleSheets) {
        tocSupported = true;
    }
}

/** Flag if dynamic TOC is supported */
var tocSupported = false;
support();
/** Current active TOC node. */
var currentHighlight = null;

/** Regexp for backslash, used to turn Windows pseudo-URLs to URLs. */
var slashRegExp = new RegExp("\\\\", "g");

function getBaseUrl() {
    var topLocation = window.top.location;
    var i = topLocation.pathname.indexOf(URL_BASE);
    if (i != -1) {
        return topLocation.protocol + "//"
             + topLocation.hostname + (topLocation.port == "" ? "" : ":" + topLocation.port)
             + topLocation.pathname.substring(0, i).replace(slashRegExp, "/");
    } else if (topLocation.pathname.substring(topLocation.pathname.length - 1) == "/") {
        return topLocation.protocol + "//"
             + topLocation.hostname + (topLocation.port == "" ? "" : ":" + topLocation.port)
             + topLocation.pathname;
    } else {    
        return undefined;
    }
}

/*
var isIE = (window.navigator.appVersion.indexOf("MSIE 6") !== -1);
*/

/**
 * Hightlight TOC node.
 *
 * @param node TOC node to highlight
 */
function highlightTocItem(node) {
    if (tocSupported) {
        // turn old off
/*
        if (isIE) {
        	var lis = node.ownerDocument.getElementsByTagName("*");
            for (var i = 0; i < lis.length; i++) {
                removeClass(lis.item(i), TOC_CLASS_ACTIVE);
            }
        } else {
*/
	        if (currentHighlight === null || !isClass(currentHighlight, TOC_CLASS_ACTIVE)) {
	            // turn all off
	            var lis = node.ownerDocument.getElementsByTagName(TOC_ELEMENT_ITEM);
	            for (var i = 0; i < lis.length; i++) {
	                removeClass(lis.item(i), TOC_CLASS_ACTIVE);
	            }
	        } else {
	            removeClass(currentHighlight, TOC_CLASS_ACTIVE);
	        }
/*
        }
*/
        // turn this on
        addClass(node.parentNode, TOC_CLASS_ACTIVE);
        currentHighlight = node.parentNode;
/*
        // IE6 addition, because it doesn't support > in CSS selectors
        if (isIE) {
      	    addClass(node, TOC_CLASS_ACTIVE);
      	}
*/
    }
}

/**
 * Toggle child node display.
 *
 * @param n Target node of the event.
 */
//moved to toc.js
/*
function toggle(n) {
    // toggle controller
    if (isClass(n, "toc-controller-open")) {
        n.firstChild.data = "+";
        switchClass(n, "toc-controller-closed", "toc-controller-open");
    } else {
        n.firstChild.data = "-";
        switchClass(n, "toc-controller-open", "toc-controller-closed");
    }
    // toggle content    
    i = getNextSiblingByTagName(n.parentNode.firstChild, "ul");
    if (i != null) {
        if (i.style.display == "none") {
            i.style.display = "block";
        } else {
            i.style.display = "none";
        }        
    }
}
*/

/* Move to common utils. */

/**
 * Get first child element by element name.
 *
 * @param node parent element
 * @param name element name of the desired child elements
 * @return DOM Element, null if none found
 * @type Object
 */
function getFirstChildElementByTagName(node, name) {
    var tag = undefined;
    if (name !== undefined) {
        tag = name.toLowerCase();
    }
    for (var n = node.firstChild; n != null; n = n.nextSibling) {
        if (n.nodeType == 1 && (name === undefined || n.nodeName.toLowerCase() == tag)) {
          return n;
        }
    }
    return null;
}

/**
 * Get first element by class name.
 *
 * @param node DOM node whose descendants select
 * @param cls class name
 * @param elem element name (optional)
 * @return DOM Element, null if none found
 * @type Object
 */
function getFirstElementByClassName(node, cls, elem) {
      if (elem === undefined) {
          elem = "*";
      }
      for (var el = document.getElementsByTagName(elem), i = 0; i < el.length; i++) {
            if (isClass(el[i], cls)) {
                return el[i];
        }
      }
      return null;
}