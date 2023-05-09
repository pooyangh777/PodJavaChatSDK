package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import com.google.gson.Gson;

public class MuteThreadRequest extends BaseRequest {
    public MuteThreadRequest(Builder builder) {
        super(builder.uniqueId, builder.subjectId, ChatMessageType.MUTE_THREAD);
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

        public MuteThreadRequest build() {
            return new MuteThreadRequest(this);
        }
    }

    @Override
    public String getChatMessageContent() {
        return gson.toJson(this);
    }
}
