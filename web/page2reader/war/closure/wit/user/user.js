goog.provide('wit.user.User');

goog.require('goog.debug.ErrorHandler');
goog.require('goog.events.EventWrapper');

goog.require('wit.user.view.UserView');



/**
 * @constructor
 */
wit.user.User = function() {

  var userView = new wit.user.view.UserView();

  // Decorate UI components
  userView.decorate(document.body);
};

// Ensures the symbol will be visible after compiler renaming.
goog.exportSymbol('wit.user.User', wit.user.User);
