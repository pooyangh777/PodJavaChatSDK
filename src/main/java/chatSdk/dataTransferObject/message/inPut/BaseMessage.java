package chatSdk.dataTransferObject.message.inPut;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BaseMessage {
    private String content;
    private String token;
    private String tokenIssuer;
    private int type;
    private long subjectId;
    private String uniqueId;
    private String typeCode;
}
