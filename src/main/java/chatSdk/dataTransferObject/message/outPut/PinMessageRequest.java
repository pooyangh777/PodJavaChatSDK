package chatSdk.dataTransferObject.message.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.outPut.BaseRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class PinMessageRequest extends BaseRequest {

    public Long messageId;
    @Expose
    public Boolean notifyAll;

    public PinMessageRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.PIN_MESSAGE);
        this.subjectId = builder.messageId;
        this.notifyAll = builder.notifyAll;
    }

    @Override
    public String getChatMessageContent() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }

    public static class Builder {
        private Long messageId;
        private Boolean notifyAll;
        private String uniqueId;

        public Long getMessageId() {
            return messageId;
        }

        public Builder setMessageId(Long messageId) {
            this.messageId = messageId;
            return this;
        }

        public Boolean getNotifyAll() {
            return notifyAll;
        }

        public Builder setNotifyAll(Boolean notifyAll) {
            this.notifyAll = notifyAll;
            return this;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public PinMessageRequest build() {
            return new PinMessageRequest(this);
        }
    }
}
