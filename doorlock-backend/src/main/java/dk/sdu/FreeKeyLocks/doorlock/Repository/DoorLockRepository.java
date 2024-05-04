package dk.sdu.FreeKeyLocks.doorlock.Repository;

import dk.sdu.FreeKeyLocks.doorlock.Model.DoorLock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoorLockRepository extends JpaRepository<DoorLock,Integer> {
}
