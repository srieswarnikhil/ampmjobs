 
			
// er basic info

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
        alert("Please enter Name.");
        $('#login-name').focus();
        return flase;
      }
      var email = $('#login-email').val();
      if( email== null || email.trim()==""){
        alert("Please enter Email.");
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
        var employerType = $("input[name='employer-type']:checked").val();
        if( employerType== null || employerType.trim()==""){
          alert("Please select Employer Type.");
          return flase;
        }
        var companyName = $("#company-name").val();
        var gstNo = $("#gst-number").val();
        var domain = $("#domain").val();
        if(employerType == "company"){
        	if( companyName== null || companyName.trim()=="" ){
                alert("Please enter Company Name.");
                $("#company-name").focus();
                return flase;
               }
        	if( gstNo== null || gstNo.trim()=="" ){
                alert("Please enter GST Number.");
                $("#gst-number").focus();
                return flase;
               }
        	if( domain== null || domain.trim()=="" ){
                alert("Please enter Business Domain.");
                $("#domain").focus();
                return flase;
               }
        }
        else{
        	companyName = null;
            gstNo = null;
            domain  = null;
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
            $('#login-checkbox').focus();
            return flase;
           }
	$("#ampm-loader").show();
      var dbMobile = $("#dbMobile").val();
      
      var dbflag = $("#flag").val();
      
      var basicDataForm = {
        name : cName,
        email : email,
        type : employerType,
        phone : dbMobile,
        company_name : companyName,
        gstno : gstNo,
        business_domain : domain,
        how_do_know_about: referralVal,
        terms : terms,
        flag:dbflag,
      };
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
        	  $("#basicDetails").hide();
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
          console.error(xhr.responseText);
          $("#ampm-loader").hide();
        }
      });
    }
