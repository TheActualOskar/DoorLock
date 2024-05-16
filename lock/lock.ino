#include <SPI.h>
#include <WiFi.h>
#include <NTPClient.h>
#include <WiFiUdp.h>
#include <Arduino.h>
#include <PubSubClient.h>
#include <time.h> 

#define GREEN_LED 15
#define YELLOW_LED 16
#define RED_LED 18

const char* ssid = "Totally_not_sus";
const char* password = "iamsecure123";

const char* mqtt_server = "id.joachimbaumann.dk";
const int mqtt_port = 1883;
const char* mqtt_username = "iot";
const char* mqtt_password = "iot";
const char* mqtt_client_id = "esp32_client";

WiFiClient wifiClient;
PubSubClient mqttClient(wifiClient);
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org", 0); // UTC offset in seconds

unsigned long lastUnlockTime = 0; // To control the unlock command time interval
unsigned long lastHeartbeatTime = 0;
bool isLocked = true;
unsigned long unlockTime = 0; // Time when the door was last unlocked
bool autoRelockEnabled = true; // Enable or disable automatic relocking


void setup() {
  Serial.begin(115200);
  delay(1000);


  pinMode(RED_LED, OUTPUT);
  pinMode(GREEN_LED, OUTPUT);
  pinMode(YELLOW_LED, OUTPUT); // Activating the yellow LED, assuming usage

  Serial.print("Connecting to ");
  Serial.println(ssid);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Connecting to WiFi...");
  }

  Serial.println("Connected to WiFi");
  Serial.print("IP Address: ");
  Serial.println(WiFi.localIP());

  timeClient.begin();
  
  mqttClient.setServer(mqtt_server, mqtt_port);
  mqttClient.setCallback(callback);
  reconnect();
  lockDoor();

}

void loop() {
    if (!mqttClient.connected()) {
        reconnect();
    }
    mqttClient.loop();
    timeClient.update();

    unsigned long currentTimeSec = timeClient.getEpochTime();
    if (currentTimeSec - lastHeartbeatTime >= 10) {
        sendHeartbeat();
    }

    // Check if it's time to re-lock the door
    if (!isLocked && autoRelockEnabled && (millis() - unlockTime >= 10000)) {
        lockDoor();
        Serial.println("Auto-relocking door after 10 seconds.");
    }
}

void reconnect() {
  while (!mqttClient.connected()) {
    Serial.println("Attempting MQTT connection...");
    if (mqttClient.connect(mqtt_client_id, mqtt_username, mqtt_password)) {
      Serial.println("Connected to MQTT broker");
      String topic = "sensor/unlock/" + String(1); // Subscribe to a topic
      mqttClient.subscribe(topic.c_str());
      Serial.println("Subscribed to: " + topic);
    } else {
      Serial.print("Failed, rc=");
      Serial.println(mqttClient.state());
      Serial.println(" Retrying in 5 seconds...");
      delay(5000);
    }
  }
}

void callback(char* topic, byte* payload, unsigned int length) {
    payload[length] = '\0';
    String fullCommand = String((char*)payload);
    int firstSpace = fullCommand.indexOf(' ');
    String command = fullCommand.substring(0, firstSpace);
    String timeStamp = fullCommand.substring(firstSpace + 1);

    // Remove milliseconds from timeStamp if present
    int dotIndex = timeStamp.indexOf('.');
    if (dotIndex != -1) {
        timeStamp = timeStamp.substring(0, dotIndex);
    }

    Serial.print("Received command: ");
    Serial.println(command);
    Serial.print("At time: ");
    Serial.println(timeStamp);

    if (command == "lock") {
        lockDoor();
    } else if (command == "unlock") {
        if (isWithinTimeWindow(timeStamp)) {
          unlockDoor("User");
            
        } else {
            Serial.println("Unlock command timestamp is not within the last 10 seconds.");
        }
    }
}

bool isWithinTimeWindow(String commandTime) {
    // Get the current time formatted as full date and time
    time_t rawtime = timeClient.getEpochTime(); // Get current epoch time
    struct tm * timeinfo; // Create a time structure
    char buffer[30]; // Buffer to hold the formatted date and time

    timeinfo = localtime(&rawtime); // Convert the epoch time to local time structure
    strftime(buffer, 30, "%Y-%m-%d %H:%M:%S", timeinfo); // Format the date and time

    
    // Print the current time
    Serial.print("Current Time: ");
    Serial.println(buffer);

    // Print the command time for comparison
    Serial.print("Command Time: ");
    Serial.println(commandTime);

    // Compare times to see if within 10 seconds
    return areTimesWithinTenSeconds(buffer, commandTime);
}

bool areTimesWithinTenSeconds(String currentTime, String commandTime) {
    struct tm tm_current;
    strptime(currentTime.c_str(), "%Y-%m-%d %H:%M:%S", &tm_current);
    time_t currentEpoch = mktime(&tm_current);

    struct tm tm_command;
    strptime(commandTime.c_str(), "%Y-%m-%d %H:%M:%S", &tm_command);
    time_t commandEpoch = mktime(&tm_command);

    int diff = abs(difftime(currentEpoch, commandEpoch));
    return diff <= 10;
}

int timeToSeconds(String time) {
    int hours = time.substring(0, 2).toInt();
    int minutes = time.substring(3, 5).toInt();
    int seconds = time.substring(6, 8).toInt();
    return hours * 3600 + minutes * 60 + seconds;
}

//TODO make it able to lock and lock it after 10 sec
void lockDoor() {
    digitalWrite(RED_LED, HIGH);   // RED LED on indicates the door is locked
    digitalWrite(GREEN_LED, LOW);  // Ensure the GREEN LED is off
    isLocked = true;               // Update lock status
    Serial.println("Door is locked.");
}

void unlockDoor(String userId) {
    digitalWrite(RED_LED, LOW);
    digitalWrite(GREEN_LED, HIGH);
    isLocked = false; // Update lock status

    unlockTime = millis(); // Record the time the door was unlocked

    char buffer[30];
    time_t now = timeClient.getEpochTime();
    struct tm * timeinfo = localtime(&now);
    strftime(buffer, 30, "%Y-%m-%d %H:%M:%S", timeinfo);
    
    String currentTime = String(buffer);
    String message = userId + " - door unlocked at " + currentTime;
    Serial.println(message);
    mqttClient.publish("sensor/unlock_door/1", message.c_str());
}



//TODO send heartbeat with current state
void sendHeartbeat() {
    // Get the current time in hours, minutes, and seconds
    char timeBuffer[30]; // Enough to hold "YYYY-MM-DD HH:MM:SS\0"
    time_t now = timeClient.getEpochTime();
    struct tm * timeinfo = localtime(&now);
    strftime(timeBuffer, sizeof(timeBuffer), "%Y-%m-%d %H:%M:%S", timeinfo);

    // Prepare the lock status message
    String lockStatus = isLocked ? "locked" : "unlocked";
    char msg[100];
    snprintf(msg, sizeof(msg), "alive");

    // Publish the heartbeat message to the MQTT topic
    mqttClient.publish("sensor/heartbeat/1", msg);
    Serial.println(msg);

    // Update the last heartbeat time
    lastHeartbeatTime = now;
}