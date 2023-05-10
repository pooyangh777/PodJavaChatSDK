package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import com.google.gson.Gson;
import lombok.Getter;

import java.util.List;

@Getter
public class AddParticipantsRequest extends BaseRequest {
    private Long threadId;
    private List<Long> contactIds;

    public AddParticipantsRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.ADD_PARTICIPANT);
        this.subjectId = builder.threadId;
        this.contactIds = builder.contactIds;
    }

    @Override
    public String getChatMessageContent() {
        return gson.toJson(contactIds);
    }

    public static class Builder {
        private Long threadId;
        private List<Long> contactIds;
        String uniqueId;

        public long getThreadId() {
            return threadId;
        }

        public Builder setThreadId(long threadId) {
            this.threadId = threadId;
            return this;
        }

        public List<Long> getContactIds() {
            return contactIds;
        }

        public Builder setContactIds(List<Long> contactIds) {
            this.contactIds = contactIds;
            return this;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public AddParticipantsRequest build() {
            return new AddParticipantsRequest(this);
        }
    }
}
