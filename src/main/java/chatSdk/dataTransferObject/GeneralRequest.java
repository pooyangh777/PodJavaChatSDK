package chatSdk.dataTransferObject;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.outPut.BaseRequest;

public class GeneralRequest extends BaseRequest {
    public GeneralRequest(Builder builder) {
        super(builder.uniqueId, builder.subjectId, builder.messageType);
    }

    @Override
    public String getChatMessageContent() {
        return gson.toJson(this);
    }

    public static class Builder {
        String uniqueId;
        Long subjectId;
        ChatMessageType messageType;

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

        public ChatMessageType getMessageType() {
            return messageType;
        }

        public Builder setMessageType(ChatMessageType messageType) {
            this.messageType = messageType;
            return this;
        }

        public GeneralRequest build() {
            return new GeneralRequest(this);
        }
    }
}