package chatSdk.dataTransferObject.message.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.outPut.BaseRequest;
import com.google.gson.Gson;
import lombok.Getter;

@Getter
public class AllUnReadMessageCountRequest extends BaseRequest {
    private Boolean mute;

    public AllUnReadMessageCountRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.ALL_UNREAD_MESSAGE_COUNT);
        this.mute = builder.countMutedThreads;
    }

    @Override
    public String getChatMessageContent() {
        return gson.toJson(this);
    }

    public static class Builder {
        private Boolean countMutedThreads;
        private String uniqueId;

        public Boolean getCountMutedThreads() {
            return countMutedThreads;
        }

        public Builder setCountMutedThreads(Boolean countMutedThreads) {
            this.countMutedThreads = countMutedThreads;
            return this;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public AllUnReadMessageCountRequest build() {
            return new AllUnReadMessageCountRequest(this);
        }
    }
}
