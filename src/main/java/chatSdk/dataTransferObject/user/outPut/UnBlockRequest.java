package chatSdk.dataTransferObject.user.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.outPut.BaseRequest;

public class UnBlockRequest extends BaseRequest {
    private long blockId;
    private long userId;
    private long contactId;

    public UnBlockRequest(Builder builder) {
        super(builder.uniqueId, builder.subjectId, ChatMessageType.UNBLOCK);
        this.blockId = builder.blockId;
        this.contactId = builder.contactId;
        this.userId = builder.userId;
    }

    @Override
    public String getChatMessageContent() {
        return null;
    }

    public static class Builder {
        private long blockId;
        private long userId;
        private long contactId;
        private String uniqueId;
        private Long subjectId;

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

        public Long getSubjectId() {
            return subjectId;
        }

        public Builder setSubjectId(Long subjectId) {
            this.subjectId = subjectId;
            return this;
        }

        public UnBlockRequest build() {
            return new UnBlockRequest(this);
        }
    }
}
