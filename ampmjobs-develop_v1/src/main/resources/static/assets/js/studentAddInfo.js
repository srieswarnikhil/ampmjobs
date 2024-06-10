		    
			    
// student addition information
$(document).ready(function() {
    $('#addInfo').submit(function(event) {
      event.preventDefault();
      validateAddInfo();
    });
});

function loadPreferredCities(cityId) {
	debugger;
	if(cityId==0){
		var select = document.getElementById("login-preffered-city");
	    var optionsToRemove = select.querySelectorAll("option");
	    optionsToRemove.forEach(function(option) {
	        option.remove();
	    });
	    
	    
	    var option = document.createElement("option");
	    option.value = "0"; // Set the value attribute of the option
	    option.text = "Preferred Place"; // Set the text content of the option
	    select.appendChild(option);
	}
	else{
		$("#ampm-loader").show();
		var templateUrl = document.getElementById('loadTemplate').getAttribute('template-url');
		$.ajax({
		        url: templateUrl+"/"+cityId+'/location',
		        type: 'GET',
		        success: function(html) {
		            $('#login-preffered-city').html(html);
		            $("#ampm-loader").hide();
		        },
		        error: function(xhr, status, error) {
		            console.error('Error loading template:', error);
		            console.error(xhr.responseText);
			$("#ampm-loader").hide();
		        }
		    });
	}
	
}


function loadCities(stateId){
	debugger;
	if(stateId==0){
		var select = document.getElementById("login-city");
	    var optionsToRemove = select.querySelectorAll("option");
	    optionsToRemove.forEach(function(option) {
	        option.remove();
	    });
	    
	    
	    var option = document.createElement("option");
	    option.value = "0"; // Set the value attribute of the option
	    option.text = "City"; // Set the text content of the option
	    select.appendChild(option);
	}
	else{
		$("#ampm-loader").show();
		var templateUrl = document.getElementById('loadTemplate').getAttribute('template-url');
		$.ajax({
		        url: templateUrl+"/"+stateId+'/city',
		        type: 'GET',
		        success: function(html) {
		            $('#login-city').html(html);
		            $("#ampm-loader").hide();
		        },
		        error: function(xhr, status, error) {
		            console.error('Error loading template:', error);
		            console.error(xhr.responseText);
			$("#ampm-loader").hide();
		        }
		    });
	}
	
}


function validateAddInfo() {
    debugger;

	var address1 = $("#login-address1").val();

	if (address1 == null || address1.trim() == "") {
		alert("please enter Address Line 1.");
		return false;
	}

	var address2 = $("#login-address2").val();
	if (address2 == null || address2.trim() == "") {
		alert("please enter Address Line 2.");
		return false;
	}
	var state = $("#login-state").val();
	if (state == null || state.trim() == "" || state == 0) {
		alert("please select State.");
		$("#login-state").focus();
		return false;
	}
	var city = $("#login-city").val();
	if (city == null || city.trim() == "" || city == 0) {
		alert("please select City.");
		$("#login-city").focus();
		return false;
	}
	var zip = $("#login-pin").val();
	if (zip == null || zip.trim() == "") {
		alert("please enter ZipCode.");
		$("#login-pin").focus();
		return false;
	}
	
	var institute= $("#login-college").val();
	if (institute == null || institute.trim() == "") {
		alert("please enter College/Institute name.");
		$("#login-college").focus();
		return false;
	}
	
	var blood_group= $("#bloodGroup").val();
	if (blood_group == null || blood_group.trim() == "" || blood_group==0) {
		alert("please select Blood Group.");
		$("#bloodGroup").focus();
		return false;
	}
	
	
	
	var qualification= $("#login-qualification").val();
	if (qualification == null || qualification.trim() == "" || qualification==0) {
		alert("please select Qualification.");
		$("#login-qualification").focus();
		return false;
	}

	var checkboxes = document.querySelectorAll('.checkbox');
    var selectedInterests = [];

    checkboxes.forEach(function(checkbox) {
        if (checkbox.checked) {
        	selectedInterests.push(checkbox.value);
        }
    });


	if (selectedInterests.length < 5) {
		alert("please select 5 Interests.");
		return false;
	}
	
	var location= $("#login-preffered-city").val();
	if (location == null || location.trim() == "" || location==0) {
		alert("please select Preferred Place.");
		$("#login-preffered-city").focus();
		return false;
	}
$("#ampm-loader").show();
	var addDataForm = {
		address_line1 : address1,
		address_line2 : address2,
		state_id : state,
		city_id : city,
		zipcode : zip,
		institute:institute,
		blood_group:blood_group,
		qualification: qualification,
		location : location,
		jobtype_id : selectedInterests
	};
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	var updateUrl = document.getElementById('stuRemInfo')
			.getAttribute('up-stu-rem-url');
	var regSucc = document.getElementById('regSuccId').getAttribute('reg-succ-url');

	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : updateUrl,
		data : JSON.stringify(addDataForm),
		beforeSend : function(xhr) {
			xhr.setRequestHeader(header, token);
		},
		success : function(response) {
			if (response == 1) {
				alert("Details updated successfully.");
				window.location.href = regSucc;
			} else {
				alert("Something went wrong!!!");
				$("#ampm-loader").hide();
			}
		},
		error : function(xhr, status, error) {
			console.error(xhr.responseText);
			$("#ampm-loader").hide();
		}
	});
}

