(function($) {

  var sendToReaderBtn = $('#sendToReaderBtn');

  var urlTB = $('#urlTB'),
      urlErrLb = $('#urlErrLb');

  var purlViews = $('#purlViews');

  var decoratePurlView = function(purlView) {
    var resendBtn = purlView.find('.resendBtn');
    resendBtn.click(function() {
      window.alert('resend');
    });

    var delBtn = purlView.find('.delBtn');
    delBtn.click(onDelBtnClick);

    var delConfirmView = purlView.find('.delConfirmView');
    delConfirmView.hide();

    var delOkBtn = delConfirmView.find('.delOkBtn');
    delOkBtn.click(onDelOkBtnClick);

    var delCancelBtn = delConfirmView.find('.delCancelBtn');
    delCancelBtn.click(onDelCancelBtnClick);
  };

  sendToReaderBtn.click(function() {
    var value = urlTB.val();
    if (value === '') {
      urlErrLb.html('This field is required.');
      return;
    }

    var htmlString = '<section class="purl-view clearfix">' +
        '<h2>Title of the web page</h2>' +
        '<p>The beginning of the web page, might be first two paragraphs.</p>' +
        '<button class="resendBtn">Resend to reader</button>' +
        '<div class="delView">' +
        '<button class="delBtn">Delete</button>' +
        '<div class="delConfirmView">' +
        '<p>Confirm delete?</p>' +
        '<div class="clearfix">' +
        '<button class="delCancelBtn">Cancel</button>' +
        '<button class="delOkBtn">Confirm</button>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '</section>';
    var purlView = $(htmlString);
    purlView.hide();
    purlView.prependTo(purlViews);

    decoratePurlView(purlView);

    purlView.animate({height: 'toggle', padding: 'toggle'}, 'fast');
  });

  var onDelBtnClick = function() {
    var delBtn = $(this);
    var purlViewElem = delBtn.parents('.purl-view')[0];
    var purlView = $(purlViewElem);
    var delConfirmView = purlView.find('.delConfirmView');
    delConfirmView.animate(
        {height: 'toggle', 'padding-top': 'toggle', 'padding-bottom': 'toggle'},
        'fast');
  };

  var onDelCancelBtnClick = function() {
    var delConfirmViewElem = $(this).parents('.delConfirmView')[0];
    var delConfirmView = $(delConfirmViewElem);
    delConfirmView.animate(
        {height: 'toggle', 'padding-top': 'toggle', 'padding-bottom': 'toggle'},
        'fast');
  };

  var onDelOkBtnClick = function() {
    var purlViewElem = $(this).parents('.purl-view')[0];
    var purlView = $(purlViewElem);
    purlView.animate({height: 'toggle', padding: 'toggle'}, 'fast');
  };

  purlViews.children().each(function() {

    var purlView = $(this);

    decoratePurlView(purlView);
  });

})(jQuery);
