// Fix IE bug: when frameset is opened in a new window with "Open in
// new window", "main" frame name is set for frameset, not content
// frame.
if (window.name === "nav" &&
        window.parent.name === "main" &&
        window.parent.frameElement === null) {
    window.parent.name = "";
}

/**
 * Initialize navigation frame document.
 */
function initNav() {
    setMainFrameContents();
    resizeFrame();
}

/**
 * Sets the main frame contents if frameset URL contains definition for it. 
 */
function setMainFrameContents() {
    if (window.parent !== undefined &&
            window.parent.frames.main !== undefined &&
            window.parent.frames.toc !== undefined) {
        var u = new RegExp(URL_SEPARATOR_PATTERN);
        var l = window.parent.location.href;
        var c = l.match(u);
        if (c !== null && c.length !== 1) {
            var mainUrl = c[1];
            findTocItem(mainUrl);            
            window.parent.frames.main.location.href = mainUrl;
        }
    }
}

/**
 * Add permalink button to document header.
 */
function addPermalinkButton() {
    var div = document.createElement("div");
    div.style.padding = "2px";
    div.style.textAlign = "right";
    var button = document.createElement("button");
    button.appendChild(document.createTextNode("Permalink"));
    attachEventListener(button, "click", openPermalink);
    div.appendChild(button);
    var body = document.getElementsByTagName("body");
    if (body.length > 0) {
        body[0].insertBefore(div, body[0].firstChild);
    }
}

/**
 * Permalink button click handler.
 */
function openPermalink(event) {
    if (window.parent !== undefined && window.parent.frames.main !== undefined) {
        var contentUrl = String(window.parent.frames.main.location.href);
        var baseUrl = getBaseUrl();
        var mainUrl = getRelativeUrl(contentUrl, baseUrl);
        window.parent.location.href = URL_BASE + URL_SEPARATOR + mainUrl;
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
    var contentUrl = String(window.top.frames.main.location.href);
    var baseUrl = getBaseUrl();
    var href = getRelativeUrl(contentUrl, baseUrl);
    findTocItem(href);
    endProcess("Done");    
}

/**
 * TOC frame toggler.
 */
function toggleTocFrame(event) {
  var target = getTargetNode(event);
  var frame = window.parent.toc.frameElement;
  var cols = frame.parentNode.cols;
  if (cols == "0, *") {
    if (previousTocWidth === undefined) {
      previousTocWidth = "300, *";
    }
    frame.parentNode.setAttribute("cols", previousTocWidth);
    target.firstChild.data = "Hide TOC";
    target.title = "Hides TOC";
  } else {
    previousTocWidth = String(cols);
    frame.parentNode.setAttribute("cols", "0, *");
    target.firstChild.data = "Show TOC";
    target.title = "Shows TOC";
  }
}
var previousTocWidth;

/**
 * Rezide nav frame to minimum height.
 */
function resizeFrame() {
    var div = document.getElementById("toolbar");
    if (div) {
        // extra 2 pixels for IE6
        var height = div.offsetTop + div.offsetHeight + 2;
        if (window.frameElement && window.frameElement.parentNode) {
            window.frameElement.parentNode.rows = height + ",*";
        }
    }
}