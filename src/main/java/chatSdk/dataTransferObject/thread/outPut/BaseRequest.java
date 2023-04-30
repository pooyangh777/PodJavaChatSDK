package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.message.outPut.Exclude;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public abstract class BaseRequest {
    String uniqueId;
    @Exclude
    Long subjectId;
    int chatMessageType;

    public BaseRequest(String uniqueId, int messageType) {
        this.uniqueId = uniqueId == null ? generateUniqueId() : uniqueId;
        this.chatMessageType = messageType;
    }

    public BaseRequest(String uniqueId) {
        this.uniqueId = uniqueId == null ? generateUniqueId() : uniqueId;
    }

    public BaseRequest(String uniqueId, Long subjectId, int messageType) {
        this.uniqueId = uniqueId == null ? generateUniqueId() : uniqueId;
        this.subjectId = subjectId;
        this.chatMessageType = messageType;
    }

    public BaseRequest(String uniqueId, Long subjectId) {
        this.uniqueId = uniqueId;
        this.subjectId = subjectId;
    }

    private String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    public abstract String getChatMessageContent();
}