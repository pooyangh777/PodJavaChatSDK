package chatSdk.dataTransferObject.message.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.outPut.BaseRequest;
import lombok.Getter;

@Getter
public class DeliveryMessageRequest extends BaseRequest {
    private String messageId;
    private long threadId;

    public DeliveryMessageRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.DELIVERY);
        this.messageId = String.format("%d", builder.messageId);
        this.subjectId = builder.threadId;
    }

    @Override
    public String getChatMessageContent() {
        return messageId;
    }

    public static class Builder {
        private long messageId;
        private long threadId;
        private String uniqueId;

        public long getMessageId() {
            return messageId;
        }

        public Builder setMessageId(long messageId) {
            this.messageId = messageId;
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

        public DeliveryMessageRequest build() {
            return new DeliveryMessageRequest(this);
        }
    }
}
