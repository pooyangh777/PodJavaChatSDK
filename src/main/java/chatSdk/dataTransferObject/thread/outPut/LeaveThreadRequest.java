package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import lombok.Getter;

@Getter
public class LeaveThreadRequest extends BaseRequest {
    private Long threadId;
    private final Boolean clearHistory;

    public LeaveThreadRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.LEAVE_THREAD);
        this.subjectId = builder.threadId;
        this.clearHistory = builder.clearHistory;
    }

    @Override
    public String getChatMessageContent() {
        return gson.toJson(this);
    }

    public static class Builder {
        private Long threadId;
        private Boolean clearHistory;
        private String uniqueId;

        public Long getThreadId() {
            return threadId;
        }

        public Builder setThreadId(Long threadId) {
            this.threadId = threadId;
            return this;
        }

        public Boolean getClearHistory() {
            return clearHistory;
        }

        public Builder setClearHistory(Boolean clearHistory) {
            this.clearHistory = clearHistory;
            return this;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public LeaveThreadRequest build() {
            return new LeaveThreadRequest(this);
        }
    }
}
