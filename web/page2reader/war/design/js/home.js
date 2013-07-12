(function($) {

  var showLogInBtn = $('#showLogInBtn'),
      logInView = $('#logInView');
  logInView.hide();
  showLogInBtn.click(function() {
    logInView.animate({height: 'toggle', padding: 'toggle'}, 'fast');
  });

  var logInBtn = $('#logInBtn'),
      logInErrLb = $('#logInErrLb');
  logInBtn.click(function() {
    logInErrLb.html('Username or passsword is incorrect');
  });

  var forgotPasswordBtn = $('#forgotPasswordBtn'),
      forgotPasswordView = $('#forgotPasswordView');
  forgotPasswordView.hide();
  forgotPasswordBtn.click(function() {
    forgotPasswordView.animate({height: 'toggle', padding: 'toggle'}, 'fast');
  });

  var forgotOkBtn = $('#forgotOkBtn'),
      forgotUsernameErrLb = $('#forgotUsernameErrLb');
  forgotOkBtn.click(function() {
    forgotUsernameErrLb.html('Username or email is incorrect');
  });

  var showSignUpBtn = $('#showSignUpBtn'),
      signUpView = $('#signUpView');
  signUpView.hide();
  showSignUpBtn.click(function() {
    signUpView.animate({height: 'toggle', padding: 'toggle'}, 'fast');
  });

  var signUpBtn = $('#signUpBtn'),
      signUpUsernameErrLb = $('#signUpUsernameErrLb'),
      signUpPasswordErrLb = $('#signUpPasswordErrLb'),
      signUpRepeatPasswordErrLb = $('#signUpRepeatPasswordErrLb'),
      signUpEmailErrLb = $('#signUpEmailErrLb');
  signUpBtn.click(function() {
    signUpUsernameErrLb.html('Username is incorrect');
    signUpPasswordErrLb.html('Password is incorrect');
    signUpRepeatPasswordErrLb.html('Repeat password is incorrect');
    signUpEmailErrLb.html('Email is incorrect');
  });

})(jQuery);
