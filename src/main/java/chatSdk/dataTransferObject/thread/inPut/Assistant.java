package chatSdk.dataTransferObject.thread.inPut;

import chatSdk.dataTransferObject.chat.RoleType;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class Assistant {
    private Integer offset;
    private Integer count;
    private Integer id;
    private String contactType = "default";
    private Invitee assistant;
    @SerializedName("participantVo")
    private Participant participant;
    private ArrayList<String> roles;
    private Boolean block;
}
