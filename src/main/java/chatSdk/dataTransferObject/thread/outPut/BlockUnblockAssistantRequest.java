package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.inPut.Assistant;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public class BlockUnblockAssistantRequest extends BaseRequest {
    private ArrayList<Assistant> assistants;

    public BlockUnblockAssistantRequest(Builder builder) {
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
        private ChatMessageType messageType;

        public ChatMessageType getMessageType() {
            return messageType;
        }

        public Builder setMessageType(ChatMessageType messageType) {
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

        public BlockUnblockAssistantRequest build() {
            return new BlockUnblockAssistantRequest(this);
        }
    }
}
