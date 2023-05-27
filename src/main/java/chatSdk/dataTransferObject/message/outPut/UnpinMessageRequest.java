package chatSdk.dataTransferObject.message.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.outPut.BaseRequest;

public class UnpinMessageRequest extends BaseRequest {
    @Exclude
    private Long messageId;
    private Boolean notifyAll;

    public UnpinMessageRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.UNPIN_MESSAGE);
        this.messageId = builder.messageId;
        this.notifyAll = builder.notifyAll;
    }

    @Override
    public String getChatMessageContent() {
        return gson.toJson(this);
    }

    public static class Builder {
        private Long messageId;
        private Boolean notifyAll;
        private String uniqueId;

        public Long getMessageId() {
            return messageId;
        }

        public Builder setMessageId(Long messageId) {
            this.messageId = messageId;
            return this;
        }

        public Boolean getNotifyAll() {
            return notifyAll;
        }

        public Builder setNotifyAll(Boolean notifyAll) {
            this.notifyAll = notifyAll;
            return this;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public UnpinMessageRequest build() {
            return new UnpinMessageRequest(this);
        }
    }
}
