/* Constants */

var STATIC_STRING_TOC_SYNC = "TOC Sync";
var STATIC_STRING_TOC_SYNC_HELP = "Refresh / Show current topic";

/** TOC item element name. */
var TOC_ELEMENT_ITEM = "li";
/** TOC block element name. */
var TOC_ELEMENT_BLOCK = "ul";
/** TOC controller element name. */
var TOC_ELEMENT_CONTROL = "span";
/** TOC link element name. */
var TOC_ELEMENT_LINK = "a";
/** TOC controller expanded symbol. */
var TOC_SYMBOL_EXPANDED = "-";
/** TOC controller collapsed symbol. */
var TOC_SYMBOL_COLLAPSED = "+";

/* TODO: move to common. */
var TOC_CLASS_ACTIVE = "on3";
var TOC_CLASS_COLLAPSED = "toc-controller-closed";
var TOC_CLASS_EXPANDED = "toc-controller-open";
var TOC_CLASS_CONTROL = "toc-controller";
var TOC_CLASS_SPACE = "toc-space";
var TOC_CLASS_TITLE = "toc-title";
var TOC_DISPLAY_COLLAPSED = "none";
var TOC_DISPLAY_EXPANDED = "block";

/* Variables */

/** Should TOC be initially fully collapsed. */
var initialCollapse = true;
/** Should node's children be collapsed upon expanding a node. */
var childCollapse = false;
/** Synchronize TOC automatically on link traversal. */
var automaticSync = true;
/** Add controllers to TOC on load. */
var addControllers = true;

/**
 * Add TOC controllers.
 */
function addTocControllers() {
    var nl = getFirstElementByClassName(document, "nav3", "div");
    if (nl === null) {
      return;
    }
    if (addControllers) {
        var lis = nl.getElementsByTagName(TOC_ELEMENT_ITEM);
        for (var j = 0; j < lis.length; j++) {
            var li = lis[j];
            // check that list item is a toc item title
            var title = null;
            for (var child = li.firstChild; child !== null; child = child.nextSibling) {
                if (isClass(child, TOC_CLASS_TITLE)) {
                    title = child;
                    break;
                }
            }
            if (title !== null) {
                var uls = li.getElementsByTagName(TOC_ELEMENT_BLOCK);        
                if (uls.length !== 0) {
                    // add controller
                    var c = document.createElement(TOC_ELEMENT_CONTROL);
                    addClass(c, TOC_CLASS_CONTROL);
//                          if (initialCollapse) {
//                            addClass(c, TOC_CLASS_COLLAPSED);
//                              c.appendChild(document.createTextNode(TOC_SYMBOL_EXPANDED));
//                          }  else {
                        addClass(c, TOC_CLASS_EXPANDED);
                        c.appendChild(document.createTextNode(TOC_SYMBOL_COLLAPSED));
//                          }
                    attachEventListener(c, "click", toggleHandler);
                    li.insertBefore(c, li.firstChild);
                    // hide kids
                    if (initialCollapse) {
                        collapseChildBlocks(li);
                    }
                } else {
                    // add space
                    var sp = document.createElement(TOC_ELEMENT_CONTROL);
                    addClass(sp, TOC_CLASS_SPACE);
                    var pls = document.createTextNode("x");
                    sp.appendChild(pls);
                    li.insertBefore(sp, li.firstChild);                        
                }
                attachEventListener(title, "click", tocItemClickHandler);
            }
        }
    // hide kids
    } else if (initialCollapse) {
        var lis = nl.getElementsByTagName(TOC_ELEMENT_ITEM);
        for (var j = 0; j < lis.length; j++) {
            var li = lis[j];
            collapseChildBlocks(li);
        }
      
    }
    /*
    } else if (childCollapse) {
        var nl = getFirstElementByClassName(document, "nav3", "div");
        if (nl !== null) {
            var tocRoot = getFirstChildElementByTagName(nl, TOC_ELEMENT_BLOCK);
            if (tocRoot !== null) {
                var li = getChildElementsByTagName(tocRoot, TOC_ELEMENT_ITEM);
                for (var i = 0; i < li.length; i++) {
                    collapseChildBlocks(li[i]);
                }
            }
        }
    }
    */   
}

/**
 * Collapse child TOC blocks.
 *
 * @param li TOC item whose child TOC blocks to collapse.
 */
function collapseChildBlocks(li) {
    for (var k = li.firstChild; k != null; k = k.nextSibling) {
        if (k.nodeType == 1 && k.nodeName.toLowerCase() == TOC_ELEMENT_CONTROL && isClass(k, TOC_CLASS_CONTROL)) {
            if (isClass(k, TOC_CLASS_EXPANDED)) {
                tocNodeCollapse(k);
            }
            break;  
        }
        /*
         else if (k.nodeType == 1 && k.nodeName.toLowerCase() == TOC_ELEMENT_BLOCK) {
            k.style.display = "none";
        }
        */
    }
}

/**
 * Toggle child node display.
 */
function toggleHandler(event) {
    toggle(getTargetNode(event));
}

/**
 * Set current node as current.
 */
function tocItemClickHandler(event) {
    var t = getTargetNode(event);
    highlightTocItem(t);
    var p = t.parentNode.firstChild;
    if (p !== null && isClass(p, "toc-controller") && isClass(p, TOC_CLASS_COLLAPSED)) {
        tocNodeExpand(p);
    }    
}

/**
 * Generate TOC synchronization button.
 */
function addSyncButton() {
    if (!automaticSync) {
        //var b = document.createElement("img");
        //b.src = "images/xhtml/e_synch_nav.gif";
        var b = document.createElement("button");
        b.appendChild(document.createTextNode(STATIC_STRING_TOC_SYNC));
        b.title = STATIC_STRING_TOC_SYNC_HELP;
        addClass(b, "button-manual_sync");
        attachEventListener(b, "click", manualSyncTocHandler);
        var bc = document.createElement("div");
        addClass(bc, "button-manual_sync-container");
        bc.appendChild(b);
        var nav3 = getFirstElementByClassName(document, "nav3", "div");
        if (nav3 !== null) {
          nav3.insertBefore(bc, nav3.firstChild);
        }
    }
}

/**
 * Button listener for manual TOC sync.
 */
function manualSyncTocHandler(event) {
    startProcess("Synchronizing TOC");
    var b = getTargetNode(event);
    var baseUrl = getBaseUrl();
    var contentUrl = String(window.top.frames.main.location.href);
    var href = getRelativeUrl(contentUrl, baseUrl);
    findTocItem(href);
    endProcess("Done");    
}

/**
 * Initialize TOC.
 */
function initToc() {
    if (tocSupported) {
        startProcess("Initializing TOC");
        addTocControllers();
        addSyncButton();
        endProcess("Done");
    }    
}

/* Move to common. */

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
        // collapse children
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

var processStart;
var processEnd;
var processDuration

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
