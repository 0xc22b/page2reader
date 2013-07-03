goog.provide('wit.user.model.UserverifierTest');

goog.require('goog.testing.PropertyReplacer');

goog.require('wit.user.model.UserVerifier');

goog.setTestOnly('wit.user.model.UserverifierTest');


/** @type {goog.testing.PropertyReplacer} */
var stubs;

var reachedFinalContinuation = true;

var log;

var logInfo;

var e;

function setUpPage() {

}

function setUp() {
  stubs = new goog.testing.PropertyReplacer();
  log = new wit.base.model.Log();
}

function tearDown() {

  assertTrue('The final continuation was not reached',
      reachedFinalContinuation);

  stubs.reset();
  log = undefined;
}

function testIsUsernameValid1() {

  e = assertThrows(function() {
    wit.user.model.UserVerifier.isUsernameValid(undefined, log);
  });
  assertEquals('UsernameValidation Error: Username can not be undefined or ' +
      'null but it is undefined', e.message);

  e = assertThrows(function() {
    wit.user.model.UserVerifier.isUsernameValid(null, log);
  });
  assertEquals('UsernameValidation Error: Username can not be undefined or ' +
      'null but it is null', e.message);

  assertFalse(wit.user.model.UserVerifier.isUsernameValid('', log));
  assertTrue(wit.user.model.UserVerifier.isUsernameValid('wit', log));
  assertTrue(wit.user.model.UserVerifier.isUsernameValid('witTestWit', log));
  assertFalse(wit.user.model.UserVerifier.isUsernameValid('test test', log));
  assertFalse(wit.user.model.UserVerifier.isUsernameValid('test@test', log));
  assertFalse(wit.user.model.UserVerifier.isUsernameValid('test(test', log));
  assertTrue(wit.user.model.UserVerifier.isUsernameValid('test_test', log));
  assertFalse(wit.user.model.UserVerifier.isUsernameValid('test.test', log));
}


function testIsUsernameValid2() {

  stubs.replace(wit.base.model.XhrService.prototype, 'doNoLoggedInPost',
      function(url, method, callback, opt_content) {
        var sLog = new wit.base.model.Log();
        sLog.addLogInfo('username', true);
        callback(sLog);
      }
  );

  reachedFinalContinuation = false;
  asyncTestCase.waitForAsync();

  wit.user.model.UserVerifier.isUsernameValid('wit', log,
      function(log) {
        asyncTestCase.continueTesting();
        assertTrue(log.isValid());
        reachedFinalContinuation = true;
      }
  );

  stubs.replace(wit.base.model.XhrService.prototype, 'doNoLoggedInPost',
      function(url, method, callback, opt_content) {
        var sLog = new wit.base.model.Log();
        sLog.addLogInfo('username', false, 'wit', 'Username is duplicate');
        callback(sLog);
      }
  );

  reachedFinalContinuation = false;
  asyncTestCase.waitForAsync();

  wit.user.model.UserVerifier.isUsernameValid('wit', log,
      function(log) {
        asyncTestCase.continueTesting();
        assertFalse(log.isValid());
        var logInfo = log.getLogInfo('username');
        assertEquals('username', logInfo.type);
        assertEquals(false, logInfo.isValid);
        assertEquals('wit', logInfo.value);
        assertEquals('Username is duplicate', logInfo.msg);
        reachedFinalContinuation = true;
      }
  );
}


function testIsEmailValid1() {
  e = assertThrows(function() {
    wit.user.model.UserVerifier.isEmailValid(undefined, log);
  });
  assertEquals('EmailValidation Error: Email can not be undefined or ' +
      'null but it is undefined', e.message);

  e = assertThrows(function() {
    wit.user.model.UserVerifier.isEmailValid(null, log);
  });
  assertEquals('EmailValidation Error: Email can not be undefined or ' +
      'null but it is null', e.message);

  assertFalse(wit.user.model.UserVerifier.isEmailValid('', log));
  assertTrue(wit.user.model.UserVerifier.isEmailValid('wit@mail.com', log));
  assertTrue(wit.user.model.UserVerifier.isEmailValid('witte@m.co.th', log));
  assertTrue(wit.user.model.UserVerifier.isEmailValid('test_test@t.c', log));

  assertFalse(wit.user.model.UserVerifier.isEmailValid('test test@ma.c', log));
  assertFalse(wit.user.model.UserVerifier.isEmailValid('witTe@m.co.th', log));
  assertFalse(wit.user.model.UserVerifier.isEmailValid('test@test@t.c', log));
  assertFalse(wit.user.model.UserVerifier.isEmailValid('test(test@m.c', log));
  assertTrue(wit.user.model.UserVerifier.isEmailValid('test.test@t.c', log));
  assertTrue(wit.user.model.UserVerifier.isEmailValid('test233@t.c', log));
}


