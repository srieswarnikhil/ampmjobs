$(document).ready(function() {

	function onlyNum() {
		$('.onlyNum').on('input', function() {
			$(this).val($(this).val().replace(/[^0-9]/g, ''));

		});
	}
	onlyNum();

	function onlyCharAndDot() {
		$('.charAndDot').on('input', function() {
			$(this).val($(this).val().replace(/^[\s.]*([^a-zA-Z\s.].*)?/g, ''));
		});
	}
	onlyCharAndDot();

	function onlyAlphabetAndDigit() {
		$('.gst-no-chk').on('input', function() {
			$(this).val($(this).val().toUpperCase().replace(/[^A-Z0-9]/g, ''));
		});
	}
	onlyAlphabetAndDigit();


});