package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.thread.inPut.Assistant;
import lombok.Getter;

import java.util.ArrayList;
@Getter
public class BlockAssistantRequest extends BaseRequest {
    private ArrayList<Assistant> assistants;

    public BlockAssistantRequest(Builder builder) {
        super(builder.uniqueId, builder.messageType);
        this.assistants = builder.assistants;
    }

    @Override
    public String getChatMessageContent() {
        return gson.toJson(assistants);
    }

    public static class Builder {
        private ArrayList<Assistant> assistants;
        private String uniqueId;
        private int messageType;

        public int getMessageType() {
            return messageType;
        }

        public Builder setMessageType(int messageType) {
            this.messageType = messageType;
            return this;
        }

        public ArrayList<Assistant> getAssistants() {
            return assistants;
        }

        public Builder setAssistants(ArrayList<Assistant> assistants) {
            this.assistants = assistants;
            return this;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public BlockAssistantRequest build() {
            return new BlockAssistantRequest(this);
        }
    }
}
