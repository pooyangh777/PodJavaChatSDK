package chatSdk.dataTransferObject.message.inPut;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultMessage {
    private Long messageId;
    private Long participantId;
    private Long conversationId;
    public Long messageTime;
    public MessageResponseState messageState;
}
