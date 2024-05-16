package dk.sdu.FreeKeyLocks.doorlock.Controllers;

import dk.sdu.FreeKeyLocks.doorlock.Model.DoorLock;
import dk.sdu.FreeKeyLocks.doorlock.Repository.DoorLockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DeviceStatusService {

    @Autowired
    DoorLockRepository doorLockRepository;


    private final Map<Integer, Instant> deviceLastSeen = new ConcurrentHashMap<>();
    private final Duration HEARTBEAT_INTERVAL = Duration.ofSeconds(40); // 30s interval + 10s grace period


    @Scheduled(fixedRate = 60_000)  // Every minute = 60_000
    public void checkDeviceStatuses() {
        Instant now = Instant.now();
        deviceLastSeen.forEach((id, lastSeen) -> {
            if (Duration.between(lastSeen, now).compareTo(HEARTBEAT_INTERVAL) > 0) {
                // Mark device as offline
                updateDeviceStatus(id, "Offline", false);
            }
        });
    }


    public void handleHeartbeatMessage(int deviceId, Instant timestamp) {
        deviceLastSeen.put(deviceId, timestamp);
        updateDeviceStatus(deviceId, "Online", true);
    }

    private void updateDeviceStatus(int deviceId, String status, boolean isOnline) {
        DoorLock doorLock = doorLockRepository.getReferenceById(deviceId);
        if (isOnline) {
            if (doorLock != null) {
                doorLock.setStatus(status);
                doorLock.setLastCheckIn(new Timestamp(System.currentTimeMillis()));
                doorLockRepository.save(doorLock);
            }
        } else {
            if (doorLock != null) {
                doorLock.setStatus(status);
                doorLockRepository.save(doorLock);
            }
        }

    }
}