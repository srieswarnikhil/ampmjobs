  
  // change password
  
  function validatePassword() {
				debugger;
				$("#match").text("");
				var oldpwd = document.getElementById("old-password").value;
				var pwd = document.getElementById("enter-password").value;
				var cnfPwd = document.getElementById("confirm-password").value;
				if (oldpwd == null || oldpwd.trim() == "") {
					$("#match").text("Please enter Current Password.");
					return false;
				} 
				else if (pwd == null || pwd.trim() == "") {
					$("#match").text("Please enter Password.");
					return false;
				} else if (cnfPwd == null || cnfPwd.trim() == "") {
					$("#match").text("Please enter Confirm Password.");
					return false;
				} else if (pwd != cnfPwd) {
					$("#match").text(
							"Password and Confirm Password are must be match.");
					return false;
				} else if (oldpwd.trim() == pwd.trim()) {
					$("#match").text("New Password can't same as Current Password.");
					return false;
				} else {
					
					var passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+])[A-Za-z\d!@#$%^&*()_+]{8,}$/;
	                if (!passwordRegex.test(pwd)) {
	                	$("#match").text("Password must contain at least one lowercase letter, one uppercase letter, one digit, one special character, and be at least 8 characters long.");
	                    return false;
	                }
	                else{
					
					var token = $("meta[name='_csrf']").attr("content");
					var header = $("meta[name='_csrf_header']").attr("content");
					var pwdUrl = document.getElementById('changePwdUrl').getAttribute('user-pwd-url');

					$.ajax({
								url : pwdUrl+'/'+ oldpwd + '/'+ pwd + '/' + cnfPwd,
								type : 'POST',
								beforeSend : function(xhr) {
									xhr.setRequestHeader(header, token);
								},
								success : function(response) {
									if (response == 1) {
										$("#match").text("Password changed successfully.");
										$("#old-password").val('');
										$("#enter-password").val('');
										$("#confirm-password").val('');
									} else if (response == 2) {
										$("#match").text("password & Confirm Password must be same.");
										return false;
									} else if (response == 3) {
										$("#match").text("Wrong mobile No. please enter correct details.");
										return false;
									} else if (response == 4) {
										$("#match").text("Mobile no. not verified.");
										return false;
									} else if (response == 5) {
										$("#match").text("email id not verified.");
										return false;
								} else if (response == 6) {
									$("#match").text("Current password is wrong.");
									return false;
								} else if (response == 7) {
									$("#match").text("New Password should not same as Current password.");
									return false;
								}
									else {
										$("#match").text("Something went wrong!!! Please try again.");
									}

								},
								error : function(xhr, status, error) {
									console.error('Error:', error);
								}
							});
	                }
				}
			}
			
