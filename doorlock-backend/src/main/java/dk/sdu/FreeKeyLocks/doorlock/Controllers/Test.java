package dk.sdu.FreeKeyLocks.doorlock.Controllers;

import dk.sdu.FreeKeyLocks.doorlock.Model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RequestMapping("/test")
@RestController
public class Test {

    @GetMapping(value = "/{id}")
    public ResponseEntity test(@PathVariable("id") int id) {

        MqttController.unlockDoorLock(id);

        return ResponseEntity.ok("happy life, happy something else");

    }
}
