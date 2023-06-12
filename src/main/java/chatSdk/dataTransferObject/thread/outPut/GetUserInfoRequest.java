package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;

public class GetUserInfoRequest extends BaseRequest {
    public GetUserInfoRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.USER_INFO);
    }

    public static class Builder {
        String uniqueId;

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public GetUserInfoRequest build() {
            return new GetUserInfoRequest(this);
        }
    }

    @Override
    public String getChatMessageContent() {
        return gson.toJson(this);
    }
}
