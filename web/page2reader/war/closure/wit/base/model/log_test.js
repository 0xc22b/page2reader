goog.provide('wit.base.model.LogTest');

goog.require('wit.base.model.Log');

goog.setTestOnly('wit.base.model.LogTest');

var log;

function setUp() {
  log = new wit.base.model.Log();
}

function tearDown() {

}

function testAddLogInfos() {
  var jsonString = '[{\'type\': \'username\', \'isValid\':true},' +
      '{\'type\': \'password\', \'isValid\':false, \'value\':\'asdf\',' +
      '\'msg\':\'this is a test.\'}]';
  log.addLogInfos(jsonString);

  assertFalse(log.isValid());

  var logInfo = log.getLogInfo('username');
  assertEquals('username', logInfo.type);
  assertEquals(true, logInfo.isValid);
  assertEquals(undefined, logInfo.value);
  assertEquals(undefined, logInfo.msg);

  logInfo = log.getLogInfo('password');
  assertEquals('password', logInfo.type);
  assertEquals(false, logInfo.isValid);
  assertEquals('asdf', logInfo.value);
  assertEquals('this is a test.', logInfo.msg);
}

function testAddLogInfo() {

  log.addLogInfo('type', true, 'value', 'message');

  assertTrue(log.isValid());

  var logInfo = log.getLogInfo('type');
  assertEquals('type', logInfo.type);
  assertEquals(true, logInfo.isValid);
  assertEquals('value', logInfo.value);
  assertEquals('message', logInfo.msg);
}

function testIsValid() {

  log.addLogInfo('type1', true, 'value1', 'message1');

  assertTrue(log.isValid());

  log.addLogInfo('type2', true, 'value2', 'message2');

  assertTrue(log.isValid());

  log.addLogInfo('type3', true, 'value3', 'message3');

  assertTrue(log.isValid());

  log.addLogInfo('type4', false, 'value4', 'message4');

  assertFalse(log.isValid());

  log.addLogInfo('type5', true, 'value5', 'message5');

  assertFalse(log.isValid());
}

function testGetLogInfo() {
  log.addLogInfo('type1', true, 'value1', 'message1');
  log.addLogInfo('type2', true, 'value2', 'message2');
  log.addLogInfo('type3', true, 'value3', 'message3');
  log.addLogInfo('type3', false, 'value4', 'message4');
  log.addLogInfo('type3', true, 'value5', 'message5');

  var logInfo = log.getLogInfo('type1');
  assertEquals('type1', logInfo.type);
  assertEquals(true, logInfo.isValid);
  assertEquals('value1', logInfo.value);
  assertEquals('message1', logInfo.msg);

  logInfo = log.getLogInfo('type2', false);
  assertEquals(undefined, logInfo);

  logInfo = log.getLogInfo('type2', true);
  assertEquals('type2', logInfo.type);
  assertEquals(true, logInfo.isValid);
  assertEquals('value2', logInfo.value);
  assertEquals('message2', logInfo.msg);

  logInfo = log.getLogInfo('type3', true);
  assertEquals('type3', logInfo.type);
  assertEquals(true, logInfo.isValid);
  assertEquals('value3', logInfo.value);
  assertEquals('message3', logInfo.msg);

  logInfo = log.getLogInfo('type1', true, 'value1');
  assertEquals('type1', logInfo.type);
  assertEquals(true, logInfo.isValid);
  assertEquals('value1', logInfo.value);
  assertEquals('message1', logInfo.msg);

  logInfo = log.getLogInfo('type1', true, 'value2');
  assertEquals(undefined, logInfo);

  logInfo = log.getLogInfo('type3', true, 'value3');
  assertEquals('type3', logInfo.type);
  assertEquals(true, logInfo.isValid);
  assertEquals('value3', logInfo.value);
  assertEquals('message3', logInfo.msg);

  logInfo = log.getLogInfo('type3', false, 'value4');
  assertEquals('type3', logInfo.type);
  assertEquals(false, logInfo.isValid);
  assertEquals('value4', logInfo.value);
  assertEquals('message4', logInfo.msg);

  logInfo = log.getLogInfo('type3', true, 'value6');
  assertEquals(undefined, logInfo);

  var e = assertThrows(function() {
    log.getLogInfo('type3');
  });
  assertEquals('GetLogInfo Error: If opt_isValid is not provided, ' +
               'the object of this type need to be unique.', e.message);
}

function testIsServerValid() {

  assertTrue(log.isServerValid());

  log.addLogInfo('type1', true, 'value1', 'message1');

  assertTrue(log.isServerValid());

  log.addLogInfo(wit.base.constants.serverStatus, true, 'value1', 'message1');

  assertTrue(log.isServerValid());

  log.addLogInfo(wit.base.constants.serverStatus, false, 'value4', 'message4');

  assertFalse(log.isServerValid());

  log.addLogInfo('type2', true, 'value2', 'message2');

  assertFalse(log.isServerValid());
}
