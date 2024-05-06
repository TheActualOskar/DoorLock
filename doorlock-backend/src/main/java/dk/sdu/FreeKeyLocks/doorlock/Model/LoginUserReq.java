package dk.sdu.FreeKeyLocks.doorlock.Model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginUserReq {
    private String email;
    private String password;

}
