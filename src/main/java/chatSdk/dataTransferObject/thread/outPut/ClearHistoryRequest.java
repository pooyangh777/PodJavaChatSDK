package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import lombok.Getter;

@Getter
public class ClearHistoryRequest extends BaseRequest {
    private Long threadId;

    public ClearHistoryRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.CLEAR_HISTORY);
        this.subjectId = builder.threadId;
    }

    @Override
    public String getChatMessageContent() {
        return gson.toJson(this);
    }

    public static class Builder {
        private String uniqueId;
        private Long threadId;

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public Long getThreadId() {
            return threadId;
        }

        public Builder setThreadId(Long threadId) {
            this.threadId = threadId;
            return this;
        }

        public ClearHistoryRequest build() {
            return new ClearHistoryRequest(this);
        }
    }
}
