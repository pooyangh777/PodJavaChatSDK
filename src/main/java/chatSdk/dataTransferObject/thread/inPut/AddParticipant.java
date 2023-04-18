package chatSdk.dataTransferObject.thread.inPut;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddParticipant {
    private long subjectId;
    private String uniqueId;
    private String content;
    private String token;
    private String tokenIssuer;
    private int type;
    private String typeCode;
}
