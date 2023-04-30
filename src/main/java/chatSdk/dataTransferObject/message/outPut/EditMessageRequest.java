package chatSdk.dataTransferObject.message.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.outPut.BaseRequest;
import com.google.gson.Gson;
import lombok.Getter;

@Getter
public class EditMessageRequest extends BaseRequest {

    private String messageContent;
    private long messageId;
    private String metaData;

    public EditMessageRequest(Builder builder) {
        super(builder.uniqueId, builder.subjectId, ChatMessageType.EDIT_MESSAGE);
        this.messageContent = builder.messageContent;
        this.messageId = builder.messageId;
        this.metaData = builder.metaData;
    }

    public static class Builder {
        private String messageContent;
        private long messageId;
        private String metaData;
        String uniqueId;
        Long subjectId;

        public String getMessageContent() {
            return messageContent;
        }

        public Builder setMessageContent(String messageContent) {
            this.messageContent = messageContent;
            return this;
        }

        public long getMessageId() {
            return messageId;
        }

        public Builder setMessageId(long messageId) {
            this.messageId = messageId;
            return this;
        }

        public String getMetaData() {
            return metaData;
        }

        public Builder setMetaData(String metaData) {
            this.metaData = metaData;
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

        public EditMessageRequest build() {
            return new EditMessageRequest(this);
        }
    }

    @Override
    public String getChatMessageContent() {
        return messageContent;
    }
}
