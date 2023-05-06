package chatSdk.dataTransferObject.user.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.outPut.BaseRequest;
import com.google.gson.Gson;

public class UnBlockRequest extends BaseRequest {
    private final Long blockId;
    private final Long userId;
    private final Long contactId;
    private final Long threadId;

    public UnBlockRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.UNBLOCK);
        this.blockId = builder.blockId;
        this.contactId = builder.contactId;
        this.userId = builder.userId;
        this.threadId = builder.threadId;
    }

    @Override
    public String getChatMessageContent() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static class Builder {
        private long blockId;
        private long userId;
        private long contactId;
        private String uniqueId;
        private Long threadId;

        public long getBlockId() {
            return blockId;
        }

        public Builder setBlockId(long blockId) {
            this.blockId = blockId;
            return this;
        }

        public long getUserId() {
            return userId;
        }

        public Builder setUserId(long userId) {
            this.userId = userId;
            return this;
        }

        public long getContactId() {
            return contactId;
        }

        public Builder setContactId(long contactId) {
            this.contactId = contactId;
            return this;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public Long getThreadId() {
            return threadId;
        }

        public Builder setThreadId(Long threadId) {
            this.threadId = threadId;
            return this;
        }

        public UnBlockRequest build() {
            return new UnBlockRequest(this);
        }
    }
}
