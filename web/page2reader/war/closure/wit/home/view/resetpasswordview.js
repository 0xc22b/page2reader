goog.provide('wit.home.view.ResetPasswordView');
goog.provide('wit.home.view.ResetPasswordView.Events');
goog.provide('wit.home.view.ResetPasswordViewEvent');

goog.require('goog.dom');
goog.require('goog.style');
goog.require('goog.ui.Component');

goog.require('wit.base.constants');
goog.require('wit.base.model.Log');



/**
 * @param {goog.dom.DomHelper=} opt_domHelper Optional DOM hepler.
 * @constructor
 * @extends {goog.ui.Component}
 */
wit.home.view.ResetPasswordView = function(opt_domHelper) {
  goog.base(this, opt_domHelper);
};
goog.inherits(wit.home.view.ResetPasswordView, goog.ui.Component);


/**
 * Constants for event names
 * @type {Object}
 */
wit.home.view.ResetPasswordView.Events = {
  RESET_PASSWORD: goog.events.getUniqueId('resetPassword')
};


/** @type {string} */
wit.home.view.ResetPasswordView.CSS_CLASS = goog.getCssName('resetPassword');


/**
 * @type {Element}
 * @private
 */
wit.home.view.ResetPasswordView.prototype.resultLb_;


/**
 * @type {Element}
 * @private
 */
wit.home.view.ResetPasswordView.prototype.formPanel_;


/**
 * @type {Element}
 * @private
 */
wit.home.view.ResetPasswordView.prototype.newPasswordErrLb_;


/**
 * @type {Element}
 * @private
 */
wit.home.view.ResetPasswordView.prototype.newPasswordTB_;


/**
 * @type {Element}
 * @private
 */
wit.home.view.ResetPasswordView.prototype.repeatPasswordErrLb_;


/**
 * @type {Element}
 * @private
 */
wit.home.view.ResetPasswordView.prototype.repeatPasswordTB_;


/**
 * @type {Element}
 * @private
 */
wit.home.view.ResetPasswordView.prototype.okBtn_;


/**
 * @type {Element}
 * @private
 */
wit.home.view.ResetPasswordView.prototype.loadingImg_;


/** @inheritDoc */
wit.home.view.ResetPasswordView.prototype.createDom = function() {
};


/** @inheritDoc */
wit.home.view.ResetPasswordView.prototype.decorateInternal = function(element) {
  goog.base(this, 'decorateInternal', element);

  // Although decorateInternal(element) expects to be called with an element
  // that is already attached to the document and therefore may already leverage
  // methods such as getElementByFragment(), it must be careful not to make that
  // assumption for a component that calls decorate Internal() from createDom().

  this.resultLb_ = goog.dom.getElement('resultLb');
  this.formPanel_ = goog.dom.getElement('formPanel');
  this.newPasswordErrLb_ = goog.dom.getElement('newPasswordErrLb');
  this.newPasswordTB_ = goog.dom.getElement('newPasswordTB');
  this.repeatPasswordErrLb_ = goog.dom.getElement('repeatPasswordErrLb');
  this.repeatPasswordTB_ = goog.dom.getElement('repeatPasswordTB');
  this.okBtn_ = goog.dom.getElement('okBtn');
  this.loadingImg_ = goog.dom.getElement('loadingImg');
};


/** @inheritDoc */
wit.home.view.ResetPasswordView.prototype.enterDocument = function() {
  goog.base(this, 'enterDocument');

  this.getHandler().listen(this.okBtn_, goog.events.EventType.CLICK,
      function(e) {
        goog.style.setElementShown(this.okBtn_, false);
        goog.style.setElementShown(this.loadingImg_, true);

        var resetPasswordViewEvent = new wit.home.view.ResetPasswordViewEvent(
            wit.home.view.ResetPasswordView.Events.RESET_PASSWORD,
            this,
            this.newPasswordTB_.value,
            this.repeatPasswordTB_.value);
        this.dispatchEvent(resetPasswordViewEvent);
      });
};


/** @inheritDoc */
wit.home.view.ResetPasswordView.prototype.disposeInternal = function() {
  // 1. Call the superclass’s disposeInternal method.
  goog.base(this, 'disposeInternal');

  // 2. Dispose of all Disposable objects owned by the class.

  // 3. Remove listeners added by the class.
  // 4. Remove references to DOM nodes.
  // 5. Remove references to COM objects.

};


/** @inheritDoc */
wit.home.view.ResetPasswordView.prototype.exitDocument = function() {
  goog.base(this, 'exitDocument');
};


/**
 * Callback function to update the results of resetting password.
 * @param {wit.base.model.Log} log LogInfo array.
 * @this {wit.home.view.ResetPasswordView}
 */
wit.home.view.ResetPasswordView.prototype.resetPasswordCallback =
    function(log) {
  var logInfo;

  // If server error occurred, there's gonna be only one log info
  // type 'server status'.
  // Don't do anything, just reset the form to let users try again later.

  this.newPasswordErrLb_.innerHTML = wit.base.constants.htmlSpace;
  this.repeatPasswordErrLb_.innerHTML = wit.base.constants.htmlSpace;

  // Inputs invalid
  logInfo = log.getLogInfo(wit.base.constants.resetPassword, false);
  if (goog.isDef(logInfo)) {
    this.newPasswordErrLb_.innerHTML = logInfo.msg;
  }

  logInfo = log.getLogInfo(wit.base.constants.password, false);
  if (goog.isDef(logInfo)) {
    this.newPasswordErrLb_.innerHTML = logInfo.msg;
  }

  logInfo = log.getLogInfo(wit.base.constants.repeatPassword, false);
  if (goog.isDef(logInfo)) {
    this.repeatPasswordErrLb_.innerHTML = logInfo.msg;
  }

  goog.style.setElementShown(this.okBtn_, true);
  goog.style.setElementShown(this.loadingImg_, false);

  // Succeeded
  logInfo = log.getLogInfo(wit.base.constants.resetPassword, true);
  if (goog.isDef(logInfo)) {
    this.resultLb_.innerHTML = logInfo.msg;
    goog.style.setElementShown(this.formPanel_, false);
  }
};



/**
 * Object representing a resetPassword view event.
 *
 * @param {string} type Event type.
 * @param {wit.home.view.ResetPasswordView} target ResetPasswordView widget
 *     initiating event.
 * @param {string} password Password.
 * @param {string} repeatPassword Repeat password.
 * @extends {goog.events.Event}
 * @constructor
 */
wit.home.view.ResetPasswordViewEvent = function(type, target, password,
    repeatPassword) {
  goog.base(this, type, target);

  /**
   * Password.
   * @type {string}
   */
  this.password = password;

  /**
   * Repeat password.
   * @type {string}
   */
  this.repeatPassword = repeatPassword;
};
goog.inherits(wit.home.view.ResetPasswordViewEvent, goog.events.Event);
