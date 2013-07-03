goog.provide('wit.user.view.UserView');

goog.require('goog.dom');
goog.require('goog.dom.classes');
goog.require('goog.style');
goog.require('goog.ui.Component');

goog.require('wit.user.model.DataStore');



/**
 * @param {goog.dom.DomHelper=} opt_domHelper Optional DOM hepler.
 * @constructor
 * @extends {goog.ui.Component}
 */
wit.user.view.UserView = function(opt_domHelper) {
  goog.base(this, opt_domHelper);
};
goog.inherits(wit.user.view.UserView, goog.ui.Component);


/** @type {string} */
wit.user.view.UserView.CSS_CLASS = goog.getCssName('user');


/**
 * @type {string}
 * @const
 * @private
 */
wit.user.view.UserView.btnLinkCss_ = goog.getCssName('btn-link');


/**
 * @type {string}
 * @const
 * @private
 */
wit.user.view.UserView.mutedCss_ = goog.getCssName('muted');


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.changeUsernameLb_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.changeUsernameErrLb_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.changeUsernameTB_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.changeUsernamePasswordErrLb_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.changeUsernamePasswordTB_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.changeUsernameOkBtn_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.changeUsernameLoadingImg_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.changeEmailLb_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.changeEmailErrLb_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.changeEmailTB_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.changeEmailPasswordErrLb_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.changeEmailPasswordTB_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.changeEmailOkBtn_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.changeEmailLoadingImg_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.resendEmailConfirmBtn_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.emailVerifiedElement_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.changeNewPasswordErrLb_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.changeNewPasswordTB_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.changeRepeatPasswordErrLb_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.changeRepeatPasswordTB_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.changePasswordErrLb_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.changePasswordTB_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.changePasswordOkBtn_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.changePasswordLoadingImg_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.deleteAccountPasswordErrLb_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.deleteAccountPasswordTB_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.deleteAccountOkBtn_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.deleteAccountLoadingImg_;


/** @inheritDoc */
wit.user.view.UserView.prototype.createDom = function() {
};


/** @inheritDoc */
wit.user.view.UserView.prototype.decorateInternal = function(element) {
  goog.base(this, 'decorateInternal', element);

  // Although decorateInternal(element) expects to be called with an element
  // that is already attached to the document and therefore may already leverage
  // methods such as getElementByFragment(), it must be careful not to make that
  // assumption for a component that calls decorate Internal() from createDom().

  this.changeUsernameLb_ = goog.dom.getElement('changeUsernameLb');
  this.changeUsernameLoadingImg_ = goog.dom.getElement(
      'changeUsernameLoadingImg');
  this.changeUsernameErrLb_ = goog.dom.getElement('changeUsernameErrLb');
  this.changeUsernameTB_ = goog.dom.getElement('changeUsernameTB');
  this.changeUsernamePasswordErrLb_ = goog.dom.getElement(
      'changeUsernamePasswordErrLb');
  this.changeUsernamePasswordTB_ = goog.dom.getElement(
      'changeUsernamePasswordTB');
  this.changeUsernameOkBtn_ = goog.dom.getElement('changeUsernameOkBtn');

  this.changeEmailLb_ = goog.dom.getElement('changeEmailLb');
  this.changeEmailLoadingImg_ = goog.dom.getElement('changeEmailLoadingImg');
  this.changeEmailErrLb_ = goog.dom.getElement('changeEmailErrLb');
  this.changeEmailTB_ = goog.dom.getElement('changeEmailTB');
  this.changeEmailPasswordErrLb_ = goog.dom.getElement(
      'changeEmailPasswordErrLb');
  this.changeEmailPasswordTB_ = goog.dom.getElement('changeEmailPasswordTB');
  this.changeEmailOkBtn_ = goog.dom.getElement('changeEmailOkBtn');

  this.resendEmailConfirmBtn_ = goog.dom.getElement('resendEmailConfirmBtn');
  this.emailVerifiedElement_ = goog.dom.getElement('emailVerifiedElement');

  this.changePasswordLoadingImg_ = goog.dom.getElement(
      'changePasswordLoadingImg');
  this.changeNewPasswordErrLb_ = goog.dom.getElement('changeNewPasswordErrLb');
  this.changeNewPasswordTB_ = goog.dom.getElement('changeNewPasswordTB');
  this.changeRepeatPasswordErrLb_ = goog.dom.getElement(
      'changeRepeatPasswordErrLb');
  this.changeRepeatPasswordTB_ = goog.dom.getElement('changeRepeatPasswordTB');
  this.changePasswordErrLb_ = goog.dom.getElement('changePasswordErrLb');
  this.changePasswordTB_ = goog.dom.getElement('changePasswordTB');
  this.changePasswordOkBtn_ = goog.dom.getElement('changePasswordOkBtn');

  this.deleteAccountLoadingImg_ = goog.dom.getElement(
      'deleteAccountLoadingImg');
  this.deleteAccountPasswordErrLb_ = goog.dom.getElement(
      'deleteAccountPasswordErrLb');
  this.deleteAccountPasswordTB_ = goog.dom.getElement(
      'deleteAccountPasswordTB');
  this.deleteAccountOkBtn_ = goog.dom.getElement('deleteAccountOkBtn');
};


