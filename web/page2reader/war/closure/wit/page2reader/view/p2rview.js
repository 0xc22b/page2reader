goog.provide('wit.page2reader.view.P2rView');

goog.require('goog.array');
goog.require('goog.dom');
goog.require('goog.dom.classes');
goog.require('goog.dom.dataset');
goog.require('goog.style');
goog.require('goog.ui.Component');

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


/** @type {string} */
wit.page2reader.view.P2rView.PURL_TB_CSS_CLASS = goog.getCssName('purl-tb');


/** @type {string} */
wit.page2reader.view.P2rView.ADD_PURL_BTN_CSS_CLASS =
    goog.getCssName('add-purl-btn');


/** @type {string} */
wit.page2reader.view.P2rView.PURL_VIEWS_CSS_CLASS =
    goog.getCssName('purl-views');


/** @type {string} */
wit.page2reader.view.P2rView.NEXT_BTN_CSS_CLASS = goog.getCssName('next-btn');


/**
 * @type {Element}
 * @private
 */
wit.page2reader.view.P2rView.prototype.pUrlTB_;


/**
 * @type {Element}
 * @private
 */
wit.page2reader.view.P2rView.prototype.addPUrlBtn_;


/**
 * @type {Element}
 * @private
 */
wit.page2reader.view.P2rView.prototype.pageUrlViews_;


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

  // Although decorateInternal(element) expects to be called with an element
  // that is already attached to the document and therefore may already leverage
  // methods such as getElementByFragment(), it must be careful not to make that
  // assumption for a component that calls decorate Internal() from createDom().

  this.pUrlTB_ = goog.dom.getElementsByTagNameAndClass('input',
      wit.page2reader.view.P2rView.PURL_TB_CSS_CLASS, element)[0];
  this.addPUrlBtn_ = goog.dom.getElementsByTagNameAndClass('button',
      wit.page2reader.view.P2rView.ADD_PURL_BTN_CSS_CLASS, element)[0];
  this.pageUrlViews_ = goog.dom.getElementsByTagNameAndClass('div',
      wit.page2reader.view.P2rView.PURL_VIEWS_CSS_CLASS, element)[0];
  this.nextBtn_ = goog.dom.getElementsByTagNameAndClass('button',
      wit.page2reader.view.P2rView.NEXT_BTN_CSS_CLASS, element)[0];
  this.cursorString_ = goog.dom.dataset.get(this.nextBtn_,
      wit.page2reader.constants.CURSOR_STRING);

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

  this.getHandler().listen(this.addPUrlBtn_, goog.events.EventType.CLICK,
      function(e) {

        // TODO: Disable pUrlTB and addPUrlBtn


        var dataStore = wit.page2reader.model.DataStore.getInstance();
        var log = new wit.base.model.Log();
        dataStore.addPageUrl(
            this.pUrlTB_.value,
            log,
            goog.bind(this.addPageUrlCallback_, this));
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

  logInfo = log.getLogInfo(wit.page2reader.constants.ADD_PAGE_URL, true);
  if (goog.isDef(logInfo) && goog.isDefAndNotNull(logInfo.value)) {
    var pageUrlObj = new wit.page2reader.model.PageUrlObj();
    pageUrlObj.setContentsFromJSONString(logInfo.value);

    var pageUrlView = new wit.page2reader.view.PageUrlView(pageUrlObj);
    this.addChildAt(pageUrlView, 0, true);

    // TODO: Enable pUrlTB, addPURLBtn

    this.pUrlTB_.value = '';

    return;
  }

  // TODO: Validate
  // TODO: Show errors

};
