// Modules needed are required, similar to CommonJS modules.
var self = require('self');
var tabs = require('tabs');
var toolbarButton = require('toolbarbutton');


/**
 * Entry point.
 */
exports.main = function() {

  // create toolbarbutton
  var tbb = toolbarButton.ToolbarButton({
    id: 'Page2Reader',
    label: 'Send to kindle',
    image: self.data.url('icon-16x16.png'),
    onCommand: function() {
      tabs.activeTab.attach({
        contentScriptFile: self.data.url('sendp2r.js')
      });
    }
  });

  if (self.loadReason == 'install') {
    tbb.moveTo({
      toolbarID: 'nav-bar',
      forceMove: false
    });
  }
};
