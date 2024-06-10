function viewApplicant(rowId,viewApplicant,student_job_applied) {
	debugger;
	showModel(rowId);
	var statusFlag = $("#applicantView"+rowId).val();
	
	
	
	if(viewApplicant==false && student_job_applied>0 && statusFlag=="false"){
		
		
		var token = $("meta[name='_csrf']").attr("content");
		var header = $("meta[name='_csrf_header']").attr("content");
		var updateIsView = document.getElementById('updateView').getAttribute('update-view-url');

		$.ajax({
			url: updateIsView + '/' + student_job_applied,
			type: 'POST',
			beforeSend: function(xhr) {
				xhr.setRequestHeader(header, token);
			},
			success: function(response) {
				if(response>0){
					$("#applicantView"+rowId).val(true);
					var notiAsTxt = $("#notification-color").text();
					var notiAsNum = parseInt(notiAsTxt);
					var finalNoti = notiAsNum - 1;
					$("#notification-color").text(finalNoti);
					$("#iconView"+rowId).text("");

				}
			},
			error: function(xhr, status, error) {
				console.error('Error:', error);
			}
		});
	}
}

function deleteApplicant(student_job_applied) {
	debugger;
	var confirmDelete = confirm("Are you sure you want to Delete ?");
	if (confirmDelete) {
$("#ampm-loader").show();
		var token = $("meta[name='_csrf']").attr("content");
		var header = $("meta[name='_csrf_header']").attr("content");
		var deleteApplicantUrl = document.getElementById('deleteApplicant').getAttribute('student-job-delete-url');

		$.ajax({
			url: deleteApplicantUrl + '/' + student_job_applied,
			type: 'POST',
			beforeSend: function(xhr) {
				xhr.setRequestHeader(header, token);
			},
			success: function(response) {
				if (response == 1) {
					alert("Applicant Deleted successfully.");
					location.reload();
				}
				else {
					alert("Something went wrong!!! Please try again.");
					$("#ampm-loader").hide();
				}

			},
			error: function(xhr, status, error) {
				console.error('Error:', error);
				$("#ampm-loader").hide();
			}
		});
	}

}

function shortListApplicant(student_job_applied) {
	
	debugger;
	var confirmShortList = confirm("Are you sure you want to Short-List ?");
	if (confirmShortList) {
$("#ampm-loader").show();
		var token = $("meta[name='_csrf']").attr("content");
		var header = $("meta[name='_csrf_header']").attr("content");
		var shortListApplicantUrl = document.getElementById('shortListApplicant').getAttribute('short-list-student-url');

		$.ajax({
			url: shortListApplicantUrl + '/' + student_job_applied,
			type: 'POST',
			beforeSend: function(xhr) {
				xhr.setRequestHeader(header, token);
			},
			success: function(response) {
				if (response == 1) {
					alert("Applicant ShotListed successfully.");
					location.reload();
				}
				else {
					alert("Something went wrong!!! Please try again.");
					$("#ampm-loader").hide();
				}

			},
			error: function(xhr, status, error) {
				console.error('Error:', error);
				$("#ampm-loader").hide();
			}
		});
	}

}



function showModel(rowId) {
	debugger;
	var modal = document.getElementById(rowId);
	modal.style.display = "block";
}

function closeModel(rowId) {
	debugger;
	var modal = document.getElementById(rowId);
	modal.style.display = "none";
}

function alreadyShortListed(){
	alert("This Applicant already Short Listed.");
}


function alreadyDeleteApplicant(){
	alert("This Applicant already Deleted.");
}


function deleteApplicantNotPossible(){
	alert("This Applicant already Short Listed, Delete not possible!!!");
}


function viewJobDetails(jobId){
    	debugger;
    	
    	$("#jobId").val(jobId);
        $("#flag").val(3);
        $('#jobActionForm').attr('target', '_blank').submit();

    	
    }