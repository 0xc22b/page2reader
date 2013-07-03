goog.provide('wit.home.model.DataStoreTest');

goog.require('goog.testing.PropertyReplacer');

goog.require('wit.home.model.DataStore');

goog.setTestOnly('wit.home.model.DataStoreTest');


/** @type {goog.testing.PropertyReplacer} */
var stubs;

var reachedFinalContinuation = true;

var dataStore;

var log;

function setUp() {
  stubs = new goog.testing.PropertyReplacer();
  dataStore = new wit.home.model.DataStore();
  log = new wit.base.model.Log();
}

function tearDown() {

  assertTrue('The final continuation was not reached',
      reachedFinalContinuation);

  stubs.reset();
  log = undefined;
}


function testSignUp1() {
  dataStore.signUp('test@', 'asdf@d.c', 'asdfjkl;', 'asdfjk;l', log,
      function(log) {
        assertFalse(log.isValid());
      });

  dataStore.signUp('test', 'asdf', 'asdfjkl;', 'asdfjk;l', log,
      function(log) {
        assertFalse(log.isValid());
      });
}


function testSignUp2() {
  stubs.replace(wit.base.model.XhrService.prototype, 'doNoLoggedInPost',
      function(url, method, callback, opt_content) {
        var sLog = new wit.base.model.Log();
        sLog.addLogInfo('signUp', true);
        callback(sLog);
      }
  );

  reachedFinalContinuation = false;
  asyncTestCase.waitForAsync();

  dataStore.signUp('test', 'asdf@g.c', 'asdfjkl;', 'asdfjkl;', log,
      function(log) {
        asyncTestCase.continueTesting();
        assertTrue(log.isValid());
        reachedFinalContinuation = true;
      });
}


function testLogIn1() {
  dataStore.logIn('test@', 'asdfjkl;', log, function(log) {
    assertFalse(log.isValid());
  });

  dataStore.logIn('test', 'asdf', log, function(log) {
    assertFalse(log.isValid());
  });
}


function testLogIn2() {
  stubs.replace(wit.base.model.XhrService.prototype, 'doNoLoggedInPost',
      function(url, method, callback, opt_content) {
        var sLog = new wit.base.model.Log();
        sLog.addLogInfo('logIn', true);
        callback(sLog);
      }
  );

  reachedFinalContinuation = false;
  asyncTestCase.waitForAsync();

  dataStore.logIn('test', 'asdfjkl;', log, function(log) {
    asyncTestCase.continueTesting();
    assertTrue(log.isValid());
    reachedFinalContinuation = true;
  });
}


function testSendEmailResetPassword1() {
  dataStore.sendEmailResetPassword('te', log, function(log) {
    assertFalse(log.isValid());
  });

  dataStore.sendEmailResetPassword('test@i', log, function(log) {
    assertFalse(log.isValid());
  });
}


function testSendEmailResetPassword2() {
  stubs.replace(wit.base.model.XhrService.prototype, 'doNoLoggedInPost',
      function(url, method, callback, opt_content) {
        var sLog = new wit.base.model.Log();
        sLog.addLogInfo('sendEmailResetPassword', true);
        callback(sLog);
      }
  );

  reachedFinalContinuation = false;
  asyncTestCase.waitForAsync();

  dataStore.sendEmailResetPassword('test', log, function(log) {
    asyncTestCase.continueTesting();
    assertTrue(log.isValid());
    reachedFinalContinuation = true;
  });
}


function testResetPassword1() {
  dataStore.resetPassword('sSID', 'fID', 'asdfas', 'asdfas', log,
      function(log) {
        assertFalse(log.isValid());
      });

  dataStore.resetPassword('sSID', 'fID', 'asdfasdf', 'asdfjkl;', log,
      function(log) {
        assertFalse(log.isValid());
      });
}


function testResetPassword2() {
  stubs.replace(wit.base.model.XhrService.prototype, 'doNoLoggedInPost',
      function(url, method, callback, opt_content) {
        var sLog = new wit.base.model.Log();
        sLog.addLogInfo('resetPassword', true);
        callback(sLog);
      }
  );

  reachedFinalContinuation = false;
  asyncTestCase.waitForAsync();

  dataStore.resetPassword('sSID', 'fID', 'testtest', 'testtest', log,
      function(log) {
        asyncTestCase.continueTesting();
        assertTrue(log.isValid());
        reachedFinalContinuation = true;
      });
}


var asyncTestCase = goog.testing.AsyncTestCase.createAndInstall();
