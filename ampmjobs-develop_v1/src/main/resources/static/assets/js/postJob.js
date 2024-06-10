$(document).ready(function() {
	$('#jobPostForm').submit(function(event) {
		var dbJobId = $('#dbJobId').val();
		var confirmSMS = "Are you sure you want to Post Job ?";
		if (dbJobId != null && dbJobId > 0) {
			confirmSMS = "Are you sure you want to Edit Job ?"
		}
		var confirmPostJob = confirm(confirmSMS);
		if (confirmPostJob) {

			event.preventDefault();
$("#ampm-loader").show();

			var formData = $(this).serialize(); // Serialize form data
			var token = $("meta[name='_csrf']").attr("content");
			var header = $("meta[name='_csrf_header']").attr("content");
			var jsonData = {};
			formData.split('&').map(function(item) {
				var parts = item.split('=');
				jsonData[decodeURIComponent(parts[0])] = decodeURIComponent(parts[1]);
			});

			var stp1 = $("#startTimePart1").val();
			var stp2 = $("#startTimePart2").val();

			var etp1 = $("#endTimePart1").val();
			var etp2 = $("#endTimePart2").val();

			jsonData["job_start_time"] = stp1 + "" + stp2;
			jsonData["job_end_time"] = etp1 + "" + etp2;
			debugger;

			$.ajax({
				type: 'POST',
				url: $(this).attr('action'),
				contentType: 'application/json',
				data: JSON.stringify(jsonData),
				beforeSend: function(xhr) {
					xhr.setRequestHeader(header, token);
				},
				success: function(response) {
					if (response == 1) {
						if (dbJobId != null && dbJobId > 0) {
							alert("Job Updated Successfully");
						}
						else {
							alert("Job Posted Successfully");
						}
						var jobUrl = document.getElementById('postJobUrl').getAttribute('job-post-url');
						window.location.href = jobUrl;
					}
					else {
						alert("Something went wrong, please try later!!!");
						$("#ampm-loader").hide();
					}
				},
				error: function(xhr, status, error) {
					console.error(xhr.responseText);
					$("#ampm-loader").hide();
				}
			});
		}

	});
});


$(document).ready(function() {
	$("#jobName").change(function() {
		debugger;
		var selectedOption = $(this).find("option:selected");
		var longDesc = selectedOption.data("long_dec");
		$("#descText").val(longDesc);

	});
});