goog.provide('wit.home.view.HomeView');

goog.require('goog.dom');
goog.require('goog.events.KeyCodes');
goog.require('goog.style');
goog.require('goog.ui.Component');

goog.require('wit.fx.dom');
goog.require('wit.home.model.DataStore');



/**
 * @param {goog.dom.DomHelper=} opt_domHelper Optional DOM hepler.
 * @constructor
 * @extends {goog.ui.Component}
 */
wit.home.view.HomeView = function(opt_domHelper) {
  goog.base(this, opt_domHelper);
};
goog.inherits(wit.home.view.HomeView, goog.ui.Component);


/** @type {string} */
wit.home.view.HomeView.CSS_CLASS = goog.getCssName('home');


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.showLogInBtn;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.logInView;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.logInErrLb;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.logInUsernameTB;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.logInPasswordTB;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.logInBtn;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.forgotBtn;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.forgotView;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.forgotFormView;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.forgotUsernameErrLb;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.forgotUsernameTB;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.forgotOkBtn;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.forgotResultView;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.forgotResultOkBtn;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.showSignUpBtn;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.signUpView;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.signUpUsernameErrLb;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.signUpUsernameTB;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.signUpPasswordErrLb;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.signUpPasswordTB;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.signUpRepeatPasswordErrLb;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.signUpRepeatPasswordTB;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.signUpEmailErrLb;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.signUpEmailTB;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.signUpBtn;


/** @inheritDoc */
wit.home.view.HomeView.prototype.createDom = function() {
};


/** @inheritDoc */
wit.home.view.HomeView.prototype.decorateInternal = function(element) {
  goog.base(this, 'decorateInternal', element);

  this.showLogInBtn = goog.dom.getElement('showLogInBtn');
  this.logInView = goog.dom.getElement('logInView');

  goog.style.setElementShown(this.logInView, false);

  this.logInErrLb = goog.dom.getElement('logInErrLb');
  this.logInUsernameTB = goog.dom.getElement('logInUsernameTB');
  this.logInPasswordTB = goog.dom.getElement('logInPasswordTB');
  this.logInBtn = goog.dom.getElement('logInBtn');

  this.forgotBtn = goog.dom.getElement('forgotBtn');
  this.forgotView = goog.dom.getElement('forgotView');

  goog.style.setElementShown(this.forgotView, false);

  this.forgotFormView = goog.dom.getElement('forgotFormView');
  this.forgotUsernameErrLb = goog.dom.getElement('forgotUsernameErrLb');
  this.forgotUsernameTB = goog.dom.getElement('forgotUsernameTB');
  this.forgotOkBtn = goog.dom.getElement('forgotOkBtn');

  this.forgotResultView = goog.dom.getElement('forgotResultView');

  goog.style.setElementShown(this.forgotResultView, false);

  this.forgotResultOkBtn = goog.dom.getElement('forgotResultOkBtn');

  this.showSignUpBtn = goog.dom.getElement('showSignUpBtn');
  this.signUpView = goog.dom.getElement('signUpView');

  goog.style.setElementShown(this.signUpView, false);

  this.signUpUsernameErrLb = goog.dom.getElement('signUpUsernameErrLb');
  this.signUpUsernameTB = goog.dom.getElement('signUpUsernameTB');
  this.signUpPasswordErrLb = goog.dom.getElement('signUpPasswordErrLb');
  this.signUpPasswordTB = goog.dom.getElement('signUpPasswordTB');
  this.signUpRepeatPasswordErrLb = goog.dom.getElement(
      'signUpRepeatPasswordErrLb');
  this.signUpRepeatPasswordTB = goog.dom.getElement('signUpRepeatPasswordTB');
  this.signUpEmailErrLb = goog.dom.getElement('signUpEmailErrLb');
  this.signUpEmailTB = goog.dom.getElement('signUpEmailTB');
  this.signUpBtn = goog.dom.getElement('signUpBtn');
};


