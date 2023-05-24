package chatSdk.dataTransferObject.message.inPut;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {
    private String token;
    private String typeCode;
    private String tokenIssuer = "1";
    private Integer type;
    private Integer messageType;
    private Long subjectId;
    private String uniqueId;
    private String content;
    private Long contentCount;
    private String systemMetadata;
    private String metadata;
    private Long repliedTo;
    private Long time;
}
