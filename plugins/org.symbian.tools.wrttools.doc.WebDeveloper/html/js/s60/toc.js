initialCollapse = false;
childCollapse = true;
automaticSync = false;
addControllers = false;

/**
 * Initialize TOC.
 */
function initToc() {
    if (tocSupported) {
        startProcess("Initializing TOC");
        addTocControllers();
        //addSyncButton();
        var baseUrl = getBaseUrl();
        var contentUrl = String(window.top.frames.main.location.href)
        var href = getRelativeUrl(contentUrl, baseUrl);
        findTocItem(href);
        endProcess("Done");
    }    
}