/** @inheritDoc */
wit.home.view.HomeView.prototype.enterDocument = function() {
  goog.base(this, 'enterDocument');

  this.getHandler().listen(this.showLogInBtn, goog.events.EventType.CLICK,
      function(e) {

        var toggle = 'hide';
        if (!goog.style.isElementShown(this.logInView)) {
          this.logInErrLb.innerHTML = wit.base.constants.htmlSpace;
          this.logInUsernameTB.value = '';
          this.logInPasswordTB.value = '';

          toggle = 'show';
        }

        wit.fx.dom.animate(
            this.logInView,
            {'height': toggle,
              'padding-top': toggle,
              'padding-bottom': toggle},
            250);
      });

  this.getHandler().listen(this.logInPasswordTB, goog.events.EventType.KEYUP,
      function(e) {
        if (e.keyCode === goog.events.KeyCodes.ENTER) {
          this.logIn_();
        }
      });

  this.getHandler().listen(this.logInBtn, goog.events.EventType.CLICK,
      function(e) {
        this.logIn_();
      });

  this.getHandler().listen(
      this.forgotBtn,
      goog.events.EventType.CLICK,
      function(e) {

        var toggle = 'hide';

        if (!goog.style.isElementShown(this.forgotView)) {
          this.forgotUsernameErrLb.innerHTML = wit.base.constants.htmlSpace;
          this.forgotUsernameTB.value = '';

          toggle = 'show';
        }

        wit.fx.dom.animate(
            this.forgotView,
            {'height': toggle,
              'padding-top': toggle,
              'padding-bottom': toggle},
            250);
      });

  this.getHandler().listen(
      this.forgotOkBtn,
      goog.events.EventType.CLICK,
      function(e) {
        goog.dom.setProperties(this.forgotUsernameTB, {'disabled': true});
        goog.dom.setProperties(this.forgotOkBtn, {'disabled': true});

        var dataStore = wit.home.model.DataStore.getInstance();
        var log = new wit.base.model.Log();
        dataStore.sendEmailResetPassword(
            this.forgotUsernameTB.value,
            log,
            goog.bind(this.sendEmailResetPasswordCallback_, this));
      });

  this.getHandler().listen(
      this.forgotResultOkBtn,
      goog.events.EventType.CLICK,
      function(e) {
        wit.fx.dom.animate(
            this.forgotView,
            {'height': 'hide',
              'padding-top': 'hide',
              'padding-bottom': 'hide'},
            250);
      });

  this.getHandler().listen(this.showSignUpBtn, goog.events.EventType.CLICK,
      function(e) {

        this.signUpUsernameErrLb.innerHTML = wit.base.constants.htmlSpace;
        this.signUpUsernameTB.value = '';
        this.signUpPasswordErrLb.innerHTML = wit.base.constants.htmlSpace;
        this.signUpPasswordTB.value = '';
        this.signUpRepeatPasswordErrLb.innerHTML = wit.base.constants.htmlSpace;
        this.signUpRepeatPasswordTB.value = '';
        this.signUpEmailErrLb.innerHTML = wit.base.constants.htmlSpace;
        this.signUpEmailTB.value = '';

        wit.fx.dom.animate(
            this.signUpView,
            {'height': 'toggle',
              'padding-top': 'toggle',
              'padding-bottom': 'toggle'},
            250);
      });

  this.getHandler().listen(
      this.signUpBtn,
      goog.events.EventType.CLICK,
      function(e) {
        goog.dom.setProperties(this.signUpUsernameTB, {'disabled': true});
        goog.dom.setProperties(this.signUpPasswordTB, {'disabled': true});
        goog.dom.setProperties(this.signUpRepeatPasswordTB, {'disabled': true});
        goog.dom.setProperties(this.signUpEmailTB, {'disabled': true});
        goog.dom.setProperties(this.signUpBtn, {'disabled': true});

        var dataStore = wit.home.model.DataStore.getInstance();
        var log = new wit.base.model.Log();
        dataStore.signUp(
            this.signUpUsernameTB.value,
            this.signUpEmailTB.value,
            this.signUpPasswordTB.value,
            this.signUpRepeatPasswordTB.value,
            log,
            goog.bind(this.signUpCallback_, this));
      });

  this.getHandler().listen(
      this.signUpUsernameTB,
      goog.events.EventType.BLUR,
      function(e) {
        var log = new wit.base.model.Log();
        wit.user.model.UserVerifier.isUsernameValid(
            this.signUpUsernameTB.value,
            log,
            goog.bind(this.validateSignUpUsernameCallback_, this));
      });

  this.getHandler().listen(
      this.signUpEmailTB,
      goog.events.EventType.BLUR,
      function(e) {
        var log = new wit.base.model.Log();
        wit.user.model.UserVerifier.isEmailValid(
            this.signUpEmailTB.value,
            log,
            goog.bind(this.validateSignUpEmailCallback_, this));
      });

  this.getHandler().listen(
      this.signUpPasswordTB,
      goog.events.EventType.BLUR,
      function(e) {
        var log = new wit.base.model.Log();
        wit.user.model.UserVerifier.isPasswordValid(
            this.signUpPasswordTB.value,
            log,
            wit.base.constants.password);
        this.validateSignUpPasswordCallback_(log);
      });

  this.getHandler().listen(
      this.signUpRepeatPasswordTB,
      goog.events.EventType.BLUR,
      function(e) {
        var log = new wit.base.model.Log();
        wit.user.model.UserVerifier.isRepeatPasswordValid(
            this.signUpPasswordTB.value,
            this.signUpRepeatPasswordTB.value,
            log);
        this.validateSignUpRepeatPasswordCallback_(log);
      });
};


