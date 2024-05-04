package dk.sdu.FreeKeyLocks.doorlock.Repository;

import dk.sdu.FreeKeyLocks.doorlock.Model.LogEntry;
import dk.sdu.FreeKeyLocks.doorlock.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LogEntryRepository extends JpaRepository<LogEntry,Integer> {
}

