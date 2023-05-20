package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;

public class DeleteTagRequest extends BaseRequest {
    private Long id;

    public DeleteTagRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.DELETE_TAG);
        this.subjectId = builder.id;
        this.id = builder.id;
    }

    @Override
    public String getChatMessageContent() {
        return gson.toJson(this);
    }

    public static class Builder {
        private String uniqueId;
        private Long id;

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public Long getId() {
            return id;
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public DeleteTagRequest build() {
            return new DeleteTagRequest(this);
        }
    }
}
