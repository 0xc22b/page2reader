goog.provide('wit.page2reader.view.PageUrlView');

goog.require('goog.dom');
goog.require('goog.dom.classes');
goog.require('goog.dom.dataset');
goog.require('goog.events.Event');
goog.require('goog.soy');
goog.require('goog.style');
goog.require('goog.ui.Component');

goog.require('wit.base.constants');
goog.require('wit.fx.dom');
goog.require('wit.page2reader.PubSub');
goog.require('wit.page2reader.constants');
goog.require('wit.page2reader.model.DataStore');
goog.require('wit.page2reader.model.PageUrlObj');
goog.require('wit.page2reader.soy.p2r');



/**
 * @param {wit.page2reader.model.PageUrlObj=} opt_pageUrlObj Page URL object.
 * @param {boolean=} opt_animatedOnEnter Whether the view is animated
 *     when entering the document.
 * @param {goog.dom.DomHelper=} opt_domHelper Optional DOM hepler.
 * @constructor
 * @extends {goog.ui.Component}
 */
wit.page2reader.view.PageUrlView = function(opt_pageUrlObj, opt_animatedOnEnter,
    opt_domHelper) {
  goog.base(this, opt_domHelper);

  /**
   * @type {wit.page2reader.model.PageUrlObj|undefined}
   * @private
   */
  this.pageUrlObj_ = opt_pageUrlObj;

  /**
   * @type {boolean}
   * @private
   */
  this.animatedOnEnter_ = !!opt_animatedOnEnter;
};
goog.inherits(wit.page2reader.view.PageUrlView, goog.ui.Component);


/**
 * Constants for event names
 * @type {Object}
 */
wit.page2reader.view.PageUrlView.Events = {
  DEL: goog.events.getUniqueId('del')
};


/** @type {string} */
wit.page2reader.view.PageUrlView.CSS_CLASS = goog.getCssName('pageUrl');


/** @type {string} */
wit.page2reader.view.PageUrlView.MAIN_VIEW_CSS_CLASS =
    goog.getCssName('main-view');


/** @type {string} */
wit.page2reader.view.PageUrlView.RESEND_BTN_CSS_CLASS =
    goog.getCssName('resend-btn');


/** @type {string} */
wit.page2reader.view.PageUrlView.DEL_BTN_CSS_CLASS =
    goog.getCssName('del-btn');


/** @type {string} */
wit.page2reader.view.PageUrlView.DEL_CONFIRM_VIEW_CSS_CLASS =
    goog.getCssName('del-confirm-view');


/** @type {string} */
wit.page2reader.view.PageUrlView.DEL_CANCEL_BTN_CSS_CLASS =
    goog.getCssName('del-cancel-btn');


/** @type {string} */
wit.page2reader.view.PageUrlView.DEL_OK_BTN_CSS_CLASS =
    goog.getCssName('del-ok-btn');


/** @type {string} */
wit.page2reader.view.PageUrlView.DEL_MSG_VIEW_CSS_CLASS =
    goog.getCssName('del-msg-view');


/** @type {string} */
wit.page2reader.view.PageUrlView.DEL_MSG_CSS_CLASS =
    goog.getCssName('del-msg');


/** @type {string} */
wit.page2reader.view.PageUrlView.DEL_CLOSE_BTN_CSS_CLASS =
    goog.getCssName('del-close-btn');


/** @type {string} */
wit.page2reader.view.PageUrlView.DEL_RETRY_BTN_CSS_CLASS =
    goog.getCssName('del-retry-btn');


/** @type {string} */
wit.page2reader.view.PageUrlView.DEL_NO_RETRY_BTN_CSS_CLASS =
    goog.getCssName('del-no-retry-btn');


/** @type {string} */
wit.page2reader.view.PageUrlView.DEL_SHOW_MSG_BTN_CSS_CLASS =
    goog.getCssName('del-show-msg-btn');


/** @type {string} */
wit.page2reader.view.PageUrlView.ERR_CSS_CLASS =
    goog.getCssName('err');


/**
 * @type {Element}
 * @private
 */
wit.page2reader.view.PageUrlView.prototype.mainView_;


