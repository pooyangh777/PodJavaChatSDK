package chatSdk.dataTransferObject.message.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.outPut.BaseRequest;
import lombok.Getter;

@Getter
public class DeleteMessageRequest extends BaseRequest {
    private Boolean deleteForAll;
    private Long messageId;

    public DeleteMessageRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.DELETE_MESSAGE);
        this.deleteForAll = builder.deleteForAll;
        this.messageId = builder.messageId;
        this.subjectId = builder.messageId;
    }

    @Override
    public String getChatMessageContent() {
        return gson.toJson(this);
    }

    public static class Builder {
        private Boolean deleteForAll;
        private Long messageId;
        private String uniqueId;

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public Boolean getDeleteForAll() {
            return deleteForAll;
        }

        public Builder setDeleteForAll(Boolean deleteForAll) {
            this.deleteForAll = deleteForAll;
            return this;
        }

        public Long getMessageId() {
            return messageId;
        }

        public Builder setMessageId(Long messageId) {
            this.messageId = messageId;
            return this;
        }

        public DeleteMessageRequest build() {
            return new DeleteMessageRequest(this);
        }
    }
}