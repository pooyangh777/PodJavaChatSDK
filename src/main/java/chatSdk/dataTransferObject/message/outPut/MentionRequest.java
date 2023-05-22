package chatSdk.dataTransferObject.message.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.outPut.BaseRequest;

public class MentionRequest extends BaseRequest {
    private Integer count = 25;
    private Integer offset = 0;
    private Long threadId;
    private Boolean onlyUnreadMention;

    public MentionRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.THREAD_PARTICIPANTS);
        this.subjectId = builder.threadId;
        this.count = builder.count;
        this.offset = builder.offset;
        this.onlyUnreadMention = builder.onlyUnreadMention;
    }

    @Override
    public String getChatMessageContent() {
        return null;
    }

    public static class Builder {
        private Integer count = 25;
        private Integer offset = 0;
        private Long threadId;
        private Boolean onlyUnreadMention;
        private String uniqueId;

        public Integer getCount() {
            return count;
        }

        public Builder setCount(Integer count) {
            this.count = count;
            return this;
        }

        public Integer getOffset() {
            return offset;
        }

        public Builder setOffset(Integer offset) {
            this.offset = offset;
            return this;
        }

        public Long getThreadId() {
            return threadId;
        }

        public Builder setThreadId(Long threadId) {
            this.threadId = threadId;
            return this;
        }

        public Boolean getOnlyUnreadMention() {
            return onlyUnreadMention;
        }

        public Builder setOnlyUnreadMention(Boolean onlyUnreadMention) {
            this.onlyUnreadMention = onlyUnreadMention;
            return this;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public MentionRequest build() {
            return new MentionRequest(this);
        }
    }
}
