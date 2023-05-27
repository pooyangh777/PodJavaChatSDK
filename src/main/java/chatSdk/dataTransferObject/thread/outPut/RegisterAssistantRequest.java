package chatSdk.dataTransferObject.thread.outPut;


import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.inPut.Assistant;
import lombok.Getter;

import java.util.ArrayList;


@Getter
public class RegisterAssistantRequest extends BaseRequest {
    private ArrayList<Assistant> assistants;

    public RegisterAssistantRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.REGISTER_ASSISTANT);
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

        public RegisterAssistantRequest build() {
            return new RegisterAssistantRequest(this);
        }
    }
}