/** @inheritDoc */
wit.user.view.UserView.prototype.enterDocument = function() {
  goog.base(this, 'enterDocument');

  this.getHandler().listen(this.changeUsernameOkBtn_,
      goog.events.EventType.CLICK,
      function(e) {
        // Hide the button, show the loading image.
        goog.style.setElementShown(this.changeUsernameOkBtn_, false);
        goog.style.setElementShown(this.changeUsernameLoadingImg_, true);

        var dataStore = wit.user.model.DataStore.getInstance();
        var log = new wit.base.model.Log();
        dataStore.changeUsername(
            this.changeUsernameTB_.value,
            this.changeUsernamePasswordTB_.value,
            log,
            goog.bind(this.changeUsernameCallback_, this));
      });

  this.getHandler().listen(this.changeEmailOkBtn_, goog.events.EventType.CLICK,
      function(e) {
        goog.style.setElementShown(this.changeEmailOkBtn_, false);
        goog.style.setElementShown(this.changeEmailLoadingImg_, true);

        var dataStore = wit.user.model.DataStore.getInstance();
        var log = new wit.base.model.Log();
        dataStore.changeEmail(
            this.changeEmailTB_.value,
            this.changeEmailPasswordTB_.value,
            log,
            goog.bind(this.changeEmailCallback_, this));
      });

  if (goog.isDefAndNotNull(this.resendEmailConfirmBtn_)) {
    this.getHandler().listen(
        this.resendEmailConfirmBtn_,
        goog.events.EventType.CLICK,
        function(e) {
          this.resendEmailConfirmBtn_.innerHTML = 'Sending...';
          this.resendEmailConfirmBtn_.setAttribute('disabled', 'disabled');
          goog.dom.classes.remove(this.resendEmailConfirmBtn_,
              wit.user.view.UserView.btnLinkCss_);
          goog.dom.classes.add(this.resendEmailConfirmBtn_,
              wit.user.view.UserView.mutedCss_);

          var dataStore = wit.user.model.DataStore.getInstance();
          var log = new wit.base.model.Log();
          dataStore.resendEmailConfirm(
              log,
              goog.bind(this.resendEmailConfirmCallback_, this));
        });
  }

  this.getHandler().listen(this.changePasswordOkBtn_,
      goog.events.EventType.CLICK,
      function(e) {
        goog.style.setElementShown(this.changePasswordOkBtn_, false);
        goog.style.setElementShown(this.changePasswordLoadingImg_, true);

        var dataStore = wit.user.model.DataStore.getInstance();
        var log = new wit.base.model.Log();
        dataStore.changePassword(
            this.changeNewPasswordTB_.value,
            this.changeRepeatPasswordTB_.value,
            this.changePasswordTB_.value,
            log,
            goog.bind(this.changePasswordCallback_, this));
      });

  this.getHandler().listen(this.deleteAccountOkBtn_,
      goog.events.EventType.CLICK,
      function(e) {
        // Hide the button, show the loading image.
        goog.style.setElementShown(this.deleteAccountOkBtn_, false);
        goog.style.setElementShown(this.deleteAccountLoadingImg_, true);

        var dataStore = wit.user.model.DataStore.getInstance();
        var log = new wit.base.model.Log();
        dataStore.deleteAccount(
            this.deleteAccountPasswordTB_.value,
            log,
            goog.bind(this.deleteAccountCallback_, this));
      });
};


/** @inheritDoc */
wit.user.view.UserView.prototype.disposeInternal = function() {
  // 1. Call the superclass’s disposeInternal method.
  goog.base(this, 'disposeInternal');

  // 2. Dispose of all Disposable objects owned by the class.

  // 3. Remove listeners added by the class.
  // 4. Remove references to DOM nodes.
  // 5. Remove references to COM objects.

};


