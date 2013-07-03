goog.provide('wit.base.model.XhrService');

goog.require('goog.Uri');
goog.require('goog.json');
goog.require('goog.net.XhrManager');
goog.require('goog.net.cookies');
goog.require('goog.uri.utils');

goog.require('wit.base.constants');
goog.require('wit.base.model.Log');



/**
 * @constructor
 */
wit.base.model.XhrService = function() {

  /**
   * @type {goog.net.XhrManager} XhrManager.
   * @private
   */
  this.xhrManager_ = new goog.net.XhrManager();
};
goog.addSingletonGetter(wit.base.model.XhrService);


/**
 * @type {string} Method parameter.
 * @const
 * @private
 */
wit.base.model.XhrService.method_ = 'method';


/**
 * @type {string} Content parameter.
 * @const
 * @private
 */
wit.base.model.XhrService.content_ = 'content';


/**
 * @type {number} Max. number of retries.
 * @const
 * @private
 */
wit.base.model.XhrService.maxRetries_ = 0;


/**
 * @type {number} Request id - auto increment.
 * @private
 */
wit.base.model.XhrService.prototype.requestID_ = 0;


/**
 * Post method with logged-in session required.
 * @param {!string} url Url to make the request to.
 * @param {!string} method Method to be executed in server.
 * @param {!function(!wit.base.model.Log)} callback Callback function
 *     when requesting completed.
 * @param {string=} opt_content Content for the method.
 * @this {wit.base.model.XhrService}
 */
wit.base.model.XhrService.prototype.doPost = function(url, method, callback,
    opt_content) {

  var ssID = goog.net.cookies.get(wit.base.constants.SSID);
  var sID = goog.net.cookies.get(wit.base.constants.SID);
  if (!ssID || !sID) {
    var log = new wit.base.model.Log();
    log.addLogInfo(wit.base.constants.didLogIn, false);
    callback(log);
    return;
  }

  var params = this.genPostParams_(ssID, sID, method, opt_content);

  this.xhrManager_.send(
      this.getRequestID_(),
      url,
      'POST',
      params,
      undefined,
      undefined,
      function(e) {
        var xhr = /** @type {goog.net.XhrIo} */ (e.target);
        var sLog = new wit.base.model.Log();
        if (xhr.isSuccess()) {
          var result = xhr.getResponseText();
          sLog.addLogInfos(result);
          callback(sLog);
        } else {
          sLog.addLogInfo(wit.base.constants.serverStatus, false);
          callback(sLog);
        }
      },
      wit.base.model.XhrService.maxRetries_
  );
};


/**
 * Post method with NO logged-in session required.
 * @param {!string} url Url to make the request to.
 * @param {!string} method Method to be executed in server.
 * @param {!function(!wit.base.model.Log)} callback Callback function
 *     when requesting completed.
 * @param {string=} opt_content Content for the method.
 * @this {wit.base.model.XhrService}
 */
wit.base.model.XhrService.prototype.doNoLoggedInPost = function(url, method,
    callback, opt_content) {

  var params = this.genNoLoggedInPostParams_(method, opt_content);

  this.xhrManager_.send(
      this.getRequestID_(),
      url,
      'POST',
      params,
      undefined,
      undefined,
      function(e) {
        var xhr = /** @type {goog.net.XhrIo} */ (e.target);
        var sLog = new wit.base.model.Log();
        if (xhr.isSuccess()) {
          var result = xhr.getResponseText();
          sLog.addLogInfos(result);
          callback(sLog);
        } else {
          sLog.addLogInfo(wit.base.constants.serverStatus, false);
          callback(sLog);
        }
      },
      wit.base.model.XhrService.maxRetries_
  );
};


/**
 * Get request id
 * @return {string} Request Id for XhrManager.
 * @private
 * @this {wit.base.model.XhrService}
 */
wit.base.model.XhrService.prototype.getRequestID_ = function() {
  var id = 'entry-' + this.requestID_;
  this.requestID_ += 1;
  return id;
};


/**
 * Generate HTTP request parameters
 * @param {!string} sSID Session key string.
 * @param {!string} sID Session ID.
 * @param {!string} method Method to be executed in server.
 * @param {string=} opt_content Object content needed by the method.
 * @return {string} Parameters string.
 * @private
 */
wit.base.model.XhrService.prototype.genPostParams_ = function(sSID, sID,
    method, opt_content) {
  var params = {};
  params[wit.base.constants.SSID] = sSID;
  params[wit.base.constants.SID] = sID;
  params[wit.base.model.XhrService.method_] = method;
  if (goog.isDef(opt_content)) {
    params[wit.base.model.XhrService.content_] = opt_content;
  }
  return goog.uri.utils.buildQueryDataFromMap(params);
};


/**
 * Generate HTTP request parameters
 * @param {!string} method Method to be executed in server.
 * @param {string=} opt_content Object content needed by the method.
 * @return {string} Parameters string.
 * @private
 */
wit.base.model.XhrService.prototype.genNoLoggedInPostParams_ = function(
    method, opt_content) {
  var params = {};
  params[wit.base.model.XhrService.method_] = method;
  if (goog.isDef(opt_content)) {
    params[wit.base.model.XhrService.content_] = opt_content;
  }
  return goog.uri.utils.buildQueryDataFromMap(params);
};
