package chatSdk.dataTransferObject.message.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.outPut.BaseRequest;

public class SeenMessageRequest extends BaseRequest {
    private long messageId;
    private long ownerId;

    public SeenMessageRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.SEEN);
        this.messageId = builder.messageId;
        this.ownerId = builder.ownerId;
    }

    @Override
    public String getChatMessageContent() {
        return gson.toJson(this);
    }

    public static class Builder {
        private long messageId;
        private long ownerId;
        private String uniqueId;

        public long getMessageId() {
            return messageId;
        }

        public Builder setMessageId(long messageId) {
            this.messageId = messageId;
            return this;
        }

        public long getOwnerId() {
            return ownerId;
        }

        public Builder setOwnerId(long ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public SeenMessageRequest build() {
            return new SeenMessageRequest(this);
        }
    }
}
