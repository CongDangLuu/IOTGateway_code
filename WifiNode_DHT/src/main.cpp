#include <Arduino.h>
#include "painlessMesh.h"

#include <SPI.h>
#include <CC1101_RF.h>

#include <Adafruit_Sensor.h>
#include <DHT.h>
#include <DHT_U.h>

#include <BH1750.h>
#include <Wire.h>

float Temp, Humi, Light =0;
String Stt;
int cnt,flat =0;

typedef struct{
    uint32_t id[5];
    int cnt[5]={0,0,0,0,0};
    int Pre_cnt[5]={0,0,0,0,0};
  }Infor;

  Infor Wifi_c, Sub_c ;
 
  int index_ID = 1;// tu 1 den 5


  uint32_t ID_sub;
  uint32_t ID_Wifi;

  void set_infor();

//------------------DHT11--------------------
#define DHTPIN D1   // Digital pin connected to the DHT sensor 
#define DHTTYPE    DHT11     // DHT 11

DHT_Unified dht(DHTPIN, DHTTYPE);
void DHT11_setup();
void DHT11_data();
//------------------DHT11--------------------


//------------------BH1750--------------------
#define sda_pin D3
#define scl_pin D4

BH1750 lightMeter(0x23);
void BH1750_setup();
void BH1750_val();
//------------------BH1750--------------------


//******************CC1101*******************
#define Led_Sub 3

String data_SubTx, data_SubRx= "";
int Stt_SubLed = 0;

CC1101 radio(D8, D2);

void CC1101_Node_RX();
void CC1101_Node_TX();

//******************CC1101*******************

//++++++++++++++++++ESP8266++++++++++++++++++
#define Led_Wifi D0
int Stt_WifiLed = 0;

//mesh network
#define MESH_PREFIX     "IoTGateway"
#define MESH_PASSWORD   "IoTGateway2021"
#define MESH_PORT   5555


painlessMesh  mesh;
Scheduler userScheduler; 
unsigned long t =0;

void sendMessage(); 
Task taskSendMessage( TASK_SECOND * 1 , TASK_FOREVER, &sendMessage );
void receivedCallback( uint32_t from, String &msg );
void mesh_setup();

void newConnectionCallback(uint32_t nodeId) {
  //Serial.printf("--> startHere: New Connection, nodeId = %u\n", nodeId);
}
void changedConnectionCallback() {
  //Serial.printf("Changed connections\n");
}
void nodeTimeAdjustedCallback(int32_t offset) {
  //Serial.printf("Adjusted time %u. Offset = %d\n", mesh.getNodeTime(), offset);
}

//++++++++++++++++++ESP8266++++++++++++++++++
void setup() {
  Serial.begin(115200);
  
  mesh_setup();
 DHT11_setup();

  SPI.begin(); // mandatory. CC1101_RF does not start SPI automatically
  radio.begin(433.2e6); // Freq=433.2Mhz. Do not forget the "e6"
  radio.setRXstate(); // Set the current state to RX : listening for RF packets
  
  BH1750_setup();
  set_infor();
  delay(1000);
  digitalWrite(Led_Sub,LOW);
  
  
}
void loop() { 
  mesh.update();
  CC1101_Node_RX();
  if(millis()-t>1000){
    BH1750_val();
    Serial.println("Light = " +String(Light));
    t = millis();
    if(flat==1){
      cnt++;
    }
  }

  
}

void set_infor(){ 
  Wifi_c.id[0] = 2741624898;
  Wifi_c.id[1] = 2487404895;
  Wifi_c.id[2] = 2486190338;
  Wifi_c.id[3] = 4208757000;//
  Wifi_c.id[4] = 2741294106;//
 
  Sub_c.id[0] = 27918;
  Sub_c.id[1] = 89184;
  Sub_c.id[2] = 99632;
  Sub_c.id[3] = 75729;
  Sub_c.id[4] = 42593;   

  ID_sub = Sub_c.id[index_ID-1];
  ID_Wifi = Wifi_c.id[index_ID-1];
  pinMode(Led_Sub, OUTPUT);
  pinMode(Led_Wifi, OUTPUT);
  digitalWrite(Led_Sub,HIGH);
  digitalWrite(Led_Wifi,HIGH);
}

//------------------DHT11--------------------
void DHT11_setup(){
  dht.begin();
  sensor_t sensor;
  dht.temperature().getSensor(&sensor);
  // Print humidity sensor details.
  dht.humidity().getSensor(&sensor);
}
void DHT11_data(){
  // Get temperature event and print its value.
  sensors_event_t event;
  dht.temperature().getEvent(&event);
  if (isnan(event.temperature)) {
    Serial.println(F("Error reading temperature!"));
  }
  else {
    Temp = event.temperature;
  }
  // Get humidity event and print its value.
  dht.humidity().getEvent(&event);
  if (isnan(event.relative_humidity)) {
    Serial.println(F("Error reading humidity!"));
  }
  else {
    Humi = event.relative_humidity;
  }
}
//------------------DHT11--------------------

