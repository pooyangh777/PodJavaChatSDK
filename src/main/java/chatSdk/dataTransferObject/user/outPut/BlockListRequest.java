package chatSdk.dataTransferObject.user.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.outPut.BaseRequest;

public class BlockListRequest extends BaseRequest {
    private long count;
    private long offset;

    public BlockListRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.GET_BLOCKED);
        this.count = builder.count;
        this.offset = builder.offset;
    }

    @Override
    public String getChatMessageContent() {
        return null;
    }

    public static class Builder {
        private long count;
        private long offset;
        private String uniqueId;

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

        public BlockListRequest build() {
            return new BlockListRequest(this);
        }
    }
}
