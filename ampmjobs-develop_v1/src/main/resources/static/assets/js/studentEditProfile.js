$(document).ready(function() {
    $('#editStudentUserProfile').submit(function(event) {
      event.preventDefault();
     
     var token = $("meta[name='_csrf']").attr("content");
		var header = $("meta[name='_csrf_header']").attr("content");
     
     var qualification = $("#qualifications").val();
     
     var address1 = $("#address1").val();
     var address2 = $("#address2").val();
     
     var state = $("#state").val();
     var city = $("#city").val();
     var fileData ="";
        var pincode = $("#login-pin").val();
   /* var fileData =  $("input[name='imageFile']")[0].files[0];
        if(fileData!=null){
        var maxSize = 500 * 1024; // 500 KB, adjust this as needed
        var fileSize = fileData.size;
        if (fileSize > maxSize) {
            alert('File size exceeds the maximum limit of 500 KB');
            $('#userPic').val('');
            return false;
        }
        }*/
     $("#ampm-loader").show();
         var updateErUrl = $("#editStudentUserProfile").attr("action");

     
     debugger;

		var xhr = new XMLHttpRequest();
            xhr.open("POST", updateErUrl);
                xhr.setRequestHeader(header, token);

            xhr.onreadystatechange = function() {
                if (xhr.readyState === XMLHttpRequest.DONE) {
                    if (xhr.status === 200) {
                        var dbRes = xhr.responseText;
                        if(dbRes==1){
							alert("Details updated successfully");
							location.reload();
						}
						else{
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
            formDataWithFile.append("qualification", qualification);
            formDataWithFile.append("address1", address1);
            formDataWithFile.append("address2", address2);
            formDataWithFile.append("state", state);
            formDataWithFile.append("city", city);
            formDataWithFile.append("zipcode", pincode);
            formDataWithFile.append("location", $("#location").val());
            
             if(fileData!=null){
            formDataWithFile.append("imageFile", fileData);
            }
            xhr.send(formDataWithFile);
           
     
    });
});

function loadCities(stateId) {
				
				var select = document.getElementById("location");
					var optionsToRemove = select.querySelectorAll("option");
					optionsToRemove.forEach(function(option) {
						option.remove();
					});

					var option = document.createElement("option");
					option.value = ""; 
					option.text = "Preferred Place"; 
					select.appendChild(option);
				
				if (stateId == 0) {
					var select = document.getElementById("city");
					var optionsToRemove = select.querySelectorAll("option");
					optionsToRemove.forEach(function(option) {
						option.remove();
					});

					var option = document.createElement("option");
					option.value = "0"; 
					option.text = "City"; 
					select.appendChild(option);
				} else {
					$("#ampm-loader").show();
					var templateUrl = document.getElementById('loadTemplate')
							.getAttribute('template-url');
					$.ajax({
						url : templateUrl + "/" + stateId + '/city',
						type : 'GET',
						success : function(html) {
							$('#city').html(html);
							$("#ampm-loader").hide();
						},
						error : function(xhr, status, error) {
							console.error('Error loading template:', error);
							$("#ampm-loader").hide();
						}
					});
				}

			}
			
			
			
			
function loadLocations(cityId) {
				
				
				if (cityId == 0) {
					var select = document.getElementById("location");
					var optionsToRemove = select.querySelectorAll("option");
					optionsToRemove.forEach(function(option) {
						option.remove();
					});

					var option = document.createElement("option");
					option.value = "0"; 
					option.text = "Preferred Place"; 
					select.appendChild(option);
				} else {
					$("#ampm-loader").show();
					var templateUrl = document.getElementById('loadTemplate')
							.getAttribute('template-url');
					$.ajax({
						url : templateUrl + "/" + cityId + '/location',
						type : 'GET',
						success : function(html) {
							$('#location').html(html);
							$("#ampm-loader").hide();
						},
						error : function(xhr, status, error) {
							console.error('Error loading template:', error);
							$("#ampm-loader").hide();
						}
					});
				}

			}			