/** @inheritDoc */
wit.home.view.HomeView.prototype.disposeInternal = function() {
  // 1. Call the superclass’s disposeInternal method.
  goog.base(this, 'disposeInternal');

  // 2. Dispose of all Disposable objects owned by the class.

  // 3. Remove listeners added by the class.
  // 4. Remove references to DOM nodes.
  // 5. Remove references to COM objects.

};


/** @inheritDoc */
wit.home.view.HomeView.prototype.exitDocument = function() {
  goog.base(this, 'exitDocument');
};


/**
 * Log in
 * @private
 */
wit.home.view.HomeView.prototype.logIn_ = function() {
  goog.dom.setProperties(this.logInUsernameTB, {'disabled': true});
  goog.dom.setProperties(this.logInPasswordTB, {'disabled': true});
  goog.dom.setProperties(this.logInBtn, {'disabled': true});

  var dataStore = wit.home.model.DataStore.getInstance();
  var log = new wit.base.model.Log();
  dataStore.logIn(
      this.logInUsernameTB.value,
      this.logInPasswordTB.value,
      log,
      goog.bind(this.logInCallback_, this));
};


/**
 * Callback function to update the results of logging in.
 * @param {wit.base.model.Log} log LogInfo array.
 * @this {wit.home.view.HomeView}
 * @private
 */
wit.home.view.HomeView.prototype.logInCallback_ = function(log) {
  var logInfo;

  // If server error occurred, there's gonna be only one log info
  // type 'server status'.
  // Just reset the form to let users try again later.
  if (goog.isDef(log.getLogInfo(wit.base.constants.serverStatus, false))) {
    window.alert('Some error occurred.' +
                 'Please wait for a bit and try again.');
  }

  // Logging in succeeded, reload the page.
  logInfo = log.getLogInfo(wit.base.constants.logIn, true);
  if (goog.isDef(logInfo)) {
    document.location.reload();
    return;
  }

  this.logInErrLb.innerHTML = wit.base.constants.htmlSpace;

  // Inputs invalid
  logInfo = log.getLogInfo(wit.base.constants.logIn, false);
  if (goog.isDef(logInfo)) {
    this.logInErrLb.innerHTML = logInfo.msg;
  }

  logInfo = log.getLogInfo(wit.base.constants.username, false);
  if (goog.isDef(logInfo)) {
    this.logInErrLb.innerHTML = logInfo.msg;
  }

  logInfo = log.getLogInfo(wit.base.constants.email, false);
  if (goog.isDef(logInfo)) {
    this.logInErrLb.innerHTML = logInfo.msg;
  }

  logInfo = log.getLogInfo(wit.base.constants.password, false);
  if (goog.isDef(logInfo)) {
    this.logInErrLb.innerHTML = logInfo.msg;
  }

  goog.dom.setProperties(this.logInUsernameTB, {'disabled': false});
  goog.dom.setProperties(this.logInPasswordTB, {'disabled': false});
  goog.dom.setProperties(this.logInBtn, {'disabled': false});
};


