goog.provide('wit.page2reader.view.P2rView');

goog.require('goog.array');
goog.require('goog.dom');
goog.require('goog.dom.dataset');
goog.require('goog.events.KeyCodes');
goog.require('goog.style');
goog.require('goog.ui.Component');

goog.require('wit.base.constants');
goog.require('wit.fx.dom');
goog.require('wit.page2reader.constants');
goog.require('wit.page2reader.model.DataStore');
goog.require('wit.page2reader.model.PageUrlObj');
goog.require('wit.page2reader.view.PageUrlView');



/**
 * @param {goog.dom.DomHelper=} opt_domHelper Optional DOM hepler.
 * @constructor
 * @extends {goog.ui.Component}
 */
wit.page2reader.view.P2rView = function(opt_domHelper) {
  goog.base(this, opt_domHelper);
};
goog.inherits(wit.page2reader.view.P2rView, goog.ui.Component);


/** @type {string} */
wit.page2reader.view.P2rView.CSS_CLASS = goog.getCssName('p2r');


/**
 * @type {Element}
 * @private
 */
wit.page2reader.view.P2rView.prototype.urlErrLb_;


/**
 * @type {Element}
 * @private
 */
wit.page2reader.view.P2rView.prototype.urlTB_;


/**
 * @type {Element}
 * @private
 */
wit.page2reader.view.P2rView.prototype.addUrlBtn_;


/**
 * @type {Element}
 * @private
 */
wit.page2reader.view.P2rView.prototype.pageUrlViews_;


/**
 * @type {Element}
 * @private
 */
wit.page2reader.view.P2rView.prototype.noData_;


/**
 * @type {Element}
 * @private
 */
wit.page2reader.view.P2rView.prototype.nextBtn_;


/**
 * @type {?string}
 * @private
 */
wit.page2reader.view.P2rView.prototype.cursorString_;


/** @inheritDoc */
wit.page2reader.view.P2rView.prototype.createDom = function() {
};


/** @inheritDoc */
wit.page2reader.view.P2rView.prototype.decorateInternal = function(element) {
  goog.base(this, 'decorateInternal', element);

  this.urlErrLb_ = goog.dom.getElement('urlErrLb');
  this.urlTB_ = goog.dom.getElement('urlTB');
  this.addUrlBtn_ = goog.dom.getElement('addUrlBtn');
  this.pageUrlViews_ = goog.dom.getElement('pageUrlViews');
  this.noData_ = goog.dom.getElement('noData');
  this.nextBtn_ = goog.dom.getElement('nextBtn');

  if (goog.dom.getChildren(this.pageUrlViews_).length > 0) {
    goog.style.setElementShown(this.noData_, false);
  } else {
    goog.style.setElementShown(this.nextBtn_, false);
  }

  this.cursorString_ = goog.dom.dataset.get(this.nextBtn_, 'cursorString');

  goog.array.forEach(
      goog.dom.getChildren(this.pageUrlViews_),
      function(el) {
        var pageUrlView = new wit.page2reader.view.PageUrlView();
        this.addChild(pageUrlView);
        pageUrlView.decorate(el);
      },
      this);
};


/** @inheritDoc */
wit.page2reader.view.P2rView.prototype.enterDocument = function() {
  goog.base(this, 'enterDocument');

  this.getHandler().listen(this.urlTB_, goog.events.EventType.KEYUP,
      function(e) {
        if (e.keyCode === goog.events.KeyCodes.ENTER) {
          this.addPageUrl_();
        }
      });

  this.getHandler().listen(this.addUrlBtn_, goog.events.EventType.CLICK,
      function(e) {
        this.addPageUrl_();
      });

  this.getHandler().listen(this.nextBtn_, goog.events.EventType.CLICK,
      function(e) {

        goog.dom.setProperties(this.nextBtn_, {'disabled': true});

        var dataStore = wit.page2reader.model.DataStore.getInstance();
        var log = new wit.base.model.Log();
        dataStore.getPagingPageUrls(
            this.cursorString_,
            log,
            goog.bind(this.getPagingPageUrlsCallback_, this));
      });

  this.getHandler().listen(this, wit.page2reader.view.PageUrlView.Events.DEL,
      function(e) {
        var child = e.target;
        this.removeChild(child, true);

        if (this.getChildCount() === 0 &&
            !goog.style.isElementShown(this.nextBtn_)) {
          goog.style.setElementShown(this.noData_, true);
        }
      });
};


/** @inheritDoc */
wit.page2reader.view.P2rView.prototype.disposeInternal = function() {
  // 1. Call the superclass’s disposeInternal method.
  goog.base(this, 'disposeInternal');

  // 2. Dispose of all Disposable objects owned by the class.

  // 3. Remove listeners added by the class.
  // 4. Remove references to DOM nodes.
  // 5. Remove references to COM objects.

};


