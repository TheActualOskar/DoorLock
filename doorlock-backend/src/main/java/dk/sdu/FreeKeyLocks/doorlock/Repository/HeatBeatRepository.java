package dk.sdu.FreeKeyLocks.doorlock.Repository;

import dk.sdu.FreeKeyLocks.doorlock.Model.HeartBeat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeatBeatRepository extends JpaRepository<HeartBeat,Integer> {
}
