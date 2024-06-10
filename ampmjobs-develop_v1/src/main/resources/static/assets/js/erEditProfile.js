$(document).ready(function() {
	$('#editErUserProfile').submit(function(event) {
		event.preventDefault();
		debugger;
		var token = $("meta[name='_csrf']").attr("content");
		var header = $("meta[name='_csrf_header']").attr("content");

		var address1 = $("#address1").val();
		var address2 = $("#address2").val();

		var state = $("#state").val();
		var city = $("#city").val();
		
		var pincode = $("#login-pin").val();
		var fileData = "";
		/*var fileData = document.getElementById('userPic').files[0];
		if (fileData != null) {
			var maxSize = 500 * 1024; // 500 KB, adjust this as needed
			var fileSize = fileData.size;
			if (fileSize > maxSize) {
				alert('File size exceeds the maximum limit of 500 KB');
				$('#userPic').val('');
				return false;
			}
		}*/

		var updateErUrl = $("#editErUserProfile").attr("action");

$("#ampm-loader").show();
		debugger;

		var xhr = new XMLHttpRequest();
		xhr.open("POST", updateErUrl);
		xhr.setRequestHeader(header, token);

		xhr.onreadystatechange = function() {
			if (xhr.readyState === XMLHttpRequest.DONE) {
				if (xhr.status === 200) {
					var dbRes = xhr.responseText;
					if (dbRes == 1) {
						alert("Details updated successfully");
						location.reload();
					}
					else {
						alert("Something went wrong, please try again!!!");
						$("#ampm-loader").hide();
					}
				} else {
					alert("Something went wrong, please try later!!!");
					$("#ampm-loader").hide();
				}
			}
		};
		var formDataWithFile = new FormData();
		formDataWithFile.append("address1", address1);
		formDataWithFile.append("address2", address2);
		formDataWithFile.append("state", state);
		formDataWithFile.append("city", city);
		formDataWithFile.append("zipcode", pincode);
		if (fileData != null) {
			formDataWithFile.append("imageFile", fileData);
		}
		xhr.send(formDataWithFile);

	});

});


function loadCities(stateId) {
	if (stateId == 0) {
		var select = document.getElementById("city");
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
				$('#city').html(html);
				$("#ampm-loader").hide();
			},
			error: function(xhr, status, error) {
				console.error('Error loading template:', error);
				$("#ampm-loader").hide();
			}
		});
	}

}