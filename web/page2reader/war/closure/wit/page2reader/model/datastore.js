goog.provide('wit.page2reader.model.DataStore');

goog.require('wit.base.model.XhrService');



/**
 * @constructor
 */
wit.page2reader.model.DataStore = function() {

};
goog.addSingletonGetter(wit.page2reader.model.DataStore);


/**
 * @type {string} Url of the server to make a request to.
 * @private
 */
wit.page2reader.model.DataStore.prototype.url_ = '/p2r';


/**
 * Add a web page URL.
 * @param {string} pUrl Page URL.
 * @param {wit.base.model.Log} log LogInfo array.
 * @param {function(wit.base.model.Log)} callback Callback function to update
 *     the results when requesting adding page URL completed.
 */
wit.page2reader.model.DataStore.prototype.addPageUrl = function(pUrl, log,
    callback) {
  // TODO: Put http:// if needed
  // TODO: Validate URL


  var xhrService = wit.base.model.XhrService.getInstance();
  xhrService.doPost(
      this.url_,
      wit.page2reader.constants.ADD_PAGE_URL,
      callback,
      pUrl
  );
};


/**
 * Delete a web page URL.
 * @param {string} keyString Page URL key string.
 * @param {wit.base.model.Log} log LogInfo array.
 * @param {function(wit.base.model.Log)} callback Callback function to update
 *     the results when requesting deleting page URL completed.
 */
wit.page2reader.model.DataStore.prototype.deletePageUrl = function(keyString,
    log, callback) {
  var xhrService = wit.base.model.XhrService.getInstance();
  xhrService.doPost(
      this.url_,
      wit.page2reader.constants.DELETE_PAGE_URL,
      callback,
      keyString
  );
};


/**
 * Update reader email.
 * @param {string} rEmail Reader email.
 * @param {wit.base.model.Log} log LogInfo array.
 * @param {function(wit.base.model.Log)} callback Callback function to update
 *     the results when requesting updating reader email completed.
 */
wit.page2reader.model.DataStore.prototype.updateReaderEmail = function(rEmail,
    log, callback) {
  var xhrService = wit.base.model.XhrService.getInstance();
  xhrService.doPost(
      this.url_,
      wit.page2reader.constants.UPDATE_READER_EMAIL,
      callback,
      rEmail
  );
};
