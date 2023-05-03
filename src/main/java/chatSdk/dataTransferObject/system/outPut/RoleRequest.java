package chatSdk.dataTransferObject.system.outPut;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class RoleRequest {
    private long userId;
    private ArrayList<String> roles;
}
