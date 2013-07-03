goog.provide('wit.base.model.XhrserviceTest');

goog.require('wit.base.model.XhrService');

goog.setTestOnly('wit.base.model.XhrserviceTest');

var xhrService;

function setUp() {
  xhrService = new wit.base.model.XhrService();
}

function tearDown() {

}

function testGetRequestID_() {
  assertEquals('entry-0', xhrService.getRequestID_());
  assertEquals('entry-1', xhrService.getRequestID_());
  assertEquals('entry-2', xhrService.getRequestID_());
}

function testGenPostParams_() {
  assertEquals('ssid=ssID&sid=sID&method=method', xhrService.genPostParams_(
      'ssID', 'sID', 'method'));

  assertEquals('ssid=asdf&sid=jkll&method=getUName&content=myname',
               xhrService.genPostParams_('asdf', 'jkll', 'getUName', 'myname'));
}

function testNoLoggedInPostParams_() {
  assertEquals('method=method', xhrService.genNoLoggedInPostParams_(
      'method'));

  assertEquals('method=getUName&content=myname',
               xhrService.genNoLoggedInPostParams_('getUName', 'myname'));
}
