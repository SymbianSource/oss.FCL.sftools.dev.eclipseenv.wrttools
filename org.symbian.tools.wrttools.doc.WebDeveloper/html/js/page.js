// TODO: move these constants to common.js
var URL_BASE = "index.html"; 
var URL_SEPARATOR = "?content=";
var URL_SEPARATOR_PATTERN = "\\?content=(.+)$";

/**
 * Initialize links
 */
function initPage() {
  initialTocSync();
  /*
    if (tocSupported) {
        var as = document.getElementsByTagName("a");
        for (var i = 0; i < as.length; i++) {
            attachEventListener(as[i], "click", syncTocHandler);
            if (as[i].addEventListener) {
                as[i].addEventListener("mousedown", syncTocHandler, false);
            } else if (as[i].attachEvent) {
                as[i].setAttribute("onmousedown", syncTocHandlerIE);    
            }
        }
    }
    */    
}

/**
 * Do initial TOC synchronization.
 */
function initialTocSync() {
      if (automaticSync) {
          var baseUrl = getBaseUrl();
          var contentUrl = String(window.location);
          var href = getRelativeUrl(contentUrl, baseUrl);
          findTocItem(href);
    }
}

/**
 * TOC sync
 */
function syncTocHandler(event) {
    if (tocSupported) {
        var n = getTargetNode(event);
        var href = n.getAttribute("href");
        /*
        if (isClass(n, "javadoc_ref")) {
        if (href.indexOf('#') != -1) {
            href = href.substring(0, url.indexOf('#'));
        }
      var a = href.split("/");
      var last = a.length - 1;
      while (a[last].indexOf(".") != -1) {
          last--;
      }
      a = a.slice(0, last + 1);
      href = a.join("/") + "/package-summary.html";
      alert(n.getAttribute("href") + "\n" + href);
        }
        */
    findTocItem(href);
    }
}

/**
 * Find current TOC node.
 *
 * TODO: move to common.js
 */
function findTocItem(url) {
    if (tocSupported) {
        var u = url;
        if (url.indexOf('#') != -1) {
            u = u.substring(0, url.indexOf('#'));
        }
    var baseUrl = getBaseUrl();        
        if (!window.parent.frames.toc) { return; }
        var d = window.parent.frames.toc.document;
        var aes = d.getElementsByTagName(TOC_ELEMENT_LINK);
        for (var i = 0; i < aes.length; i++) {
            if (compareTocUrl(aes[i], getRelativeUrl(aes[i].href, baseUrl), u)) {
                highlightTocItem(aes[i]);
                // make sure TOC is expanded if needed
                var item = aes[i].parentNode;
                /*
        if (childCollapse) {
          collapseChildBlocks(item);
        }
        */                   
                while (item != null) {
                    if (item.parentNode && item.parentNode.nodeName.toLowerCase() == TOC_ELEMENT_BLOCK &&
                            item.parentNode.parentNode && item.parentNode.parentNode.nodeName.toLowerCase() == TOC_ELEMENT_ITEM) {
                        item = item.parentNode.parentNode;
                      if (item.firstChild && item.firstChild.firstChild && item.firstChild.firstChild.data == TOC_SYMBOL_COLLAPSED) {
                          //toggle(n.firstChild);
                          tocNodeExpand(item.firstChild, false);
                      }
                    } else {
                        item = null;    
                    }
                }
                break;
            }
        }
    }
}

/**
 * Compare TOC link URL with document URL.
 *
 * TODO: move to common.js
 *
 * @param link TOC link element
 * @param tocUrl Relative TOC url
 * @param docUrl Relative document URL
 */
function compareTocUrl(link, tocUrl, docUrl) {
  var _tocUrl = tocUrl.replace("\\", "/");
  var _docUrl = docUrl.replace("\\", "/");
  return _tocUrl === _docUrl; 
}

var infoWindow;

/**
 * Open info window.
 *
 * @param event activation event
 */
function openInfo(href) {
  //var target = getTargetNode(event)
  //var href = target.getAttribute("href");
  
  if(infoWindow == null || infoWindow.closed) {
    var width = 450;
    var height = 110 + 20;
    var left = window.screen.width / 2 - width;
    var top = window.screen.height / 2 - height;
    var infoWindow = window.open(href, "info",
        "left=" + left + ",top=" + top + ",height=" + height + ",width=" + width
        + ",menubar=no,toolbar=no,location=no,status=no,scrollbars=no,resizable=yes");
  } else {
    infoWindow.location = href;
  }  
  infoWindow.focus();
  
  if (event.stopPropagation) {
    event.stopPropagation();
  } else {
    event.cancelBubble = true;
  }
  if (event.preventDefault) {
    event.preventDefault();
  } else {
    event.returnValue = false;
  }

  return false;
}