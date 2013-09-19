goog.provide('wit.page2reader.view.BannerView');

goog.require('goog.dom');
goog.require('goog.soy');
goog.require('goog.style');
goog.require('goog.ui.Component');

goog.require('wit.base.constants');
goog.require('wit.fx.dom');
goog.require('wit.page2reader.PubSub');
goog.require('wit.page2reader.constants');
goog.require('wit.page2reader.soy.p2r');



/**
 * @param {goog.dom.DomHelper=} opt_domHelper Optional DOM hepler.
 * @constructor
 * @extends {goog.ui.Component}
 */
wit.page2reader.view.BannerView = function(opt_domHelper) {
  goog.base(this, opt_domHelper);
};
goog.inherits(wit.page2reader.view.BannerView, goog.ui.Component);


/**
 * Constants for event names
 * @type {Object}
 */
wit.page2reader.view.BannerView.Events = {
  TO_ERR: goog.events.getUniqueId('toErr')
};


/** @type {string} */
wit.page2reader.view.BannerView.CSS_CLASS = goog.getCssName('banner');


/** @type {string} */
wit.page2reader.view.BannerView.WORKING_LB_CSS_CLASS =
    goog.getCssName('working-lb');


/** @type {string} */
wit.page2reader.view.BannerView.TO_ERR_BTN_CSS_CLASS =
    goog.getCssName('to-err-btn');


/**
 * @type {Element}
 * @private
 */
wit.page2reader.view.BannerView.prototype.workingLb_;


/**
 * @type {Element}
 * @private
 */
wit.page2reader.view.BannerView.prototype.toErrBtn_;


/**
 * @type {number}
 * @private
 */
wit.page2reader.view.BannerView.prototype.workingCounter_ = 0;


/**
 * @type {number}
 * @private
 */
wit.page2reader.view.BannerView.prototype.errorCounter_ = 0;


/** @inheritDoc */
wit.page2reader.view.BannerView.prototype.createDom = function() {
  var element = goog.soy.renderAsElement(wit.page2reader.soy.p2r.bannerView);
  this.setElementInternal(element);

  this.prepareDom_(element);
};


/** @inheritDoc */
wit.page2reader.view.BannerView.prototype.decorateInternal = function(
    element) {
  goog.base(this, 'decorateInternal', element);
};


/**
 * Make reference to some of element's children.
 * @param {Element} element Element whose children to be referred.
 * @private
 */
wit.page2reader.view.BannerView.prototype.prepareDom_ = function(element) {

  this.workingLb_ = goog.dom.getElementsByTagNameAndClass('p',
      wit.page2reader.view.BannerView.WORKING_LB_CSS_CLASS, element)[0];

  this.toErrBtn_ = goog.dom.getElementsByTagNameAndClass('button',
      wit.page2reader.view.BannerView.TO_ERR_BTN_CSS_CLASS, element)[0];
};


/** @inheritDoc */
wit.page2reader.view.BannerView.prototype.enterDocument = function() {
  goog.base(this, 'enterDocument');

  this.getHandler().listen(this.toErrBtn_, goog.events.EventType.CLICK,
      function(e) {
        var event = new goog.events.Event(
            wit.page2reader.view.BannerView.Events.TO_ERR,
            this);
        this.dispatchEvent(event);
      });

  wit.page2reader.PubSub.getInstance().subscribe(
      wit.page2reader.constants.ADD_WORKING,
      this.addWorking_,
      this);

  wit.page2reader.PubSub.getInstance().subscribe(
      wit.page2reader.constants.MINUS_WORKING,
      this.minusWorking_,
      this);

  wit.page2reader.PubSub.getInstance().subscribe(
      wit.page2reader.constants.ADD_ERROR,
      this.addError_,
      this);

  wit.page2reader.PubSub.getInstance().subscribe(
      wit.page2reader.constants.MINUS_ERROR,
      this.minusError_,
      this);
};


/** @inheritDoc */
wit.page2reader.view.BannerView.prototype.disposeInternal = function() {
  // 1. Call the superclass’s disposeInternal method.
  goog.base(this, 'disposeInternal');

  // 2. Dispose of all Disposable objects owned by the class.

  // 3. Remove listeners added by the class.
  // 4. Remove references to DOM nodes.
  // 5. Remove references to COM objects.

  wit.page2reader.PubSub.getInstance().unsubscribe(
      wit.page2reader.constants.ADD_WORKING,
      this.addWorking_,
      this);

  wit.page2reader.PubSub.getInstance().unsubscribe(
      wit.page2reader.constants.MINUS_WORKING,
      this.minusWorking_,
      this);

  wit.page2reader.PubSub.getInstance().unsubscribe(
      wit.page2reader.constants.ADD_ERROR,
      this.addError_,
      this);

  wit.page2reader.PubSub.getInstance().unsubscribe(
      wit.page2reader.constants.MINUS_ERROR,
      this.minusError_,
      this);
};


/** @inheritDoc */
wit.page2reader.view.BannerView.prototype.exitDocument = function() {
  goog.base(this, 'exitDocument');
};


/**
 * Add 1 for showing message "Working...".
 * @private
 */
wit.page2reader.view.BannerView.prototype.addWorking_ = function() {
  this.workingCounter_ += 1;
  this.updateView_();
};


/**
 * Minus 1 for showing message "Working...".
 * @private
 */
wit.page2reader.view.BannerView.prototype.minusWorking_ = function() {
  this.workingCounter_ -= 1;
  this.updateView_();
};


/**
 * Add 1 for showing message "Go to the error".
 * @private
 */
wit.page2reader.view.BannerView.prototype.addError_ = function() {
  this.errorCounter_ += 1;
  this.updateView_();
};


/**
 * Minus 1 for showing message "Go to the error".
 * @private
 */
wit.page2reader.view.BannerView.prototype.minusError_ = function() {
  this.errorCounter_ -= 1;
  this.updateView_();
};


/**
 * Update this view according to workingCounter and errorCounter.
 * @private
 */
wit.page2reader.view.BannerView.prototype.updateView_ = function() {
  if (this.errorCounter_ > 0) {
    goog.style.setElementShown(this.workingLb_, false);
    goog.style.setElementShown(this.toErrBtn_, true);
    return;
  }

  if (this.workingCounter_ > 0) {
    goog.style.setElementShown(this.workingLb_, true);
    goog.style.setElementShown(this.toErrBtn_, false);
    return;
  }

  goog.style.setElementShown(this.workingLb_, false);
  goog.style.setElementShown(this.toErrBtn_, false);
};
