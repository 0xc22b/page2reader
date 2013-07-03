goog.provide('wit.user.model.DataStore');

goog.require('wit.base.model.XhrService');
goog.require('wit.user.model.UserVerifier');
goog.require('wit.user.model.helper');



/**
 * @constructor
 */
wit.user.model.DataStore = function() {

};
goog.addSingletonGetter(wit.user.model.DataStore);


/**
 * @type {string} Url of the server to make a request to.
 * @private
 */
wit.user.model.DataStore.prototype.url_ = '/user';


/**
 * Requesting changing username.
 * @param {string} username Username.
 * @param {string} password Password.
 * @param {wit.base.model.Log} log LogInfo array.
 * @param {function(wit.base.model.Log)} callback Callback function to update
 *     the results when requesting changing username completed.
 */
wit.user.model.DataStore.prototype.changeUsername = function(username, password,
    log, callback) {
  wit.user.model.UserVerifier.isUsernameValid(username, log);
  wit.user.model.UserVerifier.isPasswordValid(password, log,
      wit.base.constants.password);
  if (!log.isValid()) {
    callback(log);
    return;
  }

  var content = wit.user.model.helper.genUserContent(username, undefined,
      password);

  var xhrService = wit.base.model.XhrService.getInstance();
  xhrService.doPost(
      this.url_,
      wit.base.constants.changeUsername,
      callback,
      content
  );
};


/**
 * Requesting changing email.
 * @param {string} email Email.
 * @param {string} password Password.
 * @param {wit.base.model.Log} log LogInfo array.
 * @param {function(wit.base.model.Log)} callback Callback function to update
 *     the results when requesting changing email completed.
 */
wit.user.model.DataStore.prototype.changeEmail = function(email, password, log,
    callback) {
  wit.user.model.UserVerifier.isEmailValid(email, log);
  wit.user.model.UserVerifier.isPasswordValid(password, log,
      wit.base.constants.password);
  if (!log.isValid()) {
    callback(log);
    return;
  }

  var content = wit.user.model.helper.genUserContent(undefined, email,
      password);

  var xhrService = wit.base.model.XhrService.getInstance();
  xhrService.doPost(
      this.url_,
      wit.base.constants.changeEmail,
      callback,
      content
  );
};


/**
 * Requesting resending email confirm.
 * @param {wit.base.model.Log} log LogInfo array.
 * @param {function(wit.base.model.Log)} callback Callback function to update
 *     the results when requesting changing email completed.
 */
wit.user.model.DataStore.prototype.resendEmailConfirm = function(log,
    callback) {

  var content = wit.user.model.helper.genUserContent();

  var xhrService = wit.base.model.XhrService.getInstance();
  xhrService.doPost(
      this.url_,
      wit.base.constants.resendEmailConfirm,
      callback,
      content
  );
};


/**
 * Requesting changing password.
 * @param {string} newPassword New password.
 * @param {string} repeatPassword Repeat password.
 * @param {string} password Password.
 * @param {wit.base.model.Log} log LogInfo array.
 * @param {function(wit.base.model.Log)} callback Callback function to update
 *     the results when requesting changing username completed.
 */
wit.user.model.DataStore.prototype.changePassword = function(
    newPassword, repeatPassword, password, log, callback) {
  wit.user.model.UserVerifier.isPasswordValid(newPassword, log,
      wit.base.constants.newPassword);
  wit.user.model.UserVerifier.isRepeatPasswordValid(newPassword,
      repeatPassword, log);
  wit.user.model.UserVerifier.isPasswordValid(password, log,
      wit.base.constants.password);
  if (!log.isValid()) {
    callback(log);
    return;
  }

  var content = wit.user.model.helper.genUserContent(undefined,
      undefined, password, repeatPassword, newPassword);

  var xhrService = wit.base.model.XhrService.getInstance();
  xhrService.doPost(
      this.url_,
      wit.base.constants.changePassword,
      callback,
      content
  );
};


/**
 * Requesting deleting account.
 * @param {string} password Password.
 * @param {wit.base.model.Log} log LogInfo array.
 * @param {function(wit.base.model.Log)} callback Callback function to update
 *     the results when requesting logging in completed.
 */
wit.user.model.DataStore.prototype.deleteAccount = function(password,
    log, callback) {

  wit.user.model.UserVerifier.isPasswordValid(password, log,
      wit.base.constants.password);
  if (!log.isValid()) {
    callback(log);
    return;
  }

  var content = wit.user.model.helper.genUserContent(undefined,
      undefined, password);

  var xhrService = wit.base.model.XhrService.getInstance();
  xhrService.doPost(
      this.url_,
      wit.base.constants.deleteAccount,
      callback,
      content
  );
};
