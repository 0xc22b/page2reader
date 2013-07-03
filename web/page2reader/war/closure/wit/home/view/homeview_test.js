goog.provide('wit.home.view.HomeViewTest');

goog.require('wit.home.view.HomeView');

goog.setTestOnly('wit.home.view.HomeViewTest');


var homeView, log;


function setUp() {
  homeView = new wit.home.view.HomeView();
  homeView.decorate(document.body);

  log = new wit.base.model.Log();
}


function tearDown() {
  goog.dispose(homeView);
  homeView = null;

  log = null;
}


function testLogInCallback() {

  log.addLogInfo(wit.base.constants.logIn, false, 'wit', 'this is a test.');

  homeView.logInCallback(log);

  assertEquals('this is a test.', homeView.logInErrLb_.innerHTML);
}


function testSignUpCallback() {

  log.addLogInfo(wit.base.constants.signUp, false, 'wit', 'this is a test.');

  homeView.signUpCallback(log);

  assertEquals('this is a test.', homeView.signUpUsernameErrLb_.innerHTML);
}


function testSendEmailResetPasswordCallback() {

  log.addLogInfo(wit.base.constants.sendEmailResetPassword, false, 'wit',
      'this is a test.');

  homeView.sendEmailResetPasswordCallback(log);

  assertEquals('this is a test.', homeView.forgotUsernameErrLb_.innerHTML);
}


function testValidateUsernameCallback() {

  log.addLogInfo(wit.base.constants.username, false, 'wit',
      'this is a test.');

  homeView.validateUsernameCallback(log);

  assertEquals('this is a test.', homeView.signUpUsernameErrLb_.innerHTML);
}


function testValidateEmailCallback() {

  log.addLogInfo(wit.base.constants.email, false, 'wit',
      'this is a test.');

  homeView.validateEmailCallback(log);

  assertEquals('this is a test.', homeView.signUpEmailErrLb_.innerHTML);
}


function testValidatePasswordCallback() {

  log.addLogInfo(wit.base.constants.password, false, 'wit',
      'this is a test.');

  homeView.validatePasswordCallback(log);

  assertEquals('this is a test.', homeView.signUpPasswordErrLb_.innerHTML);
}


function testValidateRepeatPasswordCallback() {

  log.addLogInfo(wit.base.constants.repeatPassword, false, 'wit',
      'this is a test.');

  homeView.validateRepeatPasswordCallback(log);

  assertEquals('this is a test.',
      homeView.signUpRepeatPasswordErrLb_.innerHTML);
}
