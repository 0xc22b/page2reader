goog.provide('wit.page2reader.model.DataStore');

goog.require('goog.string');

goog.require('wit.base.model.XhrService');
goog.require('wit.user.model.UserVerifier');



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

  if (goog.string.isEmpty(pUrl)) {
    log.addLogInfo(wit.page2reader.constants.ADD_PAGE_URL, false, pUrl,
        wit.page2reader.constants.ERR_URL_EMPTY);
    callback(log);
    return;
  }

  pUrl = goog.string.trim(pUrl);

  // Put http:// if needed
  if (pUrl.substring(0, 7) !== 'http://' &&
      pUrl.substring(0, 8) != 'https://') {
    pUrl = 'http://' + pUrl;
  }

  // Validate URL
  var pattern = new RegExp('^(https?:\\/\\/)?' + // protocol
      '((([a-z\\d]([a-z\\d-]*[a-z\\d])*)\\.)+[a-z]{2,}|' + // domain name
      '((\\d{1,3}\\.){3}\\d{1,3}))' + // OR ip (v4) address
      '(\\:\\d+)?(\\/[-a-z\\d%_.~+]*)*' + // port and path
      '(\\?[;&a-z\\d%_.~+=-]*)?' + // query string
      '(\\#[-a-z\\d_]*)?$', 'i'); // fragment locator
  if (!pattern.test(pUrl)) {
    log.addLogInfo(wit.page2reader.constants.ADD_PAGE_URL, false, pUrl,
        wit.page2reader.constants.ERR_URL);
    callback(log);
    return;
  }

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
 * Resend the page to reader.
 * @param {string} keyString Page URL key string.
 * @param {wit.base.model.Log} log LogInfo array.
 * @param {function(wit.base.model.Log)} callback Callback function to update
 *     the results when requesting deleting page URL completed.
 */
wit.page2reader.model.DataStore.prototype.resendToReader = function(keyString,
    log, callback) {
  var xhrService = wit.base.model.XhrService.getInstance();
  xhrService.doPost(
      this.url_,
      wit.page2reader.constants.RESEND_TO_READER,
      callback,
      keyString
  );
};


/**
 * Get more page urls.
 * @param {string} cursorString Cursor string.
 * @param {wit.base.model.Log} log LogInfo array.
 * @param {function(wit.base.model.Log)} callback Callback function to update
 *     the results when requesting more pageUrls completed.
 */
wit.page2reader.model.DataStore.prototype.getPagingPageUrls = function(
    cursorString, log, callback) {
  var xhrService = wit.base.model.XhrService.getInstance();
  xhrService.doPost(
      this.url_,
      wit.page2reader.constants.GET_PAGING_PAGE_URLS,
      callback,
      cursorString
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

  wit.user.model.UserVerifier.isEmailValid(rEmail, log);
  if (!log.isValid()) {
    callback(log);
    return;
  }

  var xhrService = wit.base.model.XhrService.getInstance();
  xhrService.doPost(
      this.url_,
      wit.page2reader.constants.UPDATE_READER_EMAIL,
      callback,
      rEmail
  );
};
