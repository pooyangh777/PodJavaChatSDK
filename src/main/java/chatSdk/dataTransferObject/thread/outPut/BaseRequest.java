package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.message.outPut.AnnotationExclusionStrategy;
import chatSdk.dataTransferObject.message.outPut.Exclude;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public abstract class BaseRequest {
    @Exclude
    protected String uniqueId;
    @Exclude
    protected Long subjectId;

    @Exclude
    protected int chatMessageType;
    @Exclude
    protected Long repliedTo;
    @Exclude
    protected Gson gson;

    public BaseRequest(String uniqueId, int messageType) {
        this.uniqueId = uniqueId == null ? generateUniqueId() : uniqueId;
        this.chatMessageType = messageType;
        this.gson = new GsonBuilder().setExclusionStrategies(new AnnotationExclusionStrategy()).create();
    }

    public BaseRequest(String uniqueId) {
        this.uniqueId = uniqueId == null ? generateUniqueId() : uniqueId;
        this.gson = new GsonBuilder().setExclusionStrategies(new AnnotationExclusionStrategy()).create();
    }

    public BaseRequest(String uniqueId, Long subjectId, int messageType) {
        this.uniqueId = uniqueId == null ? generateUniqueId() : uniqueId;
        this.subjectId = subjectId;
        this.chatMessageType = messageType;
        this.gson = new GsonBuilder().setExclusionStrategies(new AnnotationExclusionStrategy()).create();
    }

    public BaseRequest(String uniqueId, Long subjectId) {
        this.uniqueId = uniqueId;
        this.subjectId = subjectId;
        this.gson = new GsonBuilder().setExclusionStrategies(new AnnotationExclusionStrategy()).create();
    }

    private String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    public abstract String getChatMessageContent();

    public Long getRepliedTo() {
        return repliedTo;
    }

    public void setRepliedTo(Long repliedTo) {
        this.repliedTo = repliedTo;
    }
}