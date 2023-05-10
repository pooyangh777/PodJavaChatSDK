package chatSdk.dataTransferObject.message.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.outPut.BaseRequest;
import com.google.gson.Gson;
import lombok.Getter;

@Getter
public class SeenMessageListRequest extends BaseRequest {
    private Long messageId;
    private long count;
    private long offset;

    public SeenMessageListRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.SEEN_MESSAGE_LIST);
        this.messageId = builder.messageId;
        this.count = builder.count;
        this.offset = builder.offset;
    }

    @Override
    public String getChatMessageContent() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static class Builder {
        private Long messageId;
        private long count;
        private long offset;
        private String uniqueId;

        public Long getMessageId() {
            return messageId;
        }

        public Builder setMessageId(Long messageId) {
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

        public SeenMessageListRequest build() {
            return new SeenMessageListRequest(this);
        }
    }
}