//------------------BH1750--------------------
void BH1750_setup(){
  Wire.begin(sda_pin,scl_pin);
  if (lightMeter.begin(BH1750::CONTINUOUS_HIGH_RES_MODE)) {
    Serial.println(F("BH1750 Advanced begin"));
  } else {
    Serial.println(F("Error initialising BH1750"));
  }
}
void BH1750_val(){
   if (lightMeter.measurementReady()) {
    Light = lightMeter.readLightLevel();
  }
}
//------------------BH1750--------------------

//******************CC1101*******************

void CC1101_Node_RX(){
    data_SubRx ="";
    byte CC1101_rx[64];
    byte pkt_size = radio.getPacket(CC1101_rx);
    if (pkt_size>0 && radio.crcok()) { // We have a valid packet with some data
      Serial.println(cnt);
      flat=0;
      cnt =0; 
        for(int i=0; i<pkt_size;i++)            {
            data_SubRx += String(char(CC1101_rx[i]));
        }
        
        //request,id
        //control,id,0
       
        String cmd = data_SubRx.substring(0,data_SubRx.indexOf(','));
        data_SubRx.remove(0,data_SubRx.indexOf(',')+1);
        String ID_rx = data_SubRx.substring(0,data_SubRx.indexOf(','));
        data_SubRx.remove(0,data_SubRx.indexOf(',')+1);
        String data = data_SubRx;

        if(String(ID_sub) == ID_rx){
          Serial.println("Nhan lenh tu SubGhz: " + data_SubRx);
            if( cmd=="request") {
                CC1101_Node_TX();
            }
            else if(cmd=="control"){
                Serial.println("Nhan lenh control: " + data);
                Stt_SubLed = data.toInt();
                digitalWrite(Led_Sub, !Stt_SubLed);
                CC1101_Node_TX();
            }     
        }   
    }
}

void CC1101_Node_TX(){
    DHT11_data();
    BH1750_val();
    Stt ="OFF";
    if(Stt_SubLed){
        Stt="ON";
    }
     data_SubTx = String(ID_sub) + ",SubGHzNode"+String(index_ID)+",TEMP:"+String(Temp)+",HUMI:"+String(Humi)+",LIGHT:"+String(Light)+","+Stt;
    //data_SubTx = "42593,SubGHzNode5,TEMP:23.43,HUMI:45.23,LIGHT:0.00,ON;
    int n = data_SubTx.length()+1;
    char arr_data[n];
    data_SubTx.toCharArray(arr_data,n);
    bool success = radio.sendPacket(arr_data);
    while(!(success)){
        success = radio.sendPacket(arr_data);
    }
    Serial.println("SubGhz da duoc gui thanh cong : "+ data_SubTx);
}
//******************CC1101*******************

//++++++++++++++++++ESP8266++++++++++++++++++
void mesh_setup(){
  mesh.setDebugMsgTypes( ERROR | STARTUP );  
  mesh.init( MESH_PREFIX, MESH_PASSWORD, &userScheduler, MESH_PORT );
  mesh.onReceive(&receivedCallback);
  mesh.onNewConnection(&newConnectionCallback);
  mesh.onChangedConnections(&changedConnectionCallback);
  mesh.onNodeTimeAdjusted(&nodeTimeAdjustedCallback);
  // userScheduler.addTask( taskSendMessage );
  // taskSendMessage.enable();
}
void sendMessage()
{
  DHT11_data();
  BH1750_val();
  Serial.println("____________ send");
  
  // Serializing in JSON Format
    DynamicJsonDocument doc(1024);
    String stt = "OFF";
    String msg ;
    if(Stt_WifiLed){
      stt = "ON";
    }
    doc["N"] = "WifiNode" +String(index_ID);
    doc["T"] = String(Temp);
    doc["H"] = String(Humi);
    doc["L"] = String(Light);
    doc["S"] = stt;

    serializeJson(doc, msg);
    mesh.sendBroadcast( msg );
    Serial.println("Message from DHT node:");
    Serial.println(msg);
}
void receivedCallback( uint32_t from, String &msg ) {
  
  Serial.print("Message control form app= ");Serial.println(msg);
  String json;
  DynamicJsonDocument doc(1024);
  json = msg.c_str();
  DeserializationError error = deserializeJson(doc, json);
  if (error)
  {
    Serial.print("deserializeJson() failed: ");
    Serial.println(error.c_str());
  }
  else{
    const char* str = doc["cmd"];
    String cmd = String(str);
    if(cmd == "request"){
      sendMessage();
    }
    else{
      Stt_WifiLed = int(doc["Stt"]); 
      
      digitalWrite(Led_Wifi, !Stt_WifiLed);
    } 
  }
  flat =1;
}
//++++++++++++++++++ESP8266++++++++++++++++++








