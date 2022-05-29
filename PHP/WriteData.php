<?php
 
$servername = "localhost";
$dbname = "highalln_iotgateway_db"; // Database name
$username = "highalln_gateway";// Database user
$password = "LuanVanTotNghiep@@2022";//Database user password


$api_key_value = "iotgateway2021";
 
$api_key= $Name = $Stt = $ND = $DA = $AS = $Table =  "";

$file_name ="WriteDataJson/";

$data = json_decode($jsonString, true);
 
date_default_timezone_set('Asia/Ho_Chi_Minh');
$timest = date('Y-m-d H:i:s'); 
 
var_dump($_POST);
 
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $api_key = test_input($_POST["api_key"]);
    if($api_key == $api_key_value) {
        
        $data['Device'] = $Table = test_input($_POST["Table"]);
        $data['Name'] = $Name = test_input($_POST["Name"]);
        $data['Temperature'] = $ND = test_input($_POST["Temp"]);
        $data['Humidity'] = $DA = test_input($_POST["Humi"]);
        $data['Light'] = $AS = test_input($_POST["Light"]);
        $data['Status'] = $Stt = test_input($_POST["Stt"]);
        $data['Time'] = $timest;
        
       
        $file_name .= $Table;
        $file_name .= ".json";
        
        
        // Create connection
        $conn = new mysqli($servername, $username, $password, $dbname);
        // Check connection
        if ($conn->connect_error) {
            die("Connection failed: " . $conn->connect_error);
        } 
        $val = " (NameDV, Status_dv, Temperature, Humidity, Light, Timestamp)
        VALUES ('$Name', '$Stt', '$ND', '$DA', '$AS', '$timest')";
        
        $sql = "INSERT INTO ";
        $sql .= $Table;
        $sql .= $val;
        
        if ($conn->query($sql) === TRUE) {
            echo "New record created successfully";
            
            //xuat file json.
            $jsonString = file_get_contents($file_name);
            $newJsonString = json_encode($data);
            file_put_contents($file_name, $newJsonString);
        } 
        else {
            echo "Error: " . $sql . "<br>" . $conn->error;
        }
    
        $conn->close();
    }
    else {
        echo "Wrong API Key provided.";
    }
 
}
else {
    echo "No data posted with HTTP POST.";
}

function test_input($data) {
    $data = trim($data);
    $data = stripslashes($data);
    $data = htmlspecialchars($data);
    return $data;
}

?> 