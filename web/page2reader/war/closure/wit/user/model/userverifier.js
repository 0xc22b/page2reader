goog.provide('wit.user.model.UserVerifier');

goog.require('wit.base.constants');
goog.require('wit.base.model.Log');
goog.require('wit.base.model.XhrService');
goog.require('wit.user.model.helper');


/**
 * @type {string} Allowed characters for username.
 * @const
 * @private
 */
wit.user.model.UserVerifier.usernameAllowedCharacters_ =
    'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_';


/**
 * @type {string} Allowed characters for email.
 * @const
 * @private
 */
wit.user.model.UserVerifier.emailAllowedCharacters_ = 'abcdefghijklmnopqrst' +
    'uvwxyz0123456789-_.@';


/**
 * @type {string} An error message.
 * @const
 * @private
 */
wit.user.model.UserVerifier.errEmpty_ = 'This field is required.';


/**
 * @type {string} An error message.
 * @const
 * @private
 */
wit.user.model.UserVerifier.errNameLength_ =
    'Please use between 2 and 30 characters.';


/**
 * @type {string} An error message.
 * @const
 * @private
 */
wit.user.model.UserVerifier.errNameCharacters_ =
    'Please use only letters (a-z, A-Z), numbers, and _.';


/**
 * @type {string} An error message.
 * @const
 * @private
 */
wit.user.model.UserVerifier.errEmailSpace_ = 'Email can not contain space.';


/**
 * @type {string} An error message.
 * @const
 * @private
 */
wit.user.model.UserVerifier.errEmailFormat_ =
    'Email must contain @ and domain name i.e. @example.com';


/**
 * @type {string} An error message.
 * @const
 * @private
 */
wit.user.model.UserVerifier.errPasswordLength_ =
    'Short passwords are easy to guess. Try one with at least 7 characters.';


/**
 * @type {string} An error message.
 * @const
 * @private
 */
wit.user.model.UserVerifier.errPasswordSpace_ =
    'Password can not contain space.';


/**
 * @type {string} An error message.
 * @const
 * @private
 */
wit.user.model.UserVerifier.errRepeatPassword_ =
    'These passwords don\'t match. Please try again.';


/**
 * @type {string} Path to server.
 * @const
 * @private
 */
wit.user.model.UserVerifier.url_ = '/';


/**
 * @param {string} usernameOrEmail Username or email.
 * @param {wit.base.model.Log} log LogInfo array.
 * @return {boolean} whether the usernameOrEmail valid.
 */
wit.user.model.UserVerifier.isUsernameOrEmailValid = function(
    usernameOrEmail, log) {
  if (usernameOrEmail.indexOf('@') === -1) {
    return wit.user.model.UserVerifier.isUsernameValid(usernameOrEmail, log);
  } else {
    return wit.user.model.UserVerifier.isEmailValid(usernameOrEmail, log);
  }
};


/**
 * @param {string} username Username.
 * @param {wit.base.model.Log} log Array of logInfo.
 * @param {function(wit.base.model.Log)=} opt_callback Callback when checking
 *     username availability completed.
 * @return {boolean} the username is valid or not.
 * @throws error if username is undefined or null.
 */
wit.user.model.UserVerifier.isUsernameValid = function(username, log,
    opt_callback) {

  var i, s, dataString;

  if (username === undefined || username === null) {
    throw new Error('UsernameValidation Error: Username can not be undefined ' +
                    'or null but it is ' + username);
  }

  if (username.length === 0) {
    log.addLogInfo(wit.base.constants.username, false, username,
        wit.user.model.UserVerifier.errEmpty_);
    if (opt_callback) { opt_callback(log); }
    return false;
  }

  if (username.length < 2 || username.length > 30) {
    log.addLogInfo(wit.base.constants.username, false, username,
        wit.user.model.UserVerifier.errNameLength_);
    if (opt_callback) { opt_callback(log); }
    return false;
  }

  //Loop every character, allow only letters, number, and _.
  for (i = 0; i < username.length; i = i + 1) {
    s = username.charAt(i);
    if (wit.user.model.UserVerifier.usernameAllowedCharacters_.indexOf(
        s) === -1) {
      log.addLogInfo(wit.base.constants.username, false, username,
          wit.user.model.UserVerifier.errNameCharacters_);
      if (opt_callback) { opt_callback(log); }
      return false;
    }
  }

  if (opt_callback) {
    var xhrService = wit.base.model.XhrService.getInstance();
    var content = wit.user.model.helper.genUserContent(username);
    xhrService.doNoLoggedInPost(
        wit.user.model.UserVerifier.url_,
        wit.base.constants.validateUsername,
        function(log) {
          opt_callback(log);
        },
        content
    );
  }

  log.addLogInfo(wit.base.constants.username, true, username);
  return true;
};


/**
 * @param {string} email Email.
 * @param {wit.base.model.Log} log Array of logInfo.
 * @param {function(wit.base.model.Log)=} opt_callback Callback when checking
 *     email availability completed.
 * @return {boolean} the email is valid or not.
 */
