package dk.sdu.FreeKeyLocks.doorlock.Model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UnlockDoorReq {

    private int doorID;
    private int userID;

    public UnlockDoorReq(int doorID, int userID) {
        this.doorID = doorID;
        this.userID = userID;
    }

}
