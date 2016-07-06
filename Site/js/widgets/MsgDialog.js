
function hideDialog(){
    $("#msgBox").hide();
    $("#alertBox").hide();    
}

function showMsg(msgText, duration){
	$("#msgBoxText").text(msgText);
	
	if($("#msgBox").css("display") == "none"){
		 $("#msgBox").show("blind", 
			function (){
				$("#msgBox").delay(duration || 5000).hide("blind");
			}
		);
	}
	

    
}

function showAlert(msgText, duration){
    $("#alertBoxText").text(msgText);
	
	if($("#alertBox").css("display") == "none"){
		$("#alertBox").show("blind", 
			function (){
				$("#alertBox").delay(duration || 5000).hide("blind");
			}
		);
	}
}
