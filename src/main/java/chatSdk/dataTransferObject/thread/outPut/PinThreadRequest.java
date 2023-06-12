package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;

public class PinThreadRequest extends BaseRequest {
    public PinThreadRequest(Builder builder) {
        super(builder.uniqueId, builder.subjectId, ChatMessageType.PIN_THREAD);
    }

    public static class Builder {
        String uniqueId;
        Long subjectId;

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

        public PinThreadRequest build() {
            return new PinThreadRequest(this);
        }
    }

    @Override
    public String getChatMessageContent() {
        return gson.toJson(this);
    }

}
