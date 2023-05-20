package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;

public class DeleteTagRequest extends BaseRequest {
    private Integer id;

    public DeleteTagRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.DELETE_TAG);
        this.id = builder.id;
    }

    @Override
    public String getChatMessageContent() {
        return gson.toJson(this);
    }

    public static class Builder {
        private String uniqueId;
        private Integer id;

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public Integer getId() {
            return id;
        }

        public Builder setId(Integer id) {
            this.id = id;
            return this;
        }

        public DeleteTagRequest build() {
            return new DeleteTagRequest(this);
        }
    }
}
