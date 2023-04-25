package chatSdk.dataTransferObject.thread.outPut;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public abstract class BaseRequest {
    String uniqueId;
    Long subjectId;
    int messageType;

    public BaseRequest(String uniqueId, int messageType) {
        this.uniqueId = uniqueId == null ? generateUniqueId() : uniqueId;
        this.messageType = messageType;
    }

    public BaseRequest(String uniqueId) {
        this.uniqueId = uniqueId == null ? generateUniqueId() : uniqueId;
    }

    public BaseRequest(String uniqueId, Long subjectId, int messageType) {
        this.uniqueId = uniqueId == null ? generateUniqueId() : uniqueId;
        this.subjectId = subjectId;
        this.messageType = messageType;
    }

    private String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    public abstract String getChatMessageContent();
}