function testIsEmailValid2() {

  stubs.replace(wit.base.model.XhrService.prototype, 'doNoLoggedInPost',
      function(url, method, callback, opt_content) {
        var sLog = new wit.base.model.Log();
        sLog.addLogInfo('email', true);
        callback(sLog);
      }
  );

  reachedFinalContinuation = false;
  asyncTestCase.waitForAsync();

  wit.user.model.UserVerifier.isEmailValid('wit@mail.com', log,
      function(log) {
        asyncTestCase.continueTesting();
        assertTrue(log.isValid());
        reachedFinalContinuation = true;
      }
  );

  stubs.replace(wit.base.model.XhrService.prototype, 'doNoLoggedInPost',
      function(url, method, callback, opt_content) {
        var sLog = new wit.base.model.Log();
        sLog.addLogInfo('email', false, 'wit@mail.com', 'Email is duplicate');
        callback(sLog);
      }
  );

  reachedFinalContinuation = false;
  asyncTestCase.waitForAsync();

  wit.user.model.UserVerifier.isEmailValid('wit@mail.com', log,
      function(log) {
        asyncTestCase.continueTesting();
        assertFalse(log.isValid());
        var logInfo = log.getLogInfo('email');
        assertEquals('email', logInfo.type);
        assertEquals(false, logInfo.isValid);
        assertEquals('wit@mail.com', logInfo.value);
        assertEquals('Email is duplicate', logInfo.msg);
        reachedFinalContinuation = true;
      }
  );
}


function testIsPasswordValid1() {

  e = assertThrows(function() {
    wit.user.model.UserVerifier.isPasswordValid(undefined, log);
  });
  assertEquals('PasswordValidation Error: Password can not be undefined or ' +
      'null but it is undefined', e.message);

  e = assertThrows(function() {
    wit.user.model.UserVerifier.isPasswordValid(null, log);
  });
  assertEquals('PasswordValidation Error: Password can not be undefined or ' +
      'null but it is null', e.message);

  assertFalse(wit.user.model.UserVerifier.isPasswordValid('', log,
      'password'));
  assertFalse(wit.user.model.UserVerifier.isPasswordValid('asdf', log,
      'password'));
  assertFalse(wit.user.model.UserVerifier.isPasswordValid('asdf tf', log,
      'password'));
}


function testIsPasswordValid2() {
  assertTrue(wit.user.model.UserVerifier.isPasswordValid('asdftfe', log,
      'password1'));
  logInfo = log.getLogInfo('password1');
  assertEquals('password1', logInfo.type);
  assertEquals(true, logInfo.isValid);

  assertFalse(wit.user.model.UserVerifier.isPasswordValid('asdf', log,
      'password2'));
  logInfo = log.getLogInfo('password2');
  assertEquals('password2', logInfo.type);
  assertEquals(false, logInfo.isValid);
}


function testIsRepeatPasswordValid() {

  e = assertThrows(function() {
    wit.user.model.UserVerifier.isRepeatPasswordValid(undefined, 'tek', log);
  });
  assertEquals('RepeatPasswordValidation Error: Password and repeatPassword ' +
      'can not be undefined or null but it is undefined, tek', e.message);

  e = assertThrows(function() {
    wit.user.model.UserVerifier.isRepeatPasswordValid('asdfkj;ij', null, log);
  });
  assertEquals('RepeatPasswordValidation Error: Password and repeatPassword ' +
      'can not be undefined or null but it is asdfkj;ij, null', e.message);

  assertFalse(wit.user.model.UserVerifier.isRepeatPasswordValid('asdftfe',
      'asdef', log));

  assertFalse(wit.user.model.UserVerifier.isRepeatPasswordValid('asdftfe',
      'asdeftfe', log));
}


var asyncTestCase = goog.testing.AsyncTestCase.createAndInstall();
