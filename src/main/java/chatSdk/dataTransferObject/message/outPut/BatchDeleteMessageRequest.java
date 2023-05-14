package chatSdk.dataTransferObject.message.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.outPut.BaseRequest;
import lombok.Getter;

import java.util.ArrayList;
import java.util.UUID;

@Getter
public class BatchDeleteMessageRequest extends BaseRequest {
    private ArrayList<Long> ids;
    private boolean deleteForAll;
    private ArrayList<String> uniqueIds;

    public BatchDeleteMessageRequest(Builder builder) {
        super(builder.uniqueId, builder.subjectId, ChatMessageType.DELETE_MESSAGE);
        this.ids = builder.messageIds;
        this.deleteForAll = builder.deleteForAll;
        this.uniqueIds = builder.uniqueIds;
        if (uniqueIds == null) {
            uniqueIds = new ArrayList<>();
            for (int i = 0; i < ids.size(); i++) {
                uniqueIds.add(UUID.randomUUID().toString());
            }
        }
    }

    @Override
    public String getChatMessageContent() {
        return gson.toJson(this);
    }

    public static class Builder {
        private ArrayList<Long> messageIds;
        private boolean deleteForAll;
        private String uniqueId;
        private Long subjectId;
        private ArrayList<String> uniqueIds;

        public ArrayList<String> getUniqueIds() {
            return uniqueIds;
        }

        public Builder setUniqueIds(ArrayList<String> uniqueIds) {
            this.uniqueIds = uniqueIds;
            return this;
        }

        public Long getSubjectId() {
            return subjectId;
        }

        public Builder setSubjectId(Long subjectId) {
            this.subjectId = subjectId;
            return this;
        }

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

        public BatchDeleteMessageRequest build() {
            return new BatchDeleteMessageRequest(this);
        }
    }
}
