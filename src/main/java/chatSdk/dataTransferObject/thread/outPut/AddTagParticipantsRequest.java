package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;

import java.util.ArrayList;

public class AddTagParticipantsRequest extends BaseRequest {
    private Integer tagId;
    private ArrayList<Long> threadIds;

    public AddTagParticipantsRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.ADD_TAG_PARTICIPANTS);
        this.tagId = builder.tagId;
        this.threadIds = builder.threadIds;
    }


    @Override
    public String getChatMessageContent() {
        return gson.toJson(this);
    }


    public static class Builder {
        private Integer tagId;
        private ArrayList<Long> threadIds;
        private String uniqueId;

        public Integer getTagId() {
            return tagId;
        }

        public Builder setTagId(Integer tagId) {
            this.tagId = tagId;
            return this;
        }

        public ArrayList<Long> getThreadIds() {
            return threadIds;
        }

        public Builder setThreadIds(ArrayList<Long> threadIds) {
            this.threadIds = threadIds;
            return this;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public AddTagParticipantsRequest build() {
            return new AddTagParticipantsRequest(this);
        }
    }
}
