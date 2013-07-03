goog.provide('wit.user.view.UserViewTest');

goog.require('wit.user.view.UserView');

goog.setTestOnly('wit.user.view.UserViewTest');


var userView, log;


function setUp() {
  userView = new wit.user.view.UserView();
  userView.decorate(document.body);

  log = new wit.base.model.Log();
}


function tearDown() {
  goog.dispose(userView);
  userView = null;

  log = null;
}


function testChangeUsernameCallback() {

  log.addLogInfo(wit.base.constants.changeUsername, false, 'wit',
      'this is a test.');

  userView.changeUsernameCallback(log);

  assertEquals('this is a test.', userView.changeUsernameErrLb_.innerHTML);
}


function testChangeEmailCallback() {

  log.addLogInfo(wit.base.constants.changeEmail, false, 'wit',
      'this is a test.');

  userView.changeEmailCallback(log);

  assertEquals('this is a test.', userView.changeEmailErrLb_.innerHTML);
}


function testResendEmailConfirmCallback() {

  log.addLogInfo(wit.base.constants.resendEmailConfirm, false, 'wit',
      'this is a test.');

  userView.resendEmailConfirmCallback(log);

  assertEquals('Couldn\'t send the email. Retry?',
      userView.resendEmailConfirmBtn_.innerHTML);
}


function testChangePasswordCallback() {

  log.addLogInfo(wit.base.constants.changePassword, false, 'wit',
      'this is a test.');

  userView.changePasswordCallback(log);

  assertEquals('this is a test.', userView.changeNewPasswordErrLb_.innerHTML);
}


function testDeleteAccountCallback() {

  log.addLogInfo(wit.base.constants.deleteAccount, false, 'wit',
      'this is a test.');

  userView.deleteAccountCallback(log);

  assertEquals('this is a test.',
      userView.deleteAccountPasswordErrLb_.innerHTML);
}
