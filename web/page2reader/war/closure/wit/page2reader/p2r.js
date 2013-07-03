goog.provide('wit.page2reader.P2r');

goog.require('goog.debug.ErrorHandler');
goog.require('goog.events.EventWrapper');

goog.require('wit.page2reader.view.P2rView');



/**
 * @constructor
 */
wit.page2reader.P2r = function() {

  var p2rView = new wit.page2reader.view.P2rView();

  // Decorate UI components
  p2rView.decorate(document.body);
};

// Ensures the symbol will be visible after compiler renaming.
goog.exportSymbol('wit.page2reader.P2r', wit.page2reader.P2r);
