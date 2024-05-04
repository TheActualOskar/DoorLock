package dk.sdu.FreeKeyLocks.doorlock.Controllers;

import dk.sdu.FreeKeyLocks.doorlock.Model.DoorLock;
import dk.sdu.FreeKeyLocks.doorlock.Model.User;
import dk.sdu.FreeKeyLocks.doorlock.Model.LoginUserReq;
import dk.sdu.FreeKeyLocks.doorlock.Model.UnlockDoorReq;
import dk.sdu.FreeKeyLocks.doorlock.Repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin("*")
@RequestMapping("/user")
@RestController
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository repository) {
        this.userRepository = repository;
    }

    @CrossOrigin("*")
    @PostMapping(value = "/")
    public ResponseEntity createUser(@RequestBody User user) {
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok("Successfully created user: " + savedUser);
    }

    @GetMapping("/")
    public List<User> getUsers(){
        return userRepository.findAll();

    }

    @PostMapping(value = "/unlock")
    public ResponseEntity unlock(@RequestBody UnlockDoorReq unlockDoorReq) {
        Optional<User> user = userRepository.findById(unlockDoorReq.getUserID());
        if (user.isPresent()) {
            List<DoorLock> doorLocks = user.get().getDoorLocks().stream().toList();
            if(doorLocks.get(0).getId() == unlockDoorReq.getDoorID()) {
                return ResponseEntity.ok("Door successfully unlocked");
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

    @PostMapping(value = "/login")
    public ResponseEntity login(@RequestBody LoginUserReq loginUserReq) {
        System.out.println("some request came.. idk random?");
        User userData = userRepository.findByEmail(loginUserReq.getEmail());
        if (userData != null) {
            if(userData.getPassword().equals(loginUserReq.getPassword())){
                return ResponseEntity.ok(userData.getID());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();}
}
