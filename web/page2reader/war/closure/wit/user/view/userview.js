goog.provide('wit.user.view.UserView');

goog.require('goog.dom');
goog.require('goog.dom.dataset');
goog.require('goog.style');
goog.require('goog.ui.Component');

goog.require('wit.fx.dom');
goog.require('wit.page2reader.constants');
goog.require('wit.page2reader.model.DataStore');
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
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.readerEmailLb_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.showChangeReaderEmailBtn_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.changeReaderEmailView_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.changeReaderEmailErrLb_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.changeReaderEmailTB_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.changeReaderEmailOkBtn_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.usernameLb_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.showChangeUsernameBtn_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.changeUsernameView_;


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
wit.user.view.UserView.prototype.emailLb_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.resendEmailConfirmBtn_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.showChangeEmailBtn_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.changeEmailView_;


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
wit.user.view.UserView.prototype.showChangePasswordBtn_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.changePasswordView_;


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
wit.user.view.UserView.prototype.showDeleteAccountBtn_;


/**
 * @type {Element}
 * @private
 */
wit.user.view.UserView.prototype.deleteAccountView_;


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

  this.readerEmailLb_ = goog.dom.getElement('readerEmailLb');
  this.showChangeReaderEmailBtn_ = goog.dom.getElement(
      'showChangeReaderEmailBtn');
  this.changeReaderEmailView_ = goog.dom.getElement('changeReaderEmailView');

  goog.style.setElementShown(this.changeReaderEmailView_, false);

  this.changeReaderEmailErrLb_ = goog.dom.getElement('changeReaderEmailErrLb');
  this.changeReaderEmailTB_ = goog.dom.getElement('changeReaderEmailTB');
  this.changeReaderEmailOkBtn_ = goog.dom.getElement('changeReaderEmailOkBtn');

  this.usernameLb_ = goog.dom.getElement('usernameLb');
  this.showChangeUsernameBtn_ = goog.dom.getElement('showChangeUsernameBtn');
  this.changeUsernameView_ = goog.dom.getElement('changeUsernameView');

  goog.style.setElementShown(this.changeUsernameView_, false);

  this.changeUsernameErrLb_ = goog.dom.getElement('changeUsernameErrLb');
  this.changeUsernameTB_ = goog.dom.getElement('changeUsernameTB');
  this.changeUsernamePasswordErrLb_ = goog.dom.getElement(
      'changeUsernamePasswordErrLb');
  this.changeUsernamePasswordTB_ = goog.dom.getElement(
      'changeUsernamePasswordTB');
  this.changeUsernameOkBtn_ = goog.dom.getElement('changeUsernameOkBtn');

  this.emailLb_ = goog.dom.getElement('emailLb');
  this.resendEmailConfirmBtn_ = goog.dom.getElement('resendEmailConfirmBtn');

  // If already verified, hide resendEmailConfirmBtn
  var didConfirmEmail = goog.dom.dataset.get(this.resendEmailConfirmBtn_,
      'didConfirmEmail');
  if (didConfirmEmail === 'true') {
    goog.style.setElementShown(this.resendEmailConfirmBtn_, false);
  }

  this.showChangeEmailBtn_ = goog.dom.getElement('showChangeEmailBtn');
  this.changeEmailView_ = goog.dom.getElement('changeEmailView');

  goog.style.setElementShown(this.changeEmailView_, false);

  this.changeEmailErrLb_ = goog.dom.getElement('changeEmailErrLb');
  this.changeEmailTB_ = goog.dom.getElement('changeEmailTB');
  this.changeEmailPasswordErrLb_ = goog.dom.getElement(
      'changeEmailPasswordErrLb');
  this.changeEmailPasswordTB_ = goog.dom.getElement('changeEmailPasswordTB');
  this.changeEmailOkBtn_ = goog.dom.getElement('changeEmailOkBtn');

  this.showChangePasswordBtn_ = goog.dom.getElement('showChangePasswordBtn');
  this.changePasswordView_ = goog.dom.getElement('changePasswordView');

  goog.style.setElementShown(this.changePasswordView_, false);

  this.changeNewPasswordErrLb_ = goog.dom.getElement('changeNewPasswordErrLb');
  this.changeNewPasswordTB_ = goog.dom.getElement('changeNewPasswordTB');
  this.changeRepeatPasswordErrLb_ = goog.dom.getElement(
      'changeRepeatPasswordErrLb');
  this.changeRepeatPasswordTB_ = goog.dom.getElement('changeRepeatPasswordTB');
  this.changePasswordErrLb_ = goog.dom.getElement('changePasswordErrLb');
  this.changePasswordTB_ = goog.dom.getElement('changePasswordTB');
  this.changePasswordOkBtn_ = goog.dom.getElement('changePasswordOkBtn');

  this.showDeleteAccountBtn_ = goog.dom.getElement('showDeleteAccountBtn');
  this.deleteAccountView_ = goog.dom.getElement('deleteAccountView');

  goog.style.setElementShown(this.deleteAccountView_, false);

  this.deleteAccountPasswordErrLb_ = goog.dom.getElement(
      'deleteAccountPasswordErrLb');
  this.deleteAccountPasswordTB_ = goog.dom.getElement(
      'deleteAccountPasswordTB');
  this.deleteAccountOkBtn_ = goog.dom.getElement('deleteAccountOkBtn');
};


