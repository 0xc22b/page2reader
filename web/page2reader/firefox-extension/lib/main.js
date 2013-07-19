// Modules needed are required, similar to CommonJS modules.
var self = require('self');
var tabs = require('tabs');

exports.main = function() {

  // create toolbarbutton
  var tbb = require('toolbarbutton').ToolbarButton({
    id: 'Page2Reader',
    label: 'Send to kindle',
    image: self.data.url('icon.png'),
    onCommand: function() {
      var actionUrl = 'javascript:((function() {' +
          'var baseUrl = \'http://page2reader.appspot.com\';' +
          'var s = document.createElement(\'script\');' +
          's.setAttribute(\'type\', \'text/javascript\');' +
          's.setAttribute(\'charset\', \'UTF-8\');' +
          's.setAttribute(\'src\', baseUrl + \'/bookmarklet/sendp2r.js\');' +
          'document.documentElement.appendChild(s);' +
          '})());';
      tabs.activeTab.url = actionUrl;
    }
  });

  if (self.loadReason == 'install') {
    tbb.moveTo({
      toolbarID: 'nav-bar',
      forceMove: false // only move from palette
    });
  }
};
