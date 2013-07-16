goog.provide('wit.home.view.ResetPasswordView');

goog.require('goog.Uri');
goog.require('goog.dom');
goog.require('goog.style');
goog.require('goog.ui.Component');

goog.require('wit.base.constants');
goog.require('wit.base.model.Log');
goog.require('wit.fx.dom');
goog.require('wit.home.model.DataStore');



/**
 * @param {goog.dom.DomHelper=} opt_domHelper Optional DOM hepler.
 * @constructor
 * @extends {goog.ui.Component}
 */
wit.home.view.ResetPasswordView = function(opt_domHelper) {
  goog.base(this, opt_domHelper);
};
goog.inherits(wit.home.view.ResetPasswordView, goog.ui.Component);


/** @type {string} */
wit.home.view.ResetPasswordView.CSS_CLASS = goog.getCssName('resetPassword');


/**
 * @type {Element}
 * @private
 */
wit.home.view.ResetPasswordView.prototype.resetPasswordView_;


/**
 * @type {Element}
 * @private
 */
wit.home.view.ResetPasswordView.prototype.resetPasswordFormView_;


/**
 * @type {Element}
 * @private
 */
wit.home.view.ResetPasswordView.prototype.resetPasswordNewPasswordErrLb_;


/**
 * @type {Element}
 * @private
 */
wit.home.view.ResetPasswordView.prototype.resetPasswordNewPasswordTB_;


/**
 * @type {Element}
 * @private
 */
wit.home.view.ResetPasswordView.prototype.resetPasswordRepeatPasswordErrLb_;


/**
 * @type {Element}
 * @private
 */
wit.home.view.ResetPasswordView.prototype.resetPasswordRepeatPasswordTB_;


/**
 * @type {Element}
 * @private
 */
wit.home.view.ResetPasswordView.prototype.resetPasswordBtn_;


/**
 * @type {Element}
 * @private
 */
wit.home.view.ResetPasswordView.prototype.resetPasswordResultView_;


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

  this.resetPasswordView_ = goog.dom.getElement('resetPasswordView');
  if (goog.isDefAndNotNull(this.resetPasswordView_)) {
    this.resetPasswordFormView_ = goog.dom.getElement('resetPasswordFormView');
    this.resetPasswordNewPasswordErrLb_ = goog.dom.getElement(
        'resetPasswordNewPasswordErrLb');
    this.resetPasswordNewPasswordTB_ = goog.dom.getElement(
        'resetPasswordNewPasswordTB');
    this.resetPasswordRepeatPasswordErrLb_ = goog.dom.getElement(
        'resetPasswordRepeatPasswordErrLb');
    this.resetPasswordRepeatPasswordTB_ = goog.dom.getElement(
        'resetPasswordRepeatPasswordTB');
    this.resetPasswordBtn_ = goog.dom.getElement('resetPasswordBtn');
    this.resetPasswordResultView_ = goog.dom.getElement(
        'resetPasswordResultView');

    goog.style.setElementShown(this.resetPasswordResultView_, false);
  }
};


/** @inheritDoc */
wit.home.view.ResetPasswordView.prototype.enterDocument = function() {
  goog.base(this, 'enterDocument');

  if (goog.isDefAndNotNull(this.resetPasswordView_)) {
    this.getHandler().listen(
        this.resetPasswordBtn_,
        goog.events.EventType.CLICK,
        function(e) {
          goog.dom.setProperties(this.resetPasswordNewPasswordTB_,
              {'disabled': true});
          goog.dom.setProperties(this.resetPasswordRepeatPasswordTB_,
              {'disabled': true});
          goog.dom.setProperties(this.resetPasswordBtn_, {'disabled': true});

          var uri = new goog.Uri(window.location.href);
          var queryData = uri.getQueryData();
          var sSID = /** @type {string} */ (queryData.get(
              wit.base.constants.SSID));
          var fID = /** @type {string} */ (queryData.get(
              wit.base.constants.FID));

          var dataStore = wit.home.model.DataStore.getInstance();
          var log = new wit.base.model.Log();
          dataStore.resetPassword(
              sSID,
              fID,
              this.resetPasswordNewPasswordTB_.value,
              this.resetPasswordRepeatPasswordTB_.value,
              log,
              goog.bind(this.resetPasswordCallback_, this));
        });
  }
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
 * @private
 */
wit.home.view.ResetPasswordView.prototype.resetPasswordCallback_ = function(
    log) {
  var logInfo;

  // If server error occurred, there's gonna be only one log info
  // type 'server status'.
  // Don't do anything, just reset the form to let users try again later.
  if (goog.isDef(log.getLogInfo(wit.base.constants.serverStatus, false))) {
    window.alert('Some error occurred.' +
                 'Please wait for a bit and try again.');
  }

  goog.dom.setProperties(this.resetPasswordNewPasswordTB_,
      {'disabled': false});
  goog.dom.setProperties(this.resetPasswordRepeatPasswordTB_,
      {'disabled': false});
  goog.dom.setProperties(this.resetPasswordBtn_, {'disabled': false});

  this.resetPasswordNewPasswordErrLb_.innerHTML = wit.base.constants.htmlSpace;
  this.resetPasswordRepeatPasswordErrLb_.innerHTML =
      wit.base.constants.htmlSpace;

  // Inputs invalid
  logInfo = log.getLogInfo(wit.base.constants.resetPassword, false);
  if (goog.isDef(logInfo)) {
    this.resetPasswordNewPasswordErrLb_.innerHTML = logInfo.msg;
  }

  logInfo = log.getLogInfo(wit.base.constants.password, false);
  if (goog.isDef(logInfo)) {
    this.resetPasswordNewPasswordErrLb_.innerHTML = logInfo.msg;
  }

  logInfo = log.getLogInfo(wit.base.constants.repeatPassword, false);
  if (goog.isDef(logInfo)) {
    this.resetPasswordRepeatPasswordErrLb_.innerHTML = logInfo.msg;
  }

  // Succeeded
  logInfo = log.getLogInfo(wit.base.constants.resetPassword, true);
  if (goog.isDef(logInfo)) {
    wit.fx.dom.swap(this.resetPasswordView_, 250);
  }
};
