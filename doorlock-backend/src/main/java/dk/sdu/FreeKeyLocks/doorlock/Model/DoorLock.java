package dk.sdu.FreeKeyLocks.doorlock.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "doorlock")
public class DoorLock {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "doorlock_id")
    private int id;

    private String name;


    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;


    @OneToMany(mappedBy = "doorLock")
    @JsonManagedReference
    private Collection<HeartBeat> heartBeat;

    @OneToMany(mappedBy = "doorLock")
    @JsonManagedReference
    private List<LogEntry> logEntries;




}



