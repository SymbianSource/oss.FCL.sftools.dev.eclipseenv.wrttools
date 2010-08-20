var TOC_CLASS_ACTIVE = "on3";
var TOC_CLASS_COLLAPSED = "toc-controller-closed";
var TOC_CLASS_EXPANDED = "toc-controller-open";
var TOC_CLASS_CONTROL = "toc-controller";
var TOC_CLASS_SPACE = "toc-space";
var TOC_DISPLAY_COLLAPSED = "none";
var TOC_DISPLAY_EXPANDED = "block";

/**
 * Check required support for TOC.
 */
function support() {
	if (document.getElementsByTagName && document.styleSheets) {
		tocSupported = true;
	} else {
		//alert("Dynamic TOC not available.");		
	}
}

/** Flag if dynamic TOC is supported */
var tocSupported = false;
support();
/** Current active TOC node. */
var currentHighlight = null;
/** Process start time stamp. */
var processStart;
/** Process end time stamp. */
var processEnd;
/** Last process duration. */
var processDuration


/**
 * Hightlight TOC node.
 *
 * @param node TOC node to highlight
 */
function highlightTocItem(node) {
	if (tocSupported) {
		// turn old off
		if (currentHighlight === null || !isClass(currentHighlight, TOC_CLASS_ACTIVE)) {
			// turn all off
			var li = getFirstElementByClassName(node.ownerDocument, TOC_CLASS_ACTIVE, TOC_ITEM);
			if (li !== null) {
			  removeClass(li, TOC_CLASS_ACTIVE);
			}
		} else {
			removeClass(currentHighlight, TOC_CLASS_ACTIVE);
		}
		// turn this on
		addClass(node.parentNode, TOC_CLASS_ACTIVE);
		currentHighlight = node.parentNode;
	}
}

/**
 * Toggle child node display.
 *
 * @param n Target node of the event.
 */
function toggle(n) {
	if (isClass(n, TOC_CLASS_EXPANDED)) {
		tocNodeCollapse(n);
	} else {
	  tocNodeExpand(n);
	}
}

/**
 * Expand TOC node.
 *
 * @param n TOC node
 */
function tocNodeExpand(n, collapseOverride) {
  if (collapseOverride === undefined) {
    collapseOverride = true;
  }
  n.firstChild.data = TOC_SYMBOL_EXPANDED;
  switchClass(n, TOC_CLASS_EXPANDED, TOC_CLASS_COLLAPSED);
  var ul = getNextSiblingByTagName(n.nextSibling, TOC_ELEMENT_BLOCK);
  if (ul !== null) {
    if (childCollapse && collapseOverride) {
      var li = getChildElementsByTagName(ul, "li");
      for (var i = 0; i < li.length; i++) {
        collapseChildBlocks(li[i]);
      }
    }
    ul.style.display = TOC_DISPLAY_EXPANDED;
    //switchClass(ul, "toc-expanded", "toc-collapsed");
  }
}

/**
 * Collapse TOC node.
 *
 * @param n TOC node
 */
function tocNodeCollapse(n) {
  n.firstChild.data = TOC_SYMBOL_COLLAPSED;
  switchClass(n, TOC_CLASS_COLLAPSED, TOC_CLASS_EXPANDED);
  var ul = getNextSiblingByTagName(n.nextSibling, TOC_ELEMENT_BLOCK);
  if (ul !== null) {
    ul.style.display = TOC_DISPLAY_COLLAPSED;
    //switchClass(ul, "toc-collapsed", "toc-expanded");
  }
}

/**
 * Signal start of processing.
 *
 * @param message Message description of the process start
 */
function startProcess(message) {
    processStart = new Date();
    window.status = (message === undefined ? "" : message);    
}

/**
 * Signal end of processing.
 *
 * @param message Message description of the process result
 */
function endProcess(message) {
    processEnd = new Date();
    window.status = (message === undefined ? "" : message);
    processDuration = processEnd - processStart;
}

