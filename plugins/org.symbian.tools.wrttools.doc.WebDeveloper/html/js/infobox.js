/**
 * Infobox on load. Resize the window to show only content.
 */
function initInfo(event) {
  var box = document.getElementById("infobox");
  var tables = document.getElementsByTagName("table");
  if (box !== null && tables.length > 0) {
    var innerWidth = window.innerWidth || box.offsetHeight;
    var innerHeight = window.innerHeight || box.offsetHeight;
    var w; 
    var h;
    if (window.outerWidth && window.outerHeight) {
      w = tables[0].clientWidth + 57; 
      h = box.clientHeight;
      w += window.outerWidth - innerWidth;
      h += window.outerHeight - innerHeight;
    } else {
      w = tables[0].clientWidth + 57; 
      h = innerHeight;    
      w += 40;
      h += 40;
    }
    window.resizeTo(w, h);
  }
}
