package chatSdk.dataTransferObject.user.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.outPut.BaseRequest;
import com.google.gson.Gson;
import lombok.Getter;

@Getter
public class BlockRequest extends BaseRequest {
    private final Long contactId;
    private final Long userId;
    private final Long threadId;

    public BlockRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.BLOCK);
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
        private Long contactId;
        private Long userId;
        private String uniqueId;
        private Long threadId;

        public Long getContactId() {
            return contactId;
        }

        public Long getUserId() {
            return userId;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public Long getThreadId() {
            return threadId;
        }

        public Builder setContactId(Long contactId) {
            this.contactId = contactId;
            return this;
        }

        public Builder setUserId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public Builder setThreadId(Long threadId) {
            this.threadId = threadId;
            return this;
        }

        public BlockRequest build() {
            return new BlockRequest(this);
        }
    }
}
