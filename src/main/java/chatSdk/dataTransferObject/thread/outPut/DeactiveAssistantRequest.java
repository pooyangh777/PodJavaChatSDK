package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.inPut.Assistant;

import java.util.ArrayList;

public class DeactiveAssistantRequest extends BaseRequest {
    private ArrayList<Assistant> assistants;

    public DeactiveAssistantRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.DEACTICVE_ASSISTANT);
        this.assistants = builder.assistants;
    }

    @Override
    public String getChatMessageContent() {
        return gson.toJson(assistants);
    }

    public static class Builder {
        private ArrayList<Assistant> assistants;
        private String uniqueId;

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

        public DeactiveAssistantRequest build() {
            return new DeactiveAssistantRequest(this);
        }
    }
}
