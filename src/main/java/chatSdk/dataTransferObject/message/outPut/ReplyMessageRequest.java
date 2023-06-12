package chatSdk.dataTransferObject.message.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.outPut.BaseRequest;
import lombok.Getter;

@Getter
public class ReplyMessageRequest extends BaseRequest {
    private Long threadId;
    private final Long repliedTo;
    private final String textMessage;
    private final String systemMetaData;

    public ReplyMessageRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.MESSAGE);
        this.subjectId = builder.threadId;
        this.repliedTo = builder.repliedTo;
        this.textMessage = builder.message;
        this.systemMetaData = builder.systemMetaData;
    }

    @Override
    public String getChatMessageContent() {
        return gson.toJson(textMessage);
    }

    public static class Builder {
        private Long threadId;
        private Long repliedTo;
        private String message;
        private String uniqueId;
        private String systemMetaData;

        public String getSystemMetaData() {
            return systemMetaData;
        }

        public Builder setSystemMetaData(String systemMetaData) {
            this.systemMetaData = systemMetaData;
            return this;
        }

        public Long getThreadId() {
            return threadId;
        }

        public Builder setThreadId(Long threadId) {
            this.threadId = threadId;
            return this;
        }

        public Long getRepliedTo() {
            return repliedTo;
        }

        public Builder setRepliedTo(Long repliedTo) {
            this.repliedTo = repliedTo;
            return this;
        }

        public String getMessage() {
            return message;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public ReplyMessageRequest build() {
            return new ReplyMessageRequest(this);
        }
    }
}
