goog.provide('wit.base.model.Log');

goog.require('goog.json');

goog.require('wit.base.constants');



/**
 * @constructor
 */
wit.base.model.Log = function() {
  /**
   * @type {Array.<wit.base.model.Log.LogInfo>}
   */
  this.logInfoList = [];
};


/**
 * @typedef {{type:string, isValid:boolean, value:(string|undefined),
 *     msg:(string|undefined)}}
 */
wit.base.model.Log.LogInfo;


/**
 * @param {string} json JSON string.
 * @this {wit.base.model.Log}
 */
wit.base.model.Log.prototype.addLogInfos = function(json) {
  // As the complier's obfuscation, need to do this!
  var logInfoList = goog.json.unsafeParse(json);
  for (var i = 0; i < logInfoList.length; i++) {
    var logInfo = logInfoList[i];
    this.addLogInfo(logInfo['type'], logInfo['isValid'], logInfo['value'],
        logInfo['msg']);
  }
};


/**
 * @param {string} type LogInfo type.
 * @param {boolean} isValid The validity of this infotype.
 * @param {string=} opt_value Value of the logInfo.
 * @param {string=} opt_msg Message.
 * @this {wit.base.model.Log}
 */
wit.base.model.Log.prototype.addLogInfo = function(type, isValid,
    opt_value, opt_msg) {
  this.logInfoList[this.logInfoList.length] = {type: type,
    isValid: isValid, value: opt_value, msg: opt_msg};
};


/**
 * @return {boolean} This log is valid or not.
 * @this {wit.base.model.Log}
 */
wit.base.model.Log.prototype.isValid = function() {
  var i, logInfo;
  for (i = 0; i < this.logInfoList.length; i = i + 1) {
    logInfo = this.logInfoList[i];
    if (logInfo.isValid === false) {
      return false;
    }
  }
  return true;
};


/**
 * @param {string} type LogInfo type.
 * @param {boolean=} opt_isValid The validity of this infotype.
 * @param {string=} opt_value The value of this infotype.
 * @return {wit.base.model.Log.LogInfo|undefined} LogInfo.
 * @throws error if opt_isValid is not provided and the object of this type
 *     not unique.
 * @this {wit.base.model.Log}
 */
wit.base.model.Log.prototype.getLogInfo = function(type, opt_isValid,
    opt_value) {
  var i, logInfo, logInfoResult;
  for (i = 0; i < this.logInfoList.length; i = i + 1) {
    logInfo = this.logInfoList[i];
    if (logInfo.type === type) {
      if (opt_isValid !== undefined) {
        // If opt_isValid provided, just return the first one that matches.
        if (logInfo.isValid === opt_isValid) {
          if (opt_value !== undefined) {
            if (logInfo.value === opt_value) {
              return logInfo;
            }
          } else {
            return logInfo;
          }
        }
      } else {
        if (goog.isDef(logInfoResult)) {
          throw new Error('GetLogInfo Error: If opt_isValid is not provided, ' +
              'the object of this type need to be unique.');
        } else {
          logInfoResult = logInfo;
        }
      }
    }
  }
  return logInfoResult;
};


/**
 * Get whether server is valid or not.
 * @return {boolean} True if server's working properly.
 * @this {wit.base.model.Log}
 */
wit.base.model.Log.prototype.isServerValid = function() {
  var logInfo = this.getLogInfo(wit.base.constants.serverStatus, false);
  return !goog.isDefAndNotNull(logInfo);
};
