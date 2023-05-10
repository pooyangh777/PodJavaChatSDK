package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import com.google.gson.Gson;
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
        Gson gson = new Gson();
        return gson.toJson(clearHistory);
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
