goog.provide('wit.home.ResetPassword');

goog.require('goog.debug.ErrorHandler');
goog.require('goog.events.EventWrapper');

goog.require('wit.home.view.ResetPasswordView');



/**
 * @constructor
 */
wit.home.ResetPassword = function() {

  var resetPasswordView = new wit.home.view.ResetPasswordView();

  // Decorate UI components
  resetPasswordView.decorate(document.body);
};

// Ensures the symbol will be visible after compiler renaming.
goog.exportSymbol('wit.home.ResetPassword', wit.home.ResetPassword);
