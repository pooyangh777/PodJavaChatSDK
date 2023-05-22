package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;

import java.util.ArrayList;



public class AddTagParticipantsRequest extends BaseRequest {
    private Long tagId;
    private Long[] threadIds;

    public AddTagParticipantsRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.ADD_TAG_PARTICIPANTS);
        this.tagId = builder.tagId;
        this.subjectId = builder.tagId;
        this.threadIds = builder.threadId.toArray(new Long[builder.threadId.size()]);
    }


    @Override
    public String getChatMessageContent() {
        return gson.toJson(threadIds);
    }


    public static class Builder {
        private Long tagId;
        private ArrayList<Long> threadId;
        private String uniqueId;

        public Long getTagId() {
            return tagId;
        }

        public Builder setTagId(Long tagId) {
            this.tagId = tagId;
            return this;
        }

        public ArrayList<Long> getThreadId() {
            return threadId;
        }

        public Builder setThreadId(ArrayList<Long> threadId) {
            this.threadId = threadId;
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
