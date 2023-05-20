package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import lombok.Getter;

@Getter
public class CreateTagRequest extends BaseRequest {
    private String name;

    public CreateTagRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.CREATE_TAG);
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

        public CreateTagRequest build() {
            return new CreateTagRequest(this);
        }
    }
}
