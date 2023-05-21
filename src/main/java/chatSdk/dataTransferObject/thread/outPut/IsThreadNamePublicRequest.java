package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;

public class IsThreadNamePublicRequest extends BaseRequest {
    private String name;

    public IsThreadNamePublicRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.IS_NAME_AVAILABLE);
        this.name = builder.name;
    }

    @Override
    public String getChatMessageContent() {
        return gson.toJson(this);
    }

    public static class Builder {
        private String name;
        private String uniqueId;

        public String getName() {
            return name;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public IsThreadNamePublicRequest build() {
            return new IsThreadNamePublicRequest(this);
        }
    }
}
