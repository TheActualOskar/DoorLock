package dk.sdu.FreeKeyLocks.doorlock.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class HeartBeat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String message;
    private Timestamp timestamp;


    @ManyToOne
    @JoinColumn(name = "doorlock_id")
    @JsonBackReference
    private DoorLock doorLock;
}
