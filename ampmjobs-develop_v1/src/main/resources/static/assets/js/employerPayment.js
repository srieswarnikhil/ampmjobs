    
// er payment
function checkPayment(){
	debugger;
	
	var payId = $("#payment-type-select").val();
	if(payId==null || payId==""){
		alert("Select Subscription Duration");
		$("#payment-type-select").focus();
		return false;
	}
	
	
    var erAddInfo = document.getElementById('erAddInfo').getAttribute('er-data-url');
    location.href=erAddInfo+"/"+payId;
	
}
$(document).ready(function() {
    $('#paymentFormId').submit(function(event) {
      event.preventDefault();
      checkPayment();
    });
});