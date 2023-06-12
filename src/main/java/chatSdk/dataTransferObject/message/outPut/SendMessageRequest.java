package chatSdk.dataTransferObject.message.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.outPut.BaseRequest;
import lombok.Getter;

@Getter
public class SendMessageRequest extends BaseRequest {
    private String message;
    private Integer messageType;
    private long threadId;

    public SendMessageRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.MESSAGE);
        this.message = builder.message;
        this.messageType = builder.messageType;
        this.subjectId = builder.threadId;
    }

    @Override
    public String getChatMessageContent() {
        return message;
    }

    public static class Builder {
        private String uniqueId;
        private Integer messageType;
        private String message;
        private long threadId;

        public String getMessage() {
            return message;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public long getThreadId() {
            return threadId;
        }

        public Builder setThreadId(long threadId) {
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

        public Integer getMessageType() {
            return messageType;
        }

        public Builder setMessageType(Integer messageType) {
            this.messageType = messageType;
            return this;
        }

        public SendMessageRequest build() {
            return new SendMessageRequest(this);
        }
    }
}
