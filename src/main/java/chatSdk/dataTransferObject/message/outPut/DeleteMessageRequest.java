package chatSdk.dataTransferObject.message.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.outPut.BaseRequest;
import com.google.gson.Gson;

import java.util.ArrayList;

public class DeleteMessageRequest extends BaseRequest {
    private ArrayList<Long> messageIds;
    private boolean deleteForAll;

    public DeleteMessageRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.DELETE_MESSAGE);
        this.messageIds = builder.messageIds;
        this.deleteForAll = builder.deleteForAll;
    }

    @Override
    public String getChatMessageContent() {
        Gson gson = new Gson();
        return gson.toJson(this.messageIds);
    }

    public static class Builder {
        private ArrayList<Long> messageIds;
        private boolean deleteForAll;
        private String uniqueId;

        public ArrayList<Long> getMessageIds() {
            return messageIds;
        }

        public Builder setMessageIds(ArrayList<Long> messageIds) {
            this.messageIds = messageIds;
            return this;
        }

        public boolean isDeleteForAll() {
            return deleteForAll;
        }

        public Builder setDeleteForAll(boolean deleteForAll) {
            this.deleteForAll = deleteForAll;
            return this;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public DeleteMessageRequest build() {
            return new DeleteMessageRequest(this);
        }
    }
}
