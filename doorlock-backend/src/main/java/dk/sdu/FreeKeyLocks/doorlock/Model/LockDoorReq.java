package dk.sdu.FreeKeyLocks.doorlock.Model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LockDoorReq {

    private int doorID;
    private int userID;

    public LockDoorReq(int doorID, int userID) {
        this.doorID = doorID;
        this.userID = userID;
    }
}