wit.user.model.UserVerifier.isEmailValid = function(email, log, opt_callback) {
  var i, s, t, dataString;

  if (email === undefined || email === null) {
    throw new Error('EmailValidation Error: Email can not be undefined ' +
                    'or null but it is ' + email);
  }

  if (email.length === 0) {
    log.addLogInfo(wit.base.constants.email, false, email,
        wit.user.model.UserVerifier.errEmpty_);
    if (opt_callback) { opt_callback(log); }
    return false;
  }

  //No space
  if (email.indexOf(' ') !== -1) {
    log.addLogInfo(wit.base.constants.email, false, email,
        wit.user.model.UserVerifier.errEmailSpace_);
    if (opt_callback) { opt_callback(log); }
    return false;
  }

  if (email.length < 5) {
    log.addLogInfo(wit.base.constants.email, false, email,
        wit.user.model.UserVerifier.errEmailFormat_);
    if (opt_callback) { opt_callback(log); }
    return false;
  }

  //Loop every character, only allowed characters.
  for (i = 0; i < email.length; i = i + 1) {
    s = email.charAt(i);
    if (wit.user.model.UserVerifier.emailAllowedCharacters_.indexOf(s) === -1) {
      log.addLogInfo(wit.base.constants.email, false, email,
          wit.user.model.UserVerifier.errEmailFormat_);
      if (opt_callback) { opt_callback(log); }
      return false;
    }
  }

  if (email.indexOf('@') === -1) {
    log.addLogInfo(wit.base.constants.email, false, email,
        wit.user.model.UserVerifier.errEmailFormat_);
    if (opt_callback) { opt_callback(log); }
    return false;
  }

  s = email.substring(email.indexOf('@') + 1);

  if (s.indexOf('@') !== -1) {
    log.addLogInfo(wit.base.constants.email, false, email,
        wit.user.model.UserVerifier.errEmailFormat_);
    if (opt_callback) { opt_callback(log); }
    return false;
  }

  if (s.indexOf('.') === -1) {
    log.addLogInfo(wit.base.constants.email, false, email,
        wit.user.model.UserVerifier.errEmailFormat_);
    if (opt_callback) { opt_callback(log); }
    return false;
  }

  // example.com, example.co.th
  if (s.indexOf('.') === 0 || s.indexOf('.') === (s.length - 1)) {
    log.addLogInfo(wit.base.constants.email, false, email,
        wit.user.model.UserVerifier.errEmailFormat_);
    if (opt_callback) { opt_callback(log); }
    return false;
  }

  if (opt_callback) {
    var xhrService = wit.base.model.XhrService.getInstance();
    var content = wit.user.model.helper.genUserContent(undefined, email);
    xhrService.doNoLoggedInPost(
        wit.user.model.UserVerifier.url_,
        wit.base.constants.validateEmail,
        function(log) {
          opt_callback(log);
        },
        content
    );
  }

  log.addLogInfo(wit.base.constants.email, true, email);
  return true;
};


/**
 * @param {string} password Password.
 * @param {wit.base.model.Log} log LogInfo array.
 * @param {string} type LogInfo type.
 * @return {boolean} the password is valid or not.
 * @throws error if password is undefined or null.
 */
wit.user.model.UserVerifier.isPasswordValid = function(password, log, type) {
  if (password === undefined || password === null) {
    throw new Error('PasswordValidation Error: Password can not be undefined ' +
                    'or null but it is ' + password);
  }

  if (password.length === 0) {
    log.addLogInfo(type, false, password,
        wit.user.model.UserVerifier.errEmpty_);
    return false;
  }

  //No space
  if (password.indexOf(' ') !== -1) {
    log.addLogInfo(type, false, password,
        wit.user.model.UserVerifier.errPasswordSpace_);
    return false;
  }

  if (password.length < 7) {
    log.addLogInfo(type, false, password,
        wit.user.model.UserVerifier.errPasswordLength_);
    return false;
  }

  log.addLogInfo(type, true, password);
  return true;
};


/**
 * @param {string} password Password.
 * @param {string} repeatPassword RepeatPassword.
 * @param {wit.base.model.Log} log LogInfo array.
 * @return {boolean} the repeatPassword is valid or not.
 * @throws error if password or repeatPassword are undefined or null.
 */
wit.user.model.UserVerifier.isRepeatPasswordValid = function(password,
    repeatPassword, log) {
  if (password === undefined || password === null ||
      repeatPassword === undefined || repeatPassword === null) {
    throw new Error('RepeatPasswordValidation Error: Password and ' +
                    'repeatPassword can not be undefined or null ' +
                    'but it is ' + password + ', ' + repeatPassword);
  }

  if (password !== repeatPassword) {
    log.addLogInfo(wit.base.constants.repeatPassword, false, repeatPassword,
        wit.user.model.UserVerifier.errRepeatPassword_);
    return false;
  }

  log.addLogInfo(wit.base.constants.repeatPassword, true, repeatPassword);
  return true;
};
