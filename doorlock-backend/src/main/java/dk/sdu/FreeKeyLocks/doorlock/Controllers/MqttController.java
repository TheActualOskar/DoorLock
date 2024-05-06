package dk.sdu.FreeKeyLocks.doorlock.Controllers;

import com.github.tocrhz.mqtt.annotation.MqttSubscribe;
import com.github.tocrhz.mqtt.annotation.Payload;
import com.github.tocrhz.mqtt.publisher.MqttPublisher;
import dk.sdu.FreeKeyLocks.doorlock.Model.DoorLock;
import dk.sdu.FreeKeyLocks.doorlock.Model.HeartBeat;
import dk.sdu.FreeKeyLocks.doorlock.Model.LogEntry;
import dk.sdu.FreeKeyLocks.doorlock.Repository.DoorLockRepository;
import dk.sdu.FreeKeyLocks.doorlock.Repository.HeatBeatRepository;
import dk.sdu.FreeKeyLocks.doorlock.Repository.LogEntryRepository;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
@CrossOrigin("*")
@RequestMapping("/test")
@RestController
@Component
public class MqttController {
    @Autowired
    DoorLockRepository doorLockRepository;
    @Autowired
    HeatBeatRepository heatBeatRepository;
    @Autowired
    LogEntryRepository logEntryRepository;

    private static MqttPublisher publisher;

    public MqttController(MqttPublisher publisher) {
        this.publisher = publisher;
    }


    //TODO add unlock logic
    @GetMapping("/{id}")
    public static void unlockDoorLock(@PathVariable("id") int id) {

        publisher.send("sensor/unlockdoor/" + id,"Hello",2);
    }


    //TODO is this usefull?
    @MqttSubscribe("sensor/unlock/+")
    public void mqttrecieve(String topic, MqttMessage message, @Payload String payload) {
        System.out.println("message from the gods: " + payload);
    }

    @MqttSubscribe("sensor/log/+")
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

    @MqttSubscribe("sensor/heartbeat/+")
    public void mqttrecieveheartbeat(String topic, MqttMessage message, @Payload String payload) {
        HeartBeat heartBeat = new HeartBeat();

        String[] temp_id = topic.split("/");
        int id = Integer.parseInt(temp_id[2]);
        DoorLock doorLock = doorLockRepository.getReferenceById(id);


        heartBeat.setTimestamp(new Timestamp(System.currentTimeMillis()));
        heartBeat.setDoorLock(doorLock);
        heartBeat.setMessage(message.toString());

        heatBeatRepository.save(heartBeat);


    }
}
