<?php
	$servername = "localhost";
	
    $username = "telemetry";
	$password = "";
	$db = "telemetry_teamzeroc";

	$conn = mysqli_connect($servername, $username, $password);
	mysqli_select_db($conn, $db);
	
	if (mysqli_connect_errno()) {
		die('<p style = "color: red"> Connection failed: ' . $conn->connect_error . '<p>');
	} 
	
?>	