/** @inheritDoc */
wit.user.view.UserView.prototype.enterDocument = function() {
  goog.base(this, 'enterDocument');

  this.getHandler().listen(this.showChangeReaderEmailBtn_,
      goog.events.EventType.CLICK,
      function(e) {

        var toggle = 'hide';
        if (!goog.style.isElementShown(this.changeReaderEmailView_)) {
          this.changeReaderEmailErrLb_.innerHTML = wit.base.constants.htmlSpace;
          this.changeReaderEmailTB_.value = '';

          toggle = 'show';
        }

        wit.fx.dom.animate(
            this.changeReaderEmailView_,
            {'height': toggle,
              'padding-top': toggle,
              'padding-bottom': toggle},
            250);
      });

  this.getHandler().listen(this.changeReaderEmailOkBtn_,
      goog.events.EventType.CLICK,
      function(e) {
        goog.dom.setProperties(this.changeReaderEmailTB_, {'disabled': true});
        goog.dom.setProperties(this.changeReaderEmailOkBtn_,
            {'disabled': true});

        var dataStore = wit.page2reader.model.DataStore.getInstance();
        var log = new wit.base.model.Log();
        dataStore.updateReaderEmail(
            this.changeReaderEmailTB_.value,
            log,
            goog.bind(this.changeReaderEmailCallback_, this));
      });

  this.getHandler().listen(this.showChangeUsernameBtn_,
      goog.events.EventType.CLICK,
      function(e) {

        var toggle = 'hide';
        if (!goog.style.isElementShown(this.changeUsernameView_)) {
          this.changeUsernameErrLb_.innerHTML = wit.base.constants.htmlSpace;
          this.changeUsernameTB_.value = '';
          this.changeUsernamePasswordErrLb_.innerHTML =
              wit.base.constants.htmlSpace;
          this.changeUsernamePasswordTB_.value = '';

          toggle = 'show';
        }

        wit.fx.dom.animate(
            this.changeUsernameView_,
            {'height': toggle,
              'padding-top': toggle,
              'padding-bottom': toggle},
            250);
      });

  this.getHandler().listen(this.changeUsernameOkBtn_,
      goog.events.EventType.CLICK,
      function(e) {
        goog.dom.setProperties(this.changeUsernameTB_, {'disabled': true});
        goog.dom.setProperties(this.changeUsernameOkBtn_,
            {'disabled': true});

        var dataStore = wit.user.model.DataStore.getInstance();
        var log = new wit.base.model.Log();
        dataStore.changeUsername(
            this.changeUsernameTB_.value,
            this.changeUsernamePasswordTB_.value,
            log,
            goog.bind(this.changeUsernameCallback_, this));
      });

  if (goog.isDefAndNotNull(this.resendEmailConfirmBtn_)) {
    this.getHandler().listen(
        this.resendEmailConfirmBtn_,
        goog.events.EventType.CLICK,
        function(e) {
          goog.dom.setProperties(this.resendEmailConfirmBtn_,
              {'disabled': true});

          var dataStore = wit.user.model.DataStore.getInstance();
          var log = new wit.base.model.Log();
          dataStore.resendEmailConfirm(
              log,
              goog.bind(this.resendEmailConfirmCallback_, this));
        });
  }

  this.getHandler().listen(this.showChangeEmailBtn_,
      goog.events.EventType.CLICK,
      function(e) {

        var toggle = 'hide';
        if (!goog.style.isElementShown(this.changeEmailView_)) {
          this.changeEmailErrLb_.innerHTML = wit.base.constants.htmlSpace;
          this.changeEmailTB_.value = '';
          this.changeEmailPasswordErrLb_.innerHTML =
              wit.base.constants.htmlSpace;
          this.changeEmailPasswordTB_.value = '';

          toggle = 'show';
        }

        wit.fx.dom.animate(
            this.changeEmailView_,
            {'height': toggle,
              'padding-top': toggle,
              'padding-bottom': toggle},
            250);
      });

  this.getHandler().listen(this.changeEmailOkBtn_, goog.events.EventType.CLICK,
      function(e) {
        goog.dom.setProperties(this.changeEmailTB_, {'disabled': true});
        goog.dom.setProperties(this.changeEmailOkBtn_, {'disabled': true});

        var dataStore = wit.user.model.DataStore.getInstance();
        var log = new wit.base.model.Log();
        dataStore.changeEmail(
            this.changeEmailTB_.value,
            this.changeEmailPasswordTB_.value,
            log,
            goog.bind(this.changeEmailCallback_, this));
      });

  this.getHandler().listen(this.showChangePasswordBtn_,
      goog.events.EventType.CLICK,
      function(e) {

        var toggle = 'hide';
        if (!goog.style.isElementShown(this.changePasswordView_)) {
          this.changeNewPasswordErrLb_.innerHTML = wit.base.constants.htmlSpace;
          this.changeNewPasswordTB_.value = '';
          this.changeRepeatPasswordErrLb_.innerHTML =
              wit.base.constants.htmlSpace;
          this.changeRepeatPasswordTB_.value = '';
          this.changePasswordErrLb_.innerHTML = wit.base.constants.htmlSpace;
          this.changePasswordTB_.value = '';

          toggle = 'show';
        }

        wit.fx.dom.animate(
            this.changePasswordView_,
            {'height': toggle,
              'padding-top': toggle,
              'padding-bottom': toggle},
            250);
      });

  this.getHandler().listen(this.changePasswordOkBtn_,
      goog.events.EventType.CLICK,
      function(e) {
        goog.dom.setProperties(this.changeNewPasswordTB_, {'disabled': true});
        goog.dom.setProperties(this.changeRepeatPasswordTB_,
            {'disabled': true});
        goog.dom.setProperties(this.changePasswordTB_, {'disabled': true});
        goog.dom.setProperties(this.changePasswordOkBtn_, {'disabled': true});

        var dataStore = wit.user.model.DataStore.getInstance();
        var log = new wit.base.model.Log();
        dataStore.changePassword(
            this.changeNewPasswordTB_.value,
            this.changeRepeatPasswordTB_.value,
            this.changePasswordTB_.value,
            log,
            goog.bind(this.changePasswordCallback_, this));
      });

  this.getHandler().listen(this.showDeleteAccountBtn_,
      goog.events.EventType.CLICK,
      function(e) {

        var toggle = 'hide';
        if (!goog.style.isElementShown(this.deleteAccountView_)) {
          this.deleteAccountPasswordErrLb_.innerHTML =
              wit.base.constants.htmlSpace;
          this.deleteAccountPasswordTB_.value = '';

          toggle = 'show';
        }

        wit.fx.dom.animate(
            this.deleteAccountView_,
            {'height': toggle,
              'padding-top': toggle,
              'padding-bottom': toggle},
            250);
      });

  this.getHandler().listen(this.deleteAccountOkBtn_,
      goog.events.EventType.CLICK,
      function(e) {
        goog.dom.setProperties(this.deleteAccountPasswordTB_,
            {'disabled': true});
        goog.dom.setProperties(this.deleteAccountOkBtn_, {'disabled': true});

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
 * Callback function to update the results of updating reader email.
 * @param {wit.base.model.Log} log LogInfo array.
 * @this {wit.user.view.UserView}
 * @private
 */
wit.user.view.UserView.prototype.changeReaderEmailCallback_ = function(log) {
  var logInfo;

  // If server error occurred, there's gonna be only one log info
  // type 'server status'.
  // Just reset the form to let users try again later.
  if (goog.isDef(log.getLogInfo(wit.base.constants.serverStatus, false))) {
    window.alert('Some error occurred.' +
                 'Please wait for a bit and try again.');
  }

  this.changeReaderEmailErrLb_.innerHTML = wit.base.constants.htmlSpace;

  // Inputs invalid
  logInfo = log.getLogInfo(wit.base.constants.email, false);
  if (goog.isDef(logInfo)) {
    this.changeReaderEmailErrLb_.innerHTML = logInfo.msg;
  }

  goog.dom.setProperties(this.changeReaderEmailTB_, {'disabled': false});
  goog.dom.setProperties(this.changeReaderEmailOkBtn_,
      {'disabled': false});

  // Succeeded
  logInfo = log.getLogInfo(wit.page2reader.constants.UPDATE_READER_EMAIL, true);
  if (goog.isDef(logInfo)) {
    this.readerEmailLb_.innerHTML = logInfo.value;
    this.changeReaderEmailTB_.value = '';

    wit.fx.dom.animate(
        this.changeReaderEmailView_,
        {'height': 'hide',
          'padding-top': 'hide',
          'padding-bottom': 'hide'},
        250);
  }
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

  goog.dom.setProperties(this.changeUsernameTB_, {'disabled': false});
  goog.dom.setProperties(this.changeUsernameOkBtn_,
      {'disabled': false});

  // Succeeded
  logInfo = log.getLogInfo(wit.base.constants.changeUsername, true);
  if (goog.isDef(logInfo)) {
    this.usernameLb_.innerHTML = logInfo.value;

    wit.fx.dom.animate(
        this.changeUsernameView_,
        {'height': 'hide',
          'padding-top': 'hide',
          'padding-bottom': 'hide'},
        250);
  }
};


/**
 * Callback function to update the results of resending confirm email.
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
    this.resendEmailConfirmBtn_.innerHTML = 'Sent';
  } else {
    this.resendEmailConfirmBtn_.innerHTML = 'Couldn\'t send the email. Retry?';
    goog.dom.setProperties(this.resendEmailConfirmBtn_, {'disabled': false});
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

  goog.dom.setProperties(this.changeEmailTB_, {'disabled': false});
  goog.dom.setProperties(this.changeEmailOkBtn_, {'disabled': false});

  // Succeeded
  logInfo = log.getLogInfo(wit.base.constants.changeEmail, true);
  if (goog.isDef(logInfo)) {
    this.emailLb_.innerHTML = logInfo.value;

    wit.fx.dom.animate(
        this.changeEmailView_,
        {'height': 'hide',
          'padding-top': 'hide',
          'padding-bottom': 'hide'},
        250);

    // No need resendEmailConfirmBtn
    //goog.style.setElementShown(this.resendEmailConfirmBtn_, false);
  }
};


/**
 * callback function to update the results of changing password.
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

  goog.dom.setProperties(this.changeNewPasswordTB_, {'disabled': false});
  goog.dom.setProperties(this.changeRepeatPasswordTB_,
      {'disabled': false});
  goog.dom.setProperties(this.changePasswordTB_, {'disabled': false});
  goog.dom.setProperties(this.changePasswordOkBtn_, {'disabled': false});

  // Succeeded
  logInfo = log.getLogInfo(wit.base.constants.changePassword, true);
  if (goog.isDef(logInfo)) {
    wit.fx.dom.animate(
        this.changePasswordView_,
        {'height': 'hide',
          'padding-top': 'hide',
          'padding-bottom': 'hide'},
        250);
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

  goog.dom.setProperties(this.deleteAccountPasswordTB_, {'disabled': false});
  goog.dom.setProperties(this.deleteAccountOkBtn_, {'disabled': false});

  // Succeeded
  logInfo = log.getLogInfo(wit.base.constants.deleteAccount, true);
  if (goog.isDef(logInfo)) {
    window.location.assign('/');
  }
};
