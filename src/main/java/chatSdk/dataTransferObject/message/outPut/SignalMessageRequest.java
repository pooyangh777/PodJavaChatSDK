package chatSdk.dataTransferObject.message.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.chat.SignalMessageType;
import chatSdk.dataTransferObject.thread.outPut.BaseRequest;
import lombok.Getter;

@Getter
public class SignalMessageRequest extends BaseRequest {
    private int type;
    private Long threadId;

    public SignalMessageRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.SYSTEM_MESSAGE);
        this.threadId = builder.threadId;
        this.type = builder.type;
    }

    @Override
    public String getChatMessageContent() {
        return gson.toJson(this);
    }

    public static class Builder {
        private int type;
        private Long threadId;
        private String uniqueId;

        public int getType() {
            return type;
        }

        public Builder setType(int type) {
            this.type = type;
            return this;
        }

        public Long getThreadId() {
            return threadId;
        }

        public Builder setThreadId(Long threadId) {
            this.threadId = threadId;
            return this;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public SignalMessageRequest build() {
            return new SignalMessageRequest(this);
        }
    }
}
