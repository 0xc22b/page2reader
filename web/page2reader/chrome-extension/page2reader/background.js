chrome.browserAction.onClicked.addListener(function(tab) {
  var actionUrl = 'javascript:((function() {' +
      'var baseUrl = \'http://page2reader.appspot.com\';' +
      'var s = document.createElement(\'script\');' +
      's.setAttribute(\'type\', \'text/javascript\');' +
      's.setAttribute(\'charset\', \'UTF-8\');' +
      's.setAttribute(\'src\', baseUrl + \'/bookmarklet/sendp2r.js\');' +
      'document.documentElement.appendChild(s);' +
      '})());';
  chrome.tabs.update(tab.id, {url: actionUrl});
});
