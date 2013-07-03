goog.provide('wit.page2reader.view.PageUrlView');

goog.require('goog.dom');
goog.require('goog.dom.classes');
goog.require('goog.dom.dataset');
goog.require('goog.soy');
goog.require('goog.style');
goog.require('goog.ui.Component');

goog.require('wit.page2reader.model.DataStore');
goog.require('wit.page2reader.model.PageUrlObj');
goog.require('wit.page2reader.soy.p2r');



/**
 * @param {wit.page2reader.model.PageUrlObj=} opt_pageUrlObj Page URL object.
 * @param {goog.dom.DomHelper=} opt_domHelper Optional DOM hepler.
 * @constructor
 * @extends {goog.ui.Component}
 */
wit.page2reader.view.PageUrlView = function(opt_pageUrlObj, opt_domHelper) {
  goog.base(this, opt_domHelper);

  /**
   * @type {wit.page2reader.model.PageUrlObj|undefined}
   * @private
   */
  this.pageUrlObj_ = opt_pageUrlObj;
};
goog.inherits(wit.page2reader.view.PageUrlView, goog.ui.Component);


/** @type {string} */
wit.page2reader.view.PageUrlView.CSS_CLASS = goog.getCssName('pageUrl');


/** @type {string} */
wit.page2reader.view.PageUrlView.DEL_BTN_CSS_CLASS = goog.getCssName('del-btn');


/**
 * @type {Element}
 * @private
 */
wit.page2reader.view.PageUrlView.prototype.delBtn_;


/** @inheritDoc */
wit.page2reader.view.PageUrlView.prototype.createDom = function() {
  var element = goog.soy.renderAsElement(wit.page2reader.soy.p2r.pageUrlView,
      this.pageUrlObj_);
  this.setElementInternal(element);
  this.makeReference_(element);
};


/** @inheritDoc */
wit.page2reader.view.PageUrlView.prototype.decorateInternal = function(
    element) {
  goog.base(this, 'decorateInternal', element);

  // Although decorateInternal(element) expects to be called with an element
  // that is already attached to the document and therefore may already leverage
  // methods such as getElementByFragment(), it must be careful not to make that
  // assumption for a component that calls decorate Internal() from createDom().

  this.pageUrlObj_ = new wit.page2reader.model.PageUrlObj();

  var keyString = goog.dom.dataset.get(element,
      wit.page2reader.constants.KEY_STRING);
  if (goog.isDefAndNotNull(keyString)) {
    this.pageUrlObj_.keyString = keyString;
  }
  this.makeReference_(element);
};


/**
 * Make reference to some of element's children.
 * @param {Element} element Element whose children to be referred.
 * @private
 */
wit.page2reader.view.PageUrlView.prototype.makeReference_ = function(element) {

  this.delBtn_ = goog.dom.getElementsByTagNameAndClass('button',
      wit.page2reader.view.PageUrlView.DEL_BTN_CSS_CLASS, element)[0];
};


/** @inheritDoc */
wit.page2reader.view.PageUrlView.prototype.enterDocument = function() {
  goog.base(this, 'enterDocument');

  this.getHandler().listen(this.delBtn_, goog.events.EventType.CLICK,
      function(e) {

        // TODO: Disable delBtn

        // TODO: Confirmation


        var dataStore = wit.page2reader.model.DataStore.getInstance();
        var log = new wit.base.model.Log();
        dataStore.deletePageUrl(
            this.pageUrlObj_.keyString,
            log,
            goog.bind(this.deletePageUrlCallback_, this));
      });
};


/** @inheritDoc */
wit.page2reader.view.PageUrlView.prototype.disposeInternal = function() {
  // 1. Call the superclass’s disposeInternal method.
  goog.base(this, 'disposeInternal');

  // 2. Dispose of all Disposable objects owned by the class.

  // 3. Remove listeners added by the class.
  // 4. Remove references to DOM nodes.
  // 5. Remove references to COM objects.

};


/** @inheritDoc */
wit.page2reader.view.PageUrlView.prototype.exitDocument = function() {
  goog.base(this, 'exitDocument');
};


/**
 * Callback function to update the results of deleting a page URL.
 * @param {wit.base.model.Log} log LogInfo array.
 * @this {wit.page2reader.view.PageUrlView}
 * @private
 */
wit.page2reader.view.PageUrlView.prototype.deletePageUrlCallback_ = function(
    log) {

  var logInfo;

  // If server error occurred, there's gonna be only one log info
  // type 'server status'.
  // Just reset the form to let users try again later.
  if (goog.isDef(log.getLogInfo(wit.base.constants.serverStatus, false))) {
    window.alert('Some error occurred.' +
                 'Please wait for a bit and try again.');
  }

  logInfo = log.getLogInfo(wit.page2reader.constants.DELETE_PAGE_URL, true);
  if (goog.isDef(logInfo)) {
    // TODO: Animate to disappear


    this.getParent().removeChild(this, true);
  }

  // TODO: Validate
  // TODO: Show errors
};
