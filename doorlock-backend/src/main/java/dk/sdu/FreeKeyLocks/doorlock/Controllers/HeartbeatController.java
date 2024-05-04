package dk.sdu.FreeKeyLocks.doorlock.Controllers;

import dk.sdu.FreeKeyLocks.doorlock.Model.DoorLock;
import dk.sdu.FreeKeyLocks.doorlock.Model.HeartBeat;
import dk.sdu.FreeKeyLocks.doorlock.Model.LogEntry;
import dk.sdu.FreeKeyLocks.doorlock.Repository.DoorLockRepository;
import dk.sdu.FreeKeyLocks.doorlock.Repository.HeatBeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@CrossOrigin("*")
@RequestMapping("/heartbeat")
@RestController
public class HeartbeatController {

    @Autowired
    private HeatBeatRepository heartBeatRepository;

    @Autowired
    private DoorLockRepository doorLockRepository;

    @GetMapping("/")
    public List<HeartBeat> getHeartbeats() {
        return heartBeatRepository.findAll();
    }
    @GetMapping("/{door_id}")
    public List<HeartBeat> getHeartbeats(@PathVariable("door_id") int door_id) {
        DoorLock doorLock = doorLockRepository.getReferenceById(door_id);
        if (doorLock != null) {
            return doorLock.getHeartBeat().stream().toList();
        } else return null;
    }

    @GetMapping("/test")
    public ResponseEntity testAddEntry() {

        HeartBeat heartBeat = new HeartBeat();
        heartBeat.setTimestamp(new Timestamp(System.currentTimeMillis()));
        heartBeat.setMessage("Random test message: " + new Timestamp(System.currentTimeMillis()).toString());
        heartBeat.setDoorLock(doorLockRepository.getReferenceById(2));
        heartBeatRepository.save(heartBeat);

        return ResponseEntity.ok("saved test to database.");
    }


}
