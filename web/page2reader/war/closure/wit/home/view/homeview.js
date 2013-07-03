goog.provide('wit.home.view.HomeView');

goog.require('goog.dom');
goog.require('goog.dom.classes');
goog.require('goog.style');
goog.require('goog.ui.Component');

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
 * @type {string}
 * @const
 * @protected
 */
wit.home.view.HomeView.forgotExpandCss = goog.getCssName('forgot-expand');


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
wit.home.view.HomeView.prototype.logInErrLb;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.logInBtn;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.logInLoadingImg;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.forgotBtn;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.forgotPanel;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.forgotResult;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.forgotForm;


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
wit.home.view.HomeView.prototype.forgotCancelBtn;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.forgotLoadingImg;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.signUpUsernameTB;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.signUpEmailTB;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.signUpPasswordTB;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.signUpRepeatPasswordTB;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.signUpUsernameErrLb;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.signUpEmailErrLb;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.signUpPasswordErrLb;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.signUpRepeatPasswordErrLb;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.signUpBtn;


/**
 * @type {Element}
 * @protected
 */
wit.home.view.HomeView.prototype.signUpLoadingImg;


/** @inheritDoc */
wit.home.view.HomeView.prototype.createDom = function() {
};


/** @inheritDoc */
wit.home.view.HomeView.prototype.decorateInternal = function(element) {
  goog.base(this, 'decorateInternal', element);

  // Although decorateInternal(element) expects to be called with an element
  // that is already attached to the document and therefore may already leverage
  // methods such as getElementByFragment(), it must be careful not to make that
  // assumption for a component that calls decorate Internal() from createDom().

  this.logInUsernameTB = goog.dom.getElement('logInUsernameTB');
  this.logInPasswordTB = goog.dom.getElement('logInPasswordTB');
  this.logInErrLb = goog.dom.getElement('logInErrLb');
  this.logInBtn = goog.dom.getElement('logInBtn');
  this.logInLoadingImg = goog.dom.getElement('logInLoadingImg');

  this.forgotBtn = goog.dom.getElement('forgotBtn');

  this.forgotPanel = goog.dom.getElement('forgotP');
  this.forgotResult = goog.dom.getElement('forgotResult');
  this.forgotForm = goog.dom.getElement('forgotForm');
  this.forgotUsernameErrLb = goog.dom.getElement('forgotUsernameErrLb');
  this.forgotUsernameTB = goog.dom.getElement('forgotUsernameTB');
  this.forgotOkBtn = goog.dom.getElement('forgotOkBtn');
  this.forgotCancelBtn = goog.dom.getElement('forgotCancelBtn');
  this.forgotLoadingImg = goog.dom.getElement('forgotLoadingImg');

  this.signUpUsernameTB = goog.dom.getElement('signUpUsernameTB');
  this.signUpEmailTB = goog.dom.getElement('signUpEmailTB');
  this.signUpPasswordTB = goog.dom.getElement('signUpPasswordTB');
  this.signUpRepeatPasswordTB = goog.dom.getElement('signUpRepeatPasswordTB');
  this.signUpUsernameErrLb = goog.dom.getElement('signUpUsernameErrLb');
  this.signUpEmailErrLb = goog.dom.getElement('signUpEmailErrLb');
  this.signUpPasswordErrLb = goog.dom.getElement('signUpPasswordErrLb');
  this.signUpRepeatPasswordErrLb = goog.dom.getElement(
      'signUpRepeatPasswordErrLb');
  this.signUpBtn = goog.dom.getElement('signUpBtn');
  this.signUpLoadingImg = goog.dom.getElement('signUpLoadingImg');
};


