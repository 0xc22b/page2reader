(function($) {

  var showChangeReaderEmailBtn = $('#showChangeReaderEmailBtn'),
      changeReaderEmailView = $('#changeReaderEmailView');
  changeReaderEmailView.hide();
  showChangeReaderEmailBtn.click(function() {
    changeReaderEmailView.animate({height: 'toggle', padding: 'toggle'},
        'fast');
  });

  var showChangeUsernameBtn = $('#showChangeUsernameBtn'),
      changeUsernameView = $('#changeUsernameView');
  changeUsernameView.hide();
  showChangeUsernameBtn.click(function() {
    changeUsernameView.animate({height: 'toggle', padding: 'toggle'}, 'fast');
  });

  var changeUsernameOkBtn = $('#changeUsernameOkBtn'),
      changeUsernameErrLb = $('#changeUsernameErrLb'),
      changeUsernamePasswordErrLb = $('#changeUsernamePasswordErrLb');
  changeUsernameOkBtn.click(function() {
    changeUsernameErrLb.html('Username is incorrect');
    changeUsernamePasswordErrLb.html('Password is incorrect');
  });

  var showChangeEmailBtn = $('#showChangeEmailBtn'),
      changeEmailView = $('#changeEmailView');
  changeEmailView.hide();
  showChangeEmailBtn.click(function() {
    changeEmailView.animate({height: 'toggle', padding: 'toggle'}, 'fast');
  });

  var showChangePasswordBtn = $('#showChangePasswordBtn'),
      changePasswordView = $('#changePasswordView');
  changePasswordView.hide();
  showChangePasswordBtn.click(function() {
    changePasswordView.animate({height: 'toggle', padding: 'toggle'}, 'fast');
  });

  var showDeleteAccountBtn = $('#showDeleteAccountBtn'),
      deleteAccountView = $('#deleteAccountView');
  deleteAccountView.hide();
  showDeleteAccountBtn.click(function() {
    deleteAccountView.animate({height: 'toggle', padding: 'toggle'}, 'fast');
  });

})(jQuery);
