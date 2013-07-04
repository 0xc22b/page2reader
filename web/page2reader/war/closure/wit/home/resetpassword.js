goog.provide('wit.home.ResetPassword');

goog.require('goog.Uri');
goog.require('goog.debug.ErrorHandler');
goog.require('goog.events.EventWrapper');

goog.require('wit.base.constants');
goog.require('wit.base.model.Log');
goog.require('wit.home.model.DataStore');
goog.require('wit.home.view.ResetPasswordView');



/**
 * @constructor
 */
wit.home.ResetPassword = function() {

  /**
   * @type {wit.home.model.DataStore}
   * @private
   */
  this.dataStore_ = new wit.home.model.DataStore();

  /**
   * @type {wit.home.view.ResetPasswordView}
   * @private
   */
  this.resetPasswordView_ = new wit.home.view.ResetPasswordView();

  this.init_();
};

// Ensures the symbol will be visible after compiler renaming.
goog.exportSymbol('wit.home.ResetPassword', wit.home.ResetPassword);


/**
 * Initialization this web application.
 * @private
 * @this {wit.home.ResetPassword}
 */
wit.home.ResetPassword.prototype.init_ = function() {

  // Decorate UI components
  this.resetPasswordView_.decorate(document.body);

  // Add listeners
  goog.events.listen(
      this.resetPasswordView_,
      wit.home.view.ResetPasswordView.Events.RESET_PASSWORD,
      function(e) {
        var log = new wit.base.model.Log();

        var uri = new goog.Uri(window.location.href);
        var queryData = uri.getQueryData();
        var sSID = queryData.get(wit.base.constants.SSID);
        var fID = queryData.get(wit.base.constants.FID);

        var password = e.password;
        var repeatPassword = e.repeatPassword;
        var callback = goog.bind(this.callback_, this,
            this.resetPasswordView_.resetPasswordCallback);
        this.dataStore_.resetPassword(sSID, fID, password, repeatPassword,
            log, callback);
      },
      false,
      this
  );
};


/**
 * @param {function(wit.base.model.Log)} viewCallback Callback function of
 *     the view.
 * @param {wit.base.model.Log} log LogInfo array.
 * @private
 * @this {wit.home.ResetPassword}
 */
wit.home.ResetPassword.prototype.callback_ = function(viewCallback, log) {
  // if server errors
  if (goog.isDef(log.getLogInfo(wit.base.constants.serverStatus, false))) {
    window.alert('Some error occurred.' +
                 'Please wait for a bit and try again.');
  }

  // The scope of this of viewCallback will always be this.resetPasswordView_.
  viewCallback.call(this.resetPasswordView_, log);
};
