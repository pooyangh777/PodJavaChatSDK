package chatSdk.dataTransferObject.message.inPut;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ChatMessage {
    private String token;
    private String typeCode;
    private String tokenIssuer;
    private int type;
    private Integer messageType;
    private long subjectId;
    private String uniqueId;
    private String content;
    private int contentCount;
    private String systemMetadata;
    private String metadata;
    private long repliedTo;
}
