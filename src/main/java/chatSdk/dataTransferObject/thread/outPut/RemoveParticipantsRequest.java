package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import com.google.gson.Gson;
import lombok.Getter;

import java.util.List;

@Getter
public class RemoveParticipantsRequest extends BaseRequest {
    private long threadId;
    private final List<Long> participantIds;

    public RemoveParticipantsRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.REMOVE_PARTICIPANT);
        this.subjectId = builder.threadId;
        this.participantIds = builder.participantIds;
    }

    @Override
    public String getChatMessageContent() {
        Gson gson = new Gson();
        return gson.toJson(participantIds);
    }

    public static class Builder {
        String uniqueId;
        Long threadId;
        private List<Long> participantIds;

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public Long getThreadId() {
            return threadId;
        }

        public Builder setThreadId(Long threadId) {
            this.threadId = threadId;
            return this;
        }

        public List<Long> getParticipantIds() {
            return participantIds;
        }

        public Builder setParticipantIds(List<Long> participantIds) {
            this.participantIds = participantIds;
            return this;
        }

        public RemoveParticipantsRequest build() {
            return new RemoveParticipantsRequest(this);
        }
    }
}
