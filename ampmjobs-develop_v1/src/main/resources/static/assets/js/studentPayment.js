$(document).ready(function() {
	$("#payment-type-select").change(function() {
		debugger;
		var selectedOption = $(this).find("option:selected");
		var amtPay = selectedOption.data("amt_pay");
		$("#amt-pay-span").text(amtPay);

	});
	
	 $('#studentPayForm').submit(function(event) {
      event.preventDefault();
      checkPayment();
    });
    
    
    function checkPayment(){
		debugger;
		
		var selectedPayment = $("#payment-type-select").val();
		if(selectedPayment ==0){
			alert("Select Subscription Duration.");
			$("#payment-type-select").focus();
			return false;
		}
		
		var payUrl = document.getElementById('payUrl').getAttribute('validate-payment-url');
		window.location.href = payUrl+"?payId="+selectedPayment;
		
		
		
		
	}
});