/** @inheritDoc */
wit.user.view.UserView.prototype.exitDocument = function() {
  goog.base(this, 'exitDocument');
};


/**
 * Callback function to update the results of changing username.
 * @param {wit.base.model.Log} log LogInfo array.
 * @this {wit.user.view.UserView}
 * @private
 */
wit.user.view.UserView.prototype.changeUsernameCallback_ = function(log) {
  var logInfo;

  // If server error occurred, there's gonna be only one log info
  // type 'server status'.
  // Just reset the form to let users try again later.
  if (goog.isDef(log.getLogInfo(wit.base.constants.serverStatus, false))) {
    window.alert('Some error occurred.' +
                 'Please wait for a bit and try again.');
  }

  this.changeUsernameErrLb_.innerHTML = wit.base.constants.htmlSpace;
  this.changeUsernamePasswordErrLb_innerHTML = wit.base.constants.htmlSpace;

  // Inputs invalid
  logInfo = log.getLogInfo(wit.base.constants.changeUsername, false);
  if (goog.isDef(logInfo)) {
    this.changeUsernameErrLb_.innerHTML = logInfo.msg;
  }

  logInfo = log.getLogInfo(wit.base.constants.username, false);
  if (goog.isDef(logInfo)) {
    this.changeUsernameErrLb_.innerHTML = logInfo.msg;
  }

  logInfo = log.getLogInfo(wit.base.constants.password, false);
  if (goog.isDef(logInfo)) {
    this.changeUsernamePasswordErrLb_.innerHTML = logInfo.msg;
  }

  goog.style.setElementShown(this.changeUsernameOkBtn_, true);
  goog.style.setElementShown(this.changeUsernameLoadingImg_, false);

  // Succeeded
  logInfo = log.getLogInfo(wit.base.constants.changeUsername, true);
  if (goog.isDef(logInfo)) {
    this.changeUsernameLb_.innerHTML = logInfo.value;
  }
};


/**
 * Callback function to update the results of changing email.
 * @param {wit.base.model.Log} log LogInfo array.
 * @this {wit.user.view.UserView}
 * @private
 */
wit.user.view.UserView.prototype.changeEmailCallback_ = function(log) {
  var logInfo;

  // If server error occurred, there's gonna be only one log info
  // type 'server status'.
  // Just reset the form to let users try again later.
  if (goog.isDef(log.getLogInfo(wit.base.constants.serverStatus, false))) {
    window.alert('Some error occurred.' +
                 'Please wait for a bit and try again.');
  }

  this.changeEmailErrLb_.innerHTML = wit.base.constants.htmlSpace;
  this.changeEmailPasswordErrLb_.innerHTML = wit.base.constants.htmlSpace;

  // Inputs invalid
  logInfo = log.getLogInfo(wit.base.constants.changeEmail, false);
  if (goog.isDef(logInfo)) {
    this.changeEmailErrLb_.innerHTML = logInfo.msg;
  }

  logInfo = log.getLogInfo(wit.base.constants.email, false);
  if (goog.isDef(logInfo)) {
    this.changeEmailErrLb_.innerHTML = logInfo.msg;
  }

  logInfo = log.getLogInfo(wit.base.constants.password, false);
  if (goog.isDef(logInfo)) {
    this.changeEmailPasswordErrLb_.innerHTML = logInfo.msg;
  }

  goog.style.setElementShown(this.changeEmailOkBtn_, true);
  goog.style.setElementShown(this.changeEmailLoadingImg_, false);

  // Succeeded
  logInfo = log.getLogInfo(wit.base.constants.changeEmail, true);
  if (goog.isDef(logInfo)) {
    this.changeEmailLb_.innerHTML = logInfo.value;

    if (goog.isDefAndNotNull(this.resendEmailConfirmBtn_)) {
      goog.style.setElementShown(this.resendEmailConfirmBtn_, false);
    }
    if (goog.isDefAndNotNull(this.emailVerifiedElement_)) {
      goog.style.setElementShown(this.emailVerifiedElement_, false);
    }
  }
};


/**
 * Callback function to update the results of confirming email.
 * @param {wit.base.model.Log} log LogInfo array.
 * @this {wit.user.view.UserView}
 * @private
 */
