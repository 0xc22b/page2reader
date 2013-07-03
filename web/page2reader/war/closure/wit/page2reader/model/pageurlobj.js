goog.provide('wit.page2reader.model.PageUrlObj');

goog.require('wit.base.constants');
goog.require('wit.page2reader.constants');



/**
 * PageUrl object.
 * @constructor
 */
wit.page2reader.model.PageUrlObj = function() {
};


/** @type {string} */
wit.page2reader.model.PageUrlObj.prototype.keyString;


/** @type {string} */
wit.page2reader.model.PageUrlObj.prototype.pUrl;


/** @type {string} */
wit.page2reader.model.PageUrlObj.prototype.serverCreateTimeMillis;


/** @type {string} */
wit.page2reader.model.PageUrlObj.prototype.title;


/** @type {string} */
wit.page2reader.model.PageUrlObj.prototype.text;


/** @type {string} */
wit.page2reader.model.PageUrlObj.prototype.cleansedPage;


/** @type {number} */
wit.page2reader.model.PageUrlObj.prototype.sentCount;


/**
 * Set contents from JSON string.
 * @param {!string} jsonString JSON string.
 */
wit.page2reader.model.PageUrlObj.prototype.setContentsFromJSONString = function(
    jsonString) {

  var jsonObj = goog.json.unsafeParse(jsonString);
  this.keyString = jsonObj[wit.page2reader.constants.KEY_STRING];
  this.pUrl = jsonObj[wit.page2reader.constants.P_URL];
  this.serverCreateTimeMillis = jsonObj[
      wit.page2reader.constants.SERVER_CREATE_TIME_MILLIS];
  this.title = jsonObj[wit.page2reader.constants.TITLE];
  this.text = jsonObj[wit.page2reader.constants.TEXT];
  this.cleansedPage = jsonObj[wit.page2reader.constants.CLEANSED_PAGE];
  this.sentCount = jsonObj[wit.page2reader.constants.SENT_COUNT];
};