/**
 * @type {Element}
 * @private
 */
wit.page2reader.view.PageUrlView.prototype.resendBtn_;


/**
 * @type {Element}
 * @private
 */
wit.page2reader.view.PageUrlView.prototype.delBtn_;


/**
 * @type {Element}
 * @private
 */
wit.page2reader.view.PageUrlView.prototype.delConfirmView_;


/**
 * @type {Element}
 * @private
 */
wit.page2reader.view.PageUrlView.prototype.delCancelBtn_;


/**
 * @type {Element}
 * @private
 */
wit.page2reader.view.PageUrlView.prototype.delOkBtn_;


/**
 * @type {Element}
 * @private
 */
wit.page2reader.view.PageUrlView.prototype.delMsgView_;


/**
 * @type {Element}
 * @private
 */
wit.page2reader.view.PageUrlView.prototype.delMsg_;


/**
 * @type {Element}
 * @private
 */
wit.page2reader.view.PageUrlView.prototype.delCloseBtn_;


/**
 * @type {Element}
 * @private
 */
wit.page2reader.view.PageUrlView.prototype.delRetryBtn_;


/**
 * @type {Element}
 * @private
 */
wit.page2reader.view.PageUrlView.prototype.delNoRetryBtn_;


/**
 * @type {Element}
 * @private
 */
wit.page2reader.view.PageUrlView.prototype.delShowMsgBtn_;


/** @inheritDoc */
wit.page2reader.view.PageUrlView.prototype.createDom = function() {
  var element = goog.soy.renderAsElement(wit.page2reader.soy.p2r.pageUrlView,
      this.pageUrlObj_);
  this.setElementInternal(element);

  this.prepareDom_(element);
};


/** @inheritDoc */
wit.page2reader.view.PageUrlView.prototype.decorateInternal = function(
    element) {
  goog.base(this, 'decorateInternal', element);

  // Although decorateInternal(element) expects to be called with an element
  // that is already attached to the document and therefore may already leverage
  // methods such as getElementByFragment(), it must be careful not to make that
  // assumption for a component that calls decorateInternal() from createDom().

  this.pageUrlObj_ = new wit.page2reader.model.PageUrlObj();

  var keyString = goog.dom.dataset.get(element,
      wit.page2reader.constants.KEY_STRING);
  if (goog.isDefAndNotNull(keyString)) {
    this.pageUrlObj_.keyString = keyString;
  }

  this.prepareDom_(element);
};


/**
 * Make reference to some of element's children.
 * @param {Element} element Element whose children to be referred.
 * @private
 */
wit.page2reader.view.PageUrlView.prototype.prepareDom_ = function(element) {

  this.mainView_ = goog.dom.getElementsByTagNameAndClass('div',
      wit.page2reader.view.PageUrlView.MAIN_VIEW_CSS_CLASS, element)[0];

  this.resendBtn_ = goog.dom.getElementsByTagNameAndClass('button',
      wit.page2reader.view.PageUrlView.RESEND_BTN_CSS_CLASS, element)[0];
  this.delBtn_ = goog.dom.getElementsByTagNameAndClass('button',
      wit.page2reader.view.PageUrlView.DEL_BTN_CSS_CLASS, element)[0];

  this.delConfirmView_ = goog.dom.getElementsByTagNameAndClass('div',
      wit.page2reader.view.PageUrlView.DEL_CONFIRM_VIEW_CSS_CLASS, element)[0];
  this.delCancelBtn_ = goog.dom.getElementsByTagNameAndClass('button',
      wit.page2reader.view.PageUrlView.DEL_CANCEL_BTN_CSS_CLASS, element)[0];
  this.delOkBtn_ = goog.dom.getElementsByTagNameAndClass('button',
      wit.page2reader.view.PageUrlView.DEL_OK_BTN_CSS_CLASS, element)[0];

  this.delMsgView_ = goog.dom.getElementsByTagNameAndClass('div',
      wit.page2reader.view.PageUrlView.DEL_MSG_VIEW_CSS_CLASS, element)[0];
  this.delMsg_ = goog.dom.getElementsByTagNameAndClass('p',
      wit.page2reader.view.PageUrlView.DEL_MSG_CSS_CLASS, element)[0];
  this.delCloseBtn_ = goog.dom.getElementsByTagNameAndClass('button',
      wit.page2reader.view.PageUrlView.DEL_CLOSE_BTN_CSS_CLASS, element)[0];
  this.delRetryBtn_ = goog.dom.getElementsByTagNameAndClass('button',
      wit.page2reader.view.PageUrlView.DEL_RETRY_BTN_CSS_CLASS, element)[0];
  this.delNoRetryBtn_ = goog.dom.getElementsByTagNameAndClass('button',
      wit.page2reader.view.PageUrlView.DEL_NO_RETRY_BTN_CSS_CLASS, element)[0];
  this.delShowMsgBtn_ = goog.dom.getElementsByTagNameAndClass('button',
      wit.page2reader.view.PageUrlView.DEL_SHOW_MSG_BTN_CSS_CLASS, element)[0];

  if (this.animatedOnEnter_) {
    goog.style.setElementShown(element, false);
  }
};


