package chatSdk.dataTransferObject.message.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.outPut.BaseRequest;
import lombok.Getter;

@Getter
public class DeliveredMessageListRequest extends BaseRequest {
    private long messageId;
    private long count;
    private long offset;

    public DeliveredMessageListRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.GET_MESSAGE_DELIVERY_PARTICIPANTS);
        this.count = builder.count;
        this.offset = builder.offset;
        this.messageId = builder.messageId;
    }

    @Override
    public String getChatMessageContent() {
        return gson.toJson(this);
    }

    public static class Builder {
        private long messageId;
        private long count;
        private long offset;
        private String uniqueId;

        public long getMessageId() {
            return messageId;
        }

        public Builder setMessageId(long messageId) {
            this.messageId = messageId;
            return this;
        }

        public long getCount() {
            return count;
        }

        public Builder setCount(long count) {
            this.count = count;
            return this;
        }

        public long getOffset() {
            return offset;
        }

        public Builder setOffset(long offset) {
            this.offset = offset;
            return this;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public DeliveredMessageListRequest build() {
            return new DeliveredMessageListRequest(this);
        }
    }
}
