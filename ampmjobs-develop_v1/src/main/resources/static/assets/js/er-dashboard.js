//Interest Table Display Handler Code:
function displayTable(jobTypeId) {
	$("#ampm-loader").show();
	$(".colColour").css({
		"color": "#1da1e0",
		"font-weight": "normal"
	});
	$("#" + jobTypeId).css({
		"color": "blue",
		"font-weight": "bold"
	});
	var stuUrl = document.getElementById('getStudents').getAttribute('data-students-url');
	$.ajax({
		url: stuUrl + "/" + jobTypeId,
		type: 'GET',
		success: function(html) {
			$('#myResult').html(html);
			$("#ampm-loader").hide();
		},
		error: function(xhr, status, error) {
			console.error('Error loading template:', error);
			$("#ampm-loader").hide();
		}
	});

}


function showModel(id) {
	debugger;
	$("#k" + id).css("display", "block");
}

function closeJobModal() {
	$("#jobUnique").css("display", "none");
	$('#uniqueJobForm')[0].reset();
	$("#viewJobLink").css("display", "none");

}

function doShortList(studentId) {
	$("#studentId").val(studentId);
	$("#jobUnique").css("display", "block");
	$("#short-list-btn").text("Shortlist")
	$("#short-list-btn").prop('disabled', false).css('cursor', 'pointer');
}

function showJobLink(jobId) {
	debugger;
	if (jobId == 0 || jobId == "") {
		$("#jobId").val(0);
		$("#flag").val(0);
		$("#viewJobLink").css("display", "none");
	}
	else {
		$("#jobId").val(jobId);
		$("#flag").val(3);
		$("#viewJobLink").css("display", "block");
		
		updateButton();
	}

}
function viewJobByLink() {
	$('#jobActionFormInDashboard').attr('target', '_blank').submit();
}


$(document).ready(function() {
	$(document).on('submit', '#uniqueJobForm', function(event) {
		event.preventDefault();
		finalShortList();
	});
});

function updateButton(){
	debugger;
	var erJobId = $("#erJobId").val();
	var stuId = $("#studentId").val();
	
	$("#ampm-loader").show();
	var verifyStuUrl = document.getElementById('verifyStudentApplication').getAttribute('verify-status-students-url');

	var myData = {
		student_id: stuId,
		jobtype_id: erJobId,
	};

	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$.ajax({
		type: 'POST',
		contentType: 'application/json',
		url: verifyStuUrl,
		data: JSON.stringify(myData),
		beforeSend: function(xhr) {
			xhr.setRequestHeader(header, token);
		},
		success: function(response) {
			debugger;
			if (response == true) {
				$("#short-list-btn").text("Shortlisted")
				$("#short-list-btn").attr('disabled', true).css('cursor', 'not-allowed');
				$("#short-list-btn").css('color','white');
			}
			else{
				$("#short-list-btn").text("Shortlist")
				$("#short-list-btn").prop('disabled', false).css('cursor', 'pointer');
			}
			
			$("#ampm-loader").hide();
		},
		error: function(xhr, status, error) {
			console.error('Error:', error);
			$("#ampm-loader").hide();
		}
	});
}


function finalShortList() {
	debugger;
	var erJobId = $("#erJobId").val();
	var stuId = $("#studentId").val();

	if (erJobId == 0 || erJobId == "") {
		alert("Please select Job Unique Code");
		return false;
	}

	if (erJobId == 0 || erJobId == "" || stuId == 0 || stuId == "") {
		alert("Something went wrong, please try again later!!!");
		return false;
	}
	else {
		shortListStudent(stuId, erJobId);
	}

}


function shortListStudent(student_id, job_type_id) {
	var confirmShortList = confirm("Are you sure you want to Short-List ?");
	if (confirmShortList == false) {
		return false;
	}
	$("#ampm-loader").show();
	var shortListUrl = document.getElementById('shortListStudent').getAttribute('short-list-students-url');

	var myData = {
		student_id: student_id,
		jobtype_id: job_type_id,
	};

	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$.ajax({
		type: 'POST',
		contentType: 'application/json',
		url: shortListUrl,
		data: JSON.stringify(myData),
		beforeSend: function(xhr) {
			xhr.setRequestHeader(header, token);
		},
		success: function(response) {
			debugger;
			if (response == 1) {
				alert("Candidate has been successfully shortlisted.");
			}
			else if (response == 2) {
				alert("Candidate already shortlisted.");
			}

			else {
				alert("Something went wrong, Please try later!!!");
			}
			closeJobModal();
			$("#ampm-loader").hide();
		},
		error: function(xhr, status, error) {
			console.error('Error:', error);
			$("#ampm-loader").hide();
		}
	});
}


function closeModal(id) {
	debugger;
	$("#k" + id).css("display", "none");
}