/**
 * Callback function to update the results of signing up.
 * @param {wit.base.model.Log} log LogInfo array.
 * @this {wit.home.view.HomeView}
 * @private
 */
wit.home.view.HomeView.prototype.signUpCallback_ = function(log) {
  var logInfo;

  // If server error occurred, there's gonna be only one log info
  // type 'server status'.
  // Just reset the form to let users try again later.
  if (goog.isDef(log.getLogInfo(wit.base.constants.serverStatus, false))) {
    window.alert('Some error occurred.' +
                 'Please wait for a bit and try again.');
  }

  // Signing up succeeded, reload the page.
  logInfo = log.getLogInfo(wit.base.constants.signUp, true);
  if (goog.isDef(logInfo)) {
    document.location.reload();
    return;
  }

  this.signUpUsernameErrLb.innerHTML = wit.base.constants.htmlSpace;
  this.signUpEmailErrLb.innerHTML = wit.base.constants.htmlSpace;
  this.signUpPasswordErrLb.innerHTML = wit.base.constants.htmlSpace;
  this.signUpRepeatPasswordErrLb.innerHTML = wit.base.constants.htmlSpace;

  // Concurrent modification exception
  logInfo = log.getLogInfo(wit.base.constants.signUp, false);
  if (goog.isDef(logInfo)) {
    this.signUpUsernameErrLb.innerHTML = logInfo.msg;
  }

  // Inputs invalid
  logInfo = log.getLogInfo(wit.base.constants.username, false);
  if (goog.isDef(logInfo)) {
    this.signUpUsernameErrLb.innerHTML = logInfo.msg;
  }

  logInfo = log.getLogInfo(wit.base.constants.email, false);
  if (goog.isDef(logInfo)) {
    this.signUpEmailErrLb.innerHTML = logInfo.msg;
  }

  logInfo = log.getLogInfo(wit.base.constants.password, false);
  if (goog.isDef(logInfo)) {
    this.signUpPasswordErrLb.innerHTML = logInfo.msg;
  }

  logInfo = log.getLogInfo(wit.base.constants.repeatPassword, false);
  if (goog.isDef(logInfo)) {
    this.signUpRepeatPasswordErrLb.innerHTML = logInfo.msg;
  }

  goog.dom.setProperties(this.signUpUsernameTB, {'disabled': false});
  goog.dom.setProperties(this.signUpPasswordTB, {'disabled': false});
  goog.dom.setProperties(this.signUpRepeatPasswordTB, {'disabled': false});
  goog.dom.setProperties(this.signUpEmailTB, {'disabled': false});
  goog.dom.setProperties(this.signUpBtn, {'disabled': false});
};


/**
 * Callback function to update the results of requesting to reset password.
 * @param {wit.base.model.Log} log LogInfo array.
 * @this {wit.home.view.HomeView}
 * @private
 */
