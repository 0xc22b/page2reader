goog.provide('wit.user.model.helperTest');

goog.require('wit.user.model.helper');

goog.setTestOnly('wit.user.model.helperTest');

function setUp() {

}

function tearDown() {

}

function testGenUserContent() {
  assertEquals('{"username":"username","email":"email","password":"password",' +
      '"repeatPassword":"repeatPassword","newPassword":"newPassword"}',
      wit.user.model.helper.genUserContent('username', 'email', 'password',
      'repeatPassword', 'newPassword'));

  assertEquals('{"username":null,"email":"email","password":"password",' +
      '"repeatPassword":null,"newPassword":null}',
      wit.user.model.helper.genUserContent(undefined, 'email', 'password'));
}

function testGenResetPasswordContent() {
  assertEquals('{"ssid":"ssid1","fid":"sid1","password":"password1",' +
      '"repeatPassword":"repeatPassword1"}',
      wit.user.model.helper.genResetPasswordContent('ssid1', 'sid1',
      'password1', 'repeatPassword1'));
}