/** @inheritDoc */
wit.page2reader.view.PageUrlView.prototype.enterDocument = function() {
  goog.base(this, 'enterDocument');

  this.getHandler().listen(this.resendBtn_, goog.events.EventType.CLICK,
      function(e) {
        goog.dom.setProperties(this.resendBtn_, {'disabled': true});

        var dataStore = wit.page2reader.model.DataStore.getInstance();
        var log = new wit.base.model.Log();
        dataStore.resendToReader(
            this.pageUrlObj_.keyString,
            log,
            goog.bind(this.resendToReaderCallback_, this));
      });

  this.getHandler().listen(this.delBtn_, goog.events.EventType.CLICK,
      function(e) {
        wit.fx.dom.animate(this.delConfirmView_,
            { 'height': 'toggle',
              'padding-top': 'toggle',
              'padding-bottom': 'toggle' },
            250);
      });

  this.getHandler().listen(this.delCancelBtn_, goog.events.EventType.CLICK,
      function(e) {
        wit.fx.dom.animate(this.delConfirmView_,
            {'height': 'hide',
              'padding-top': 'hide',
              'padding-bottom': 'hide'},
            250);
      });

  this.getHandler().listen(this.delOkBtn_, goog.events.EventType.CLICK,
      function(e) {
        goog.dom.setProperties(this.resendBtn_, {'disabled': true});
        goog.dom.setProperties(this.delBtn_, {'disabled': true});

        goog.dom.setTextContent(this.delMsg_,
            wit.page2reader.constants.DELETING);
        goog.dom.classes.remove(this.delMsg_,
            wit.page2reader.view.PageUrlView.ERR_CSS_CLASS);
        goog.style.setElementShown(this.delCloseBtn_, false);
        goog.style.setElementShown(this.delRetryBtn_, false);
        goog.style.setElementShown(this.delNoRetryBtn_, false);
        goog.style.setElementShown(this.delShowMsgBtn_, false);

        wit.page2reader.PubSub.getInstance().publish(
            wit.page2reader.constants.ADD_WORKING);

        // Caveat: could not use helper functions of wit.fx.dom.animate
        //     because it can be used once at a time!

        // Animate scrollTop if needed
        var documentScrollTop = goog.dom.getDocumentScroll().y;
        var elemPageOffsetTop = goog.style.getPageOffsetTop(this.getElement());
        if (elemPageOffsetTop - documentScrollTop < 0) {
          var scrollTo = documentScrollTop -
              (documentScrollTop - elemPageOffsetTop + 40);

          var scrollElem = goog.dom.getDocumentScrollElement();

          var anim0 = new goog.fx.Animation(
              [documentScrollTop],
              [scrollTo],
              250);
          goog.events.listen(
              anim0,
              [goog.fx.Transition.EventType.BEGIN,
                goog.fx.Animation.EventType.ANIMATE,
                goog.fx.Transition.EventType.END],
              function(e) {
                scrollElem.scrollTop = e.x;
              },
              false,
              this);
          goog.events.listen(
              anim0,
              goog.fx.Transition.EventType.END,
              function() {
                goog.events.removeAll(anim0);
              },
              false,
              this);
          anim0.play(false);
        }

        var anim1 = new wit.fx.dom.Animation(
            this.mainView_,
            {'height': 'hide',
              'padding-top': 'hide',
              'padding-bottom': 'hide'},
            250);
        goog.events.listen(
            anim1,
            goog.fx.Transition.EventType.END,
            function() {
              goog.events.removeAll(anim1);
            },
            false,
            this);
        anim1.play(false);

        var anim2 = new wit.fx.dom.Animation(
            this.delMsgView_,
            {'height': 'show',
              'padding-top': 'show',
              'padding-bottom': 'show'},
            250);
        goog.events.listen(
            anim2,
            goog.fx.Transition.EventType.END,
            function() {
              goog.events.removeAll(anim2);

              goog.style.setElementShown(this.delConfirmView_, false);

              var dataStore = wit.page2reader.model.DataStore.getInstance();
              var log = new wit.base.model.Log();
              dataStore.deletePageUrl(
                  this.pageUrlObj_.keyString,
                  log,
                  goog.bind(this.deletePageUrlCallback_, this));
            },
            false,
            this);
        anim2.play(false);
      });

  this.getHandler().listen(this.delCloseBtn_, goog.events.EventType.CLICK,
      function(e) {
        wit.fx.dom.animate(
            this.getElement(),
            {'height': 'hide',
              'padding-top': 'hide',
              'padding-bottom': 'hide'},
            250,
            undefined,
            function() {
              var event = new goog.events.Event(
                  wit.page2reader.view.PageUrlView.Events.DEL,
                  this);
              this.dispatchEvent(event);
            },
            this);
      });

  this.getHandler().listen(this.delRetryBtn_, goog.events.EventType.CLICK,
      function(e) {
        goog.dom.setTextContent(this.delMsg_,
            wit.page2reader.constants.DELETING);
        goog.dom.classes.remove(this.delMsg_,
            wit.page2reader.view.PageUrlView.ERR_CSS_CLASS);
        goog.style.setElementShown(this.delRetryBtn_, false);
        goog.style.setElementShown(this.delNoRetryBtn_, false);
        goog.style.setElementShown(this.delShowMsgBtn_, false);

        wit.page2reader.PubSub.getInstance().publish(
            wit.page2reader.constants.ADD_WORKING);
        wit.page2reader.PubSub.getInstance().publish(
            wit.page2reader.constants.MINUS_ERROR);

        var dataStore = wit.page2reader.model.DataStore.getInstance();
        var log = new wit.base.model.Log();
        dataStore.deletePageUrl(
            this.pageUrlObj_.keyString,
            log,
            goog.bind(this.deletePageUrlCallback_, this));
      });

  this.getHandler().listen(this.delNoRetryBtn_, goog.events.EventType.CLICK,
      function(e) {

        wit.page2reader.PubSub.getInstance().publish(
            wit.page2reader.constants.MINUS_ERROR);

        // Caveat: could not use helper functions of wit.fx.dom.animate
        //     because it can be used once at a time!
        var anim1 = new wit.fx.dom.Animation(
            this.mainView_,
            {'height': 'show',
              'padding-top': 'show',
              'padding-bottom': 'show'},
            250);
        goog.events.listen(
            anim1,
            goog.fx.Transition.EventType.END,
            function() {
              goog.events.removeAll(anim1);
            },
            false,
            this);
        anim1.play(false);

        var anim2 = new wit.fx.dom.Animation(
            this.delMsgView_,
            {'height': 'hide',
              'padding-top': 'hide',
              'padding-bottom': 'hide'},
            250);
        goog.events.listen(
            anim2,
            goog.fx.Transition.EventType.END,
            function() {
              goog.events.removeAll(anim2);
            },
            false,
            this);
        anim2.play(false);
      });

  this.getHandler().listen(this.delShowMsgBtn_, goog.events.EventType.CLICK,
      function(e) {
        window.alert(goog.dom.getTextContent(this.delMsg_));
      });

  if (this.animatedOnEnter_) {
    wit.fx.dom.animate(this.getElement(),
        {'height': 'show',
          'padding-top': 'show',
          'padding-bottom': 'show'},
        250);
  }
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
 * Whether deletion ended up in error.
 * @return {boolean}
 */
wit.page2reader.view.PageUrlView.prototype.didDelGetError = function() {
  return goog.style.isElementShown(this.delMsgView_) &&
      goog.dom.classes.has(
          this.delMsg_,
          wit.page2reader.view.PageUrlView.ERR_CSS_CLASS);
};


/**
 * Callback function to update the results of resending to reader.
 * @param {wit.base.model.Log} log LogInfo array.
 * @this {wit.page2reader.view.PageUrlView}
 * @private
 */
wit.page2reader.view.PageUrlView.prototype.resendToReaderCallback_ = function(
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

  logInfo = log.getLogInfo(wit.page2reader.constants.SEND_TO_READER, true);
  if (goog.isDef(logInfo)) {
    this.resendBtn_.innerHTML = 'Sent';
  } else {
    // Show errors
    this.resendBtn_.innerHTML = 'Couldn\'t resend. Retry?';
    goog.dom.setProperties(this.resendBtn_, {'disabled': false});
  }
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
    goog.dom.setTextContent(this.delMsg_,
        wit.page2reader.constants.ERR);
    goog.dom.classes.add(this.delMsg_,
        wit.page2reader.view.PageUrlView.ERR_CSS_CLASS);
    goog.style.setElementShown(this.delCloseBtn_, false);
    goog.style.setElementShown(this.delRetryBtn_, true);
    goog.style.setElementShown(this.delNoRetryBtn_, true);
    goog.style.setElementShown(this.delShowMsgBtn_, true);

    wit.page2reader.PubSub.getInstance().publish(
        wit.page2reader.constants.ADD_ERROR);
  }

  if (goog.isDef(log.getLogInfo(wit.base.constants.didLogIn, false))) {
    goog.dom.setTextContent(this.delMsg_,
        wit.page2reader.constants.ERR_NOT_LOGGED_IN);
    goog.dom.classes.add(this.delMsg_,
        wit.page2reader.view.PageUrlView.ERR_CSS_CLASS);
    goog.style.setElementShown(this.delCloseBtn_, false);
    goog.style.setElementShown(this.delRetryBtn_, true);
    goog.style.setElementShown(this.delNoRetryBtn_, true);
    goog.style.setElementShown(this.delShowMsgBtn_, true);

    wit.page2reader.PubSub.getInstance().publish(
        wit.page2reader.constants.ADD_ERROR);
  }

  goog.dom.setProperties(this.resendBtn_, {'disabled': false});
  goog.dom.setProperties(this.delBtn_, {'disabled': false});

  wit.page2reader.PubSub.getInstance().publish(
      wit.page2reader.constants.MINUS_WORKING);

  logInfo = log.getLogInfo(wit.page2reader.constants.DELETE_PAGE_URL, false);
  if (goog.isDef(logInfo)) {
    goog.dom.setTextContent(this.delMsg_,
        wit.page2reader.constants.ERR);
    goog.dom.classes.add(this.delMsg_,
        wit.page2reader.view.PageUrlView.ERR_CSS_CLASS);
    goog.style.setElementShown(this.delCloseBtn_, false);
    goog.style.setElementShown(this.delRetryBtn_, true);
    goog.style.setElementShown(this.delNoRetryBtn_, true);
    goog.style.setElementShown(this.delShowMsgBtn_, true);

    wit.page2reader.PubSub.getInstance().publish(
        wit.page2reader.constants.ADD_ERROR);
  }

  logInfo = log.getLogInfo(wit.page2reader.constants.DELETE_PAGE_URL, true);
  if (goog.isDef(logInfo)) {
    goog.dom.setTextContent(this.delMsg_,
        wit.page2reader.constants.DELETED);
    goog.dom.classes.remove(this.delMsg_,
        wit.page2reader.view.PageUrlView.ERR_CSS_CLASS);
    goog.style.setElementShown(this.delCloseBtn_, true);
    goog.style.setElementShown(this.delRetryBtn_, false);
    goog.style.setElementShown(this.delNoRetryBtn_, false);
    goog.style.setElementShown(this.delShowMsgBtn_, false);
  }
};
