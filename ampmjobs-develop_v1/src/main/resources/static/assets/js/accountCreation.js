 // acc creation
 
 
 
 $(document).ready(function() {
    $('#accCreationForm').submit(function(event) {
      event.preventDefault();
      validatePassword();
    });
});
    
 function validatePassword(){
      debugger;
      $("#match").text("");
      var pwd = document.getElementById("enter-password").value;
      var cnfPwd = document.getElementById("confirm-password").value;
      if(pwd==null || pwd.trim()==""){
        $("#match").text("Please enter Password.");
        $("#enter-password").focus();
          return false;
      }
      else if(cnfPwd==null || cnfPwd.trim()==""){
    	  $("#match").text("Please enter Confirm Password.");
    	  $("#confirm-password").focus();
          return false;
      }
      else if(pwd != cnfPwd){
    	  $("#match").text("Password and Confirm Password are must be match.");
    	  $("#confirm-password").focus();
          return false;
      }
      else {
       
    	  var passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+])[A-Za-z\d!@#$%^&*()_+]{8,}$/;
          if (!passwordRegex.test(pwd)) {
          	$("#match").text("Password must contain at least one lowercase letter, one uppercase letter, one digit, one special character, and be at least 8 characters long.");
             $("#enter-password").focus();
              return false;
          }
          else{
			  
			  $("#ampm-loader").show();
    	  
    	  var mobileNo =$("#dbMobileNo").val();
      var flag =$("#encodedCode").val();
    
      var token = $("meta[name='_csrf']").attr("content");
     var header = $("meta[name='_csrf_header']").attr("content");
     var checkPasswordUrl = document.getElementById('checkPasswordUrl').getAttribute('check-pass-url');
      $.ajax({
             url: checkPasswordUrl+'/'+mobileNo+'/'+flag+'/'+pwd+'/'+cnfPwd,
             type: 'POST',
             beforeSend: function(xhr) {
              xhr.setRequestHeader(header, token);
          },
             success: function(response) {
                if(response==1){
                // submit login page
                $("#login-email").val($("#dbEmail").val());
                 $("#login-password").val($("#enter-password").val());
                 submitManuLogin();
                }
                else if(response==2){
                   var newUrl = document.getElementById('myLogInUrl2').getAttribute('log-in-url-2');
                    location.href=newUrl;
                  }
                else{
					$("#ampm-loader").hide();
                  alert("Something went wrong!!! Please try again.");
                }
               
             },
             error: function(xhr, status, error) {
                 console.error('Error:', error);
             }
         });
      }
    }
    }
    
    
     function submitManuLogin(){
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
	  
	  document.getElementById("authManuForm").submit();
  }