/** @inheritDoc */
wit.page2reader.view.P2rView.prototype.exitDocument = function() {
  goog.base(this, 'exitDocument');
};


/** @inheritDoc */
wit.page2reader.view.P2rView.prototype.getContentElement = function() {
  return this.pageUrlViews_;
};


/**
 * Add a page URL.
 * @private
 */
wit.page2reader.view.P2rView.prototype.addPageUrl_ = function() {
  goog.dom.setProperties(this.urlTB_, {'disabled': true});
  goog.dom.setProperties(this.addUrlBtn_, {'disabled': true});

  var dataStore = wit.page2reader.model.DataStore.getInstance();
  var log = new wit.base.model.Log();
  dataStore.addPageUrl(
      this.urlTB_.value,
      log,
      goog.bind(this.addPageUrlCallback_, this));
};


/**
 * Callback function to update the results of adding a page URL.
 * @param {wit.base.model.Log} log LogInfo array.
 * @this {wit.page2reader.view.P2rView}
 * @private
 */
wit.page2reader.view.P2rView.prototype.addPageUrlCallback_ = function(log) {

  var logInfo;

  // If server error occurred, there's gonna be only one log info
  // type 'server status'.
  // Just reset the form to let users try again later.
  if (goog.isDef(log.getLogInfo(wit.base.constants.serverStatus, false))) {
    window.alert('Some error occurred.' +
                 'Please wait for a bit and try again.');
  }

  if (goog.isDef(log.getLogInfo(wit.base.constants.didLogIn, false))) {
    window.alert('Please sign in first.');
  }

  this.urlErrLb_.innerHTML = wit.base.constants.htmlSpace;

  goog.dom.setProperties(this.urlTB_, {'disabled': false});
  goog.dom.setProperties(this.addUrlBtn_, {'disabled': false});

  logInfo = log.getLogInfo(wit.page2reader.constants.ADD_PAGE_URL, false);
  if (goog.isDef(logInfo)) {
    this.urlErrLb_.innerHTML = logInfo.msg;
  }

  logInfo = log.getLogInfo(wit.page2reader.constants.ADD_PAGE_URL, true);
  if (goog.isDef(logInfo)) {
    goog.style.setElementShown(this.noData_, false);

    var jsonObj = goog.json.unsafeParse(/** @type {!string} */ (logInfo.value));

    var pageUrlObj = new wit.page2reader.model.PageUrlObj();
    pageUrlObj.setContentsFromJSONObject(/** @type {!Object} */ (jsonObj));

    var pageUrlView = new wit.page2reader.view.PageUrlView(pageUrlObj, true);
    this.addChildAt(pageUrlView, 0, true);

    this.urlTB_.value = '';
  }
};


/**
 * Callback function to update the results of getting pageUrls.
 * @param {wit.base.model.Log} log LogInfo array.
 * @this {wit.page2reader.view.P2rView}
 * @private
 */
wit.page2reader.view.P2rView.prototype.getPagingPageUrlsCallback_ = function(
    log) {
  var logInfo;

  // If server error occurred, there's gonna be only one log info
  // type 'server status'.
  // Just reset the form to let users try again later.
  if (goog.isDef(log.getLogInfo(wit.base.constants.serverStatus, false))) {
    window.alert('Some error occurred.' +
                 'Please wait for a bit and try again.');
  }

  if (goog.isDef(log.getLogInfo(wit.base.constants.didLogIn, false))) {
    window.alert('Please sign in first.');
  }

  goog.dom.setProperties(this.nextBtn_, {'disabled': false});

  logInfo = log.getLogInfo(wit.page2reader.constants.GET_PAGING_PAGE_URLS,
      false);
  if (goog.isDef(logInfo)) {
    window.alert('Some error occurred.' +
                 'Please wait for a bit and try again.');
  }

  logInfo = log.getLogInfo(wit.page2reader.constants.GET_PAGING_PAGE_URLS,
      true);
  if (goog.isDef(logInfo)) {

    var jsonObj = goog.json.unsafeParse(/** @type {!string} */ (logInfo.value));

    var jsonPageUrls = jsonObj[wit.page2reader.constants.PAGE_URLS];
    for (var i = 0; i < jsonPageUrls.length; i = i + 1) {
      var pageUrlObj = new wit.page2reader.model.PageUrlObj();
      pageUrlObj.setContentsFromJSONObject(
          /** @type {!Object} */ (jsonPageUrls[i]));

      var pageUrlView = new wit.page2reader.view.PageUrlView(pageUrlObj);
      this.addChild(pageUrlView, true);
    }

    this.cursorString_ = jsonObj[wit.page2reader.constants.CURSOR_STRING];

    // No more data, hide next button
    if (jsonPageUrls.length === 0) {
      goog.style.setElementShown(this.nextBtn_, false);
      if (this.getChildCount() === 0) {
        goog.style.setElementShown(this.noData_, true);
      }
    }
  }
};