wit.user.view.UserView.prototype.resendEmailConfirmCallback_ = function(log) {
  var logInfo;

  // If server error occurred, there's gonna be only one log info
  // type 'server status'.
  // Just reset the form to let users try again later.
  if (goog.isDef(log.getLogInfo(wit.base.constants.serverStatus, false))) {
    window.alert('Some error occurred.' +
                 'Please wait for a bit and try again.');
  }

  logInfo = log.getLogInfo(wit.base.constants.resendEmailConfirm, true);
  if (goog.isDef(logInfo)) {
    this.resendEmailConfirmBtn_.innerHTML = 'Email verification sent';
  } else {
    this.resendEmailConfirmBtn_.innerHTML = 'Couldn\'t send the email. Retry?';
    this.resendEmailConfirmBtn_.removeAttribute('disabled');

    goog.dom.classes.remove(this.resendEmailConfirmBtn_,
        wit.user.view.UserView.mutedCss_);
    goog.dom.classes.add(this.resendEmailConfirmBtn_,
        wit.user.view.UserView.btnLinkCss_);
  }
};


/**
 * Callback function to update the results of changing password.
 * @param {wit.base.model.Log} log LogInfo array.
 * @this {wit.user.view.UserView}
 * @private
 */
wit.user.view.UserView.prototype.changePasswordCallback_ = function(log) {
  var logInfo;

  // If server error occurred, there's gonna be only one log info
  // type 'server status'.
  // Just reset the form to let users try again later.
  if (goog.isDef(log.getLogInfo(wit.base.constants.serverStatus, false))) {
    window.alert('Some error occurred.' +
                 'Please wait for a bit and try again.');
  }

  this.changeNewPasswordErrLb_.innerHTML = wit.base.constants.htmlSpace;
  this.changeRepeatPasswordErrLb_.innerHTML = wit.base.constants.htmlSpace;
  this.changePasswordErrLb_.innerHTML = wit.base.constants.htmlSpace;

  // Inputs invalid
  logInfo = log.getLogInfo(wit.base.constants.changePassword, false);
  if (goog.isDef(logInfo)) {
    this.changeNewPasswordErrLb_.innerHTML = logInfo.msg;
  }

  logInfo = log.getLogInfo(wit.base.constants.newPassword, false);
  if (goog.isDef(logInfo)) {
    this.changeNewPasswordErrLb_.innerHTML = logInfo.msg;
  }

  logInfo = log.getLogInfo(wit.base.constants.repeatPassword, false);
  if (goog.isDef(logInfo)) {
    this.changeRepeatPasswordErrLb_.innerHTML = logInfo.msg;
  }

  logInfo = log.getLogInfo(wit.base.constants.password, false);
  if (goog.isDef(logInfo)) {
    this.changePasswordErrLb_.innerHTML = logInfo.msg;
  }

  goog.style.setElementShown(this.changePasswordOkBtn_, true);
  goog.style.setElementShown(this.changePasswordLoadingImg_, false);

  // Succeeded
  logInfo = log.getLogInfo(wit.base.constants.changePassword, true);
  if (goog.isDef(logInfo)) {

  }
};


/**
 * Callback function to update the results of deleting account.
 * @param {wit.base.model.Log} log LogInfo array.
 * @this {wit.user.view.UserView}
 * @private
 */
wit.user.view.UserView.prototype.deleteAccountCallback_ = function(log) {
  var logInfo;

  // If server error occurred, there's gonna be only one log info
  // type 'server status'.
  // Just reset the form to let users try again later.
  if (goog.isDef(log.getLogInfo(wit.base.constants.serverStatus, false))) {
    window.alert('Some error occurred.' +
                 'Please wait for a bit and try again.');
  }

  this.deleteAccountPasswordErrLb_.innerHTML = wit.base.constants.htmlSpace;

  // Inputs invalid
  logInfo = log.getLogInfo(wit.base.constants.deleteAccount, false);
  if (goog.isDef(logInfo)) {
    this.deleteAccountPasswordErrLb_.innerHTML = logInfo.msg;
  }

  logInfo = log.getLogInfo(wit.base.constants.password, false);
  if (goog.isDef(logInfo)) {
    this.deleteAccountPasswordErrLb_.innerHTML = logInfo.msg;
  }

  goog.style.setElementShown(this.deleteAccountOkBtn_, true);
  goog.style.setElementShown(this.deleteAccountLoadingImg_, false);

  // Succeeded
  logInfo = log.getLogInfo(wit.base.constants.deleteAccount, true);
  if (goog.isDef(logInfo)) {
    window.location.assign('/');
  }
};
