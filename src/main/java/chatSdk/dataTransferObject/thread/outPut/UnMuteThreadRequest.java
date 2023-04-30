package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import com.google.gson.Gson;

public class UnMuteThreadRequest extends BaseRequest {
    public UnMuteThreadRequest(Builder builder) {
        super(builder.uniqueId, builder.subjectId, ChatMessageType.UN_MUTE_THREAD);
    }

    public static class Builder {
        String uniqueId;
        Long subjectId;

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

        public UnMuteThreadRequest build() {
            return new UnMuteThreadRequest(this);
        }
    }

    @Override
    public String getChatMessageContent() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
