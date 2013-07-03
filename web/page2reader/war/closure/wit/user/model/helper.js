goog.provide('wit.user.model.helper');

goog.require('goog.json');

goog.require('wit.base.constants');


/**
 * Generate JSON string content for user methods.
 * @param {string=} opt_username Username.
 * @param {string=} opt_email Email.
 * @param {string=} opt_password Password.
 * @param {string=} opt_repeatPassword Repeat password.
 * @param {string=} opt_newPassword New password.
 * @return {string} JSON string.
 */
wit.user.model.helper.genUserContent = function(opt_username,
    opt_email, opt_password, opt_repeatPassword, opt_newPassword) {
  var obj = {};
  obj[wit.base.constants.username] = opt_username;
  obj[wit.base.constants.email] = opt_email;
  obj[wit.base.constants.password] = opt_password;
  obj[wit.base.constants.repeatPassword] = opt_repeatPassword;
  obj[wit.base.constants.newPassword] = opt_newPassword;
  return goog.json.serialize(obj);
};


/**
 * Generate JSON string content for reset password method.
 * @param {!string} ssid Session key string.
 * @param {!string} fid Session id.
 * @param {!string} password Password.
 * @param {!string} repeatPassword Repeat password.
 * @return {string} JSON string.
 */
wit.user.model.helper.genResetPasswordContent = function(
    ssid, fid, password, repeatPassword) {
  var obj = {};
  obj[wit.base.constants.SSID] = ssid;
  obj[wit.base.constants.FID] = fid;
  obj[wit.base.constants.password] = password;
  obj[wit.base.constants.repeatPassword] = repeatPassword;
  return goog.json.serialize(obj);
};
