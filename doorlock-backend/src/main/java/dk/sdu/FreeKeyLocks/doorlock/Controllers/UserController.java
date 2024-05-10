package dk.sdu.FreeKeyLocks.doorlock.Controllers;

import dk.sdu.FreeKeyLocks.doorlock.Model.*;
import dk.sdu.FreeKeyLocks.doorlock.Repository.DoorLockRepository;
import dk.sdu.FreeKeyLocks.doorlock.Repository.LogEntryRepository;
import dk.sdu.FreeKeyLocks.doorlock.Repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;

@CrossOrigin("*")
@RequestMapping("/user")
@RestController
public class UserController {

    private final UserRepository userRepository;
    private final DoorLockRepository doorLockRepository;
    private final LogEntryRepository logEntryRepository;

    public UserController(UserRepository repository, DoorLockRepository doorLockRepository, LogEntryRepository logEntryRepository) {
        this.userRepository = repository;
        this.doorLockRepository = doorLockRepository;
        this.logEntryRepository = logEntryRepository;
    }

    @CrossOrigin("*")
    @PostMapping(value = "/")
    public ResponseEntity createUser(@RequestBody User user) {
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok("Successfully created user: " + savedUser);
    }

    @GetMapping("/")
    public List<User> getUsers() {
        return userRepository.findAll();

    }

    //TODO add logic with smart door lock
    @PostMapping(value = "/unlock")
    public ResponseEntity unlock(@RequestBody UnlockDoorReq unlockDoorReq) {
        Optional<User> user = userRepository.findById(unlockDoorReq.getUserID());
        if (user.isPresent()) {
            List<DoorLock> doorLocks = user.get().getDoorLocks().stream().toList();

            for (DoorLock doorLock : doorLocks) {
                if (doorLock.getId() == unlockDoorReq.getDoorID()) {
                    LogEntry logEntry = new LogEntry();
                    logEntry.setMessage("Door unlocked by:" + unlockDoorReq.getUserID());
                    logEntry.setTimestamp(new Timestamp(System.currentTimeMillis()));
                    logEntry.setDoorLock(doorLocks.get(0));
                    logEntryRepository.save(logEntry);
                    MqttController.unlockDoorLock(doorLocks.get(0).getId());
                    return ResponseEntity.ok("Door successfully unlocked");
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }

    //TODO add logic with smart door lock
    @PostMapping(value = "/lock")
    public ResponseEntity lock(@RequestBody LockDoorReq lockDoorReq) {
        Optional<User> user = userRepository.findById(lockDoorReq.getUserID());
        if (user.isPresent()) {
            List<DoorLock> doorLocks = user.get().getDoorLocks().stream().toList();
            for (DoorLock doorLock : doorLocks) {
                if (doorLock.getId() == lockDoorReq.getDoorID()) {
                    LogEntry logEntry = new LogEntry();
                    logEntry.setMessage("Door locked by:" + lockDoorReq.getUserID());
                    logEntry.setTimestamp(new Timestamp(System.currentTimeMillis()));
                    logEntry.setDoorLock(doorLocks.get(0));
                    logEntryRepository.save(logEntry);
                    MqttController.lockDoorLock(doorLocks.get(0).getId());
                    return ResponseEntity.ok("Door successfully locked");
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") int id) {
        Optional<User> userData = userRepository.findById(id);

        if (userData.isPresent()) {
            return new ResponseEntity<>(userData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/doors/{user_id}")
    public ResponseEntity getDoors(@PathVariable("user_id") int id) {

        Optional<User> userData = userRepository.findById(id);
        if (userData.isPresent()) {

            return new ResponseEntity<>(userData.get().getDoorLocks(), HttpStatus.OK);
        }

        return ResponseEntity.ok("test");
    }

    @PostMapping(value = "/login")
    public ResponseEntity login(@RequestBody LoginUserReq loginUserReq) {
        System.out.println("some request came.. idk random?");
        User userData = userRepository.findByEmail(loginUserReq.getEmail());
        if (userData != null) {
            if (userData.getPassword().equals(loginUserReq.getPassword())) {
                return ResponseEntity.ok(userData);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}
