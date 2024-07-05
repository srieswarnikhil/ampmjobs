		
			
// reset password


$(document).ready(function() {
	$('#passwordResetForm').submit(function(event) {
		event.preventDefault();
		validateEmail();
	});
});



function validateEmail() {
	debugger;
	var email = $("#enter-email").val();
	if (email == null || email.trim() == "") {
		alert("Please enter email.");
		$("#enter-email").focus();
		return false;
	}
	else {
		$("#ampm-loader").show();
		var token = $("meta[name='_csrf']").attr("content");
		var header = $("meta[name='_csrf_header']").attr("content");
		var resetUrl = document.getElementById('emailCheck').getAttribute('reset-pass-email-url');

		$.ajax({
			url: resetUrl + '/' + email,
			type: 'POST',
			beforeSend: function(xhr) {
				xhr.setRequestHeader(header, token);
			},
			success: function(response) {
				if (response === 1) {
					$("#passResetDiv").hide();
					$("#success-pass-reset").show();
				} else if (response === 0) {
					alert("This email is not registered with us.");
					$("#enter-email").focus();
				}
				else {
					alert("Something went wrong!!! Please try again.");
				}
$("#ampm-loader").hide();
			},
			error: function(xhr, status, error) {
				console.error('Error:', error);
				$("#ampm-loader").hide();
			}
		});
	}
} 



$(document).ready(function() {
	$('#reGenPassForm').submit(function(event) {
		event.preventDefault();
		validatePassword();
	});
});

function validatePassword() {
				debugger;
				$("#match").text("");
				
				var pwd = document.getElementById("enter-password").value;
				var cnfPwd = document.getElementById("confirm-password").value;
				if (pwd == null || pwd.trim() == "") {
					$("#match").text("Please enter Password.");
					return false;
				} else if (cnfPwd == null || cnfPwd.trim() == "") {
					$("#match").text("Please enter Confirm Password.");
					return false;
				} else if (pwd != cnfPwd) {
					$("#match").text(
							"Password and Confirm Password are must be match.");
					return false;
				} else {
					
					var passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+])[A-Za-z\d!@#$%^&*()_+]{8,}$/;
	                if (!passwordRegex.test(pwd)) {
	                	$("#match").text("Password must contain at least one lowercase letter, one uppercase letter, one digit, one special character, and be at least 8 characters long.");
	                    return false;
	                }
	                else{
					$("#ampm-loader").show();
					var encodedEmail = $("#encodedEmail").val();					
					var token = $("meta[name='_csrf']").attr("content");
					var header = $("meta[name='_csrf_header']").attr("content");
					var resetPassUrl = document.getElementById('updateReSetPass').getAttribute('reset-pass-url-update');
				     
					$.ajax({
								url : resetPassUrl+'/' + encodedEmail + '/'
										+ pwd + '/' + cnfPwd,
								type : 'POST',
								beforeSend : function(xhr) {
									xhr.setRequestHeader(header, token);
								},
								success : function(response) {
									if (response == 1) {
										alert("Password Reset Successful!");
										var myLoginUrl = document.getElementById('loginUrl').getAttribute('login-user-url');
										
										window.location.href = myLoginUrl+'?error=pars';
									} 
									else {
										alert("Something went wrong!!! Please try again.");
										$("#ampm-loader").hide();
									}

								},
								error : function(xhr, status, error) {
									console.error('Error:', error);
									$("#ampm-loader").hide();
								}
							});
	                }
				}
			}
			
			
			 function generateNewOTP() {
			      debugger;
			      var dbflag = $("#dbflag").val();
			      var mobileNo = document.getElementById("enter-number").value;
			       if(mobileNo == "0" || mobileNo.length <10 || mobileNo.length >10){
			        alert("Please enter valid mobile no.");
			        return false;
			      }
			       else{
			       
			       var myFormData = {
			           mobile : mobileNo,
			           flag : dbflag,
			      };
			       $("#ampm-loader").show();
			       var token = $("meta[name='_csrf']").attr("content");
			       var header = $("meta[name='_csrf_header']").attr("content");
			       var validateUser = document.getElementById('otpGenUrl').getAttribute('otp-gen-url');
			       $.ajax({
			         type : 'POST',
			        contentType : 'application/json',
			        url : validateUser,
			        data : JSON.stringify(myFormData),
			        beforeSend: function(xhr) {
			              xhr.setRequestHeader(header, token);
			          },
			        success: function(response) {
			               debugger;
			               if(response==1){
			                  alert("OTP has been sent to mobile number.");
			                  
			                  //document.getElementById("phoneNumber").readOnly =true;
			                  //document.getElementById("optBtn").disabled = true;
			                }
			                else if(response==0){
			                  alert("Something went wrong!!! Please try later.");
			                  //document.getElementById("phoneNumber").readOnly = false;
			                  //document.getElementById("optBtn").disabled = false
			                }
			                else if(response==2){
			                  alert("User not exist. Please register.");
			                  //document.getElementById("phoneNumber").readOnly = false;
			                  //document.getElementById("optBtn").disabled = false
			                }
			                else if(response==3){
			                  alert("Email not verified");
			                  //document.getElementById("phoneNumber").readOnly = false;
			                  //document.getElementById("optBtn").disabled = false
			                }
			                else if(response==4){
			                  alert("Since user role is different, you can't access.");
			                  //document.getElementById("phoneNumber").readOnly = false;
			                  //document.getElementById("optBtn").disabled = false
			                }
			                else {
			                	alert("Something went wrong!!! Please try later.");
			                  //document.getElementById("phoneNumber").readOnly = false;
			                  //document.getElementById("optBtn").disabled = false
			                }
			               $("#ampm-loader").hide();
			             },
			             error: function(xhr, status, error) {
			                 console.error('Error:', error);
			                 $("#ampm-loader").hide();
			             }
			         });
			       }
			    }

			    function verifyOTP() {
			     
			      var mobileNo = document.getElementById("enter-number").value;
			     if(mobileNo == "0" || mobileNo.length <10){
			        alert("Please enter valid mobile no.");
			        return false;
			      }
			      
			      var userOTP = document.getElementById("enter-otp").value;
			      if(userOTP == null || userOTP.trim()=="") {
			        alert("Please enter valid OTP.");
			      } else {
			       $("#ampm-loader").show();
			        var token = $("meta[name='_csrf']").attr("content");
			       var header = $("meta[name='_csrf_header']").attr("content");
			       var checkOtp = document.getElementById('otpCheckUrl').getAttribute('otp-check-url');
			        $.ajax({
			              url: checkOtp+'/'+mobileNo+'/'+userOTP,
			              type: 'POST',
			              beforeSend: function(xhr) {
			              xhr.setRequestHeader(header, token);
			          },
			              success: function(response) {
			                 if(response==true){
			                alert("OTP has verified successfully.");
			                $("#enter-number").hide();
			                $("#enter-otp").hide();
			                $("#login-button-gen-otp").hide();
			                $("#login-button-ver-otp").hide();
			                $("#enter-password").show();
			                $("#confirm-password").show();
			                $("#login-button").show();
			                 }
			                 else{
			                  alert("Invalid OTP!!! Please try again.");
			                 }
			               $("#ampm-loader").hide();
			              },
			              error: function(xhr, status, error) {
			                  console.error('Error:', error);
			                  $("#ampm-loader").hide();
			              }
			          });
			      }
			    }
			    
