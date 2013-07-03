goog.provide('wit.home.model.DataStore');

goog.require('wit.base.model.XhrService');
goog.require('wit.user.model.UserVerifier');
goog.require('wit.user.model.helper');



/**
 * @constructor
 */
wit.home.model.DataStore = function() {

};
goog.addSingletonGetter(wit.home.model.DataStore);


/**
 * @type {string} Url of the server to make a request to.
 * @private
 */
wit.home.model.DataStore.prototype.url_ = '/';


/**
 * Requesting logging in.
 * @param {string} usernameOrEmail Username or email.
 * @param {string} password Password.
 * @param {wit.base.model.Log} log LogInfo array.
 * @param {function(wit.base.model.Log)} callback Callback function to update
 *     the results when requesting logging in completed.
 */
wit.home.model.DataStore.prototype.logIn = function(usernameOrEmail, password,
    log, callback) {

  if (usernameOrEmail.indexOf('@') === -1) {
    wit.user.model.UserVerifier.isUsernameValid(usernameOrEmail, log);
  } else {
    wit.user.model.UserVerifier.isEmailValid(usernameOrEmail, log);
  }
  wit.user.model.UserVerifier.isPasswordValid(password, log,
      wit.base.constants.password);
  if (!log.isValid()) {
    callback(log);
    return;
  }

  var content = wit.user.model.helper.genUserContent(usernameOrEmail,
      undefined, password);

  var xhrService = wit.base.model.XhrService.getInstance();
  xhrService.doNoLoggedInPost(
      this.url_,
      wit.base.constants.logIn,
      callback,
      content
  );
};


/**
 * Requesting signing up.
 * @param {string} username Username.
 * @param {string} email Email.
 * @param {string} password Password.
 * @param {string} repeatPassword Repeat password.
 * @param {wit.base.model.Log} log LogInfo array.
 * @param {function(wit.base.model.Log)} callback Callback function to update
 *     the results when requesting signing up completed.
 */
wit.home.model.DataStore.prototype.signUp = function(username, email,
    password, repeatPassword, log, callback) {
  wit.user.model.UserVerifier.isUsernameValid(username, log);
  wit.user.model.UserVerifier.isEmailValid(email, log);
  wit.user.model.UserVerifier.isPasswordValid(password, log,
      wit.base.constants.password);
  wit.user.model.UserVerifier.isRepeatPasswordValid(password, repeatPassword,
      log);
  if (!log.isValid()) {
    callback(log);
    return;
  }

  var content = wit.user.model.helper.genUserContent(username, email,
      password, repeatPassword);

  var xhrService = wit.base.model.XhrService.getInstance();
  xhrService.doNoLoggedInPost(
      this.url_,
      wit.base.constants.signUp,
      callback,
      content
  );
};


/**
 * Requesting to send an email to reset password.
 * @param {string} usernameOrEmail The username or email.
 * @param {wit.base.model.Log} log LogInfo array.
 * @param {function(wit.base.model.Log)} callback Callback function to update
 *     the results when requesting to send an email
 *     to reset password completed.
 */
wit.home.model.DataStore.prototype.sendEmailResetPassword = function(
    usernameOrEmail, log, callback) {
  wit.user.model.UserVerifier.isUsernameOrEmailValid(usernameOrEmail, log);
  if (!log.isValid()) {
    callback(log);
    return;
  }

  var content = wit.user.model.helper.genUserContent(usernameOrEmail);

  var xhrService = wit.base.model.XhrService.getInstance();
  xhrService.doNoLoggedInPost(
      this.url_,
      wit.base.constants.sendEmailResetPassword,
      callback,
      content
  );
};


/**
 * Requesting resetting password.
 * @param {!string} sSID Session key string.
 * @param {!string} fID Session id.
 * @param {!string} password Password.
 * @param {!string} repeatPassword Repeat password.
 * @param {!wit.base.model.Log} log LogInfo array.
 * @param {function(wit.base.model.Log)} callback Callback function to update
 *     the results when requesting logging in completed.
 */
wit.home.model.DataStore.prototype.resetPassword = function(sSID, fID,
    password, repeatPassword, log, callback) {
  wit.user.model.UserVerifier.isPasswordValid(password, log,
      wit.base.constants.password);
  wit.user.model.UserVerifier.isRepeatPasswordValid(password,
      repeatPassword, log);
  if (!log.isValid()) {
    callback(log);
    return;
  }

  var content = wit.user.model.helper.genResetPasswordContent(sSID,
      fID, password, repeatPassword);

  var xhrService = wit.base.model.XhrService.getInstance();
  xhrService.doNoLoggedInPost(
      this.url_,
      wit.base.constants.resetPassword,
      callback,
      content
  );
};
