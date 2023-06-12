package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import lombok.Getter;

@Getter
public class ChangeThreadTypeRequest extends BaseRequest {
    private String uniqueName;
    private Long threadId;
    private int type;

    public ChangeThreadTypeRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.CHANGE_THREAD_PRIVACY);
        this.uniqueName = builder.uniqueName;
        this.subjectId = builder.threadId;
        this.type = builder.threadType;
    }

    @Override
    public String getChatMessageContent() {
        return gson.toJson(this);
    }

    public static class Builder {
        private String uniqueName;
        private Long threadId;
        private int threadType;
        private String uniqueId;

        public String getUniqueName() {
            return uniqueName;
        }

        public Builder setUniqueName(String uniqueName) {
            this.uniqueName = uniqueName;
            return this;
        }

        public Long getThreadId() {
            return threadId;
        }

        public Builder setThreadId(Long threadId) {
            this.threadId = threadId;
            return this;
        }

        public int getType() {
            return threadType;
        }

        public Builder setType(int type) {
            this.threadType = type;
            return this;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public ChangeThreadTypeRequest build() {
            return new ChangeThreadTypeRequest(this);
        }
    }
}
