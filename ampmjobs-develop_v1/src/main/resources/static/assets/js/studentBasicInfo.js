	
// student basic information 


$(document).ready(function() {
    $('#basicDetailsForm').submit(function(event) {
      event.preventDefault();
      validateBasicDetails();
    });
});

 function validateBasicDetails() {
      debugger;
      
      var cName = $('#login-name').val();
      if( cName== null || cName.trim()==""){
    	  alert("Please enter Name");
    	  $('#login-name').focus();
    	  return flase;
      }
      var email = $('#login-email').val();
      if( email== null || email.trim()==""){
    	  alert("Please enter Email");
    	  $('#login-email').focus();
    	  return flase;
      }
      else{
    	  var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
          if (!emailRegex.test(email)) {
              alert("Please enter a valid email.");
              $('#login-email').focus();
              return false;
          }
      }
       var dob = $('#login-dob').val();
       if( dob== null || dob.trim()==""){
     	  alert("Please select Date of Birth");
     	  $('#login-dob').focus();
     	  return flase;
       }
        var gerderVal = $("input[name='gender']:checked").val();
        if( gerderVal== null || gerderVal.trim()==""){
      	  alert("Please select Gender");
      	  return flase;
        }
         var country = $('#login-country').val();
         if( country== null || country.trim()=="" || country=="0"){
       	  alert("Please select Country");
       	  $('#login-country').focus();
       	  return flase;
         }
          var referralVal = $('#login-referral').val();
          if( referralVal== null || referralVal.trim()=="" || referralVal=="0"){
        	  alert("Select How did you hear about us");
        	  $('#login-referral').focus();
        	  return flase;
          }
           var terms = $('#login-checkbox').prop('checked');
           if( terms== null || terms==false){
         	  alert("Please accept the Terms & Conditions");
         	  return flase;
           }
           var dbMobile = $("#dbMobile").val();
           
           var dbflag = $("#flag").val();
      
      
      var basicDataForm = {
        name : cName,
        email : email,
        countryCode : $('#countryCode').val(),
        phone : dbMobile,
        dob : dob,
        gender : gerderVal,
        country_id : country,
        how_do_know_about : referralVal,
        terms : terms,
        flag:dbflag,
      };
      $("#ampm-loader").show();
       var token = $("meta[name='_csrf']").attr("content");
         var header = $("meta[name='_csrf_header']").attr("content");
         var updateInfoUrl = document.getElementById('updateBasicInfo').getAttribute('basic-data-url');
         
      $.ajax({
        type : 'POST',
        contentType : 'application/json',
        url : updateInfoUrl,
        data : JSON.stringify(basicDataForm),
        beforeSend: function(xhr) {
              xhr.setRequestHeader(header, token);
          },
        success : function(response) {
          $("#ampm-loader").hide();
          if(response==1){
        	  alert("Details updated successfully, check your mail to create password.");
        	  $("#basicDetailsDiv").hide();
         	  $("#success-basic-info").show();
          }
          else if(response==5){
				  alert("The email you entered is already in use. Please choose a different email address.");
				  $("#login-email").focus();
		  }
          else{
        	  alert("Something went wrong!!!"); 
          }
        },
        error : function(xhr, status, error) {
          $("#ampm-loader").hide();
          console.error(xhr.responseText);
        }
      });
    }
    