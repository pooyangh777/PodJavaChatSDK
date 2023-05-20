package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import lombok.Getter;

@Getter
public class EditTagRequest extends BaseRequest {
    private String name;
    private Integer id;

    public EditTagRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.EDIT_TAG);
        this.id = builder.id;
        this.name = builder.name;
    }

    @Override
    public String getChatMessageContent() {
        return null;
    }

    public static class Builder {
        private String name;
        private Integer id;
        private String uniqueId;

        public String getName() {
            return name;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Integer getId() {
            return id;
        }

        public Builder setId(Integer id) {
            this.id = id;
            return this;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public EditTagRequest build() {
            return new EditTagRequest(this);
        }
    }
}
