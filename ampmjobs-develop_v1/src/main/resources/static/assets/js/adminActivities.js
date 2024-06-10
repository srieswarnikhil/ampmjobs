$(document).ready(function() {
	$('#res-cus').click(function() {
		$("#enter-email").val('');
		$("#login-number").val('');
		$('#cResponse').hide();
	});
	$('#val-cus').click(function(event) {
		event.preventDefault();
		debugger;
		var cEmail = $("#enter-email").val();

		if (cEmail == null || cEmail == "") {
			alert("Enter email.");
			$("#enter-email").focus();
			return false;
		}


		var cPhone = $("#login-number").val();

		if (cPhone == null || cPhone == "") {
			alert("Enter phone.");
			$("#login-number").focus();
			return false;
		}

		$("#ampm-loader").show();
		var cdUserUrl = document.getElementById('getCd').getAttribute('cd-url');
		$.ajax({
			url: cdUserUrl + "/" + cEmail + "/" + cPhone,
			type: 'GET',
			async: false,
			success: function(res) {
				debugger;
				$('#cResponse').html(res);
				$('#cResponse').show();
				$("#ampm-loader").hide();
			},
			error: function(xhr, status, error) {
				console.error('Error loading template:', error);
				$("#ampm-loader").hide();
			}
		});


	});

	/* er-re start*/



	$('#searchBox').on('input', function() {
		debugger;
		var inData = $(this).val();
		var srUrl = document.getElementById('getSR').getAttribute('sr-url');

		$.ajax({
			type: 'GET',
			url: srUrl,
			data: { inData: inData },
			success: function(data) {
				debugger;
				$('#searchBox').autocomplete({
					source: data,
					minLength: 0
				}).focus(function() {
					$(this).autocomplete("search");
				});
			},
			error: function() {
				console.error('Error fetching search results');
			}
		});
	});


	/*er-re end*/
});



function updateDiscount() {
	debugger;
	var numDiscount = $("#num-discount").val();

	if (numDiscount == null || numDiscount == "") {
		alert("Enter discount");
		$("#num-discount").focus();
		return false;
	}

	if ($.isNumeric(numDiscount)) {
		if (numDiscount > 99) {
			alert("This is not allowed.");
			$("#num-discount").val('');
			$("#num-discount").focus();
			return false;
		}
	} else {
		alert("Enter numbers only.");
		$("#num-discount").val('');
		$("#num-discount").focus();
		return false;
	}

	var cUserRole = $("#cUserRole").val();

	var selectedPayId = 1;
	if (cUserRole != "STUDENT") {



		var selectedPayId = $("input[name='selectedPayId']:checked").val();
		if (selectedPayId == null || selectedPayId == "") {
			alert("Selected Pay type");
			return false;
		}
	}

	var userId = $("#cUserId").val();

	$("#ampm-loader").show();
	var updateDisUrl = document.getElementById('updateDiscountInfo').getAttribute('up-discount-url');
	$.ajax({
		url: updateDisUrl + "/" + numDiscount + "/" + userId + "/" + selectedPayId,
		type: 'GET',
		success: function(response) {
			$("#ampm-loader").hide();
			if (response == 1) {
				alert("discount Updated.");
			}
			else {
				alert("Something went wrong");
			}

		},
		error: function(xhr, status, error) {
			console.error('Error loading template:', error);
			$("#ampm-loader").hide();
		}
	});

}



function goBack() {
	$("#cdInfoDivId").hide();
	$("#disCountDivId").show();
}

function doAssign() {

	debugger;
	var erIn = $("#erIn").val();
	var reIn = $("#reIn").val();

	if (erIn == null || erIn.trim() == "" || erIn.trim() == "-") {
		alert("please enter employer details");
		$("#erIn").focus();
		return false;
	}


	if (reIn == null || reIn.trim() == "" || reIn.trim() == "-") {
		alert("please enter recruiter details");
		$("#reIn").focus();
		return false;
	}
	
	if(erIn==reIn){
		alert("both employer and recruiter details must not same.");
		return false;
	}

	if (confirm("Are you sure you want Assgin ?")) {


		alert("erIn : " + erIn);
		alert("reIn : " + reIn);
	}

}

function reset() {
	debugger;

	if (confirm("Are you sure you want reset data ?")) {

		$("#erIn").val("");
		$("#reIn").val("");
	}
}