wit.home.view.HomeView.prototype.sendEmailResetPasswordCallback_ =
    function(log) {
  var logInfo;

  // If server error occurred, there's gonna be only one log info
  // type 'server status'.
  // Just reset the form to let users try again later.
  if (goog.isDef(log.getLogInfo(wit.base.constants.serverStatus, false))) {
    window.alert('Some error occurred.' +
                 'Please wait for a bit and try again.');
  }

  logInfo = log.getLogInfo(wit.base.constants.sendEmailResetPassword, true);
  if (goog.isDef(logInfo)) {
    wit.fx.dom.swap(this.forgotView, 250);
    return;
  }

  this.forgotUsernameErrLb.innerHTML = wit.base.constants.htmlSpace;

  logInfo = log.getLogInfo(wit.base.constants.sendEmailResetPassword, false);
  if (goog.isDef(logInfo)) {
    this.forgotUsernameErrLb.innerHTML = logInfo.msg;
  }

  logInfo = log.getLogInfo(wit.base.constants.username, false);
  if (goog.isDef(logInfo)) {
    this.forgotUsernameErrLb.innerHTML = logInfo.msg;
  }

  logInfo = log.getLogInfo(wit.base.constants.email, false);
  if (goog.isDef(logInfo)) {
    this.forgotUsernameErrLb.innerHTML = logInfo.msg;
  }

  goog.dom.setProperties(this.forgotUsernameTB, {'disabled': false});
  goog.dom.setProperties(this.forgotOkBtn, {'disabled': false});
};


/**
 * Callback function to update the results of validating username (sign up).
 * @param {wit.base.model.Log} log LogInfo array.
 * @this {wit.home.view.HomeView}
 * @private
 */
wit.home.view.HomeView.prototype.validateSignUpUsernameCallback_ = function(
    log) {
  var logInfo = log.getLogInfo(wit.base.constants.username);
  if (goog.isDef(logInfo)) {
    // Check if the value is still the same
    if (logInfo.value === this.signUpUsernameTB.value) {
      if (logInfo.isValid === false) {
        this.signUpUsernameErrLb.innerHTML = logInfo.msg;
      } else {
        this.signUpUsernameErrLb.innerHTML = wit.base.constants.htmlSpace;
      }
    }
  }
  // Else, maybe server went wrong, should be able to ignore.
};


/**
 * Callback function to update the results of validating email (sign up).
 * @param {wit.base.model.Log} log LogInfo array.
 * @this {wit.home.view.HomeView}
 * @private
 */
wit.home.view.HomeView.prototype.validateSignUpEmailCallback_ = function(log) {
  var logInfo = log.getLogInfo(wit.base.constants.email);
  if (goog.isDef(logInfo)) {
    // Check if the value is still the same
    if (logInfo.value === this.signUpEmailTB.value) {
      if (logInfo.isValid === false) {
        this.signUpEmailErrLb.innerHTML = logInfo.msg;
      } else {
        this.signUpEmailErrLb.innerHTML = wit.base.constants.htmlSpace;
      }
    }
  }
  // Else, maybe server went wrong, should be able to ignore.
};


/**
 * Callback function to update the results of validating password (sign up).
 * @param {wit.base.model.Log} log LogInfo array.
 * @this {wit.home.view.HomeView}
 * @private
 */
wit.home.view.HomeView.prototype.validateSignUpPasswordCallback_ = function(
    log) {
  var logInfo = log.getLogInfo(wit.base.constants.password);
  if (goog.isDef(logInfo)) {
    // Check if the value is still the same
    if (logInfo.value === this.signUpPasswordTB.value) {
      if (logInfo.isValid === false) {
        this.signUpPasswordErrLb.innerHTML = logInfo.msg;
      } else {
        this.signUpPasswordErrLb.innerHTML = wit.base.constants.htmlSpace;
      }
    }
  }
  // Else, maybe server went wrong, should be able to ignore.
};


/**
 * Callback function to update the results of
 *     validating repeat password (sign up).
 * @param {wit.base.model.Log} log LogInfo array.
 * @this {wit.home.view.HomeView}
 * @private
 */
wit.home.view.HomeView.prototype.validateSignUpRepeatPasswordCallback_ =
    function(log) {
  var logInfo = log.getLogInfo(wit.base.constants.repeatPassword);
  if (goog.isDef(logInfo)) {
    // Check if the value is still the same
    if (logInfo.value === this.signUpRepeatPasswordTB.value) {
      if (logInfo.isValid === false) {
        this.signUpRepeatPasswordErrLb.innerHTML = logInfo.msg;
      } else {
        this.signUpRepeatPasswordErrLb.innerHTML = wit.base.constants.htmlSpace;
      }
    }
  }
  // Else, maybe server went wrong, should be able to ignore.
};
