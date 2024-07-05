

// er additional information 


$(document).ready(function() {
	$('#erAddInfo').submit(function(event) {
		event.preventDefault();
		validateErAddInfo();
	});
});

function loadCities(stateId) {
	if (stateId == 0) {
		var select = document.getElementById("cityId");
		var optionsToRemove = select.querySelectorAll("option");
		optionsToRemove.forEach(function(option) {
			option.remove();
		});

		var option = document.createElement("option");
		option.value = "0"; // Set the value attribute of the option
		option.text = "City"; // Set the text content of the option
		select.appendChild(option);
	} else {
		$("#ampm-loader").show();
		var templateUrl = document.getElementById('loadTemplate')
			.getAttribute('template-url');
		$.ajax({
			url: templateUrl + "/" + stateId + '/city',
			type: 'GET',
			success: function(html) {
				$('#cityId').html(html);
				$("#ampm-loader").hide();
			},
			error: function(xhr, status, error) {
				console.error('Error loading template:', error);
				$("#ampm-loader").hide();
			}
		});
	}

}

function validateErAddInfo() {
	debugger;

	var address1 = $("#login-address1").val();

	if (address1 == null || address1.trim() == "") {
		alert("please enter Address Line 1");
		$("#login-address1").focus();
		return false;
	}

	var address2 = $("#login-address2").val();
	if (address2 == null || address2.trim() == "") {
		alert("please enter Address Line 2");
		$("#login-address2").focus();
		return false;
	}
	var state = $("#stateId").val();
	if (state == null || state.trim() == "" || state == 0) {
		alert("please select State");
		$("#stateId").focus();
		return false;
	}
	var city = $("#cityId").val();
	if (city == null || city.trim() == "" || city == 0) {
		alert("please select City");
		$("#cityId").focus();
		return false;
	}
	var zip = $("#login-pin").val();
	if (zip == null || zip.trim() == "") {
		alert("please enter Pincode");
		$("#login-pin").focus();
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
		alert("please select 5 Domains.");
		return false;
	}
	$("#ampm-loader").show();
	var addDataForm = {
		address_line1: address1,
		address_line2: address2,
		state_id: state,
		city_id: city,
		zipcode: zip,
		jobtype_id: selectedInterests
	};
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	var updateUrl = document.getElementById('erUrlAddInfo').getAttribute('update-add-info-url');
	var erRegSucc = document.getElementById('regErSuccId').getAttribute('reg-er-succ-url');

	$.ajax({
		type: 'POST',
		contentType: 'application/json',
		url: updateUrl,
		data: JSON.stringify(addDataForm),
		beforeSend: function(xhr) {
			xhr.setRequestHeader(header, token);
		},
		success: function(response) {
			if (response == 1) {
				alert("Details updated successfully.");
				window.location.href = erRegSucc;
			} else {
				alert("Something went wrong!!!");
				$("#ampm-loader").hide();
			}
		},
		error: function(xhr, status, error) {
			console.error(xhr.responseText);
			$("#ampm-loader").hide();
		}
	});
}

