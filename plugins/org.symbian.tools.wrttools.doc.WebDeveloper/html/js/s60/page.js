/**
* Initialize page
*/
function initPage() {
    verifyContainer();
    //addPermalinkButton();
    initialTocSync();
}

/**
 * Compare TOC link URL with document URL.
 *
 * @param link TOC link element
 * @param tocUrl Relative TOC url
 * @param docUrl Relative document URL
 */
function compareTocUrl(link, tocUrl, docUrl) {
    var _tocUrl = tocUrl.replace("\\", "/");
    var _docUrl = docUrl.replace("\\", "/");
    if (_tocUrl === _docUrl) {
        return true;
    } else if (javadocUrl.test(tocUrl)) {
        var strippedTocUrl = _tocUrl.replace(stripLastSlash, "$1");
        var strippedDocUrl = _docUrl.replace(stripLastSlash, "$1");
        return strippedTocUrl === strippedDocUrl;
    }
    return false; 
}

var javadocUrl = new RegExp("[\\\\\\/](overview-summary\\.html|package-summary\\.html)$");
var stripLastSlash = new RegExp("(^.+[\\\\\\/]).*$");

/**
 * Verify page is loaded into main frame.
 */
function verifyContainer() {
    if (window.frameElement === null ||
            window.frameElement.name !== "main") {
        var u = new RegExp(".*[\\\\\\/](.+)");
        var l = window.location.href;
        var c = l.match(u);
        if (c !== null && c.length !== 1) {
            window.location.href = URL_BASE + URL_SEPARATOR + c[1];
        }
    }
}