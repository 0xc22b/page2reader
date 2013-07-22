(function() {
  window.page2reader = (function() {

    var frameName, formName,
        action = 'http://page2reader.appspot.com/submit',
        charset = document.characterSet || document.charset,
        pUrl = document.location.href;

    var listen = function(evnt, elem, func) {
      if (elem.addEventListener) {
        elem.addEventListener(evnt, func, false);
      } else if (elem.attachEvent) {
        elem.attachEvent('on' + evnt, func);
      } else {
        return false;
      }
      return true;
    };

    var unlisten = function(evnt, elem, func) {
      if (elem.removeEventListener) {
        elem.removeEventListener(evnt, func, false);
        return true;
      } else if (elem.detachEvent) {
        var r = elem.detachEvent('on' + evnt, func);
        return r;
      } else {
        return false;
      }
    };

    var genFrame = function(frameName) {
      var f;
      try {
        f = document.createElement('<iframe name="' + frameName + '">');
      } catch (e) {
        f = document.createElement('iframe');
      }
      f.setAttribute('id', frameName);
      f.setAttribute('name', frameName);
      f.setAttribute('allowTransparency', 'true');
      f.style.cssText = 'display: none;';
      return f;
    };

    var genForm = function(formName, url, charset) {
      var f = document.createElement('form');
      f.setAttribute('id', formName);
      f.setAttribute('action', url);
      f.setAttribute('accept-charset', charset);
      f.setAttribute('enctype', 'application/x-www-form-urlencoded');
      f.setAttribute('method', 'post');
      return f;
    };

    var showMsg = function(text) {
      var msg = document.getElementById('page2reader-msg');
      if (!msg) {
        msg = document.createElement('div');
        msg.setAttribute('id', 'page2reader-msg');
        msg.style.cssText = ['display: block;', 'height: 54px;',
          'left: 50%;', 'margin-left: -129px;', 'padding: 22px 0 0;',
          'position: fixed;', 'top: 10%;', 'width: 258px;',
          'z-index: 100000;', 'background-color: #7d7d7d;',
          '-moz-border-radius: 5px;', '-webkit-border-radius: 5px;',
          'border-radius: 5px;',
          'filter: alpha(opacity=97);', '-moz-opacity: .97;', 'opacity: .97;',
          'text-align: center;', 'color: white;', 'font-size: 23px;',
          'font-family: Helvetica, Arial, sans-serif;',
          'box-sizing: content-box;', '-moz-box-sizing: content-box;',
          '-webkit-box-sizing: content-box;'].join('');
        document.body.appendChild(msg);
      }

      msg.innerHTML = text;
    };

    var hideMsg = function() {
      var msg = document.getElementById('page2reader-msg');
      if (msg) {
        msg.parentNode.removeChild(msg);
      }
    };

    var receiveMessage = function(event) {

      if (!event.data || !event.data.substring) {
        return;
      }
      if (event.data.substring(0, 12) !== 'page2reader:') {
        return;
      }

      var msg = event.data.substring(12);

      showMsg(msg);

      window.setTimeout(function() {
        hideMsg();
      }, 1000);

      // unlisten
      unlisten('message', window, receiveMessage);
      unlisten('unload', window, hideMsg);

      // removeFrame
      var frame = document.getElementById(frameName);
      if (frame) {
        frame.parentNode.removeChild(frame);
      }

      // removeForm
      var form = document.getElementById(formName);
      if (form) {
        form.parentNode.removeChild(form);
      }
    };

    var send = function() {
      showMsg('Sending...');

      frameName = 'page2readerFrameName' + (new Date()).getTime();
      formName = 'page2readerFormName' + (new Date()).getTime();

      listen('message', window, receiveMessage);
      listen('unload', window, hideMsg);

      var frame = genFrame(frameName);
      document.body.appendChild(frame);

      var form = genForm(formName, action, charset);
      form.setAttribute('target', frame.getAttribute('name'));
      document.body.appendChild(form);

      var inputList = {
        'method': 'submitPageUrl',
        'content': pUrl
      };

      for (var inputKey in inputList) {
        if (inputList.hasOwnProperty(inputKey)) {
          var input = document.createElement('input');
          input.setAttribute('type', 'hidden');
          input.setAttribute('name', inputKey);
          input.setAttribute('value', inputList[inputKey]);
          form.appendChild(input);
        }
      }

      window.setTimeout(function() {
        form.submit();
      }, 50);
    };

    return {send: send};
  }());

  window.page2reader.send();
}());
