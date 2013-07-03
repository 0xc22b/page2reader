goog.provide('wit.home.Home');

goog.require('goog.debug.ErrorHandler');
goog.require('goog.events.EventWrapper');

goog.require('wit.home.view.HomeView');



/**
 * @constructor
 */
wit.home.Home = function() {

  var homeView = new wit.home.view.HomeView();

  // Decorate UI components
  homeView.decorate(document.body);
};

// Ensures the symbol will be visible after compiler renaming.
goog.exportSymbol('wit.home.Home', wit.home.Home);
