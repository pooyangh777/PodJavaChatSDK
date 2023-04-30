package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import com.google.gson.Gson;

public class ThreadParticipantRequest extends BaseRequest {
    Long count;
    Long offset;

    public ThreadParticipantRequest(Builder builder) {
        super(builder.uniqueId, builder.subjectId, ChatMessageType.THREAD_PARTICIPANTS);
        this.count = builder.count;
        this.offset = builder.offset;
    }

    public static class Builder {
        Long count = 25l;
        Long offset = 0l;
        String uniqueId;
        Long subjectId;

        public Long getCount() {
            return count;
        }

        public Builder setCount(Long count) {
            this.count = count;
            return this;
        }

        public Long getOffset() {
            return offset;
        }

        public Builder setOffset(Long offset) {
            this.offset = offset;
            return this;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public Long getSubjectId() {
            return subjectId;
        }

        public Builder setSubjectId(Long subjectId) {
            this.subjectId = subjectId;
            return this;
        }

        public ThreadParticipantRequest build() {
            return new ThreadParticipantRequest(this);
        }
    }

    @Override
    public String getChatMessageContent() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
