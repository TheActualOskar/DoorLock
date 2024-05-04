package dk.sdu.FreeKeyLocks.doorlock.Repository;

import dk.sdu.FreeKeyLocks.doorlock.Model.AccessList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessListRepository extends JpaRepository<AccessList,Integer> {
}
