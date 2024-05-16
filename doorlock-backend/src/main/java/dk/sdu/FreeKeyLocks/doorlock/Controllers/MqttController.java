package dk.sdu.FreeKeyLocks.doorlock.Controllers;

import com.github.tocrhz.mqtt.annotation.MqttSubscribe;
import com.github.tocrhz.mqtt.annotation.Payload;
import com.github.tocrhz.mqtt.publisher.MqttPublisher;
import dk.sdu.FreeKeyLocks.doorlock.Model.DoorLock;
import dk.sdu.FreeKeyLocks.doorlock.Model.LogEntry;
import dk.sdu.FreeKeyLocks.doorlock.Repository.DoorLockRepository;
import dk.sdu.FreeKeyLocks.doorlock.Repository.LogEntryRepository;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;

@CrossOrigin("*")
@RequestMapping("/test")
@RestController
@Component
public class MqttController {
    @Autowired
    DoorLockRepository doorLockRepository;

    @Autowired
    LogEntryRepository logEntryRepository;

    private static MqttPublisher publisher;
    @Autowired
    private DeviceStatusService deviceStatusService;

    public MqttController(MqttPublisher publisher) {
        this.publisher = publisher;
    }

    public static void lockDoorLock(int id) {

        publisher.send("sensor/unlock/" + id, "lock " + new Timestamp(System.currentTimeMillis()), 2);
    }


    public static void unlockDoorLock(int id) {

        publisher.send("sensor/unlock/" + id, "unlock " + new Timestamp(System.currentTimeMillis()), 2);
    }


    //TODO is this usefull?
    @MqttSubscribe(value = "sensor/unlock/+", qos = 1)
    public void mqttrecieve(String topic, MqttMessage message, @Payload String payload) {
        System.out.println("message from the gods: " + payload);
    }

    @MqttSubscribe(value = "sensor/log/+", qos = 1)
    public void mqttrecieveLog(String topic, MqttMessage message, @Payload String payload) {
        LogEntry log = new LogEntry();

        String[] temp_id = topic.split("/");
        int id = Integer.parseInt(temp_id[2]);
        DoorLock doorLock = doorLockRepository.getReferenceById(id);


        log.setTimestamp(new Timestamp(System.currentTimeMillis()));
        log.setDoorLock(doorLock);
        log.setMessage(message.toString());

        logEntryRepository.save(log);

    }

    @MqttSubscribe(value = "sensor/heartbeat/+", qos = 1)
    public void mqttrecieveheartbeat(String topic, MqttMessage message, @Payload String payload) {


        topic = topic.split("/")[2];
        deviceStatusService.handleHeartbeatMessage(Integer.parseInt(topic), Instant.now());


    }
}
