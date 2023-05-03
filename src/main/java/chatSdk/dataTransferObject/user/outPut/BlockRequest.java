package chatSdk.dataTransferObject.user.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.outPut.BaseRequest;
import com.google.gson.Gson;
import lombok.Getter;

@Getter
public class BlockRequest extends BaseRequest{
    private long contactId;
    private long userId;

    public BlockRequest(Builder builder) {
        super(builder.uniqueId, builder.subjectId, ChatMessageType.BLOCK);
        this.contactId = builder.contactId;
        this.userId = builder.userId;
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
        private Long subjectId;

        public Long getContactId() {
            return contactId;
        }

        public Long getUserId() {
            return userId;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public Long getSubjectId() {
            return subjectId;
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

        public Builder setSubjectId(Long subjectId) {
            this.subjectId = subjectId;
            return this;
        }

        public BlockRequest build() {
            return new BlockRequest(this);
        }
    }
}
