package dk.sdu.FreeKeyLocks.doorlock.Model;

public class UnlockDoorReq {

    private int doorID;
    private int userID;

    public UnlockDoorReq(int doorID, int userID) {
        this.doorID = doorID;
        this.userID = userID;
    }

    public int getDoorID() {
        return doorID;
    }

    public void setDoorID(int doorID) {
        this.doorID = doorID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}
