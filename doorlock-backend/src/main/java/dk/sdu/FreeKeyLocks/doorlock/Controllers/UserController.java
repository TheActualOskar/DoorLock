package dk.sdu.FreeKeyLocks.doorlock.Controllers;

import dk.sdu.FreeKeyLocks.doorlock.Model.*;
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
    private final LogEntryRepository logEntryRepository;

    public UserController(UserRepository repository, LogEntryRepository logEntryRepository) {
        this.userRepository = repository;
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
            List<DoorLock> doorLocks = user.get().getDoorLocks();
            doorLocks.addAll(user.get().getAccessList());
            for (DoorLock doorLock : doorLocks) {
                if (doorLock.getId() == unlockDoorReq.getDoorID()) {
                    LogEntry logEntry = new LogEntry();
                    logEntry.setMessage("Door unlocked by: \n" + user.get().getFirstName() + " " + user.get().getLastName() + "\n" + user.get().getEmail());
                    logEntry.setTimestamp(new Timestamp(System.currentTimeMillis()));
                    logEntry.setDoorLock(doorLock);
                    logEntryRepository.save(logEntry);
                    MqttController.unlockDoorLock(doorLock.getId());
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
            List<DoorLock> doorLocks = user.get().getDoorLocks();
            doorLocks.addAll(user.get().getAccessList());
            for (DoorLock doorLock : doorLocks) {
                if (doorLock.getId() == lockDoorReq.getDoorID()) {
                    LogEntry logEntry = new LogEntry();
                    logEntry.setMessage("Door locked by: \n" + user.get().getFirstName() + " " + user.get().getLastName() + "\n" + user.get().getEmail());
                    logEntry.setTimestamp(new Timestamp(System.currentTimeMillis()));
                    logEntry.setDoorLock(doorLock);
                    logEntryRepository.save(logEntry);
                    MqttController.lockDoorLock(doorLock.getId());
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
            List<DoorLock> doorLocks = userData.get().getDoorLocks();
            doorLocks.addAll(userData.get().getAccessList());


            return new ResponseEntity<>(doorLocks, HttpStatus.OK);
        }

        return ResponseEntity.ok("User not found");
    }

    @PostMapping(value = "/login")
    public ResponseEntity login(@RequestBody LoginUserReq loginUserReq) {
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
