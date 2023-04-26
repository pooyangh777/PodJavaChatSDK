package chatSdk.dataTransferObject.message.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.outPut.BaseRequest;

public class SendMessageRequest extends BaseRequest {
    private String message;
    private long threadId;
    private Integer messageType;

    public SendMessageRequest(Builder builder) {
        super(builder.uniqueId, builder.subjectId, ChatMessageType.MESSAGE);
        this.message = builder.message;
        this.threadId = builder.threadId;
        this.messageType = builder.messageType;
    }

    @Override
    public String getChatMessageContent() {
        return message;
    }

    public static class Builder {
        private String uniqueId;
        private transient Long subjectId;
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

        public Long getSubjectId() {
            return subjectId;
        }

        public Builder setSubjectId(Long subjectId) {
            this.subjectId = subjectId;
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
