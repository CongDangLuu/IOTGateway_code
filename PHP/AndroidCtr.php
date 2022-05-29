   <?php
  
   $file_name = 'control/control.json';
   $data = json_decode($jsonString, true);
   

    $user='abcd_ef';
    
	if ($_SERVER["REQUEST_METHOD"] == "POST") {
	
      $data['Device'] = test_input($_POST["Device"]);
      $data['Stt'] = test_input($_POST["Stt"]);
      $data['MCU'] = test_input($_POST["MCU"]);
      
      $jsonString = file_get_contents($file_name);
      $newJsonString = json_encode($data);
      file_put_contents($file_name, $newJsonString);
	}

	
 function test_input($data) {
    $data = trim($data);
    $data = stripslashes($data);
    $data = htmlspecialchars($data);
    return $data;
}
   ?>  