/** @inheritDoc */
wit.home.view.HomeView.prototype.enterDocument = function() {
  goog.base(this, 'enterDocument');

  this.getHandler().listen(this.logInBtn, goog.events.EventType.CLICK,
      function(e) {
        goog.style.setElementShown(this.logInBtn, false);
        goog.style.setElementShown(this.logInLoadingImg, true);

        var dataStore = wit.home.model.DataStore.getInstance();
        var log = new wit.base.model.Log();
        dataStore.logIn(
            this.logInUsernameTB.value,
            this.logInPasswordTB.value,
            log,
            goog.bind(this.logInCallback_, this));
      });

  this.getHandler().listen(
      this.signUpBtn,
      goog.events.EventType.CLICK,
      function(e) {
        goog.style.setElementShown(this.signUpBtn, false);
        goog.style.setElementShown(this.signUpLoadingImg, true);

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
      this.forgotBtn,
      goog.events.EventType.CLICK,
      function(e) {
        // Show forgot panel to let the user enters his/her username or email.
        if (!goog.dom.classes.has(this.forgotPanel,
            wit.home.view.HomeView.forgotExpandCss)) {
          // Reset all inputs
          goog.style.setElementShown(this.forgotResult, false);
          goog.style.setElementShown(this.forgotForm, true);

          this.forgotUsernameErrLb.innerHTML = wit.base.constants.htmlSpace;
          this.forgotUsernameTB.value = '';

          goog.dom.classes.add(this.forgotPanel,
              wit.home.view.HomeView.forgotExpandCss);
        }
      });

  this.getHandler().listen(
      this.forgotOkBtn,
      goog.events.EventType.CLICK,
      function(e) {
        // Hide the button, show the loading image.
        goog.style.setElementShown(this.forgotOkBtn, false);
        goog.style.setElementShown(this.forgotCancelBtn, false);
        goog.style.setElementShown(this.forgotLoadingImg, true);

        var dataStore = wit.home.model.DataStore.getInstance();
        var log = new wit.base.model.Log();
        dataStore.sendEmailResetPassword(
            this.forgotUsernameTB.value,
            log,
            goog.bind(this.sendEmailResetPasswordCallback_, this));
      });

  this.getHandler().listen(
      this.forgotCancelBtn,
      goog.events.EventType.CLICK,
      function(e) {
        goog.dom.classes.remove(this.forgotPanel,
            wit.home.view.HomeView.forgotExpandCss);
      });

  this.getHandler().listen(
      this.signUpUsernameTB,
      goog.events.EventType.BLUR,
      function(e) {
        var log = new wit.base.model.Log();
        wit.user.model.UserVerifier.isUsernameValid(
            this.signUpUsernameTB.value,
            log,
            goog.bind(this.validateUsernameCallback_, this));
      });

  this.getHandler().listen(
      this.signUpEmailTB,
      goog.events.EventType.BLUR,
      function(e) {
        var log = new wit.base.model.Log();
        wit.user.model.UserVerifier.isEmailValid(
            this.signUpEmailTB.value,
            log,
            goog.bind(this.validateEmailCallback_, this));
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
        this.validatePasswordCallback_(log);
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
        this.validateRepeatPasswordCallback_(log);
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

  // Show the button, hide the image.
  goog.style.setElementShown(this.logInBtn, true);
  goog.style.setElementShown(this.logInLoadingImg, false);
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

  // Show the button, hide the image.
  goog.style.setElementShown(this.signUpBtn, true);
  goog.style.setElementShown(this.signUpLoadingImg, false);
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
    goog.style.setElementShown(this.forgotResult, true);
    goog.style.setElementShown(this.forgotForm, false);
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

  // Show the button, hide the image.
  goog.style.setElementShown(this.forgotOkBtn, true);
  goog.style.setElementShown(this.forgotCancelBtn, true);
  goog.style.setElementShown(this.forgotLoadingImg, false);
};


/**
 * Callback function to update the results of validating username (sign up).
 * @param {wit.base.model.Log} log LogInfo array.
 * @this {wit.home.view.HomeView}
 * @private
 */
wit.home.view.HomeView.prototype.validateUsernameCallback_ = function(log) {
  var logInfo = log.getLogInfo(wit.base.constants.username, false);
  if (goog.isDef(logInfo)) {
    this.signUpUsernameErrLb.innerHTML = logInfo.msg;
  } else {
    this.signUpUsernameErrLb.innerHTML = wit.base.constants.htmlSpace;
  }
};


/**
 * Callback function to update the results of validating email (sign up).
 * @param {wit.base.model.Log} log LogInfo array.
 * @this {wit.home.view.HomeView}
 * @private
 */
wit.home.view.HomeView.prototype.validateEmailCallback_ = function(log) {
  var logInfo = log.getLogInfo(wit.base.constants.email, false);
  if (goog.isDef(logInfo)) {
    this.signUpEmailErrLb.innerHTML = logInfo.msg;
  } else {
    this.signUpEmailErrLb.innerHTML = wit.base.constants.htmlSpace;
  }
};


/**
 * Callback function to update the results of validating password (sign up).
 * @param {wit.base.model.Log} log LogInfo array.
 * @this {wit.home.view.HomeView}
 * @private
 */
wit.home.view.HomeView.prototype.validatePasswordCallback_ = function(log) {
  var logInfo = log.getLogInfo(wit.base.constants.password, false);
  if (goog.isDef(logInfo)) {
    this.signUpPasswordErrLb.innerHTML = logInfo.msg;
  } else {
    this.signUpPasswordErrLb.innerHTML = wit.base.constants.htmlSpace;
  }
};


/**
 * Callback function to update the results of
 *     validating repeat password (sign up).
 * @param {wit.base.model.Log} log LogInfo array.
 * @this {wit.home.view.HomeView}
 * @private
 */
wit.home.view.HomeView.prototype.validateRepeatPasswordCallback_ = function(
    log) {
  var logInfo = log.getLogInfo(wit.base.constants.repeatPassword, false);
  if (goog.isDef(logInfo)) {
    this.signUpRepeatPasswordErrLb.innerHTML = logInfo.msg;
  } else {
    this.signUpRepeatPasswordErrLb.innerHTML = wit.base.constants.htmlSpace;
  }
};
