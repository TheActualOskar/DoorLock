package dk.sdu.FreeKeyLocks.doorlock.Model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "accesslist")
public class AccessList {

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    @JoinColumn(name = "doorlock_id")
    @JsonBackReference
    private DoorLock doorLock;

    @ManyToMany
    @JsonBackReference
    Set<User> users;



    private boolean permanentAccess;
    private Timestamp expirationDate;







}
