package dk.sdu.FreeKeyLocks.doorlock.Controllers;


import dk.sdu.FreeKeyLocks.doorlock.Model.DoorLock;
import dk.sdu.FreeKeyLocks.doorlock.Model.LogEntry;
import dk.sdu.FreeKeyLocks.doorlock.Repository.DoorLockRepository;
import dk.sdu.FreeKeyLocks.doorlock.Repository.LogEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@CrossOrigin("*")
@RequestMapping("/log")
@RestController
public class LogEntryController {

    @Autowired
    private LogEntryRepository logEntryRepository;
    @Autowired
    private DoorLockRepository doorLockRepository;


    @GetMapping("/")
    public List<LogEntry> getAllLogEntries() {
        return logEntryRepository.findAll();
    }

    @GetMapping("/{door_id}")
    public List<LogEntry> getLogEntries(@PathVariable("door_id") int door_id) {
        DoorLock doorLock = doorLockRepository.getReferenceById(door_id);
        if (doorLock != null) {
            return doorLock.getLogEntries().stream().toList();
        } else return null;
    }


}d
