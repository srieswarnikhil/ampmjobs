// sign up


$(document).ready(function() {
    $('#signUpForm').submit(function(event) {
      event.preventDefault();
      verifyOTP();
    });
});

function generateOTP() {
      var dbflag = $('#myDbFlag').val();
      debugger;
      var mobileNo = document.getElementById("enter-number").value;
       if(mobileNo == "0" || mobileNo.length <10 || mobileNo.length >10){
        alert("Please enter valid mobile no.");
        $("#enter-number").focus();
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
      var myOtpGenUrl = document.getElementById('myUrlForGO').getAttribute('data-otp-url');
       $.ajax({
         type : 'POST',
        contentType : 'application/json',
        url : myOtpGenUrl,
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
                  alert("This User already exist. OTP not verified");
                  //document.getElementById("phoneNumber").readOnly = false;
                  //document.getElementById("optBtn").disabled = false
                }
                else if(response==3){
                  alert("This User already exist. email not verified");
                  //document.getElementById("phoneNumber").readOnly = false;
                  //document.getElementById("optBtn").disabled = false
                }
                else if(response==4){
                  alert("This User already exist. payment not verified");
                  //document.getElementById("phoneNumber").readOnly = false;
                  //document.getElementById("optBtn").disabled = false
                }
                else if(response==5){
                  alert("This User already exist.");
                  //document.getElementById("phoneNumber").readOnly = false;
                  //document.getElementById("optBtn").disabled = false
                }
                $("#ampm-loader").hide(); 
               
             },
             error: function(xhr, status, error) {
                 $("#ampm-loader").hide(); 
                 console.error('Error:', error);
             }
         });
       }
    }

    function verifyOTP() {
     
      var mobileNo = document.getElementById("enter-number").value;
     if(mobileNo == "0" || mobileNo.length <10 || mobileNo.length >10){
        alert("Please enter valid mobile no.");
        $("#enter-number").focus();
        return false;
      }
      
      var userOTP = document.getElementById("enter-otp").value;
      if(userOTP == null || userOTP.trim()=="") {
        alert("Please enter OTP.");
        $("#enter-otp").focus();
      } else {
		  $("#ampm-loader").show(); 
        var token = $("meta[name='_csrf']").attr("content");
       var header = $("meta[name='_csrf_header']").attr("content");
       var checkUrl = document.getElementById('checkOtp').getAttribute('check-otp-url');
        $.ajax({
              url: checkUrl+'/'+mobileNo+'/'+userOTP,
              type: 'POST',
              beforeSend: function(xhr) {
              xhr.setRequestHeader(header, token);
          },
              success: function(response) {
                 $("#ampm-loader").hide(); 
                 if(response==1){
                  alert("OTP has verified successfully.");
                  var infoUrl = document.getElementById('basicInfoUrl').getAttribute('basic-info-url');
              	location.href=infoUrl;
                 }
                 else if(response==0){
                  alert("Invalid OTP!!! Please try again.");
                 }
                  else {
                  alert("Something went wrong!!! Please try again.");
                 }
               
              },
              error: function(xhr, status, error) {
                  console.error('Error:', error);
              }
          });
      }
    }
    
    
    
	