goog.provide('wit.user.model.DataStoreTest');

goog.require('goog.testing.PropertyReplacer');

goog.require('wit.user.model.DataStore');

goog.setTestOnly('wit.user.model.DataStoreTest');


/** @type {goog.testing.PropertyReplacer} */
var stubs;

var reachedFinalContinuation = true;

var dataStore;

var log;

function setUp() {
  stubs = new goog.testing.PropertyReplacer();
  dataStore = new wit.user.model.DataStore();
  log = new wit.base.model.Log();
}

function tearDown() {

  assertTrue('The final continuation was not reached',
      reachedFinalContinuation);

  stubs.reset();
  log = undefined;
}


function testChangeUsername1() {
  dataStore.changeUsername('test@', 'asdfjkl;', log, function(log) {
    assertFalse(log.isValid());
  });

  dataStore.changeUsername('test', 'asdf', log, function(log) {
    assertFalse(log.isValid());
  });
}


function testChangeUsername2() {
  stubs.replace(wit.base.model.XhrService.prototype, 'doPost',
      function(url, method, callback, opt_content) {
        var sLog = new wit.base.model.Log();
        sLog.addLogInfo('changeUsername', true);
        callback(sLog);
      }
  );

  reachedFinalContinuation = false;
  asyncTestCase.waitForAsync();

  dataStore.changeUsername('test', 'asdfjkl;', log, function(log) {
    asyncTestCase.continueTesting();
    assertTrue(log.isValid());
    reachedFinalContinuation = true;
  });
}


function testChangeEmail1() {
  dataStore.changeEmail('test@', 'asdfjkl;', log, function(log) {
    assertFalse(log.isValid());
  });

  dataStore.changeEmail('test@mail.com', 'asdf', log, function(log) {
    assertFalse(log.isValid());
  });
}


function testChangeEmail2() {
  stubs.replace(wit.base.model.XhrService.prototype, 'doPost',
      function(url, method, callback, opt_content) {
        var sLog = new wit.base.model.Log();
        sLog.addLogInfo('changeEmail', true);
        callback(sLog);
      }
  );

  reachedFinalContinuation = false;
  asyncTestCase.waitForAsync();

  dataStore.changeEmail('test@mail.com', 'asdfjkl;', log, function(log) {
    asyncTestCase.continueTesting();
    assertTrue(log.isValid());
    reachedFinalContinuation = true;
  });
}


function testResendEmailConfirm() {
  stubs.replace(wit.base.model.XhrService.prototype, 'doPost',
      function(url, method, callback, opt_content) {
        var sLog = new wit.base.model.Log();
        sLog.addLogInfo('resendEmailConfirm', true);
        callback(sLog);
      }
  );

  reachedFinalContinuation = false;
  asyncTestCase.waitForAsync();

  dataStore.resendEmailConfirm(log, function(log) {
    asyncTestCase.continueTesting();
    assertTrue(log.isValid());
    reachedFinalContinuation = true;
  });
}


function testChangePassword1() {
  dataStore.changePassword('test@', 'asdfjkl;', 'asdfklss', log,
      function(log) {
        assertFalse(log.isValid());
      });

  dataStore.changePassword('asdfqwer', 'asdfsdas', 'asdfjkl;', log,
      function(log) {
        assertFalse(log.isValid());
      });
}


function testChangePassword2() {
  stubs.replace(wit.base.model.XhrService.prototype, 'doPost',
      function(url, method, callback, opt_content) {
        var sLog = new wit.base.model.Log();
        sLog.addLogInfo('changePassword', true);
        callback(sLog);
      }
  );

  reachedFinalContinuation = false;
  asyncTestCase.waitForAsync();

  dataStore.changePassword('testtest', 'testtest', 'asdfjkl;', log,
      function(log) {
        asyncTestCase.continueTesting();
        assertTrue(log.isValid());
        reachedFinalContinuation = true;
      });
}


function testDeleteAccount1() {
  dataStore.deleteAccount('asdf', log,
      function(log) {
        assertFalse(log.isValid());
      });
}


function testDeleteAccount2() {
  stubs.replace(wit.base.model.XhrService.prototype, 'doPost',
      function(url, method, callback, opt_content) {
        var sLog = new wit.base.model.Log();
        sLog.addLogInfo('deleteAccount', true);
        callback(sLog);
      }
  );

  reachedFinalContinuation = false;
  asyncTestCase.waitForAsync();

  dataStore.deleteAccount('testtest', log,
      function(log) {
        asyncTestCase.continueTesting();
        assertTrue(log.isValid());
        reachedFinalContinuation = true;
      });
}


var asyncTestCase = goog.testing.AsyncTestCase.createAndInstall();
