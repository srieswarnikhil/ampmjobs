

// login
$(document).ready(function() {
    $('#authForm').submit(function(event) {
      event.preventDefault();
      submitLogin();
    });
});
  
  function submitLogin(){
  debugger;
	  var email = $("#login-email").val();
	  
	  if(email==null || email.trim() ==""){
		  alert("Please enter Email.");
		  $("#login-email").focus();
		  return false;
	  }
	  var password = $("#login-password").val();
	  if(password==null || password.trim() ==""){
		  alert("Please enter password.");
		  $("#login-password").focus();
		  return false;
	  }
	  
	  document.getElementById("authForm").submit();
  }
  
  function forgotPassword(){
	  debugger;
      var resetPassUrl = document.getElementById('resetPassUrl').getAttribute('reset-pass-url');
      location.href=resetPassUrl;
  }
  
